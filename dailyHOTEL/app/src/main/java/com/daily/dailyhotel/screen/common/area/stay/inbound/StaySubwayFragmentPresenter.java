package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BasePagerFragmentPresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StaySubwayFragmentPresenter extends BasePagerFragmentPresenter<StaySubwayFragment, StaySubwayFragmentInterface.ViewInterface>//
    implements StaySubwayFragmentInterface.OnEventListener
{
    private StaySubwayFragmentInterface.AnalyticsInterface mAnalytics;

    public StaySubwayFragmentPresenter(@NonNull StaySubwayFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getFragment().getArguments();


        return getViewInterface().getContentView(inflater, R.layout.fragment_stay_subway_list_data, container);
    }

    @NonNull
    @Override
    protected StaySubwayFragmentInterface.ViewInterface createInstanceViewInterface()
    {
        return new StaySubwayFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new StaySubwayFragmentAnalyticsImpl());

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StaySubwayFragmentInterface.AnalyticsInterface) analytics;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
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
    public void onDestroy()
    {
        super.onDestroy();

    }

    @Override
    public void onBackClick()
    {
        // 사용하지 않음.
    }

    @Override
    public void onSelected()
    {
    }

    @Override
    public void onUnselected()
    {
    }

    @Override
    public void onRefresh()
    {
    }

    @Override
    public void scrollTop()
    {
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            setRefresh(false);
            return;
        }

        setRefresh(false);
        screenLock(showProgress);


    }
}
