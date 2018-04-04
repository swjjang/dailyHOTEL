package com.daily.dailyhotel.screen.booking.detail.gourmet.receipt;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.GourmetReceipt;
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
public class GourmetReceiptPresenter extends BaseExceptionPresenter<GourmetReceiptActivity, GourmetReceiptInterface> implements GourmetReceiptView.OnEventListener
{
    private GourmetReceiptAnalyticsInterface mAnalytics;

    ReceiptRemoteImpl mReceiptRemoteImpl;

    boolean mIsFullScreenMode;
    String mAggregationId;
    int mReservationIndex;

    public interface GourmetReceiptAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public GourmetReceiptPresenter(@NonNull GourmetReceiptActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetReceiptInterface createInstanceViewInterface()
    {
        return new GourmetReceiptView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetReceiptActivity activity)
    {
        setContentView(R.layout.activity_gourmet_receipt_data);

        mAnalytics = new GourmetReceiptAnalyticsImpl();

        mReceiptRemoteImpl = new ReceiptRemoteImpl(getActivity());

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

        mReservationIndex = intent.getIntExtra(GourmetReceiptActivity.INTENT_EXTRA_RESERVATION_INDEX, -1);
        mAggregationId = intent.getStringExtra(GourmetReceiptActivity.INTENT_EXTRA_AGGREGATION_ID);

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
    }

    @Override
    public void onStart()
    {
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

        if (requestCode == GourmetReceiptActivity.REQUEST_CODE_EMAIL)
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

        Observable<GourmetReceipt> receiptObservable = Observable.defer(new Callable<ObservableSource<GourmetReceipt>>()
        {
            @Override
            public ObservableSource<GourmetReceipt> call() throws Exception
            {
                if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                {
                    return mReceiptRemoteImpl.getGourmetReceipt(mReservationIndex);
                }

                return mReceiptRemoteImpl.getGourmetReceipt(mAggregationId);
            }
        });

        addCompositeDisposable(receiptObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetReceipt>()
        {
            @Override
            public void accept(GourmetReceipt gourmetReceipt) throws Exception
            {
                mReservationIndex = gourmetReceipt.gourmetReservationIdx;

                if (gourmetReceipt.gourmetReservationIdx < 0)
                {
                    Crashlytics.logException(new NullPointerException("GourmetReceiptActivity : mReservationIndex == null"));
                }

                getViewInterface().setReceipt(gourmetReceipt);

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
            startActivityForResult(intent, GourmetReceiptActivity.REQUEST_CODE_EMAIL);
        }
    }

    @Override
    public void onSendEmailClick(String email)
    {
        if (DailyTextUtils.isTextEmpty(email) == true || mReceiptRemoteImpl == null || getViewInterface() == null)
        {
            return;
        }

        Observable<String> emailObservable = Observable.defer(new Callable<ObservableSource<? extends String>>()
        {
            @Override
            public ObservableSource<? extends String> call() throws Exception
            {
                if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                {
                    return mReceiptRemoteImpl.getGourmetReceiptByEmail(mReservationIndex, email);
                }

                return mReceiptRemoteImpl.getGourmetReceiptByEmail(mAggregationId, email);
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
