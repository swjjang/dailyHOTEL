package com.daily.dailyhotel.screen.home.stay.outbound.search;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.parcel.SuggestParcel;
import com.daily.dailyhotel.repository.local.SuggestLocalImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundSearchSuggestPresenter extends BaseExceptionPresenter<StayOutboundSearchSuggestActivity, StayOutboundSearchSuggestViewInterface> implements StayOutboundSearchSuggestView.OnEventListener
{
    StayOutboundSearchSuggestAnalyticsInterface mAnalytics;
    SuggestRemoteImpl mSuggestRemoteImpl;
    private SuggestLocalImpl mSuggestLocalImpl;

    String mKeyword;

    public interface StayOutboundSearchSuggestAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventSuggestEmpty(Activity activity, String keyword);

        void onEventCloseClick(Activity activity);

        void onEventDeleteAllRecentlySuggestClick(Activity activity);

        void onEventSuggestClick(Activity activity, String suggestDisplayName, String keyword);

        void onEventRecentlySuggestClick(Activity activity, String suggestDisplayName, String keyword);

        void onEventPopularSuggestClick(Activity activity, String suggestDisplayName);
    }

    public StayOutboundSearchSuggestPresenter(@NonNull StayOutboundSearchSuggestActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundSearchSuggestViewInterface createInstanceViewInterface()
    {
        return new StayOutboundSearchSuggestView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundSearchSuggestActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_search_suggest_data);

        setAnalytics(new StayOutboundSearchSuggestAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mSuggestLocalImpl = new SuggestLocalImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundSearchSuggestAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mKeyword = intent.getStringExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        if (DailyTextUtils.isTextEmpty(mKeyword) == false)
        {
            getViewInterface().setKeywordEditText(mKeyword);
            //            return;
        }

        getViewInterface().showKeyboard();
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
        mAnalytics.onEventCloseClick(getActivity());

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
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mSuggestLocalImpl.getRecentlySuggestList() //
            .observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<List<Suggest>, ObservableSource<List<Suggest>>>()
            {
                @Override
                public ObservableSource<List<Suggest>> apply(List<Suggest> suggests) throws Exception
                {
                    getViewInterface().setRecentlySuggests(suggests);

                    return suggests.size() == 0 ? mSuggestRemoteImpl.getPopularRegionSuggestsByStayOutbound() : Observable.just(new ArrayList<>());
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Suggest>>()
            {
                @Override
                public void accept(List<Suggest> suggests) throws Exception
                {
                    if (suggests.size() > 0)
                    {
                        getViewInterface().setPopularAreaSuggests(suggests);
                    }

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    getViewInterface().setRecentlySuggests(null);

                    unLockAll();
                }
            }));
    }

    @Override
    public void onBackClick()
    {
        addCompositeDisposable(Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (getViewInterface() != null)
                {
                    getViewInterface().hideKeyboard();
                }

                return Observable.just(true);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                getActivity().onBackPressed();
            }
        }));
    }

    @Override
    public void onSearchSuggest(String keyword)
    {
        clearCompositeDisposable();

        mKeyword = keyword;

        getViewInterface().setEmptySuggestsVisible(false);
        getViewInterface().setProgressBarVisible(true);

        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            onSuggestList(null);
        } else
        {
            addCompositeDisposable(mSuggestRemoteImpl.getSuggestsByStayOutbound(keyword)//
                .delaySubscription(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<List<Suggest>>()
                {
                    @Override
                    public void accept(List<Suggest> suggests) throws Exception
                    {
                        StayOutboundSearchSuggestPresenter.this.onSuggestList(suggests);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        StayOutboundSearchSuggestPresenter.this.onSuggestList(null);
                    }
                }));
        }
    }

    @Override
    public void onSuggestClick(Suggest suggest)
    {
        if (suggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        try
        {
            mAnalytics.onEventSuggestClick(getActivity(), suggest.display, mKeyword);
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
        }

        startFinishAction(suggest, mKeyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_AUTO);
    }

    @Override
    public void onRecentlySuggestClick(Suggest suggest)
    {
        if (suggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mSuggestLocalImpl.getRecentlySuggestKeyword(suggest.id) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(String keyword) throws Exception
                {
                    try
                    {
                        mAnalytics.onEventRecentlySuggestClick(getActivity(), suggest.display, mKeyword);
                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    startFinishAction(suggest, keyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECENT);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    try
                    {
                        mAnalytics.onEventRecentlySuggestClick(getActivity(), suggest.display, mKeyword);
                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    startFinishAction(suggest, "", AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECENT);
                }
            }));
    }

    @Override
    public void onPopularSuggestClick(Suggest suggest)
    {
        if (suggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mSuggestLocalImpl.getRecentlySuggestKeyword(suggest.id) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(String keyword) throws Exception
                {
                    try
                    {
                        mAnalytics.onEventPopularSuggestClick(getActivity(), suggest.display);
                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    startFinishAction(suggest, keyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    try
                    {
                        mAnalytics.onEventPopularSuggestClick(getActivity(), suggest.display);
                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    startFinishAction(suggest, "", AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND);
                }
            }));
    }

    void startFinishAction(Suggest suggest, String keyword, String analyticsClickType)
    {
        Intent intent = new Intent();
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new SuggestParcel(suggest));
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_CLICK_TYPE, DailyTextUtils.isTextEmpty(analyticsClickType) ? "" : analyticsClickType);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDeleteAllRecentlySuggest()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().setRecentlySuggests(null);

        addCompositeDisposable(mSuggestLocalImpl.deleteAllRecentlySuggest() //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer()
            {
                @Override
                public void accept(Object o) throws Exception
                {
                    mAnalytics.onEventDeleteAllRecentlySuggestClick(getActivity());

                    unLockAll();

                    setRefresh(true);
                    onRefresh(true);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    mAnalytics.onEventDeleteAllRecentlySuggestClick(getActivity());

                    unLockAll();
                }
            }));
    }

    void onSuggestList(List<Suggest> suggestList)
    {
        getViewInterface().setProgressBarVisible(false);

        if (suggestList == null || suggestList.size() == 0)
        {
            getViewInterface().setSuggestsVisible(false);

            boolean isShowEmpty = DailyTextUtils.isTextEmpty(mKeyword) == false;
            getViewInterface().setEmptySuggestsVisible(isShowEmpty);

            mAnalytics.onEventSuggestEmpty(getActivity(), mKeyword);
        } else
        {
            getViewInterface().setSuggestsVisible(true);
            getViewInterface().setEmptySuggestsVisible(false);
        }

        getViewInterface().setSuggests(suggestList);
    }
}
