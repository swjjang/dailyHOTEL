package com.daily.dailyhotel.repository.local.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.repository.remote.model.StayOutboundSuggestData;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public class SearchResultHistory
{
    public Constants.ServiceType serviceType;
    private String startDate;
    private String endDate;
    public StayBookDateTime stayBookDateTime;
    public GourmetBookDateTime gourmetBookDateTime;
    public StaySuggestV2 staySuggest;
    public GourmetSuggestV2 gourmetSuggest;
    public StayOutboundSuggest stayOutboundSuggest;
    public int adultCount;
    private ArrayList<Integer> mChildAgeList;

    public SearchResultHistory(String serviceTypeString, String startDate, String endDate //
        , String suggest, int adultCount, String childAgeList)
    {
        if (DailyTextUtils.isTextEmpty(serviceTypeString, startDate, suggest))
        {
            throw new NullPointerException("SearchResultHistory error - serviceTypeString = " //
                + serviceTypeString + " , startDate = " + startDate + " , suggest = " + suggest);
        }

        try
        {
            serviceType = Constants.ServiceType.valueOf(serviceTypeString);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
            serviceType = null;
        }

        if (serviceType == null)
        {
            throw new NullPointerException("SearchResultHistory error - serviceType = " + serviceType);
        }

        this.startDate = startDate;
        this.endDate = endDate;
        this.adultCount = adultCount;

        if (DailyTextUtils.isTextEmpty(childAgeList))
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
            switch (serviceType)
            {
                case HOTEL:
                {
                    if (DailyTextUtils.isTextEmpty(endDate))
                    {
                        Calendar calendar = DailyCalendar.getInstance(startDate, DailyCalendar.ISO_8601_FORMAT);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);

                        endDate = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
                    }

                    stayBookDateTime = new StayBookDateTime(startDate, endDate);
                    gourmetBookDateTime = null;


                    StaySuggestData staySuggestData = LoganSquare.parse(suggest, StaySuggestData.class);

                    staySuggest = staySuggestData.getSuggest();
                    gourmetSuggest = null;
                    stayOutboundSuggest = null;
                    break;
                }

                case GOURMET:
                {
                    stayBookDateTime = null;
                    gourmetBookDateTime = new GourmetBookDateTime(startDate);

                    GourmetSuggestData gourmetSuggestData = LoganSquare.parse(suggest, GourmetSuggestData.class);

                    staySuggest = null;
                    gourmetSuggest = gourmetSuggestData.getSuggest();
                    stayOutboundSuggest = null;
                    break;
                }

                case OB_STAY:
                {
                    if (DailyTextUtils.isTextEmpty(endDate))
                    {
                        Calendar calendar = DailyCalendar.getInstance(startDate, DailyCalendar.ISO_8601_FORMAT);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);

                        endDate = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
                    }

                    stayBookDateTime = new StayBookDateTime(startDate, endDate);
                    gourmetBookDateTime = null;

                    StayOutboundSuggestData stayOutboundSuggestData = LoganSquare.parse(suggest, StayOutboundSuggestData.class);

                    staySuggest = null;
                    gourmetSuggest = null;
                    stayOutboundSuggest = stayOutboundSuggestData.getSuggests();
                    break;
                }
            }
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
