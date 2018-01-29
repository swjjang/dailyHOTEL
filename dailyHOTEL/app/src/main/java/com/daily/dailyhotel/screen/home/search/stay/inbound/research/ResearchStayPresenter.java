package com.daily.dailyhotel.screen.home.search.stay.inbound.research;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Locale;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ResearchStayPresenter extends BaseExceptionPresenter<ResearchStayActivity, ResearchStayInterface> implements ResearchStayView.OnEventListener
{
    private ResearchStayAnalyticsInterface mAnalytics;

    RecentlyLocalImpl mRecentlyLocalImpl;
    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    CommonDateTime mCommonDateTime;
    StayBookDateTime mStayBookDateTime;
    StaySuggest mSuggest;

    public interface ResearchStayAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public ResearchStayPresenter(@NonNull ResearchStayActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected ResearchStayInterface createInstanceViewInterface()
    {
        return new ResearchStayView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(ResearchStayActivity activity)
    {
        setContentView(R.layout.activity_research_stay_data);

        setAnalytics(new ResearchStayAnalyticsImpl());

        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (ResearchStayAnalyticsInterface) analytics;
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
            mStayBookDateTime = new StayBookDateTime();
            mStayBookDateTime.setCheckInDateTime(intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME));
            mStayBookDateTime.setCheckOutDateTime(intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        StaySuggestParcel staySuggestParcel = intent.getParcelableExtra(ResearchStayActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (staySuggestParcel != null)
        {
            mSuggest = staySuggestParcel.getSuggest();
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
        getViewInterface().setSearchStaySuggestText(mSuggest.displayName);

        getViewInterface().setSearchStayCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
            , mStayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
            , mStayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
            , mStayBookDateTime.getNights()));
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
            case ResearchStayActivity.REQUEST_CODE_STAY_SUGGEST:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    //                    mSuggest = data.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                    //
                    //                    getViewInterface().setSearchStaySuggestText(mSuggest);
                }
                break;

            case ResearchStayActivity.REQUEST_CODE_STAY_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE);

                        mStayBookDateTime.setCheckInDateTime(checkInDateTime);
                        mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);

                        getViewInterface().setSearchStayCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                            , mStayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                            , mStayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
                            , mStayBookDateTime.getNights()));
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
    public void onStaySuggestClick()
    {
        //        try
        //        {
        //            startActivityForResult(SearchStaySuggestActivity.newInstance(getActivity()//
        //                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)), ResearchStayActivity.REQUEST_CODE_STAY_SUGGEST);
        //        } catch (Exception e)
        //        {
        //            ExLog.e(e.toString());
        //        }
    }

    @Override
    public void onStayCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        TodayDateTime todayDateTime = new TodayDateTime();
        todayDateTime.openDateTime = mCommonDateTime.openDateTime;
        todayDateTime.closeDateTime = mCommonDateTime.closeDateTime;
        todayDateTime.currentDateTime = mCommonDateTime.currentDateTime;
        todayDateTime.dailyDateTime = mCommonDateTime.dailyDateTime;

        startActivityForResult(StayCalendarActivity.newInstance(getActivity(), todayDateTime//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT) //
            , StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT, AnalyticsManager.ValueType.SEARCH, true, true), ResearchStayActivity.REQUEST_CODE_STAY_CALENDAR);
    }

    @Override
    public void onStayDoSearchClick()
    {
        try
        {
            Intent intent = new Intent();
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchStayActivity.INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(mSuggest));

            setResult(Activity.RESULT_OK, intent);
            onBackClick();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }
}
