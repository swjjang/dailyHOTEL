package com.daily.dailyhotel.screen.booking.detail.stay.receipt;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.StayReceipt;
import com.daily.dailyhotel.repository.remote.ReceiptRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.email.receipt.EmailDialogActivity;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayReceiptPresenter extends BaseExceptionPresenter<StayReceiptActivity, StayReceiptInterface> implements StayReceiptView.OnEventListener
{
    private StayReceiptAnalyticsInterface mAnalytics;

    ReceiptRemoteImpl mReceiptRemoteImpl;

    private int mBookingState;
    private String mAggregationId;
    int mReservationIndex;
    boolean mIsFullScreenMode;

    public interface StayReceiptAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);
    }

    public StayReceiptPresenter(@NonNull StayReceiptActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayReceiptInterface createInstanceViewInterface()
    {
        return new StayReceiptView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayReceiptActivity activity)
    {
        setContentView(R.layout.activity_stay_receipt_data);

        mAnalytics = new StayReceiptAnalyticsImpl();

        mReceiptRemoteImpl = new ReceiptRemoteImpl();

        mIsFullScreenMode = false;

        if (getViewInterface() != null)
        {
            getViewInterface().setFullScreenMode(mIsFullScreenMode);
        }

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mReservationIndex = intent.getIntExtra(StayReceiptActivity.INTENT_EXTRA_RESERVATION_INDEX, -1);
        mBookingState = intent.getIntExtra(StayReceiptActivity.INTENT_EXTRA_BOOKING_STATE, Booking.BOOKING_STATE_NONE);
        mAggregationId = intent.getStringExtra(StayReceiptActivity.INTENT_EXTRA_AGGREGATION_ID);

        if (mReservationIndex < 0 && DailyTextUtils.isTextEmpty(mAggregationId) == true)
        {
            return false;
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        if (getViewInterface() == null)
        {
            return;
        }

        getViewInterface().setBookingState(mBookingState);
    }

    @Override
    public void onStart()
    {
        mAnalytics.onScreen(getActivity());

        super.onStart();

        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);
            restartExpiredSession();
            return;
        }

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        if (getViewInterface() != null && mIsFullScreenMode == true)
        {
            mIsFullScreenMode = false;
            getViewInterface().setFullScreenMode(mIsFullScreenMode);
            return true;
        }

        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        if (requestCode == StayReceiptActivity.REQUEST_CODE_EMAIL)
        {
            if (resultCode == Activity.RESULT_OK && data != null)
            {
                String email = data.getStringExtra(EmailDialogActivity.INTENT_EXTRA_DATA_EMAIL);
                onSendEmailClick(email);
            }
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        Observable<StayReceipt> receiptObservable = Observable.defer(new Callable<ObservableSource<StayReceipt>>()
        {
            @Override
            public ObservableSource<StayReceipt> call() throws Exception
            {
                //                if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                //                {
                return mReceiptRemoteImpl.getStayReceipt(mReservationIndex);
                //                }

                // 현재 Stay의 경우 AggregationId를 지원하지 않아 아래 코드가 불리면 죽음 - 때문에 주석 처리 함
                //                return mReceiptRemoteImpl.getStayReceipt(mAggregationId);
            }
        });

        addCompositeDisposable(receiptObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StayReceipt>()
        {
            @Override
            public void accept(StayReceipt stayReceipt) throws Exception
            {
                mReservationIndex = stayReceipt.reservationIndex;

                if (stayReceipt.reservationIndex < 0)
                {
                    Crashlytics.logException(new NullPointerException("StayReceiptActivity : mReservationIndex == null"));
                }

                getViewInterface().setReceipt(stayReceipt);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onEmailClick()
    {
        if (mReservationIndex < 0 && DailyTextUtils.isTextEmpty(mAggregationId) == true)
        {
            restartExpiredSession();
        } else
        {
            Intent intent = EmailDialogActivity.newInstance(getActivity(), DailyUserPreference.getInstance(getActivity()).getEmail());
            startActivityForResult(intent, StayReceiptActivity.REQUEST_CODE_EMAIL);
        }
    }

    @Override
    public void onSendEmailClick(String email)
    {
        if (DailyTextUtils.isTextEmpty(email) == true || mReceiptRemoteImpl == null || getViewInterface() == null)
        {
            return;
        }

        Observable<String> emailObservable = Observable.defer(new Callable<ObservableSource<String>>()
        {
            @Override
            public ObservableSource<String> call() throws Exception
            {
                //                if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                //                {
                return mReceiptRemoteImpl.getStayReceiptByEmail(mReservationIndex, email);
                //                }

                //                return mReceiptRemoteImpl.getStayReceiptByEmail(mAggregationId, email);
            }
        });

        addCompositeDisposable(emailObservable.observeOn(AndroidSchedulers.mainThread()) //
            .subscribe(new Consumer<String>()
            {
                @Override
                public void accept(String message) throws Exception
                {
                    getViewInterface().showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    onHandleError(throwable);
                }
            }));
    }

    @Override
    public void onScreenClick()
    {
        if (getViewInterface() == null)
        {
            return;
        }

        mIsFullScreenMode = !mIsFullScreenMode;
        getViewInterface().setFullScreenMode(mIsFullScreenMode);
    }
}
