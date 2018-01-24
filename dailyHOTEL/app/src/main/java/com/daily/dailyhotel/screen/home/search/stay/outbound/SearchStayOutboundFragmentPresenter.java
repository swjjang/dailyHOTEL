package com.daily.dailyhotel.screen.home.search.stay.outbound;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BasePagerFragmentPresenter;
import com.daily.dailyhotel.screen.home.search.SearchPresenter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayOutboundFragmentPresenter extends BasePagerFragmentPresenter<SearchStayOutboundFragment, SearchStayOutboundFragmentInterface.ViewInterface>//
    implements SearchStayOutboundFragmentInterface.OnEventListener
{
    private SearchStayOutboundFragmentInterface.AnalyticsInterface mAnalytics;

    SearchPresenter.SearchModel mSearchModel;

    public SearchStayOutboundFragmentPresenter(@NonNull SearchStayOutboundFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return getViewInterface().getContentView(inflater, R.layout.fragment_search_stay_outbound_data, container);
    }

    @NonNull
    @Override
    protected SearchStayOutboundFragmentInterface.ViewInterface createInstanceViewInterface()
    {
        return new SearchStayOutboundFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new SearchStayOutboundFragmentAnalyticsImpl());

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchStayOutboundFragmentInterface.AnalyticsInterface) analytics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

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
        if (isCurrentFragment() == false)
        {
            return false;
        }


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

    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mSearchModel = ViewModelProviders.of(activity).get(SearchPresenter.SearchModel.class);
    }

    boolean isCurrentFragment()
    {
        return (mSearchModel.serviceType.getValue() != null && Constants.ServiceType.OB_STAY == mSearchModel.serviceType.getValue());
    }
}
