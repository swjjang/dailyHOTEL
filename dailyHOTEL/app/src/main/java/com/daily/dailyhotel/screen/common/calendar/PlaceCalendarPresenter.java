package com.daily.dailyhotel.screen.common.calendar;


import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.SparseIntArray;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogViewInterface;
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
public abstract class PlaceCalendarPresenter<T1 extends BaseActivity, T2 extends BaseDialogViewInterface> extends BaseExceptionPresenter<T1, T2>
{
    public PlaceCalendarPresenter(@NonNull T1 activity)
    {
        super(activity);
    }

    @Override
    public void constructorInitialize(T1 activity)
    {

    }

    public ArrayList<Pair<String, Day[]>> makeCalendar(String startDateTime, String endDateTime, SparseIntArray holidaySparseIntArray)
    {
        ArrayList<Pair<String, PlaceCalendarPresenter.Day[]>> arrayList = new ArrayList<>();

        Date startDate;
        Date endDate;

        try
        {
            startDate = DailyCalendar.convertStringToDate(startDateTime);
            endDate = DailyCalendar.convertStringToDate(endDateTime);
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            return null;
        }

        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTime(startDate);

        int maxMonth = getMonthInterval(startDate, endDate);

        for (int i = 0; i <= maxMonth; i++)
        {
            String titleMonth = DailyCalendar.format(calendar.getTime(), "yyyy.MM");

            PlaceCalendarPresenter.Day[] days = getMonthCalendar(calendar, startDate, endDate, holidaySparseIntArray);

            arrayList.add(new Pair(titleMonth, days));

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, 1);
        }

        return arrayList;
    }

    private PlaceCalendarPresenter.Day[] getMonthCalendar(final Calendar calendar, final Date startDate, final Date endDate, SparseIntArray holidaySparseIntArray)
    {
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayValue = calendar.get(Calendar.DAY_OF_MONTH);
        int todayMonthValue = calendar.get(Calendar.MONTH);

        boolean isStart = false;
        boolean isLast = false;

        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(startDate);

        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate);

        int endDayValue = endDateCalendar.get(Calendar.DAY_OF_MONTH);
        int endMonthValue = endDateCalendar.get(Calendar.MONTH);

        int startGap = 0;

        if (calendar.get(Calendar.MONTH) == startDateCalendar.get(Calendar.MONTH))
        {
            isStart = true;

            startGap = Calendar.SUNDAY - todayDayOfWeek;
        }

        if (calendar.get(Calendar.MONTH) == endDateCalendar.get(Calendar.MONTH))
        {
            isLast = true;

            maxDayOfMonth = endDayValue;
        }

        Calendar cloneCalendar = (Calendar) calendar.clone();

        if (startGap != 0)
        {
            cloneCalendar.add(Calendar.DAY_OF_MONTH, startGap);
        }

        int startDayValue = cloneCalendar.get(Calendar.DAY_OF_MONTH);
        int startDayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

        final int LENGTH_OF_WEEK = 7;
        int length = maxDayOfMonth - startDayValue + startDayOfWeek;
        if (length % LENGTH_OF_WEEK != 0)
        {
            length += (LENGTH_OF_WEEK - (length % LENGTH_OF_WEEK));
        }

        PlaceCalendarPresenter.Day[] days = new PlaceCalendarPresenter.Day[length];

        final boolean hasHolidays = holidaySparseIntArray != null && holidaySparseIntArray.size() > 0;

        for (int i = startDayOfWeek - 1; i < length; i++)
        {
            int dayValue = cloneCalendar.get(Calendar.DAY_OF_MONTH);
            int monthValue = cloneCalendar.get(Calendar.MONTH);

            days[i] = new PlaceCalendarPresenter.Day();
            days[i].dateTime = DailyCalendar.format(cloneCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            days[i].dayOfMonth = Integer.toString(dayValue);
            days[i].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

            if (hasHolidays)
            {
                days[i].isHoliday = holidaySparseIntArray.get(Integer.parseInt(DailyCalendar.format(cloneCalendar.getTime(), "yyyyMMdd")), -1) != -1;
            }

            days[i].isDefaultDimmed = isStart == true && (dayValue < todayValue || monthValue < todayMonthValue) //
                || isLast == true && dayValue > endDayValue || monthValue > endMonthValue;

            if (isStart == true && todayMonthValue == monthValue && dayValue == maxDayOfMonth)
            {
                break;
            }

            if (isLast == false && dayValue == maxDayOfMonth)
            {
                break;
            }

            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }

    private int getMonthInterval(final Date startDate, final Date endDate)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTime(startDate);

        int startMonth = calendar.get(Calendar.MONTH);

        calendar.setTime(endDate);

        int endMonth = calendar.get(Calendar.MONTH);

        if (startMonth > endMonth)
        {
            return 12 - startMonth + endMonth;
        } else
        {
            return endMonth - startMonth;
        }
    }

    protected static class Day
    {
        public String dateTime; // ISO-8601
        String dayOfMonth;
        int dayOfWeek;
        boolean isHoliday;
        boolean isSoldOut;
        boolean isDefaultDimmed;
    }
}
