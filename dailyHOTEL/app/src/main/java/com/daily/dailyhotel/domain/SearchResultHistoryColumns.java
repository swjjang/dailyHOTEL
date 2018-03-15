package com.daily.dailyhotel.domain;

import android.provider.BaseColumns;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public interface SearchResultHistoryColumns extends BaseColumns
{
    String SERVICE_TYPE = "service_type"; // stay, gourmet, stay outbound
    String START_DATE = "start_date"; // Check in date, visit date
    String END_DATE = "end_date"; // check out date, gourmet 는 empty
    String SUGGEST = "suggest"; // StaySuggestV2, GourmetSuggestV2, StayOutboundSuggest - json String 예정
    String ADULT_COUNT = "adult_count"; // 성인 수
    String CHILD_AGE_LIST = "child_age_list"; // 아이 나이 리스트
}
