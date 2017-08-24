package com.daily.dailyhotel.entity;

import java.util.List;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewQuestionItem
{
    public String answerCode;
    public String description;
    public String title;
    private List<ReviewAnswerValue> mAnswerValueList;

    public List<ReviewAnswerValue> getAnswerValueList()
    {
        return mAnswerValueList;
    }

    public void setAnswerValueList(List<ReviewAnswerValue> reviewAnswerValueList)
    {
        mAnswerValueList = reviewAnswerValueList;
    }
}
