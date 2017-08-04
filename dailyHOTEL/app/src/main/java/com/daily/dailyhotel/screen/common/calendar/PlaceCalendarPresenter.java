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

    private PlaceCalendarPresenter.Day[] getMonthCalendar(final Calendar todayCalendar, final Date startDate, final Date endDate, SparseIntArray holidaySparseIntArray)
    {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        long startTimeInMillis = startCalendar.getTimeInMillis();
        long endTimeInMillis = endCalendar.getTimeInMillis();

        int endDayValue = endCalendar.get(Calendar.DAY_OF_MONTH);
        int endMonthValue = endCalendar.get(Calendar.MONTH);

        int todayMaxDayOfMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayDayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK);

        boolean isLast = false;
        if (todayMonth == endMonthValue)
        {
            isLast = true;
            todayMaxDayOfMonth = endDayValue;
        }

        int startGap = 0;
        if (todayMonth == startCalendar.get(Calendar.MONTH))
        {
            startGap = Calendar.SUNDAY - todayDayOfWeek;
        }

        Calendar cloneCalendar = (Calendar) todayCalendar.clone();
        // 처음 달인 경우만 스타트 데이의 갭을 설정해 준다.
        if (startGap != 0)
        {
            cloneCalendar.add(Calendar.DAY_OF_MONTH, startGap);
        }

        int currentDayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

        int length = getMonthCalendarLength(todayCalendar, startCalendar, endCalendar);

        PlaceCalendarPresenter.Day[] days = new PlaceCalendarPresenter.Day[length];

        final boolean hasHolidays = holidaySparseIntArray != null && holidaySparseIntArray.size() > 0;

        for (int i = currentDayOfWeek - 1; i < length; i++)
        {
            int dayValue = cloneCalendar.get(Calendar.DAY_OF_MONTH);
            int monthValue = cloneCalendar.get(Calendar.MONTH);
            long currentTimeMillis = cloneCalendar.getTimeInMillis();

            days[i] = new PlaceCalendarPresenter.Day();
            days[i].dateTime = DailyCalendar.format(cloneCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            days[i].dayOfMonth = Integer.toString(dayValue);
            days[i].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

            if (hasHolidays)
            {
                days[i].isHoliday = holidaySparseIntArray.get(Integer.parseInt(DailyCalendar.format(cloneCalendar.getTime(), "yyyyMMdd")), -1) != -1;
            }

            // 현재 날짜의 시간이 시작 날짜보다 작거나 종료 날짜보다 클 경우
            days[i].isDefaultDimmed = currentTimeMillis < startTimeInMillis || currentTimeMillis > endTimeInMillis;

            if (isLast == false && monthValue == todayMonth && dayValue == todayMaxDayOfMonth)
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

    private int getMonthCalendarLength(final Calendar todayCalendar, final Calendar startCalendar, final Calendar endCalendar)
    {
        if (startCalendar == null || endCalendar == null || todayCalendar == null)
        {
            return 0;
        }

        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int endMonth = endCalendar.get(Calendar.MONTH);

        int today = todayCalendar.get(Calendar.DAY_OF_MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);

        int availableLastDay = todayMonth == endMonth ? endDay : todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int monthDay = availableLastDay - today + 1; // 이달이 31일 일경우 오늘이 최소 1일임으로 31 - 1 하면 30일이 되어 버린다 채워야 하는 칸은 31개 이므로 1일을 보정한다.
        int todayDayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK); // 요일은 1부터 시작하지만 배열은 0부터
        // 시작함으로 1 빼고 계산 해야 함
        int endDayOfWeek = (((todayDayOfWeek - 1) + monthDay) % 7); // monthDay 에 오늘이 포함 되었음으로 1일을 빼고 이전 날을 계산 해야 함
        if (endDayOfWeek == 0)
        {
            endDayOfWeek = Calendar.SATURDAY;
        }

        int length = (todayDayOfWeek - 1) + monthDay + (7 - endDayOfWeek);

        ExLog.d("length : " + length + " , availableLastDay : " + availableLastDay + " , today : " + today + " , todayweek : " + todayDayOfWeek + " , endDay Week : " + endDayOfWeek);
        return length;
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
