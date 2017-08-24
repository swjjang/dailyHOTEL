package com.daily.dailyhotel.entity;

import java.util.List;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class Review
{
    public boolean requiredCommentReview;
    public int reserveIdx;
    public ReviewItem mReviewItem;
    public List<ReviewQuestionItem> mReviewPickQuestionList;
    public List<ReviewQuestionItem> mReviewScoreQuestionList;

    public void setReviewItem(ReviewItem reviewItem)
    {
        mReviewItem = reviewItem;
    }

    public ReviewItem getReviewItem()
    {
        return mReviewItem;
    }

    public void setReviewPickQuestionList(List<ReviewQuestionItem> reviewPickQuestionList)
    {
        mReviewPickQuestionList = reviewPickQuestionList;
    }

    public List<ReviewQuestionItem> getReviewPickQuestionList()
    {
        return mReviewPickQuestionList;
    }

    public void setReviewScoreQuestionList(List<ReviewQuestionItem> reviewScoreQuestionList)
    {
        mReviewScoreQuestionList = reviewScoreQuestionList;
    }

    public List<ReviewQuestionItem> getReviewScoreQuestionList()
    {
        return mReviewScoreQuestionList;
    }
}
