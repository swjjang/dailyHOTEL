package com.daily.dailyhotel.util;

import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.util.DailyCalendar;

public class ConvertFormat
{
    public static TodayDateTime convertTodayDateTime(@NonNull CommonDateTime commonDateTime)
    {
        return new TodayDateTime(commonDateTime.openDateTime, commonDateTime.closeDateTime//
            , commonDateTime.currentDateTime, commonDateTime.dailyDateTime);
    }

    public static StayBookingDay commonStayBookingDay(@NonNull StayBookDateTime stayBookDateTime) throws Exception
    {
        StayBookingDay stayBookingDay = new StayBookingDay();

        stayBookingDay.setCheckInDay(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
        stayBookingDay.setCheckOutDay(stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));

        return stayBookingDay;
    }

    public static StayBookDateTime commonStayBookDateTime(@NonNull StayBookingDay stayBookingDay) throws Exception
    {
        StayBookDateTime stayBookDateTime = new StayBookDateTime();

        stayBookDateTime.setCheckInDateTime(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT));
        stayBookDateTime.setCheckOutDateTime(stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT));

        return stayBookDateTime;
    }
}