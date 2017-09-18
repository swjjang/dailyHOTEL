package com.daily.dailyhotel.entity;

import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class TrueReview
{
    public String email;
    public String comment;
    public String createdAt; // ISO-8601
    public float avgScore;
    public TrueReviewReplay reviewReply;

    public TrueReview()
    {
    }
}
