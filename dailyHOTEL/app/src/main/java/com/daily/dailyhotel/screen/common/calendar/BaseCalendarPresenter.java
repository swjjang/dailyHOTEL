package com.daily.dailyhotel.screen.common.calendar;


import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by sheldon
 * Clean Architecture
 */
public abstract class BaseCalendarPresenter<T1 extends BaseActivity, T2 extends BaseDialogViewInterface> extends BaseExceptionPresenter<T1, T2>
{
    final int MONTH_OF_YEAR = 12;
    final int WEEK = 7;

    public BaseCalendarPresenter(@NonNull T1 activity)
    {
        super(activity);
    }

    @Override
    public void constructorInitialize(T1 activity)
    {

    }

    public List<ObjectItem> makeCalendar(String startDateTime, String endDateTime//
        , SparseIntArray holidaySparseIntArray, SparseIntArray soldOutDaySparseIntArray)
    {
        List<ObjectItem> calendarList = new ArrayList<>();

        try
        {
            Calendar startCalendar = DailyCalendar.getInstance(startDateTime, DailyCalendar.ISO_8601_FORMAT);
            Calendar endCalendar = DailyCalendar.getInstance(endDateTime, DailyCalendar.ISO_8601_FORMAT);

            int maxMonth = getMonthInterval(startCalendar, endCalendar);

            Calendar calendar = (Calendar) startCalendar.clone();

            for (int i = 0; i <= maxMonth; i++)
            {
                calendarList.add(new ObjectItem(ObjectItem.TYPE_MONTH_VIEW, new Month(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)));
                calendarList.addAll(getMonthCalendar(calendar, startCalendar, endCalendar, holidaySparseIntArray, soldOutDaySparseIntArray));

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, 1);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            return null;
        }

        return calendarList;
    }

    private List<ObjectItem> getMonthCalendar(final Calendar monthCalendar, final Calendar startCalendar//
        , final Calendar endCalendar, SparseIntArray holidaySparseIntArray, SparseIntArray soldOutDaySparseIntArray)
    {
        int currentMonth = monthCalendar.get(Calendar.MONTH);
        int currentYear = monthCalendar.get(Calendar.YEAR);
        int underDayCount = 0;
        int overDayCount = 0;

        boolean firstCalendar = false;
        boolean lastCalendar = false;

        List<ObjectItem> weeksOfMonthList = new ArrayList<>();

        // 시작 달이 같은 경우 뒷날짜로 week 의 시작일이 있는지 체크한다.
        if (equalsMonth(startCalendar, currentYear, currentMonth) == true)
        {
            firstCalendar = true;
            underDayCount = getInFirstWeekOfMonthHasInPreviousMonthDayCount(startCalendar);
        }

        // 종료 달이 같은 경우 앞날짜로 week의 마지막 일이 있는지 체크한다.
        if (equalsMonth(endCalendar, currentYear, currentMonth) == true)
        {
            lastCalendar = true;
            overDayCount = getInLastWeekOfMonthHasInNextMonthDayCount(endCalendar);
        }

        int daysOfMonthCount; // 한달의 일 개수
        int startDay = firstCalendar ? startCalendar.get(Calendar.DAY_OF_MONTH) : monthCalendar.get(Calendar.DAY_OF_MONTH);
        int endDay = lastCalendar ? endCalendar.get(Calendar.DAY_OF_MONTH) : monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        daysOfMonthCount = underDayCount + overDayCount + endDay - startDay + 1;

        Calendar calendar = (Calendar) monthCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, startDay);
        calendar.add(Calendar.DAY_OF_MONTH, -underDayCount);

        for (int i = 0; i < daysOfMonthCount; )
        {
            Day[] days = new Day[WEEK];

            // 일주일 단위로 진행한다.
            for (int j = 0; j < WEEK && i < daysOfMonthCount; j++)
            {
                if (calendar.get(Calendar.DAY_OF_WEEK) != j + 1)
                {
                    continue;
                }

                days[j] = getDay(calendar);

                int yyyyMMdd = days[j].toyyyyMMdd();

                days[j].holiday = holidaySparseIntArray != null && holidaySparseIntArray.get(yyyyMMdd, -1) != -1;
                days[j].soldOut = soldOutDaySparseIntArray != null && soldOutDaySparseIntArray.get(yyyyMMdd, -1) != -1;
                days[j].sideDay = i < underDayCount || i >= daysOfMonthCount - overDayCount;
                days[j].lastDay = lastCalendar == true && i == daysOfMonthCount - overDayCount - 1;

                calendar.add(Calendar.DAY_OF_MONTH, 1);
                i++;
            }

            weeksOfMonthList.add(new ObjectItem(ObjectItem.TYPE_WEEK_VIEW, days));
        }

        return weeksOfMonthList;
    }

    private boolean equalsMonth(@NonNull Calendar calendar, int year, int month)
    {
        return month == calendar.get((Calendar.MONTH)) && year == calendar.get(Calendar.YEAR);
    }

    private int getInFirstWeekOfMonthHasInPreviousMonthDayCount(@NonNull Calendar calendar)
    {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 0 : calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
    }

    private int getInLastWeekOfMonthHasInNextMonthDayCount(@NonNull Calendar calendar)
    {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ? 0 : Calendar.SATURDAY - calendar.get(Calendar.DAY_OF_WEEK);
    }

    private Day getDay(@NonNull Calendar calendar)
    {
        Day day = new Day();
        day.year = calendar.get(Calendar.YEAR);
        day.month = calendar.get(Calendar.MONTH) + 1;
        day.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        return day;
    }

    private int getMonthInterval(@NonNull Calendar startCalendar, @NonNull Calendar endCalendar)
    {
        int startYear = startCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH);

        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH) + MONTH_OF_YEAR * (endYear - startYear);

        if (startMonth > endMonth)
        {
            return MONTH_OF_YEAR - startMonth + endMonth;
        } else
        {
            return endMonth - startMonth;
        }
    }

    public static class Day
    {
        public int year;
        public int month;
        public int dayOfMonth;
        public boolean holiday;
        public boolean soldOut;
        public boolean lastDay;
        public boolean sideDay; // 시작 날짜와 끝 날짜 양쪽으로 사용하지 않는 달력

        public String getDateTime()
        {
            return String.format(Locale.KOREA, "%4d-%02d-%02dT12:00:00+09:00", year, month, dayOfMonth);
        }

        public int toyyyyMMdd()
        {
            return year * 10000 + month * 100 + dayOfMonth;
        }
    }

    public static class Month
    {
        public int year;
        public int month;

        public Month(int year, int month)
        {
            this.year = year;
            this.month = month;
        }
    }
}
