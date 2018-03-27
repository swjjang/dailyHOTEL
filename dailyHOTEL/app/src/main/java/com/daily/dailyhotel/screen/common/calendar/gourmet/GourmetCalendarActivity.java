package com.daily.dailyhotel.screen.common.calendar.gourmet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

public class GourmetCalendarActivity extends BaseActivity<GourmetCalendarPresenter>
{
    public static final String INTENT_EXTRA_DATA_VISIT_DATETIME = "visitDateTime";
    static final String INTENT_EXTRA_DATA_START_DATETIME = "startDateTime";
    static final String INTENT_EXTRA_DATA_END_DATETIME = "endDateTime";
    static final String INTENT_EXTRA_DATA_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_SOLD_OUT_DAYS = "soldOutDays";
    static final String INTENT_EXTRA_DATA_CALLBYSCREEN = "callByScreen";
    static final String INTENT_EXTRA_DATA_IS_SELECTED = "isSelected";
    static final String INTENT_EXTRA_DATA_MARGIN_TOP = "marginTop";
    static final String INTENT_EXTRA_DATA_ISANIMATION = "isAnimation";

    public static Intent newInstance(Context context//
        , String startDateTime, String endDateTime, String visitDateTime//
        , String callByScreen, boolean isSelected, int marginTop, boolean isAnimation)
    {
        Intent intent = new Intent(context, GourmetCalendarActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_START_DATETIME, startDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_DATETIME, endDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATETIME, visitDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CALLBYSCREEN, callByScreen);
        intent.putExtra(INTENT_EXTRA_DATA_IS_SELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_MARGIN_TOP, marginTop);
        intent.putExtra(INTENT_EXTRA_DATA_ISANIMATION, isAnimation);

        return intent;
    }

    public static Intent newInstance(Context context//
        , String startDateTime, String endDateTime, String visitDateTime//
        , int gourmetIndex, int[] soldOutDays//
        , String callByScreen, boolean isSelected, int marginTop, boolean isAnimation)
    {
        Intent intent = new Intent(context, GourmetCalendarActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_START_DATETIME, startDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_DATETIME, endDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATETIME, visitDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, gourmetIndex);
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
    protected GourmetCalendarPresenter createInstancePresenter()
    {
        return new GourmetCalendarPresenter(this);
    }
}
