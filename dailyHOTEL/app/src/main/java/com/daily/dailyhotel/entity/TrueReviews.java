package com.daily.dailyhotel.entity;

import java.util.List;

public class TrueReviews
{
    public int totalElements;
    public int totalPages;
    public int numberOfElements;
    public int page;

    private List<TrueReview> mTrueReviewList;

    public TrueReviews()
    {
    }

    public List<TrueReview> getTrueReviewList()
    {
        return mTrueReviewList;
    }

    public void setTrueReviewList(List<TrueReview> trueReviewList)
    {
        mTrueReviewList = trueReviewList;
    }
}
