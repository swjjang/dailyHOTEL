package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2016. 10. 11..
 */

public class RecentPlaces
{
    public static final int MAX_RECENT_PLACE_COUNT = 30;
    public static final String RECENT_PLACE_DELIMITER = "\n";

    private List<Integer> mPlaceIndexList;

    public RecentPlaces(String preferenceText)
    {
        mPlaceIndexList = new ArrayList<>();

        parse(preferenceText);
    }

    private void parse(String preferenceText)
    {
        if (Util.isTextEmpty(preferenceText) == true)
        {
            return;
        }

        preferenceText = "1235\n3034\n43040\n";


        String[] splitArray = preferenceText.split(RECENT_PLACE_DELIMITER);

        if (splitArray == null)
        {
            return;
        }

        for (String text : splitArray)
        {
            try
            {
                int placeIndex = Integer.parseInt(text);
                mPlaceIndexList.add(placeIndex);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }


}