package com.daily.dailyhotel.repository.local.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.remote.model.StayOutboundSuggestData;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public class StayObSearchResultHistory extends SearchResultHistory
{
    public StayBookDateTime stayBookDateTime;
    public StayOutboundSuggest stayOutboundSuggest;
    public int adultCount;
    protected ArrayList<Integer> mChildAgeList;

    public StayObSearchResultHistory(String startDate, String endDate //
        , String suggest, int adultCount, String childAgeList)
    {
        super( startDate, endDate);

        if (DailyTextUtils.isTextEmpty(suggest))
        {
            throw new NullPointerException("StayObSearchResultHistory error - suggest = " + suggest);
        }

        this.adultCount = adultCount;

        if (DailyTextUtils.isTextEmpty(childAgeList) == false)
        {
            String[] array = childAgeList.replaceAll("\\[|\\]| ", "").split(",");
            mChildAgeList = new ArrayList<>();
            for (String ageString : array)
            {
                try
                {
                    mChildAgeList.add(Integer.parseInt(ageString));
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

            ExLog.d("sam - mChildAgeList = " + mChildAgeList.toString());
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

            StayOutboundSuggestData stayOutboundSuggestData = LoganSquare.parse(suggest, StayOutboundSuggestData.class);
            stayOutboundSuggest = stayOutboundSuggestData.getSuggests();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public ArrayList<Integer> getChildAgeList()
    {
        return mChildAgeList;
    }
}
