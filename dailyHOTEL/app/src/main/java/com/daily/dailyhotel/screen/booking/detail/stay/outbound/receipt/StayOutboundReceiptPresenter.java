package com.daily.dailyhotel.screen.booking.detail.stay.outbound.receipt;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayOutboundReceipt;
import com.daily.dailyhotel.repository.remote.StayOutboundReceiptRemoteImpl;
import com.twoheart.dailyhotel.R;

import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundReceiptPresenter extends BaseExceptionPresenter<StayOutboundReceiptActivity, StayOutboundReceiptInterface> implements StayOutboundReceiptView.OnEventListener
{
    private CopyAnalyticsInterface mAnalytics;

    private StayOutboundReceiptRemoteImpl mStayOutboundReceiptRemoteImpl;
    private int mBookingIndex;
    private StayOutboundReceipt mStayOutboundReceipt;
    private boolean mFullScreenEnabled;

    public interface CopyAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundReceiptPresenter(@NonNull StayOutboundReceiptActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundReceiptInterface createInstanceViewInterface()
    {
        return new StayOutboundReceiptView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundReceiptActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_receipt_data);

        setAnalytics(new StayOutboundReceiptAnalyticsImpl());

        mStayOutboundReceiptRemoteImpl = new StayOutboundReceiptRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (CopyAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mBookingIndex = intent.getIntExtra(StayOutboundReceiptActivity.INTENT_EXTRA_DATA_BOOKING_INDEX, -1);

        if (mBookingIndex < 0)
        {
            return false;
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.frag_issuing_receipt));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

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

        addCompositeDisposable(mStayOutboundReceiptRemoteImpl.getReceipt(mBookingIndex).subscribe(new Consumer<StayOutboundReceipt>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull StayOutboundReceipt stayOutboundReceipt) throws Exception
            {
                setStayOutboundReceipt(stayOutboundReceipt);
                notifyStayOutboundReceiptChanged();

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                onHandleErrorAndFinish(throwable);
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
        getViewInterface().showSendEmailDialog(mStayOutboundReceipt.userEmail);
    }

    @Override
    public void onSendEmailClick(String email)
    {
        if (DailyTextUtils.isTextEmpty(email) == true)
        {
            DailyToast.showToast(getActivity(), R.string.toast_msg_please_input_email, DailyToast.LENGTH_SHORT);
            return;
        }

        if (DailyTextUtils.validEmail(email) == false)
        {
            DailyToast.showToast(getActivity(), R.string.toast_msg_wrong_email_address, DailyToast.LENGTH_SHORT);
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mStayOutboundReceiptRemoteImpl.getEmailReceipt(mBookingIndex, email).subscribe(new Consumer<String>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull String result) throws Exception
            {
                unLockAll();

                getViewInterface().showSimpleDialog(null, result, getString(R.string.dialog_btn_text_confirm), null);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onFullScreenClick()
    {
        mFullScreenEnabled = !mFullScreenEnabled;
        getViewInterface().setFullScreenEnabled(mFullScreenEnabled);
    }

    void setStayOutboundReceipt(StayOutboundReceipt stayOutboundReceipt)
    {
        mStayOutboundReceipt = stayOutboundReceipt;
    }

    void notifyStayOutboundReceiptChanged()
    {
        if (mStayOutboundReceipt == null)
        {
            return;
        }

        getViewInterface().setStayOutboundReceipt(mStayOutboundReceipt);
    }
}
