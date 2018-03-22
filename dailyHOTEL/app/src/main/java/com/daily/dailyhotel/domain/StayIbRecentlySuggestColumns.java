package com.daily.dailyhotel.domain;

import android.provider.BaseColumns;

/**
 * Created by android_sam on 2018. 3. 12..
 */

public interface StayIbRecentlySuggestColumns extends BaseColumns
{
    String TYPE = "type";
    String DISPLAY = "display"; // 디비에서 저장 및 검색 용
    String SUGGEST = "suggest";
    String SAVING_TIME = "saving_time";
    String KEYWORD = "keyword";
}
