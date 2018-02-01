package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;

/**
 * Created by android_sam on 2018. 2. 1..
 */

public class GourmetSuggest
{
    public static final String CATEGORY_REGION = "region"; // default - 지역
    public static final String CATEGORY_GOURMET = "gourmet"; // 레스토랑
    public static final String CATEGORY_LOCATION = "location"; // 위치
    public static final String CATEGORY_DIRECT = "direct"; // 검색어 - 직접 입력

    public static final int MENU_TYPE_DIRECT = 1;
    public static final int MENU_TYPE_LOCATION = 2;
    public static final int MENU_TYPE_RECENTLY_SEARCH = 3;
    public static final int MENU_TYPE_RECENTLY_GOURMET = 4;
    public static final int MENU_TYPE_SUGGEST = 5;

    private static final String SEARCH_SEPARATOR = " > ";

    public int gourmetIndex;
    public String gourmetName;
    public String regionName;
    public String provinceName;
    public String displayName;
    public int discountPrice;
    public int availableTickets;
    public boolean isExpired;
    public int minimumOrderQuantity;
    public double latitude;
    public double longitude;
    public String categoryKey;
    public int menuType;

    public GourmetSuggest()
    {
    }

    public GourmetSuggest(int menuType, String categoryKey, String displayName)
    {
        this.menuType = menuType;
        this.categoryKey = categoryKey;
        this.displayName = displayName;
    }

    public GourmetSuggest(GourmetKeyword gourmetKeyword)
    {
        if (gourmetKeyword == null)
        {
            return;
        }

        gourmetIndex = gourmetKeyword.index;
        discountPrice = gourmetKeyword.price;
        displayName = gourmetKeyword.name;
        availableTickets = gourmetKeyword.availableTickets;
        isExpired = gourmetKeyword.isExpired;
        minimumOrderQuantity = gourmetKeyword.minimumOrderQuantity;

        String[] splitArray = splitRightAngleBracket(gourmetKeyword.name);

        if (gourmetIndex > 0)
        {
            categoryKey = CATEGORY_GOURMET;

            regionName = splitArray[0];
            gourmetName = splitArray[1];
        } else {
            categoryKey = CATEGORY_REGION;

            regionName = splitArray[0];
            gourmetName = splitArray[1];
        }

        menuType = MENU_TYPE_SUGGEST;
    }

    public GourmetSuggest(Keyword keyword)
    {
        if (keyword == null)
        {
            return;
        }

        displayName = keyword.name;

        String[] splitArray = splitRightAngleBracket(keyword.name);

        if (keyword.icon == 2)
        {
            categoryKey = CATEGORY_GOURMET;

            regionName = splitArray[0];
            gourmetName = splitArray[1];
        } else
        {
            categoryKey = DailyTextUtils.isTextEmpty(splitArray[1]) ? CATEGORY_DIRECT : CATEGORY_REGION;

            regionName = splitArray[0];
            provinceName = splitArray[1];
        }

        menuType = MENU_TYPE_RECENTLY_SEARCH;
    }

    public GourmetSuggest(RecentlyPlace recentlyPlace)
    {
        if (recentlyPlace == null)
        {
            return;
        }

        displayName = recentlyPlace.regionName + SEARCH_SEPARATOR + recentlyPlace.title;

        categoryKey = CATEGORY_GOURMET;
        regionName = recentlyPlace.regionName;
        gourmetName = recentlyPlace.title;
        gourmetIndex = recentlyPlace.index;
        menuType = MENU_TYPE_RECENTLY_GOURMET;
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
