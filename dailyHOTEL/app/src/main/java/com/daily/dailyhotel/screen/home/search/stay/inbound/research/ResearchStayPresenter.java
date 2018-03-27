package com.daily.dailyhotel.screen.home.search.stay.inbound.research;


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
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.repository.local.model.StaySearchResultHistory;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.search.CommonDateTimeViewModel;
import com.daily.dailyhotel.screen.home.search.SearchStayViewModel;
import com.daily.dailyhotel.screen.home.search.stay.inbound.suggest.SearchStaySuggestActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ResearchStayPresenter extends BaseExceptionPresenter<ResearchStayActivity, ResearchStayInterface.ViewInterface> implements ResearchStayInterface.OnEventListener
{
    private ResearchStayInterface.AnalyticsInterface mAnalytics;

    SearchStayViewModel mSearchViewModel;
    CommonDateTimeViewModel mCommonDateTimeViewModel;

    public ResearchStayPresenter(@NonNull ResearchStayActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected ResearchStayInterface.ViewInterface createInstanceViewInterface()
    {
        return new ResearchStayView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(ResearchStayActivity activity)
    {
        setContentView(R.layout.activity_research_stay_data);

        setAnalytics(new ResearchStayAnalyticsImpl());

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (ResearchStayInterface.AnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        CommonDateTime commonDateTime = new CommonDateTime(intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_OPEN_DATE_TIME)//
            , intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CLOSE_DATE_TIME)//
            , intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CURRENT_DATE_TIME)//
            , intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_DAILY_DATE_TIME));

        mCommonDateTimeViewModel.commonDateTime = commonDateTime;

        try
        {
            String checkInDateTime = intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
            String checkOutDateTime = intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

            mSearchViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        StaySuggestParcel suggestParcel = intent.getParcelableExtra(ResearchStayActivity.INTENT_EXTRA_DATA_SUGGEST);

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
        getViewInterface().setToolbarTitle(getString(R.string.label_search_search_stay));

        StaySuggest suggest = mSearchViewModel.getSuggest();
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

        StayBookDateTime stayBookDateTime = mSearchViewModel.getBookDateTime();

        getViewInterface().setSearchCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
            , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
            , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
            , stayBookDateTime.getNights()));

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
            case ResearchStayActivity.REQUEST_CODE_SUGGEST:
                onSuggestActivityResult(resultCode, data);
                break;

            case ResearchStayActivity.REQUEST_CODE_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME);

                        mSearchViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;
        }
    }

    private void onSuggestActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
            case Activity.RESULT_OK:
                if (intent == null)
                {
                    return;
                }

                try
                {
                    StaySuggestParcel staySuggestParcel = intent.getParcelableExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_SUGGEST);

                    if (staySuggestParcel != null)
                    {
                        mSearchViewModel.setSuggest(staySuggestParcel.getSuggest());
                    }

                    mSearchViewModel.inputKeyword = intent.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
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
            StayBookDateTime stayBookDateTime = mSearchViewModel.getBookDateTime();

            startActivityForResult(SearchStaySuggestActivity.newInstance(getActivity()//
                , null //
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)), ResearchStayActivity.REQUEST_CODE_SUGGEST);
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

        final int DAYS_OF_MAX_COUNT = 60;

        try
        {
            Calendar calendar = DailyCalendar.getInstance(mCommonDateTimeViewModel.commonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookDateTime stayBookDateTime = mSearchViewModel.getBookDateTime();

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, DAYS_OF_MAX_COUNT - 1//
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true//
                , ScreenUtils.dpToPx(getActivity(), 44), true);

            startActivityForResult(intent, ResearchStayActivity.REQUEST_CODE_CALENDAR);
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
            StayBookDateTime stayBookDateTime = mSearchViewModel.getBookDateTime();

            Intent intent = new Intent();
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(mSearchViewModel.getSuggest()));
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_KEYWORD, mSearchViewModel.inputKeyword);

            setResult(Activity.RESULT_OK, intent);
            onBackClick();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onRecentlyHistoryClick(StaySearchResultHistory recentlyHistory)
    {
        if (recentlyHistory == null || lock() == true)
        {
            return;
        }

        try
        {
            StayBookDateTime stayBookDateTime = recentlyHistory.stayBookDateTime;

            mSearchViewModel.inputKeyword = null;
            mSearchViewModel.setBookDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
            mSearchViewModel.setSuggest(recentlyHistory.staySuggest);

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
    }

    @Override
    public void onPopularTagClick(CampaignTag campaignTag)
    {
        if (campaignTag == null || lock() == true)
        {
            return;
        }

        StaySuggest.CampaignTag suggestItem = StaySuggest.CampaignTag.getSuggestItem(campaignTag);
        StaySuggest suggest = new StaySuggest(StaySuggest.MenuType.CAMPAIGN_TAG, suggestItem);

        mSearchViewModel.setSuggest(suggest);

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
        mSearchViewModel = ViewModelProviders.of(activity, new SearchStayViewModel.SearchStayViewModelFactory()).get(SearchStayViewModel.class);

        // Stay
        mSearchViewModel.setSuggestObserver(activity, new Observer<StaySuggest>()
        {
            @Override
            public void onChanged(@Nullable StaySuggest suggest)
            {
                String displayName = suggest.getText1();

                getViewInterface().setSearchSuggestText(displayName);

                getViewInterface().setSearchButtonEnabled(DailyTextUtils.isTextEmpty(displayName) == false);
            }
        });

        mSearchViewModel.setBookDateTimeObserver(activity, new Observer<StayBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable StayBookDateTime stayBookDateTime)
            {
                getViewInterface().setSearchCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                    , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getNights()));
            }
        });
    }
}
