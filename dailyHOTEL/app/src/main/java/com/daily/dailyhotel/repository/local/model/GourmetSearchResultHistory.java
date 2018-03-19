package com.daily.dailyhotel.repository.local.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggestV2;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public class GourmetSearchResultHistory extends SearchResultHistory
{
    public GourmetBookDateTime gourmetBookDateTime;
    public GourmetSuggestV2 gourmetSuggest;

    public GourmetSearchResultHistory(String visitDate, String suggest)
    {
        super(visitDate, null);

        if (DailyTextUtils.isTextEmpty(suggest))
        {
            throw new NullPointerException("GourmetSearchResultHistory error - suggest = " + suggest);
        }

        try
        {
            gourmetBookDateTime = new GourmetBookDateTime(startDate);

            GourmetSuggestData gourmetSuggestData = LoganSquare.parse(suggest, GourmetSuggestData.class);
            gourmetSuggest = gourmetSuggestData.getSuggest();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
