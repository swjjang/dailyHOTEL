package com.daily.dailyhotel.domain;

import android.provider.BaseColumns;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public interface GourmetRecentlySuggestColumns extends BaseColumns
{
    String TYPE = "type";
    String DISPLAY = "display"; // 디비에서 저장 및 검색 용
    String GOURMET_INDEX = "gourmet_index"; // 고메 인덱스
    String GOURMET_NAME = "gourmet_name";
    String AREA_GROUP_INDEX = "area_group_index";
    String AREA_GROUP_NAME = "area_group_name";
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
