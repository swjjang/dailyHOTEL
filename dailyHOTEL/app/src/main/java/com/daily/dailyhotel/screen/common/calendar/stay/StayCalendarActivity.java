package com.daily.dailyhotel.screen.common.calendar.stay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

public class StayCalendarActivity extends BaseActivity<StayCalendarPresenter>
{
    public static final String INTENT_EXTRA_DATA_CHECKIN_DATETIME = "checkInDateTime";
    public static final String INTENT_EXTRA_DATA_CHECKOUT_DATETIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_START_DATETIME = "startDateTime";
    static final String INTENT_EXTRA_DATA_END_DATETIME = "endDateTime";
    static final String INTENT_EXTRA_DATA_NIGHTS_OF_MAXCOUNT = "nightsOfMaxCount";
    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_SOLD_OUT_DAYS = "soldOutDays";
    static final String INTENT_EXTRA_DATA_CALLBYSCREEN = "callByScreen";
    static final String INTENT_EXTRA_DATA_IS_SELECTED = "isSelected";
    static final String INTENT_EXTRA_DATA_MARGIN_TOP = "marginTop";
    static final String INTENT_EXTRA_DATA_ISANIMATION = "isAnimation";

    public static Intent newInstance(Context context//
        , String startDateTime, String endDateTime, int nightsOfMaxCount//
        , String checkInDateTime, String checkOutDateTime//
        , String callByScreen, boolean isSelected, int marginTop, boolean isAnimation)
    {
        Intent intent = new Intent(context, StayCalendarActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_START_DATETIME, startDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_DATETIME, endDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS_OF_MAXCOUNT, nightsOfMaxCount);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN_DATETIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT_DATETIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CALLBYSCREEN, callByScreen);
        intent.putExtra(INTENT_EXTRA_DATA_IS_SELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_MARGIN_TOP, marginTop);
        intent.putExtra(INTENT_EXTRA_DATA_ISANIMATION, isAnimation);

        return intent;
    }

    public static Intent newInstance(Context context//
        , String startDateTime, String endDateTime, int nightsOfMaxCount//
        , String checkInDateTime, String checkOutDateTime, int stayIndex, int[] soldOutDays//
        , String callByScreen, boolean isSelected, int marginTop, boolean isAnimation)
    {
        Intent intent = new Intent(context, StayCalendarActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_START_DATETIME, startDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_DATETIME, endDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS_OF_MAXCOUNT, nightsOfMaxCount);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN_DATETIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT_DATETIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_SOLD_OUT_DAYS, soldOutDays);
        intent.putExtra(INTENT_EXTRA_DATA_CALLBYSCREEN, callByScreen);
        intent.putExtra(INTENT_EXTRA_DATA_IS_SELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_MARGIN_TOP, marginTop);
        intent.putExtra(INTENT_EXTRA_DATA_ISANIMATION, isAnimation);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayCalendarPresenter createInstancePresenter()
    {
        return new StayCalendarPresenter(this);
    }
}
