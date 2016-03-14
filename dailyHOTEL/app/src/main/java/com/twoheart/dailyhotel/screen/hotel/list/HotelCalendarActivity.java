package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.DailyTextView;

public class HotelCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 60;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 60;

    private Day mCheckInDay;
    private Day mCheckOutDay;

    public static Intent newInstance(Context context, SaleTime dailyTime)
    {
        Intent intent = new Intent(context, HotelCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        SaleTime dailyTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME);

        initLayout(HotelCalendarActivity.this, dailyTime, ENABLE_DAYCOUNT_OF_MAX, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_hotel_select_checkin));
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_CALENDAR, null);

        super.onStart();
    }

    private void setRangeDaysEnable(View view, boolean enable)
    {
        for (View textview : mDailyTextViews)
        {
            if (view == textview)
            {
                break;
            } else
            {
                textview.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        Day day = (Day) view.getTag();
        DailyTextView dailyTextView = (DailyTextView) view;

        if (day == null)
        {
            return;
        }

        if (isLockUiComponent() == true)
        {
            return;
        }

        if (mCheckInDay == null)
        {
            mCheckInDay = day;
            dailyTextView.setSelected(true);
            dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.BOLD);

            setToolbarText(getString(R.string.label_calendar_hotel_select_checkout));
            setRangeDaysEnable(view, false);
            mDailyTextViews[mDailyTextViews.length - 1].setEnabled(true);
        } else
        {
            if (mCheckInDay.dayTime == day.dayTime)
            {
                mCheckInDay = null;
                view.setSelected(false);
                dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);

                setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
                setRangeDaysEnable(view, true);
                mDailyTextViews[mDailyTextViews.length - 1].setEnabled(false);
                return;
            }

            if (mCheckInDay.dayTime.getOffsetDailyDay() >= day.dayTime.getOffsetDailyDay())
            {
                return;
            }

            lockUiComponent();
            mCheckOutDay = day;

            dailyTextView.setSelected(true);
            dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.BOLD);

            Intent intent = new Intent();
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mCheckInDay.dayTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, mCheckOutDay.dayTime);

            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
