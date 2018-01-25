package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.network.model.StayKeyword;

/**
 * Created by android_sam on 2018. 1. 25..
 */

public class StaySuggest
{
    public static final String CATEGORY_STAY = "stay";
    public static final String CATEGORY_STATION = "station";
    public static final String CATEGORY_POINT = "point";
    public static final String CATEGORY_REGION = "region";

    public int stayIndex;
    public String stayName;
    public String regionName;
    public String provinceName;
    public String displayName;
    public int discountAveragePrice;
    public int availableRooms;
    public double latitude;
    public double longitude;
    public String categoryKey;

    public StaySuggest()
    {
    }

    public StaySuggest(StayKeyword stayKeyword)
    {
        if (stayKeyword == null)
        {
            return;
        }

        stayIndex = stayKeyword.index;
        discountAveragePrice = stayKeyword.price;
        displayName = stayKeyword.name;
        availableRooms = stayKeyword.availableRooms;

        String[] splitArray = splitRightAngleBracket(stayKeyword.name);

        if (stayIndex > 0)
        {
            categoryKey = CATEGORY_STAY;

            regionName = splitArray[0];
            stayName = splitArray[1];
        } else {
            categoryKey = CATEGORY_REGION;

            regionName = splitArray[0];
            provinceName = splitArray[1];
        }
    }

    private String[] splitRightAngleBracket(String displayName)
    {
        String[] splitString = new String[2];

        if (DailyTextUtils.isTextEmpty(displayName) == true)
        {
            return splitString;
        }

        String separator = " > ";

        int index = displayName.indexOf(separator);
        if (index < 0)
        {
            splitString[0] = displayName;
            return splitString;
        }

        splitString[0] = displayName.substring(0, index);
        splitString[1] = displayName.substring(index + separator.length(), displayName.length());

        return splitString;
    }
}
