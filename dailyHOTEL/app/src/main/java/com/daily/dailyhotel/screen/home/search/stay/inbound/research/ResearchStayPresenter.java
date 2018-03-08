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
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.campaigntag.stay.StayCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.search.SearchStayViewModel;
import com.daily.dailyhotel.screen.home.search.stay.inbound.suggest.SearchStaySuggestActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
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

    SearchStayViewModel mSearchModel;

    CommonDateTime mCommonDateTime;

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

        mCommonDateTime = new CommonDateTime(intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_OPEN_DATE_TIME)//
            , intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CLOSE_DATE_TIME)//
            , intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CURRENT_DATE_TIME)//
            , intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_DAILY_DATE_TIME));

        try
        {
            String checkInDateTime = intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
            String checkOutDateTime = intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

            mSearchModel.setBookDateTime(checkInDateTime, checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        StaySuggestParcel suggestParcel = intent.getParcelableExtra(ResearchStayActivity.INTENT_EXTRA_DATA_SUGGEST);

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
        getViewInterface().setToolbarTitle(getString(R.string.label_search_search_stay));

        if (mSearchModel.suggest.getValue() == null || DailyTextUtils.isTextEmpty(mSearchModel.suggest.getValue().displayName) == true)
        {
            getViewInterface().setSearchSuggestText(null);
            getViewInterface().setSearchButtonEnabled(false);
        } else
        {
            getViewInterface().setSearchSuggestText(mSearchModel.suggest.getValue().displayName);
            getViewInterface().setSearchButtonEnabled(true);
        }

        StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

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
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        StaySuggestParcel staySuggestParcel = data.getParcelableExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchModel.suggest.setValue(staySuggestParcel.getSuggest());
                        mSearchModel.inputString = data.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;

            case ResearchStayActivity.REQUEST_CODE_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                        mSearchModel.setBookDateTime(checkInDateTime, checkOutDateTime);
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
            StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

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
            Calendar calendar = DailyCalendar.getInstance(mCommonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

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
            StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

            Intent intent = new Intent();
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(mSearchModel.suggest.getValue()));
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_KEYWORD, mSearchModel.inputString);

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

        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
        StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

        startActivityForResult(StayDetailActivity.newInstance(getActivity() //
            , recentlyDbPlace.index, recentlyDbPlace.name, recentlyDbPlace.imageUrl//
            , StayDetailActivity.NONE_PRICE//
            , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam)//
            , ResearchStayActivity.REQUEST_CODE_DETAIL);

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

        StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

        startActivityForResult(StayCampaignTagListActivity.newInstance(getActivity() //
            , campaignTag.index, campaignTag.campaignTag//
            , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
            , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT))//
            , ResearchStayActivity.REQUEST_CODE_SEARCH_RESULT);

        onBackClick();
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mSearchModel = ViewModelProviders.of(activity, new SearchStayViewModel.SearchStayViewModelFactory()).get(SearchStayViewModel.class);

        // Stay
        mSearchModel.suggest.observe(activity, new Observer<StaySuggest>()
        {
            @Override
            public void onChanged(@Nullable StaySuggest staySuggest)
            {
                getViewInterface().setSearchSuggestText(staySuggest.displayName);

                getViewInterface().setSearchButtonEnabled(DailyTextUtils.isTextEmpty(staySuggest.displayName) == false);
            }
        });

        mSearchModel.bookDateTime.observe(activity, new Observer<StayBookDateTime>()
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
