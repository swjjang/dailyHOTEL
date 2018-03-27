package com.twoheart.dailyhotel.screen.gourmet.filter;

import android.content.Intent;
import android.os.Bundle;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;

@Deprecated
public class GourmetSearchCalendarActivity extends GourmetCalendarActivity
{
    public static final String INTENT_EXTRA_DATA_SEARCH_TYPE = "searchType";

    private String mSearchType;

    /**
     * @param context
     * @param todayDateTime
     * @param gourmetBookingDay
     * @param screen
     * @param isSelected
     * @param isAnimation
     * @param searchType
     * @return
     */
    //    public static Intent newInstance(Context context, TodayDateTime todayDateTime //
    //        , GourmetBookingDay gourmetBookingDay, int dayOfMaxCount, String screen, boolean isSelected //
    //        , boolean isAnimation, String searchType)
    //    {
    //        Intent intent = new Intent(context, GourmetSearchCalendarActivity.class);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
    //        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
    //        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
    //        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);
    //        intent.putExtra(INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT, dayOfMaxCount);
    //        intent.putExtra(INTENT_EXTRA_DATA_SEARCH_TYPE, searchType);
    //
    //        return intent;
    //    }
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
    protected int getConfirmTextResId()
    {
        return DailyTextUtils.isTextEmpty(mSearchType) //
            ? R.string.label_calendar_gourmet_search_selected_date //
            : R.string.label_calendar_gourmet_search_selected_date_after_search;
    }
}
