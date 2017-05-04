package com.daily.dailyhotel.screen.stay.outbound.detail;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseAsyncPresenter;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.twoheart.dailyhotel.R;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundDetailPresenter extends BaseAsyncPresenter<StayOutboundDetailActivity, StayOutboundDetailViewInterface> implements StayOutboundDetailView.OnEventListener
{
    private StayOutboundDetailAnalyticsInterface mAnalytics;

    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;

    private int mStayIndex;
    private StayBookDateTime mStayBookDateTime;
    private Persons mPersons;

    public interface StayOutboundDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundDetailPresenter(@NonNull StayOutboundDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundDetailViewInterface createInstanceViewInterface()
    {
        return new StayOutboundDetailView(getActivity(), this);
    }

    @Override
    public void initialize(StayOutboundDetailActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_data);

        setAnalytics(new StayStayOutboundDetailAnalyticsImpl());

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundDetailAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        }

        return true;
    }

    @Override
    public void onIntentAfter()
    {

    }

    @Override
    protected void startAsync(@NonNull Observable<Boolean> observable)
    {
        Observable.zip(observable, mStayOutboundRemoteImpl.getStayOutBoundDetail(mStayIndex, mStayBookDateTime, mPersons), new BiFunction<Boolean, StayOutboundDetail, StayOutboundDetail>()
        {
            @Override
            public StayOutboundDetail apply(Boolean aBoolean, StayOutboundDetail stayOutboundDetail) throws Exception
            {
                return stayOutboundDetail;
            }
        }).subscribe(new Consumer<StayOutboundDetail>()
        {
            @Override
            public void accept(StayOutboundDetail stayOutboundDetail) throws Exception
            {
                if (stayOutboundDetail == null)
                {
                    return;
                }

                // 화면 업데이트.
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {

                onHandleError(throwable);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh();
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

        switch (requestCode)
        {
        }
    }

    @Override
    protected void onRefresh()
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(true);


    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }
}
