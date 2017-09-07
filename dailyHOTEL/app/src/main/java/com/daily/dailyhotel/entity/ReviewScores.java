package com.daily.dailyhotel.entity;

import java.util.List;

public class ReviewScores
{
    public int reviewScoreTotalCount;
    private List<ReviewScore> mReviewScoreList;

    public ReviewScores()
    {
    }

    public void setReviewScoreList(List<ReviewScore> reviewScoreList)
    {
        mReviewScoreList = reviewScoreList;
    }

    public List<ReviewScore> getReviewScoreList()
    {
        return mReviewScoreList;
    }
}
