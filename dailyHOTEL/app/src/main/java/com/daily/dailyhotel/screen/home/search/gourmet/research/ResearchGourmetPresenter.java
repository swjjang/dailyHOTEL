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
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.parcel.GourmetSuggestParcel;
import com.daily.dailyhotel.repository.local.model.GourmetSearchResultHistory;
import com.daily.dailyhotel.screen.common.calendar.gourmet.GourmetCalendarActivity;
import com.daily.dailyhotel.screen.home.search.CommonDateTimeViewModel;
import com.daily.dailyhotel.screen.home.search.SearchGourmetViewModel;
import com.daily.dailyhotel.screen.home.search.gourmet.suggest.SearchGourmetSuggestActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;

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

    SearchGourmetViewModel mSearchViewModel;
    CommonDateTimeViewModel mCommonDateTimeViewModel;

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

        CommonDateTime commonDateTime = new CommonDateTime(intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_OPEN_DATE_TIME)//
            , intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_CLOSE_DATE_TIME)//
            , intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_CURRENT_DATE_TIME)//
            , intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_DAILY_DATE_TIME));

        mCommonDateTimeViewModel.commonDateTime = commonDateTime;

        try
        {
            String visitDateTime = intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);

            mSearchViewModel.setBookDateTime(visitDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        GourmetSuggestParcel suggestParcel = intent.getParcelableExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (suggestParcel != null)
        {
            mSearchViewModel.setSuggest(suggestParcel.getSuggest());
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

        GourmetSuggest suggest = mSearchViewModel.getSuggest();
        String displayName = suggest == null ? null : suggest.getText1();

        if (DailyTextUtils.isTextEmpty(displayName) == true)
        {
            getViewInterface().setSearchSuggestText(null);
            getViewInterface().setSearchButtonEnabled(false);
        } else
        {
            getViewInterface().setSearchSuggestText(displayName);
            getViewInterface().setSearchButtonEnabled(true);
        }

        GourmetBookDateTime gourmetBookDateTime = mSearchViewModel.getBookDateTime();

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
                        GourmetSuggestParcel gourmetSuggestParcel = data.getParcelableExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchViewModel.setSuggest(gourmetSuggestParcel.getSuggest());
                        mSearchViewModel.inputKeyword = data.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
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
                        String visitDateTime = data.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_VISIT_DATETIME);

                        mSearchViewModel.setBookDateTime(visitDateTime);
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
            GourmetBookDateTime gourmetBookDateTime = mSearchViewModel.getBookDateTime();

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

        final int DAYS_OF_MAX_COUNT = 30;

        try
        {
            CommonDateTime commonDateTime = mCommonDateTimeViewModel.commonDateTime;
            GourmetBookDateTime gourmetBookDateTime = mSearchViewModel.getBookDateTime();

            Calendar calendar = DailyCalendar.getInstance(commonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = GourmetCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true//
                , ScreenUtils.dpToPx(getActivity(), 41), true);

            startActivityForResult(intent, ResearchGourmetActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLockAll();
        }
    }

    @Override
    public void onDoSearchClick()
    {
        try
        {
            GourmetBookDateTime gourmetBookDateTime = mSearchViewModel.getBookDateTime();
            GourmetSuggest suggest = mSearchViewModel.getSuggest();

            Intent intent = new Intent();
            intent.putExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME, gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcel(suggest));

            if (suggest.isCampaignTagSuggestType() == false)
            {
                intent.putExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_KEYWORD, mSearchViewModel.inputKeyword);
            }

            setResult(Activity.RESULT_OK, intent);
            onBackClick();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onRecentlyHistoryClick(GourmetSearchResultHistory recentlyHistory)
    {
        if (recentlyHistory == null || lock() == true)
        {
            return;
        }

        try
        {
            mSearchViewModel.inputKeyword = null;
            mSearchViewModel.setBookDateTime(recentlyHistory.gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
            mSearchViewModel.setSuggest(recentlyHistory.gourmetSuggest);

            addCompositeDisposable(getViewInterface().getSuggestAnimation().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
            {
                @Override
                public void run() throws Exception
                {
                    unLockAll();
                }
            }));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }


        //        GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
        //        GourmetBookDateTime gourmetBookDateTime = mSearchModel.getBookDateTime();
        //
        //        startActivityForResult(GourmetDetailActivity.newInstance(getActivity() //
        //            , recentlyDbPlace.index, recentlyDbPlace.name, recentlyDbPlace.imageUrl//
        //            , GourmetDetailActivity.NONE_PRICE//
        //            , gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //            , null, false, false, false, false, GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam)//
        //            , ResearchGourmetActivity.REQUEST_CODE_DETAIL);
        //
        //        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        //        addCompositeDisposable(Completable.complete().delay(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        //        {
        //            @Override
        //            public void run() throws Exception
        //            {
        //                finish();
        //            }
        //        }));
    }

    @Override
    public void onPopularTagClick(CampaignTag campaignTag)
    {
        if (campaignTag == null || lock() == true)
        {
            return;
        }

        GourmetSuggest.CampaignTag suggestItem = GourmetSuggest.CampaignTag.getSuggestItem(campaignTag);
        GourmetSuggest gourmetSuggest = new GourmetSuggest(GourmetSuggest.MenuType.CAMPAIGN_TAG, suggestItem);

        mSearchViewModel.setSuggest(gourmetSuggest);

        addCompositeDisposable(getViewInterface().getSuggestAnimation().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                unLockAll();
            }
        }));
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mCommonDateTimeViewModel = ViewModelProviders.of(activity, new CommonDateTimeViewModel.CommonDateTimeViewModelFactory()).get(CommonDateTimeViewModel.class);
        mSearchViewModel = ViewModelProviders.of(activity, new SearchGourmetViewModel.SearchGourmetViewModelFactory()).get(SearchGourmetViewModel.class);

        // Gourmet
        mSearchViewModel.setSuggestObserver(activity, new Observer<GourmetSuggest>()
        {
            @Override
            public void onChanged(@Nullable GourmetSuggest gourmetSuggest)
            {
                String displayName = gourmetSuggest.getText1();

                getViewInterface().setSearchSuggestText(displayName);

                getViewInterface().setSearchButtonEnabled(DailyTextUtils.isTextEmpty(displayName) == false);
            }
        });

        mSearchViewModel.setBookDateTimeObserver(activity, new Observer<GourmetBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable GourmetBookDateTime gourmetBookDateTime)
            {
                getViewInterface().setSearchCalendarText(gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));
            }
        });
    }
}
