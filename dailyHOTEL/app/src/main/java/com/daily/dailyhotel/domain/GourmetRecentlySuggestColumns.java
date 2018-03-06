package com.daily.dailyhotel.domain;

import android.provider.BaseColumns;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public interface GourmetRecentlySuggestColumns extends BaseColumns
{
    String TYPE = "type";
    String NAME = "name"; // 직접 검색어, 고메 이름, 위치 동명, 지역명
    String GOURMET_INDEX = "gourmet_index"; // 고메 인덱스
    String PROVINCE_INDEX = "province_index";
    String PROVINCE_NAME = "province_name";
    String AREA_INDEX = "area_index";
    String AREA_NAME = "area_name";
    String ADDRESS = "address";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String SAVING_TIME = "saving_time";
    String KEYWORD = "keyword";
}
