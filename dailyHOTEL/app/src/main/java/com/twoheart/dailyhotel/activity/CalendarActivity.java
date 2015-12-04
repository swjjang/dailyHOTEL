package com.twoheart.dailyhotel.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarActivity extends BaseActivity implements GridView.OnItemClickListener
{
    private static final int DAYCOUNT_OF_MAX = 60;

    private PlaceMainFragment.TYPE mPlaceType;
    private Day mCheckInDay;
    private Day mCheckOutDay;

    public static Intent newInstance(Context context, PlaceMainFragment.TYPE type, long dailyTime)
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

        mPlaceType = PlaceMainFragment.TYPE.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));

        long dailyTime = intent.getLongExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, 0L);

        initLayout(CalendarActivity.this, dailyTime);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.CALENDAR);

        super.onStart();
    }

    private void initLayout(Context context, long dailyTime)
    {
        setContentView(R.layout.activity_calendar);

        switch (mPlaceType)
        {
            case HOTEL:
                setActionBar(R.string.label_calendar_hotel_select_checkin);
                break;

            case GOURMET:
                setActionBar(R.string.label_calendar_gourmet_select);
                break;
        }

        ViewGroup calendarsLayout = (ViewGroup) findViewById(R.id.calendarLayout);

        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(dailyTime);

        int maxMonth = getMonthInterval(calendar);
        int maxDay = DAYCOUNT_OF_MAX;

        for (int i = 0; i <= maxMonth; i++)
        {
            int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            makeMonthCalendarView(context, calendarsLayout, calendar, maxDay > maxDayOfMonth ? maxDayOfMonth : maxDay);

            maxDay -= (maxDayOfMonth - day + 1);

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private View makeMonthCalendarView(Context context, ViewGroup parent, final Calendar calendar, final int maxDayOfMonth)
    {
        View calendarLayout = LayoutInflater.from(context).inflate(R.layout.view_calendar, null);

        TextView monthTextView = (TextView) calendarLayout.findViewById(R.id.monthTextView);
        GridView calendarGridView = (GridView) calendarLayout.findViewById(R.id.calendarGridView);
        calendarGridView.setOnItemClickListener(this);

        SimpleDateFormat simpleDayFormat = new SimpleDateFormat("MMM", Locale.KOREA);
        simpleDayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        monthTextView.setText(simpleDayFormat.format(calendar.getTime()));

        // day
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        // 일요일 이면
        if (dayOfWeek < 0)
        {
            dayOfWeek = 6;
        }

        int length = maxDayOfMonth + dayOfWeek;
        final int LENGHT_OF_WEEK = 7;

        if (length % LENGHT_OF_WEEK != 0)
        {
            length += (LENGHT_OF_WEEK - (length % LENGHT_OF_WEEK));
        }

        Day[] days = new Day[length];

        Calendar cloneCalendar = (Calendar) calendar.clone();

        for (int j = dayOfWeek, k = day; k <= maxDayOfMonth; j++, k++)
        {
            days[j] = new Day();
            days[j].dayTime = cloneCalendar.getTimeInMillis();
            days[j].day = Integer.toString(cloneCalendar.get(Calendar.DAY_OF_MONTH));
            days[j].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, //
            Util.dpToPx(this, 23) + length / LENGHT_OF_WEEK * Util.dpToPx(this, 38));
        calendarLayout.setLayoutParams(layoutParams);

        calendarGridView.setAdapter(new CalendarAdapter(context, days));
        parent.addView(calendarLayout);

        return calendarLayout;
    }

    private int getMonthInterval(final Calendar calendar)
    {
        Calendar lastMonthCalendar = (Calendar) calendar.clone();
        lastMonthCalendar.add(Calendar.DAY_OF_MONTH, DAYCOUNT_OF_MAX);

        long loopTime = lastMonthCalendar.getTimeInMillis() - calendar.getTimeInMillis();

        Calendar monthCalendar = DailyCalendar.getInstance();
        monthCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        monthCalendar.setTimeInMillis(loopTime);

        return monthCalendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Day day = (Day) view.getTag();

        if (day == null)
        {
            return;
        }

        if (isLockUiComponent() == true)
        {
            return;
        }

        day.isSelected = true;

        if (mCheckInDay == null)
        {
            mCheckInDay = day;

            ((CalendarAdapter) parent.getAdapter()).notifyDataSetChanged();

            if (mPlaceType == PlaceMainFragment.TYPE.GOURMET)
            {
                lockUiComponent();

                Intent intent = new Intent();
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mCheckInDay.dayTime);

                setResult(RESULT_OK, intent);
                finish();
            }
        } else
        {
            if (mCheckInDay.dayTime == day.dayTime)
            {
                mCheckInDay = null;
                day.isSelected = false;

                ((CalendarAdapter) parent.getAdapter()).notifyDataSetChanged();
                return;
            }

            lockUiComponent();
            mCheckOutDay = day;

            ((CalendarAdapter) parent.getAdapter()).notifyDataSetChanged();

            Intent intent = new Intent();
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mCheckInDay.dayTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, mCheckOutDay.dayTime);

            ExLog.d("night : " + ((mCheckOutDay.dayTime - mCheckInDay.dayTime) / SaleTime.MILLISECOND_IN_A_DAY));

            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private class CalendarAdapter extends BaseAdapter
    {
        private Context mContext;
        private final Day[] mDays;

        public CalendarAdapter(Context context, Day[] days)
        {
            mContext = context;
            mDays = days;
        }

        @Override
        public int getCount()
        {
            if (mDays == null)
            {
                return 0;
            }

            return mDays.length;
        }

        @Override
        public Day getItem(int position)
        {
            if (mDays == null)
            {
                return null;
            }

            return mDays[position];
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            DailyTextView dayTextView = (DailyTextView) convertView;
            Day day = mDays[position];

            if (dayTextView == null)
            {
                dayTextView = new DailyTextView(mContext);
                dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                dayTextView.setGravity(Gravity.CENTER);

                GridView.LayoutParams layoutParams = new GridView.LayoutParams(Util.dpToPx(mContext, 50), Util.dpToPx(mContext, 38));
                dayTextView.setLayoutParams(layoutParams);
            }

            if (day == null)
            {
                dayTextView.setText(null);
                dayTextView.setTag(null);
                dayTextView.setBackgroundResource(R.drawable.date_month_bg);
            } else
            {
                if (day.isSelected == true)
                {
                    dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.BOLD);
                    dayTextView.setBackgroundResource(R.drawable.date_select_btn);
                    dayTextView.setTextColor(getResources().getColor(R.color.calendar_day_text_select));
                } else
                {
                    dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.NORMAL);

                    // 일요일
                    if (day.dayOfWeek == 1)
                    {
                        dayTextView.setTextColor(mContext.getResources().getColorStateList(R.drawable.selector_calendar_sunday_textcolor));
                    } else
                    {
                        dayTextView.setTextColor(mContext.getResources().getColorStateList(R.drawable.selector_calendar_default_text_color));
                    }

                    dayTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_calendar_day_background));
                }

                dayTextView.setText(mDays[position].day);
                dayTextView.setTag(mDays[position]);
            }

            return dayTextView;
        }
    }

    private class Day
    {
        long dayTime;
        String day;
        int dayOfWeek;
        boolean isSelected;
    }
}
