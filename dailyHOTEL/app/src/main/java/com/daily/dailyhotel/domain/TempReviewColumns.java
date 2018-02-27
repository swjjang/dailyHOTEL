package com.daily.dailyhotel.domain;

import android.provider.BaseColumns;

/**
 * Created by android_sam on 2018. 2. 27..
 */

public interface TempReviewColumns extends BaseColumns
{
    String RESERVATION_INDEX = "reservation_index";
    String SERVICE_TYPE = "service_type";
    String START_DATE = "start_date";
    String END_DATE = "end_date";
    String SCORE_QUESTION = "score_question";
    String PICK_QUESTION = "pick_question";
    String COMMENT = "comment";
}
