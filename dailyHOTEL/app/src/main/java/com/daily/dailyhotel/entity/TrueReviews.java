package com.daily.dailyhotel.entity;

import java.util.List;

public class TrueReviews
{
    public int totalElements;
    public int totalPages;
    public int numberOfElements;
    public int page;

    private TrueReview mPrimaryReview;
    private List<TrueReview> mTrueReviewList;

    public TrueReviews()
    {
    }

    public TrueReview getPrimaryReview()
    {
        return mPrimaryReview;
    }

    public void setPrimaryReview(TrueReview primaryReview)
    {
        mPrimaryReview = primaryReview;
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
