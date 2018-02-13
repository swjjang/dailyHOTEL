package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.network.model.StayKeyword;

/**
 * Created by android_sam on 2018. 1. 25..
 */

public class StaySuggest
{
    public static final String CATEGORY_REGION = "region"; // default - 지역
    public static final String CATEGORY_STAY = "stay"; // 호텔
    public static final String CATEGORY_STATION = "station"; // 역
    public static final String CATEGORY_LOCATION = "location"; // 위치
    public static final String CATEGORY_DIRECT = "direct"; // 검색어 - 직접 입력

    public static final int MENU_TYPE_DIRECT = 1;
    public static final int MENU_TYPE_LOCATION = 2;
    public static final int MENU_TYPE_RECENTLY_SEARCH = 3;
    public static final int MENU_TYPE_RECENTLY_STAY = 4;
    public static final int MENU_TYPE_SUGGEST = 5;

    private static final String SEARCH_SEPARATOR = " > ";

    public int stayIndex; // 업장 인덱스
    public String stayName; // 업장 명
    public String regionName; // 대지역 명 (예  서울)
    public String provinceName; // 중지역 명 (예 강남구/서초구)
    public String displayName; // 전체 이름 ( 지역 명의 경우 역삼1동)
    public String address; // 전체 주소 - Suggest 화면 에서는 location 타입일때 diplayName 대신 address 사용
    public int discountAveragePrice; // 판매 가격 - 업장타입일때
    public int availableRooms; // 사용 가능 룸 개수
    public double latitude; // 위도
    public double longitude; // 경도
    public String categoryKey; // 객체 종류 - 호텔/위치/역/지역/직접검색 등
    public int menuType; // 검색어 입력창에서 선택 된 메뉴 - 주로 Analytics 에서 사용,  선택된 메뉴가 필요할때 사용

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

        menuType = MENU_TYPE_SUGGEST;
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

        menuType = MENU_TYPE_RECENTLY_SEARCH;
    }

    public StaySuggest(RecentlyPlace recentlyPlace)
    {
        if (recentlyPlace == null)
        {
            return;
        }

        displayName = recentlyPlace.regionName + SEARCH_SEPARATOR + recentlyPlace.title;

        categoryKey = CATEGORY_STAY;
        regionName = recentlyPlace.regionName;
        stayName = recentlyPlace.title;
        stayIndex = recentlyPlace.index;
        menuType = MENU_TYPE_RECENTLY_STAY;
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
