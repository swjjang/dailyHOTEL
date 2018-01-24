package com.daily.dailyhotel.screen.home.search.stay.inbound;


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
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.RecentlyRemoteImpl;
import com.daily.dailyhotel.screen.home.search.SearchPresenter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayFragmentPresenter extends BasePagerFragmentPresenter<SearchStayFragment, SearchStayFragmentInterface.ViewInterface>//
    implements SearchStayFragmentInterface.OnEventListener
{
    private SearchStayFragmentInterface.AnalyticsInterface mAnalytics;

    RecentlyLocalImpl mRecentlyLocalImpl;
    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    SearchPresenter.SearchModel mSearchModel;

    public SearchStayFragmentPresenter(@NonNull SearchStayFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return getViewInterface().getContentView(inflater, R.layout.fragment_search_stay_data, container);
    }

    @NonNull
    @Override
    protected SearchStayFragmentInterface.ViewInterface createInstanceViewInterface()
    {
        return new SearchStayFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new SearchStayFragmentAnalyticsImpl());

        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchStayFragmentInterface.AnalyticsInterface) analytics;
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

        // 최근 검색결과
        addCompositeDisposable(mRecentlyLocalImpl.getRecentlyTypeList().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<RecentlyDbPlace>>()
        {
            @Override
            public void accept(ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
            {

            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {

            }
        }));

        // 국내스테이 인기검색 태그
        addCompositeDisposable(mCampaignTagRemoteImpl.getCampaignTagList(Constants.ServiceType.HOTEL.name()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<CampaignTag>>()
        {
            @Override
            public void accept(ArrayList<CampaignTag> campaignTags) throws Exception
            {

            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {

            }
        }));
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
        return (mSearchModel.serviceType.getValue() != null && Constants.ServiceType.HOTEL == mSearchModel.serviceType.getValue());
    }
}
