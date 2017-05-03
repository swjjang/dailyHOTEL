package com.daily.dailyhotel.screen.common.calendar;


import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.daily.base.BaseActivity;
import com.daily.base.BaseViewInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sheldon
 * Clean Architecture
 */
public abstract class PlaceCalendarPresenter<T1 extends BaseActivity, T2 extends BaseViewInterface> extends BaseExceptionPresenter<T1, T2>
{
    public PlaceCalendarPresenter(@NonNull T1 activity)
    {
        super(activity);
    }

    @Override
    public void initialize(T1 activity)
    {

    }

    public ArrayList<Pair<String, Day[]>> makeCalendar(String startDateTime, String endDateTime, int[] holidays)
    {
        ArrayList<Pair<String, PlaceCalendarPresenter.Day[]>> arrayList = new ArrayList<>();

        try
        {
            Date startDate = DailyCalendar.convertStringToDate(startDateTime);
            Date endDate = DailyCalendar.convertStringToDate(endDateTime);

            Calendar calendar = DailyCalendar.getInstance();
            calendar.setTime(startDate);

            final int DAYS_OF_MAX = DailyCalendar.compareDateDay(endDateTime, startDateTime) + 1;
            int remainDay = DAYS_OF_MAX;
            int maxMonth = getMonthInterval(calendar, DAYS_OF_MAX);

            int dayOffset = 0;

            for (int i = 0; i <= maxMonth; i++)
            {
                int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                String titleMonth = DailyCalendar.format(calendar.getTime(), "yyyy.MM");

                PlaceCalendarPresenter.Day[] days = getMonthCalendar(dayOffset//
                    , calendar, day + remainDay - 1 > maxDayOfMonth ? maxDayOfMonth : day + remainDay - 1, holidays);

                arrayList.add(new Pair(titleMonth, days));

                dayOffset += maxDayOfMonth - day + 1;
                remainDay = DAYS_OF_MAX - dayOffset;

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, 1);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            return null;
        }

        return arrayList;
    }

    private PlaceCalendarPresenter.Day[] getMonthCalendar(final int dayOffset, final Calendar calendar, final int maxDayOfMonth, int[] holidays)
    {
        // dayOfMonth
        final int LENGTH_OF_WEEK = 7;
        final int DAY = calendar.get(Calendar.DAY_OF_MONTH);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int length = maxDayOfMonth - DAY + 1 + dayOfWeek;

        if (length % LENGTH_OF_WEEK != 0)
        {
            length += (LENGTH_OF_WEEK - (length % LENGTH_OF_WEEK));
        }

        PlaceCalendarPresenter.Day[] days = new PlaceCalendarPresenter.Day[length];

        Calendar cloneCalendar = (Calendar) calendar.clone();

        for (int i = 0, j = dayOfWeek, k = DAY; k <= maxDayOfMonth; i++, j++, k++)
        {
            days[j] = new PlaceCalendarPresenter.Day();
            days[j].dateTime = DailyCalendar.format(cloneCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            days[j].dayOfMonth = Integer.toString(cloneCalendar.get(Calendar.DAY_OF_MONTH));
            days[j].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);
            days[j].isHoliday = isHoliday(DailyCalendar.format(cloneCalendar.getTime(), "yyyyMMdd"), holidays);

            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
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

    /**
     * @param dateFormat yyyyMMdd
     * @param holidays
     * @return
     */
    private boolean isHoliday(String dateFormat, int[] holidays)
    {
        if (DailyTextUtils.isTextEmpty(dateFormat) == true || holidays == null || holidays.length == 0)
        {
            return false;
        }

        Calendar calendar = DailyCalendar.getInstance();

        try
        {
            int calendarDay = Integer.parseInt(dateFormat);

            for (int holiday : holidays)
            {
                if (holiday == calendarDay)
                {
                    return true;
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    protected static class Day
    {
        public String dateTime; // ISO-8601
        String dayOfMonth;
        int dayOfWeek;
        boolean isHoliday;
    }
}
