package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.network.model.StayKeyword;

/**
 * Created by android_sam on 2018. 1. 25..
 */

public class StaySuggest
{
    public static final String CATEGORY_REGION = "region"; // default - 지역검색
    public static final String CATEGORY_STAY = "stay"; // 호텔검색
    public static final String CATEGORY_STATION = "station"; // 역검색
    public static final String CATEGORY_LOCATION = "location"; // 위치검색
    public static final String CATEGORY_DIRECT = "direct"; // 직접검색
    public static final String CATEGORY_RECENTLY = "recently"; // 최근 본 업장 <-- 종류 삭제 예정

    public static final int MENU_TYPE_DIRECT = 1;
    public static final int MENU_TYPE_LOCATION = 2;
    public static final int MENU_TYPE_RECENTLY_SEARCH = 3;
    public static final int MENU_TYPE_RECENTLY_STAY = 4;
    public static final int MENU_TYPE_SUGGEST = 5;




    private static final String SEARCH_SEPARATOR = " > ";

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
    public int menuType;

    public StaySuggest()
    {
    }

    public StaySuggest(int menuType, String categoryKey, String displayName)
    {
        this.menuType = menuType;
        this.categoryKey = categoryKey;
        this.displayName = displayName;
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
        } else
        {
            categoryKey = CATEGORY_REGION;

            regionName = splitArray[0];
            provinceName = splitArray[1];
        }
    }

    public StaySuggest(Keyword keyword)
    {
        if (keyword == null)
        {
            return;
        }

        displayName = keyword.name;

        String[] splitArray = splitRightAngleBracket(keyword.name);

        if (keyword.icon == 1)
        {
            categoryKey = CATEGORY_STAY;

            regionName = splitArray[0];
            stayName = splitArray[1];
        } else
        {
            categoryKey = DailyTextUtils.isTextEmpty(splitArray[1]) ? CATEGORY_DIRECT : CATEGORY_REGION;

            regionName = splitArray[0];
            provinceName = splitArray[1];
        }
    }

    public StaySuggest(RecentlyPlace recentlyPlace)
    {
        if (recentlyPlace == null)
        {
            return;
        }

        displayName = recentlyPlace.regionName + SEARCH_SEPARATOR + recentlyPlace.title;

        categoryKey = CATEGORY_RECENTLY;
        regionName = recentlyPlace.regionName;
        stayName = recentlyPlace.title;
        stayIndex = recentlyPlace.index;
    }

    private String[] splitRightAngleBracket(String displayName)
    {
        String[] splitString = new String[2];

        if (DailyTextUtils.isTextEmpty(displayName) == true)
        {
            return splitString;
        }

        int index = displayName.indexOf(SEARCH_SEPARATOR);
        if (index < 0)
        {
            splitString[0] = displayName;
            return splitString;
        }

        splitString[0] = displayName.substring(0, index);
        splitString[1] = displayName.substring(index + SEARCH_SEPARATOR.length(), displayName.length());

        return splitString;
    }
}
