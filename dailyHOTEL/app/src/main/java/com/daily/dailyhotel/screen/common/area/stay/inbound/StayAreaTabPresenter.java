package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAreaTabPresenter extends BaseExceptionPresenter<StayAreaTabActivity, StayAreaTabInterface.ViewInterface> implements StayAreaTabInterface.OnEventListener
{
    private StayAreaTabInterface.AnalyticsInterface mAnalytics;

    private StayRemoteImpl mStayRemoteImpl;

    StayAreaViewModel mStayAreaViewModel;
    String mCategoryCode;
    DailyCategoryType mDailyCategoryType;

    public StayAreaTabPresenter(@NonNull StayAreaTabActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayAreaTabInterface.ViewInterface createInstanceViewInterface()
    {
        return new StayAreaTabView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayAreaTabActivity activity)
    {
        setContentView(R.layout.activity_stay_area_list_data);

        setAnalytics(new StayAreaAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayAreaTabInterface.AnalyticsInterface) analytics;
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

        addCompositeDisposable(mStayRemoteImpl.getRegionList(mDailyCategoryType).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayAreaGroup>>()
        {
            @Override
            public void accept(List<StayAreaGroup> areaGroupList) throws Exception
            {
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
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

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mStayAreaViewModel = ViewModelProviders.of(activity, new StayAreaViewModel.StayAreaViewModelFactory()).get(StayAreaViewModel.class);

    }
}
