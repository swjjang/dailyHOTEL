package com.daily.dailyhotel.screen.home.search.gourmet.research;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.parcel.GourmetSuggestParcelV2;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.search.SearchGourmetViewModel;
import com.daily.dailyhotel.screen.home.search.gourmet.suggest.SearchGourmetSuggestActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ResearchGourmetPresenter extends BaseExceptionPresenter<ResearchGourmetActivity, ResearchGourmetInterface.ViewInterface> implements ResearchGourmetInterface.OnEventListener
{
    private ResearchGourmetInterface.AnalyticsInterface mAnalytics;

    SearchGourmetViewModel mSearchModel;

    CommonDateTime mCommonDateTime;


    public ResearchGourmetPresenter(@NonNull ResearchGourmetActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected ResearchGourmetInterface.ViewInterface createInstanceViewInterface()
    {
        return new ResearchGourmetView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(ResearchGourmetActivity activity)
    {
        setContentView(R.layout.activity_research_gourmet_data);

        setAnalytics(new ResearchGourmeAnalyticsImpl());

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (ResearchGourmetInterface.AnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mCommonDateTime = new CommonDateTime(intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_OPEN_DATE_TIME)//
            , intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_CLOSE_DATE_TIME)//
            , intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_CURRENT_DATE_TIME)//
            , intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_DAILY_DATE_TIME));

        try
        {
            String visitDateTime = intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);

            mSearchModel.setBookDateTime(visitDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        GourmetSuggestParcelV2 suggestParcel = intent.getParcelableExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (suggestParcel != null)
        {
            mSearchModel.suggest.setValue(suggestParcel.getSuggest());
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
        getViewInterface().setToolbarTitle(getString(R.string.label_search_search_gourmet));

        String displayName = mSearchModel.suggest.getValue().getText1();

        if (mSearchModel.suggest.getValue() == null || DailyTextUtils.isTextEmpty(displayName) == true)
        {
            getViewInterface().setSearchSuggestText(null);
            getViewInterface().setSearchButtonEnabled(false);
        } else
        {
            getViewInterface().setSearchSuggestText(displayName);
            getViewInterface().setSearchButtonEnabled(true);
        }

        GourmetBookDateTime gourmetBookDateTime = mSearchModel.getBookDateTime();

        getViewInterface().setSearchCalendarText(gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));

        addCompositeDisposable(getViewInterface().getCompleteCreatedFragment().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer()
        {
            @Override
            public void accept(Object o) throws Exception
            {
                getViewInterface().showSearch();
            }
        }));
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

        switch (requestCode)
        {
            case ResearchGourmetActivity.REQUEST_CODE_SUGGEST:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        GourmetSuggestParcelV2 gourmetSuggestParcel = data.getParcelableExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchModel.suggest.setValue(gourmetSuggestParcel.getSuggest());
                        mSearchModel.inputKeyword = data.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;

            case ResearchGourmetActivity.REQUEST_CODE_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        String visitDateTime = data.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_VISIT_DATE);

                        mSearchModel.setBookDateTime(visitDateTime);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;
        }
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

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onSuggestClick()
    {
        try
        {
            GourmetBookDateTime gourmetBookDateTime = mSearchModel.getBookDateTime();

            startActivityForResult(SearchGourmetSuggestActivity.newInstance(getActivity()//
                , null //
                , gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)), ResearchGourmetActivity.REQUEST_CODE_SUGGEST);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            GourmetBookDateTime gourmetBookDateTime = mSearchModel.getBookDateTime();

            startActivityForResult(GourmetCalendarActivity.newInstance(getActivity(), mCommonDateTime.getTodayDateTime()//
                , gourmetBookDateTime.getGourmetBookingDay()//
                , GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT, AnalyticsManager.ValueType.SEARCH, true, true), ResearchGourmetActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onDoSearchClick()
    {
        try
        {
            GourmetBookDateTime gourmetBookDateTime = mSearchModel.getBookDateTime();
            GourmetSuggestV2 suggest = mSearchModel.suggest.getValue();

            Intent intent = new Intent();
            intent.putExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME, gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcelV2(suggest));

            if (suggest.isCampaignTagSuggestType() == false)
            {
                intent.putExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_KEYWORD, mSearchModel.inputKeyword);
            }

            setResult(Activity.RESULT_OK, intent);
            onBackClick();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
    {
        if (recentlyDbPlace == null || lock() == true)
        {
            return;
        }

        GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
        GourmetBookDateTime gourmetBookDateTime = mSearchModel.getBookDateTime();

        startActivityForResult(GourmetDetailActivity.newInstance(getActivity() //
            , recentlyDbPlace.index, recentlyDbPlace.name, recentlyDbPlace.imageUrl//
            , GourmetDetailActivity.NONE_PRICE//
            , gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , null, false, false, false, false, GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam)//
            , ResearchGourmetActivity.REQUEST_CODE_DETAIL);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        addCompositeDisposable(Completable.complete().delay(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                finish();
            }
        }));
    }

    @Override
    public void onPopularTagClick(CampaignTag campaignTag)
    {
        if (campaignTag == null || lock() == true)
        {
            return;
        }

        GourmetSuggestV2.CampaignTag suggestItem = GourmetSuggestV2.CampaignTag.getSuggestItem(campaignTag);
        GourmetSuggestV2 gourmetSuggest = new GourmetSuggestV2(GourmetSuggestV2.MenuType.CAMPAIGN_TAG, suggestItem);

        mSearchModel.suggest.setValue(gourmetSuggest);

        addCompositeDisposable(getViewInterface().getSuggestAnimation().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                unLockAll();
            }
        }));

        //        GourmetBookDateTime gourmetBookDateTime = mSearchModel.getBookDateTime();
        //
        //        startActivityForResult(GourmetCampaignTagListActivity.newInstance(getActivity() //
        //            , campaignTag.index, campaignTag.campaignTag//
        //            , gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)) //
        //            , ResearchGourmetActivity.REQUEST_CODE_SEARCH_RESULT);
        //
        //        onBackClick();
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mSearchModel = ViewModelProviders.of(activity, new SearchGourmetViewModel.SearchGourmetViewModelFactory()).get(SearchGourmetViewModel.class);

        // Gourmet
        mSearchModel.suggest.observe(activity, new Observer<GourmetSuggestV2>()
        {
            @Override
            public void onChanged(@Nullable GourmetSuggestV2 gourmetSuggest)
            {
                String displayName = gourmetSuggest.getText1();

                getViewInterface().setSearchSuggestText(displayName);

                getViewInterface().setSearchButtonEnabled(DailyTextUtils.isTextEmpty(displayName) == false);
            }
        });

        mSearchModel.bookDateTime.observe(activity, new Observer<GourmetBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable GourmetBookDateTime gourmetBookDateTime)
            {
                getViewInterface().setSearchCalendarText(gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));
            }
        });
    }
}
