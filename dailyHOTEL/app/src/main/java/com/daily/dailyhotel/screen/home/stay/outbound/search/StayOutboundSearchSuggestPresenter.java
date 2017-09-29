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
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.twoheart.dailyhotel.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundSearchSuggestPresenter extends BaseExceptionPresenter<StayOutboundSearchSuggestActivity, StayOutboundSearchSuggestViewInterface> implements StayOutboundSearchSuggestView.OnEventListener
{
    private StayOutboundSearchSuggestAnalyticsInterface mAnalytics;
    private SuggestRemoteImpl mSuggestRemoteImpl;
    private SuggestLocalImpl mSuggestLocalImpl;

    private String mKeyword;

    public interface StayOutboundSearchSuggestAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventSuggestEmpty(Activity activity, String keyword);

        void onEventCloseClick(Activity activity);

        void onEventDeleteAllRecentlySuggestClick(Activity activity);

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
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Suggest>>()
            {
                @Override
                public void accept(List<Suggest> suggests) throws Exception
                {
                    getViewInterface().setRecentlySuggests(suggests);

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
        getActivity().onBackPressed();
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
                .delaySubscription(500, TimeUnit.MILLISECONDS).subscribe(suggests -> onSuggestList(suggests), throwable -> onSuggestList(null)));
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

        Intent intent = new Intent();
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new SuggestParcel(suggest));
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD, mKeyword);

        setResult(Activity.RESULT_OK, intent);
        finish();
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

        Intent intent = new Intent();
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new SuggestParcel(suggest));
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD, mKeyword);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDeleteAllRecentlySuggest()
    {
        getViewInterface().setRecentlySuggests(null);

        DailyDb dailyDb = DailyDbHelper.getInstance().open(getActivity());

        try
        {
            dailyDb.deleteAllStayObRecentlySuggest();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        DailyDbHelper.getInstance().close();

        mAnalytics.onEventDeleteAllRecentlySuggestClick(getActivity());
    }

    private void onSuggestList(List<Suggest> suggestList)
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
