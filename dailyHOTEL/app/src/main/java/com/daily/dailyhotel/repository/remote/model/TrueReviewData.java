package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class TrueReviewData
{
    @JsonField(name = "userId")
    public String email;

    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "createdAt")
    public String createdAt; // ISO-8601

    @JsonField(name = "avgScore")
    public float avgScore;

    @JsonField(name = "reviewReply")
    public TrueReviewReplyData reviewReply;

    public TrueReviewData()
    {
    }

    @JsonObject
    static class TrueReviewReplyData
    {
        @JsonField(name = "replier")
        public String replier;

        @JsonField(name = "reply")
        public String reply;

        @JsonField(name = "repliedAt")
        public String repliedAt; // ISO-8601
    }
}
