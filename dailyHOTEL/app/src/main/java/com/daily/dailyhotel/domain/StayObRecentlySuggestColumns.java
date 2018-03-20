package com.daily.dailyhotel.domain;

import android.provider.BaseColumns;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public interface StayObRecentlySuggestColumns extends BaseColumns
{
    String NAME = "name";
    String CITY = "city";
    String COUNTRY = "country";
    String COUNTRY_CODE = "country_code";
    String CATEGORY_KEY = "category_key";
    String DISPLAY = "display";
    String DISPLAY_TEXT = "display_text";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String SAVING_TIME = "saving_time";
    String KEYWORD = "keyword";
}
