package com.daily.dailyhotel.repository.local.model;

import com.daily.base.util.DailyTextUtils;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public class SearchResultHistory
{
    protected String startDate;
    protected String endDate;

    public SearchResultHistory(String startDate, String endDate)
    {
        if (DailyTextUtils.isTextEmpty(startDate))
        {
            throw new NullPointerException("SearchResultHistory error - startDate = " + startDate);
        }

        this.startDate = startDate;
        this.endDate = endDate;
    }
}
