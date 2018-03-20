package com.daily.dailyhotel.domain;

import android.provider.BaseColumns;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public interface RecentlyColumns extends BaseColumns
{
    String PLACE_INDEX = "place_index";
    /*
     * ServiceType.name() 으로 저장 예정 HOTEL, OB_STAY, GOURMET
     */ String SERVICE_TYPE = "service_type";
    String NAME = "korean_name";
    String ENGLISH_NAME = "english_name";
    String IMAGE_URL = "image_url";
    String SAVING_TIME = "saving_time";
    String AREA_GROUP_NAME = "area_group_name";
}
