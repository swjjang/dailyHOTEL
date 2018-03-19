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
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.SuggestLocalImpl;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.screen.home.search.CommonDateTimeViewModel;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.search.SearchStayOutboundViewModel;
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
public class SearchStayOutboundFragmentPresenter extends BasePagerFragmentPresenter<SearchStayOutboundFragment, SearchStayOutboundFragmentInterface.ViewInterface>//
    implements SearchStayOutboundFragmentInterface.OnEventListener
{
    SearchStayOutboundFragmentInterface.AnalyticsInterface mAnalytics;

    RecentlyLocalImpl mRecentlyLocalImpl;
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

        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mSuggestLocalImpl = new SuggestLocalImpl(activity);

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
                onRecentlyRefresh();
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
        onRecentlyRefresh();

        // 해외 인기지역
        if (mHasPopularArea == false)
        {
            mHasPopularArea = true;

            onPopularAreaRefresh();
        }
    }

    @Override
    public void onRecentlySearchResultDeleteClick(int index, String stayName)
    {
        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem(Constants.ServiceType.OB_STAY, index).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<ArrayList<RecentlyDbPlace>>>()
        {
            @Override
            public ObservableSource<ArrayList<RecentlyDbPlace>> apply(Boolean aBoolean) throws Exception
            {
                mAnalytics.onEventRecentlyDeleteClick(getActivity(), stayName);

                return mRecentlyLocalImpl.getRecentlyTypeList(Constants.ServiceType.OB_STAY);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<RecentlyDbPlace>>()
        {
            @Override
            public void accept(ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
            {
                if (recentlyDbPlaces.size() == 0)
                {
                    getViewInterface().setRecentlySearchResultVisible(false);
                } else
                {
                    getViewInterface().setRecentlySearchResultVisible(true);
                    getViewInterface().setRecentlySearchResultList(recentlyDbPlaces);
                }

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setRecentlySearchResultVisible(false);

                unLockAll();
            }
        }));
    }

    @Override
    public void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
    {
        getFragment().getFragmentEventListener().onRecentlySearchResultClick(recentlyDbPlace);
    }

    @Override
    public void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest)
    {
        addCompositeDisposable(mSuggestLocalImpl.addStayOutboundSuggestDb(stayOutboundSuggest, stayOutboundSuggest.display).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
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

    void onRecentlyRefresh()
    {
        addCompositeDisposable(mRecentlyLocalImpl.getRecentlyTypeList(Constants.ServiceType.OB_STAY).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<RecentlyDbPlace>>()
        {
            @Override
            public void accept(ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
            {
                if (recentlyDbPlaces.size() == 0)
                {
                    getViewInterface().setRecentlySearchResultVisible(false);
                } else
                {
                    getViewInterface().setRecentlySearchResultVisible(true);
                    getViewInterface().setRecentlySearchResultList(recentlyDbPlaces);
                }

                mAnalytics.onEventRecentlyList(getActivity(), recentlyDbPlaces.size() == 0);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setRecentlySearchResultVisible(false);

                mAnalytics.onEventRecentlyList(getActivity(), true);

                ExLog.e(throwable.toString());
            }
        }));
    }

    void onPopularAreaRefresh()
    {
        addCompositeDisposable(mSuggestRemoteImpl.getPopularRegionSuggestsByStayOutbound().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutboundSuggest>>()
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
