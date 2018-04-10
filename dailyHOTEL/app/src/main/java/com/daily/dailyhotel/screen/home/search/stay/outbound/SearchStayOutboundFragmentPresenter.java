package com.daily.dailyhotel.screen.home.search.stay.outbound;


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
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.SearchLocalImpl;
import com.daily.dailyhotel.repository.local.SuggestLocalImpl;
import com.daily.dailyhotel.repository.local.model.StayObSearchResultHistory;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.screen.home.search.CommonDateTimeViewModel;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.search.SearchStayOutboundViewModel;
import com.twoheart.dailyhotel.R;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayOutboundFragmentPresenter extends BasePagerFragmentPresenter<SearchStayOutboundFragment, SearchStayOutboundFragmentInterface.ViewInterface>//
    implements SearchStayOutboundFragmentInterface.OnEventListener
{
    SearchStayOutboundFragmentInterface.AnalyticsInterface mAnalytics;

    SearchLocalImpl mSearchLocalImpl;
    SuggestRemoteImpl mSuggestRemoteImpl;
    SuggestLocalImpl mSuggestLocalImpl;

    SearchStayOutboundViewModel mSearchViewModel;
    CommonDateTimeViewModel mCommonDateTimeViewModel;

    boolean mHasPopularArea;

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

        mSearchLocalImpl = new SearchLocalImpl();
        mSuggestRemoteImpl = new SuggestRemoteImpl();
        mSuggestLocalImpl = new SuggestLocalImpl();

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchStayOutboundFragmentInterface.AnalyticsInterface) analytics;
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
            case SearchActivity.REQUEST_CODE_STAY_OUTBOUND_DETAIL:
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

        // 해외 인기지역
        if (mHasPopularArea == false)
        {
            mHasPopularArea = true;

            onPopularAreaRefresh();
        }
    }

    @Override
    public void onRecentlyHistoryDeleteClick(StayObSearchResultHistory recentlyHistory)
    {
        if (lock() == true)
        {
            return;
        }

        StayOutboundSuggest suggest = recentlyHistory.stayOutboundSuggest;

        addCompositeDisposable(mSearchLocalImpl.deleteStayObSearchResultHistory(getActivity(), suggest).observeOn(AndroidSchedulers.mainThread())//
            .flatMap(new Function<Boolean, ObservableSource<List<StayObSearchResultHistory>>>()
            {
                @Override
                public ObservableSource<List<StayObSearchResultHistory>> apply(Boolean aBoolean) throws Exception
                {
                    final int RECENTLY_HISTORY_MAX_COUNT = 3;

                    mAnalytics.onEventRecentlyHistoryDeleteClick(getActivity(), suggest.display);

                    return mSearchLocalImpl.getStayObSearchResultHistoryList(getActivity(), mCommonDateTimeViewModel.commonDateTime, RECENTLY_HISTORY_MAX_COUNT);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayObSearchResultHistory>>()
            {
                @Override
                public void accept(List<StayObSearchResultHistory> recentlyHistoryList) throws Exception
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
    public void onRecentlyHistoryClick(StayObSearchResultHistory recentlyHistory)
    {
        getFragment().getFragmentEventListener().onRecentlyHistoryClick(recentlyHistory);
    }

    @Override
    public void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest)
    {
        addCompositeDisposable(mSuggestLocalImpl.addStayOutboundSuggestDb(getActivity(), stayOutboundSuggest, stayOutboundSuggest.display) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    getFragment().getFragmentEventListener().onPopularAreaClick(stayOutboundSuggest);
                }
            }));
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mCommonDateTimeViewModel = ViewModelProviders.of(activity).get(CommonDateTimeViewModel.class);
        mSearchViewModel = ViewModelProviders.of(activity).get(SearchStayOutboundViewModel.class);
    }

    void onRecentlyHistoryRefresh()
    {
        final int RECENTLY_HISTORY_MAX_COUNT = 3;
        CommonDateTime commonDateTime = mCommonDateTimeViewModel.commonDateTime;

        addCompositeDisposable(mSearchLocalImpl.getStayObSearchResultHistoryList(getActivity(), commonDateTime, RECENTLY_HISTORY_MAX_COUNT)//
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayObSearchResultHistory>>()
            {
                @Override
                public void accept(List<StayObSearchResultHistory> recentlyHistoryList) throws Exception
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

    void onPopularAreaRefresh()
    {
        addCompositeDisposable(mSuggestRemoteImpl.getPopularRegionSuggestsByStayOutbound(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutboundSuggest>>()
        {
            @Override
            public void accept(List<StayOutboundSuggest> stayOutboundSuggests) throws Exception
            {
                if (stayOutboundSuggests == null || stayOutboundSuggests.size() == 0)
                {
                    getViewInterface().setPopularAreaVisible(false);
                } else
                {
                    getViewInterface().setPopularAreaVisible(true);
                    getViewInterface().setPopularAreaList(stayOutboundSuggests);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setPopularAreaVisible(false);
            }
        }));
    }
}
