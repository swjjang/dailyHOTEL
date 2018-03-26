package com.daily.dailyhotel.screen.home.search.stay.inbound;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BasePagerFragmentPresenter;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.repository.local.SearchLocalImpl;
import com.daily.dailyhotel.repository.local.model.StaySearchResultHistory;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.screen.home.search.CommonDateTimeViewModel;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.search.SearchStayViewModel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayFragmentPresenter extends BasePagerFragmentPresenter<SearchStayFragment, SearchStayFragmentInterface.ViewInterface>//
    implements SearchStayFragmentInterface.OnEventListener
{
    SearchStayFragmentInterface.AnalyticsInterface mAnalytics;

    SearchLocalImpl mSearchLocalImpl;
    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    SearchStayViewModel mSearchViewModel;
    CommonDateTimeViewModel mCommonDateTimeViewModel;

    boolean mHasPopularTag;

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

        mSearchLocalImpl = new SearchLocalImpl(activity);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case SearchActivity.REQUEST_CODE_STAY_DETAIL:
                onRecentlyHistoryRefresh();
                break;
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
        if (getActivity() == null)
        {
            return;
        }


        onRefresh();
    }

    @Override
    public void onUnselected()
    {
        if (getActivity() == null)
        {
            return;
        }


    }

    @Override
    public void onRefresh()
    {
        setRefresh(true);
        onRefresh(false);
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
        if (getActivity() == null || getActivity().isFinishing() == true || isRefresh() == false)
        {
            setRefresh(false);
            return;
        }

        setRefresh(false);

        // 최근 검색결과
        onRecentlyHistoryRefresh();

        // 국내스테이 인기검색 태그
        if (mHasPopularTag == false)
        {
            mHasPopularTag = true;

            onCampaignTagRefresh();
        }
    }

    @Override
    public void onRecentlyHistoryDeleteClick(StaySearchResultHistory recentlyHistory)
    {
        if (lock() == true)
        {
            return;
        }

        StaySuggest suggest = recentlyHistory.staySuggest;

        addCompositeDisposable(mSearchLocalImpl.deleteStayIbSearchResultHistory(suggest).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<List<StaySearchResultHistory>>>()
        {
            @Override
            public ObservableSource<List<StaySearchResultHistory>> apply(Boolean aBoolean) throws Exception
            {
                final int RECENTLY_HISTORY_MAX_COUNT = 3;

                mAnalytics.onEventRecentlyHistoryDeleteClick(getActivity(), suggest.getText1());

                return mSearchLocalImpl.getStayIbSearchResultHistoryList(mCommonDateTimeViewModel.commonDateTime, RECENTLY_HISTORY_MAX_COUNT);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StaySearchResultHistory>>()
        {
            @Override
            public void accept(List<StaySearchResultHistory> recentlyHistoryList) throws Exception
            {
                if (recentlyHistoryList.size() == 0)
                {
                    getViewInterface().setRecentlyHistoryVisible(false);
                } else
                {
                    getViewInterface().setRecentlyHistoryVisible(true);
                    getViewInterface().setRecentlyHistory(recentlyHistoryList);
                }

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setRecentlyHistoryVisible(false);

                unLockAll();
            }
        }));
    }

    @Override
    public void onRecentlyHistoryClick(StaySearchResultHistory recentlyHistory)
    {
        getFragment().getFragmentEventListener().onRecentlyHistoryClick(recentlyHistory);
    }

    @Override
    public void onPopularTagClick(CampaignTag campaignTag)
    {
        getFragment().getFragmentEventListener().onPopularTagClick(campaignTag);
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mCommonDateTimeViewModel = ViewModelProviders.of(activity).get(CommonDateTimeViewModel.class);
        mSearchViewModel = ViewModelProviders.of(activity).get(SearchStayViewModel.class);
    }

    void onRecentlyHistoryRefresh()
    {
        final int RECENTLY_HISTORY_MAX_COUNT = 3;
        CommonDateTime commonDateTime = mCommonDateTimeViewModel.commonDateTime;

        addCompositeDisposable(mSearchLocalImpl.getStayIbSearchResultHistoryList(commonDateTime, RECENTLY_HISTORY_MAX_COUNT)//
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StaySearchResultHistory>>()
            {
                @Override
                public void accept(List<StaySearchResultHistory> recentlyHistoryList) throws Exception
                {
                    if (recentlyHistoryList.size() == 0)
                    {
                        getViewInterface().setRecentlyHistoryVisible(false);
                    } else
                    {
                        getViewInterface().setRecentlyHistoryVisible(true);
                        getViewInterface().setRecentlyHistory(recentlyHistoryList);
                    }

                    mAnalytics.onEventRecentlyHistory(getActivity(), recentlyHistoryList.size() == 0);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    getViewInterface().setRecentlyHistoryVisible(false);

                    mAnalytics.onEventRecentlyHistory(getActivity(), true);

                    ExLog.e(throwable.toString());
                }
            }));
    }

    void onCampaignTagRefresh()
    {
        addCompositeDisposable(mCampaignTagRemoteImpl.getCampaignTagList(Constants.ServiceType.HOTEL.name()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<CampaignTag>>()
        {
            @Override
            public void accept(ArrayList<CampaignTag> campaignTags) throws Exception
            {
                if (campaignTags.size() == 0)
                {
                    getViewInterface().setPopularSearchTagVisible(false);
                } else
                {
                    getViewInterface().setPopularSearchTagVisible(true);
                    getViewInterface().setPopularSearchTagList(campaignTags);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setPopularSearchTagVisible(false);

                ExLog.e(throwable.toString());
            }
        }));
    }
}
