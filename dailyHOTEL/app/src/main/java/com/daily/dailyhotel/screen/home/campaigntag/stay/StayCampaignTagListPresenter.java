package com.daily.dailyhotel.screen.home.campaigntag.stay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class StayCampaignTagListPresenter extends BaseExceptionPresenter<StayCampaignTagListActivity, StayCampaignTagListInterface> implements StayCampaignTagListView.OnEventListener
{
    private int mTagIndex;
    private boolean mIsUsedMultiTransition;
    private int mType;
    private int mAfterDay;
    private int mNights;
    private String mTitle;
    private StayBookingDay mStayBookingDay;


    public StayCampaignTagListPresenter(@NonNull StayCampaignTagListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayCampaignTagListInterface createInstanceViewInterface()
    {
        return new StayCampaignTagListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayCampaignTagListActivity activity)
    {
        setContentView(R.layout.activity_place_campaign_tag_list_data);

        //        setAnalytics(new StayCampaignTagListAnalyticsImpl());

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {

    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTagIndex = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_INDEX, -1);
        mIsUsedMultiTransition = intent.getBooleanExtra(Constants.NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, false);

//        if (mTagIndex == -1)
//        {
//            return false;
//        }

        mType = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_TYPE, StayCampaignTagListActivity.TYPE_DEFAULT);

        mTitle = intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_TITLE);

        switch (mType)
        {
            case StayCampaignTagListActivity.TYPE_DEFAULT:
            {
                mStayBookingDay = intent.getParcelableExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
                break;
            }

            case StayCampaignTagListActivity.TYPE_DATE:
            {
                String checkInDateTime = intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE);
                String checkOutDateTime = intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE);

                if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == false)
                {
                    try
                    {
                        mStayBookingDay = new StayBookingDay();
                        mStayBookingDay.setCheckInDay(checkInDateTime);
                        mStayBookingDay.setCheckOutDay(checkOutDateTime);
                    } catch (Exception e)
                    {
                        mStayBookingDay = null;
                    }
                }
                break;
            }

            case StayCampaignTagListActivity.TYPE_AFTER_DAY:
            {
                mAfterDay = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_AFTER_DAY, 0);
                mNights = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_NIGHTS, 1);
                break;
            }
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mTitle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    protected void onRefresh(boolean showProgress)
    {

    }




    @Override
    public void onBackClick()
    {

    }

    @Override
    public void onCalendarClick()
    {

    }

    @Override
    public void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
    {

    }

    @Override
    public void onPlaceLongClick(View view, PlaceViewItem placeViewItem, int count)
    {

    }
}
