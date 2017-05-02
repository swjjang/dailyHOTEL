package com.daily.dailyhotel.screen.common.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.daily.base.BaseActivity;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;

public class StayCalendarActivity extends BaseActivity<StayCalendarPresenter>
{
    private static final String INTENT_EXTRA_DATA_CHECKIN_DATETIME = "checkInDateTime";
    private static final String INTENT_EXTRA_DATA_CHECKOUT_DATETIME = "checkOutDateTime";
    private static final String INTENT_EXTRA_DATA_START_DATETIME = "startDateTime";
    private static final String INTENT_EXTRA_DATA_END_DATETIME = "endDateTime";
    private static final String INTENT_EXTRA_DATA_CHECK_DAYS_COUNT = "checkDaysCount";
    private static final String INTENT_EXTRA_DATA_SCREEN = "screen";
    private static final String INTENT_EXTRA_DATA_ISSELECTED = "isSelected";
    private static final String INTENT_EXTRA_DATA_ANIMATION = "animation";

    protected PlaceBookingDay mPlaceBookingDay;

    public static Intent newInstance(Context context, String checkInDateTime, String checkOutDateTime//
        , String startDateTime, String endDateTime, int checkDaysCount//
        , String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, StayCalendarActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN_DATETIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT_DATETIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_START_DATETIME, startDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_DATETIME, endDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_DAYS_COUNT, checkDaysCount);
        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);

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
