package com.daily.dailyhotel.screen.stay.outbound.payment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.PaymentRemoteImpl;
import com.twoheart.dailyhotel.R;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPaymentPresenter extends BaseExceptionPresenter<StayOutboundPaymentActivity, StayOutobundPaymentInterface> implements StayOutobundPaymentView.OnEventListener
{
    private StayOutboundPaymentAnalyticsInterface mAnalytics;

    private PaymentRemoteImpl mPaymentRemoteImpl;

    public interface StayOutboundPaymentAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundPaymentPresenter(@NonNull StayOutboundPaymentActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutobundPaymentInterface createInstanceViewInterface()
    {
        return new StayOutobundPaymentView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundPaymentActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_payment_data);

        setAnalytics(new StayOutboundPaymentAnalyticsImpl());

        mPaymentRemoteImpl = new PaymentRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundPaymentAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
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
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }


        setRefresh(false);
        screenLock(showProgress);

        Observable<StayOutboundPayment> observable;

        addCompositeDisposable(mPaymentRemoteImpl.getStayOutBoundPayment().subscribe(new Consumer<StayOutboundPayment>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull StayOutboundPayment stayOutboundPayment) throws Exception
            {

            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {

            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

}
