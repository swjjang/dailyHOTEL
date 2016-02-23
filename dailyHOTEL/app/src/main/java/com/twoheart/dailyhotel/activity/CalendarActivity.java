package com.twoheart.dailyhotel.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.DailyTextView;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarActivity extends BaseActivity implements View.OnClickListener
{
    private static final int HOTEL_DAYCOUNT_OF_MAX = 60;
    private static final int GOURMET_DAYCOUNT_OF_MAX = 30;

    private static final int HOTEL_ENABLE_DAYCOUNT_OF_MAX = 60;
    private static final int GOURMET_ENABLE_DAYCOUNT_OF_MAX = 14;

    private Constants.TYPE mPlaceType;
    private Day mCheckInDay;
    private Day mCheckOutDay;

    private View[] mDailyTextViews;
    private DailyToolbarLayout mDailyToolbarLayout;

    public static Intent newInstance(Context context, Constants.TYPE type, SaleTime dailyTime)
    {
        Intent intent = new Intent(context, CalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, type.toString());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        mPlaceType = TYPE.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
        SaleTime dailyTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME);

        switch (mPlaceType)
        {
            case HOTEL:
                initLayout(CalendarActivity.this, dailyTime//
                    , HOTEL_ENABLE_DAYCOUNT_OF_MAX, HOTEL_DAYCOUNT_OF_MAX);
                break;

            case FNB:
                initLayout(CalendarActivity.this, dailyTime//
                    , GOURMET_ENABLE_DAYCOUNT_OF_MAX, GOURMET_DAYCOUNT_OF_MAX);
                break;
        }
    }

    @Override
    protected void onStart()
    {
        switch (mPlaceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_CALENDAR, null);
                break;

            case FNB:
                AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_CALENDAR, null);
                break;
        }

        super.onStart();
    }

    private void initLayout(Context context, SaleTime dailyTime, int enableDayCountOfMax, int dayCountOfMax)
    {
        setContentView(R.layout.activity_calendar);

        switch (mPlaceType)
        {
            case HOTEL:
                initToolbar(getString(R.string.label_calendar_hotel_select_checkin));
                break;

            case FNB:
                initToolbar(getString(R.string.label_calendar_gourmet_select));
                break;
        }

        ViewGroup calendarsLayout = (ViewGroup) findViewById(R.id.calendarLayout);

        mDailyTextViews = new DailyTextView[dayCountOfMax];

        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(dailyTime.getDailyTime());

        int maxMonth = getMonthInterval(calendar, dayCountOfMax);
        int maxDay = dayCountOfMax;
        int dayCount = 0;
        int enableDayCount = enableDayCountOfMax;

        for (int i = 0; i <= maxMonth; i++)
        {
            int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            calendarsLayout.addView(getMonthCalendarView(context, dailyTime.getClone(dayCount)//
                , calendar, day + maxDay - 1 > maxDayOfMonth ? maxDayOfMonth : day + maxDay - 1, enableDayCount));

            dayCount += maxDayOfMonth - day + 1;
            maxDay = dayCountOfMax - dayCount;
            enableDayCount = enableDayCountOfMax - dayCount;

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }

        mDailyTextViews[dayCountOfMax - 1].setEnabled(false);
    }

    private void initToolbar(String title)
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title);
    }

    private View getMonthCalendarView(Context context, final SaleTime dailyTime, final Calendar calendar, final int maxDayOfMonth, final int enableDayCountMax)
    {
        View calendarLayout = LayoutInflater.from(context).inflate(R.layout.view_calendar, null);

        TextView monthTextView = (TextView) calendarLayout.findViewById(R.id.monthTextView);
        android.support.v7.widget.GridLayout calendarGridLayout = (android.support.v7.widget.GridLayout) calendarLayout.findViewById(R.id.calendarGridLayout);

        SimpleDateFormat simpleDayFormat = new SimpleDateFormat("M", Locale.KOREA);
        simpleDayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        monthTextView.setText(simpleDayFormat.format(calendar.getTime()) + "월");

        // day
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int length = maxDayOfMonth - day + 1 + dayOfWeek;
        final int LENGHT_OF_WEEK = 7;

        if (length % LENGHT_OF_WEEK != 0)
        {
            length += (LENGHT_OF_WEEK - (length % LENGHT_OF_WEEK));
        }

        Day[] days = new Day[length];

        Calendar cloneCalendar = (Calendar) calendar.clone();

        for (int i = 0, j = dayOfWeek, k = day; k <= maxDayOfMonth; i++, j++, k++)
        {
            days[j] = new Day();
            days[j].dayTime = dailyTime.getClone(dailyTime.getOffsetDailyDay() + i);
            days[j].day = Integer.toString(cloneCalendar.get(Calendar.DAY_OF_MONTH));
            days[j].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        int enableDayCount = enableDayCountMax < 0 ? 0 : enableDayCountMax;

        for (Day dayClass : days)
        {
            View view = getDayView(context, dayClass);

            if (dayClass != null)
            {
                mDailyTextViews[dayClass.dayTime.getOffsetDailyDay()] = view;

                if (enableDayCount-- <= 0)
                {
                    view.setEnabled(false);
                }
            }

            calendarGridLayout.addView(view);
        }

        return calendarLayout;
    }

    public View getDayView(Context context, Day day)
    {
        DailyTextView dayTextView = new DailyTextView(context);
        dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        dayTextView.setGravity(Gravity.CENTER);
        dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.NORMAL);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = Util.dpToPx(context, 38);
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        dayTextView.setLayoutParams(layoutParams);
        dayTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_calendar_day_background));

        if (day == null)
        {
            dayTextView.setText(null);
            dayTextView.setTag(null);
            dayTextView.setEnabled(false);
        } else
        {
            switch (day.dayOfWeek)
            {
                // 일요일
                case 1:
                    dayTextView.setTextColor(context.getResources().getColorStateList(R.drawable.selector_calendar_sunday_textcolor));
                    break;

                default:
                    dayTextView.setTextColor(context.getResources().getColorStateList(R.drawable.selector_calendar_default_text_color));
                    break;
            }

            dayTextView.setText(day.day);
            dayTextView.setTag(day);
        }

        dayTextView.setOnClickListener(this);

        return dayTextView;
    }

    private int getMonthInterval(final Calendar calendar, int interval)
    {
        Calendar lastMonthCalendar = (Calendar) calendar.clone();
        lastMonthCalendar.add(Calendar.DAY_OF_MONTH, interval);

        int lastMonth = lastMonthCalendar.get(Calendar.MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);

        if (currentMonth > lastMonth)
        {
            return 12 - currentMonth + lastMonth;
        } else
        {
            return lastMonth - currentMonth;
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

            if (mPlaceType == TYPE.FNB)
            {
                lockUiComponent();

                Intent intent = new Intent();
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mCheckInDay.dayTime);

                setResult(RESULT_OK, intent);
                finish();
            } else
            {
                mDailyToolbarLayout.setToolbarText(getString(R.string.label_calendar_hotel_select_checkout));

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

                mDailyTextViews[mDailyTextViews.length - 1].setEnabled(true);
            }
        } else
        {
            if (mPlaceType == TYPE.FNB)
            {
                return;
            }

            if (mCheckInDay.dayTime == day.dayTime)
            {
                mCheckInDay = null;
                view.setSelected(false);
                dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);

                mDailyToolbarLayout.setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));

                for (View dailTextView : mDailyTextViews)
                {
                    if (view == dailTextView)
                    {
                        break;
                    } else
                    {
                        dailTextView.setEnabled(true);
                    }
                }

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

    private static class Day
    {
        SaleTime dayTime;
        String day;
        int dayOfWeek;
    }
}
