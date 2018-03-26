package com.daily.dailyhotel.repository.local.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySuggest;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.Calendar;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public class StaySearchResultHistory extends SearchResultHistory
{
    public StayBookDateTime stayBookDateTime;
    public StaySuggest staySuggest;

    public StaySearchResultHistory(String startDate, String endDate, String suggest)
    {
        super(startDate, endDate);

        if (DailyTextUtils.isTextEmpty(suggest))
        {
            throw new NullPointerException("StaySearchResultHistory error -  suggest = " + suggest);
        }

        try
        {
            if (DailyTextUtils.isTextEmpty(endDate))
            {
                Calendar calendar = DailyCalendar.getInstance(startDate, DailyCalendar.ISO_8601_FORMAT);
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                endDate = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            }

            stayBookDateTime = new StayBookDateTime(startDate, endDate);

            StaySuggestData staySuggestData = LoganSquare.parse(suggest, StaySuggestData.class);
            staySuggest = staySuggestData.getSuggest();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
