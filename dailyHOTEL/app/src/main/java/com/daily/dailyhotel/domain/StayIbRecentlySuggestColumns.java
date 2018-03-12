package com.daily.dailyhotel.domain;

import android.provider.BaseColumns;

/**
 * Created by android_sam on 2018. 3. 12..
 */

public interface StayIbRecentlySuggestColumns extends BaseColumns
{
    String TYPE = "type";
    String DISPLAY = "display"; // 디비에서 저장 및 검색 용
    String STATION_INDEX = "station_index";
    String STATION_NAME = "station_name";
    String STATION_REGION = "station_region";
    String STATION_LINE = "station_line";
    String STAY_INDEX = "stay_index";
    String STAY_NAME = "stay_name";
    String PROVINCE_INDEX = "province_index";
    String PROVINCE_NAME = "province_name";
    String AREA_INDEX = "area_index";
    String AREA_NAME = "area_name";
    String LOCATION_NAME = "location_name";
    String ADDRESS = "address";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String DIRECT_NAME = "direct_name";
    String SAVING_TIME = "saving_time";
    String KEYWORD = "keyword";
}
