package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;

public class StaySearchCalendarActivity extends StayCalendarActivity
{
    public static final String INTENT_EXTRA_DATA_SEARCH_TYPE = "searchType";

    private String mSearchType;

    /**
     * @param context
     * @param todayDateTime
     * @param stayBookingDay
     * @param screen
     * @param isSelected
     * @param isAnimation
     * @param searchType
     * @return
     */
    public static Intent newInstance(Context context, TodayDateTime todayDateTime //
        , StayBookingDay stayBookingDay, int dayOfMaxCount, String screen, boolean isSelected //
        , boolean isAnimation, String searchType)
    {
        Intent intent = new Intent(context, StaySearchCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);
        intent.putExtra(INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT, dayOfMaxCount);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCH_TYPE, searchType);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Intent intent = getIntent();

        mSearchType = intent.getStringExtra(INTENT_EXTRA_DATA_SEARCH_TYPE);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setResult(int resultCode, PlaceBookingDay placeBookingDay)
    {
        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, placeBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCH_TYPE, mSearchType);

        setResult(resultCode, intent);
    }

    @Override
    protected String getConfirmText(int nights)
    {
        return getString(DailyTextUtils.isTextEmpty(mSearchType) //
                ? R.string.label_calendar_stay_search_selected_date //
                : R.string.label_calendar_stay_search_selected_date_after_search //
            , nights);
    }
}
