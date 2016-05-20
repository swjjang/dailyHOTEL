package com.twoheart.dailyhotel.place.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public abstract class PlaceCalendarActivity extends BaseActivity implements View.OnClickListener
{
    public static final String INTENT_EXTRA_DATA_SCREEN = "screen";

    protected View mCancelView, mToastView;
    protected TextView[] mDailyTextViews;
    private DailyToolbarLayout mDailyToolbarLayout;

    protected void initLayout(Context context, SaleTime dailyTime, int enableDayCountOfMax, int dayCountOfMax)
    {
        setContentView(R.layout.activity_calendar);

        ViewGroup calendarsLayout = (ViewGroup) findViewById(R.id.calendarLayout);
        ScrollView scrollView = (ScrollView) findViewById(R.id.calendarScrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollView, getResources().getColor(R.color.over_scroll_edge));

        mCancelView = findViewById(R.id.cancelView);
        mCancelView.setVisibility(View.GONE);
        mCancelView.setOnClickListener(this);

        mToastView = findViewById(R.id.toastLayout);
        mToastView.setVisibility(View.GONE);

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

    protected void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    protected void setToolbarText(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            initToolbar(title);
        } else
        {
            mDailyToolbarLayout.setToolbarText(title);
        }
    }

    protected void setToolbarMenuEnable(boolean enableMenu1, boolean enableMenu2)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarMenuEnable(enableMenu1, enableMenu2);
    }

    protected void setCancelViewVisibility(int visibility)
    {
        if (mCancelView == null)
        {
            return;
        }

        mCancelView.setVisibility(visibility);
    }

    protected void setToastVisibility(int visibility)
    {
        if (mToastView == null)
        {
            return;
        }

        mToastView.setVisibility(visibility);
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
        TextView textView;

        for (Day dayClass : days)
        {
            textView = getDayView(context, dayClass);

            if (dayClass != null)
            {
                mDailyTextViews[dayClass.dayTime.getOffsetDailyDay()] = textView;

                if (enableDayCount-- <= 0)
                {
                    textView.setEnabled(false);
                }
            }

            calendarGridLayout.addView(textView);
        }

        return calendarLayout;
    }

    public TextView getDayView(Context context, Day day)
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
        lastMonthCalendar.add(Calendar.DAY_OF_MONTH, interval - 1);

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

    protected static class Day
    {
        public SaleTime dayTime;
        String day;
        int dayOfWeek;
    }
}
