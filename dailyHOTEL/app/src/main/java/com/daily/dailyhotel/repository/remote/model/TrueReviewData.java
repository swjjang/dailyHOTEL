package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.TrueReview;
import com.daily.dailyhotel.entity.TrueReviewReply;

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

    @JsonField(name = "itemName")
    public String itemName;

    public TrueReviewData()
    {
    }

    public TrueReview getTrueReview()
    {
        TrueReview trueReview = new TrueReview();

        trueReview.email = email;
        trueReview.comment = comment;
        trueReview.createdAt = createdAt;
        trueReview.averageScore = avgScore;
        trueReview.productName = itemName;

        if (reviewReply != null)
        {
            TrueReviewReply trueReviewReply = reviewReply.getTrueReviewReply();

            trueReview.setReply(trueReviewReply);
        }

        return trueReview;
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

        public TrueReviewReply getTrueReviewReply()
        {
            TrueReviewReply trueReviewReply = new TrueReviewReply();

            trueReviewReply.replier = replier;
            trueReviewReply.comment = reply;
            trueReviewReply.repliedAt = repliedAt;

            return trueReviewReply;
        }
    }
}
