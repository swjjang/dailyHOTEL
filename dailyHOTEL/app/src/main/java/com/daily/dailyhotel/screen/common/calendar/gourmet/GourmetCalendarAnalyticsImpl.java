package com.daily.dailyhotel.screen.common.calendar.gourmet;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GourmetCalendarAnalyticsImpl implements GourmetCalendarInterface.AnalyticsInterface
{
    private String mEnteredVisitDateTime;

    @Override
    public void setVisitDateTime(String visitDateTime)
    {
        mEnteredVisitDateTime = visitDateTime;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYGOURMET_LIST_CALENDAR, null);
    }

    @Override
    public void onEventCloseClick(Activity activity, String callByScreen)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLOSED, callByScreen, null);
    }

    @Override
    public void onEventConfirmClick(Activity activity, String callByScreen, String visitDateTime)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(visitDateTime) == true)
        {
            return;
        }

        try
        {
            String visitDay = DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd");
            String visitDate = DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");
            boolean changedDay = mEnteredVisitDateTime != null && mEnteredVisitDateTime.equalsIgnoreCase(visitDateTime);

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.VISIT_DATE, visitDay);
            params.put(AnalyticsManager.KeyType.SCREEN, callByScreen);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED//
                , (changedDay ? AnalyticsManager.ValueType.CHANGED : AnalyticsManager.ValueType.NONE_)//
                    + "-" + visitDate + "-" + DailyCalendar.format(new Date(), "yyyy.MM.dd(EEE) HH시 mm분"), params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }
}
