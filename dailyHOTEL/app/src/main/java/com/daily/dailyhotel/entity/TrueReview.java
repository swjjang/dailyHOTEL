package com.daily.dailyhotel.entity;

import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class TrueReview
{
    public String email;
    public String comment;
    public String createdAt; // ISO-8601
    public float averageScore;
    public boolean more;

    private TrueReviewReply mReply;

    public TrueReview()
    {
    }

    public TrueReviewReply getReply()
    {
        return mReply;
    }

    public void setReply(TrueReviewReply reply)
    {
        mReply = reply;
    }
}
