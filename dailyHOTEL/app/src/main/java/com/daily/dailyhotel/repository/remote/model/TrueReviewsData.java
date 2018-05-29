package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.TrueReview;
import com.daily.dailyhotel.entity.TrueReviews;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class TrueReviewsData
{
    @JsonField(name = "primaryReview")
    public TrueReviewData primaryReview;

    @JsonField(name = "content")
    public List<TrueReviewData> content;

    @JsonField(name = "totalElements")
    public int totalElements;

    @JsonField(name = "totalPages")
    public int totalPages;

    @JsonField(name = "numberOfElements")
    public int numberOfElements;

    @JsonField(name = "number")
    public int page;

    public TrueReviewsData()
    {
    }

    public TrueReviews getTrueReviews()
    {
        TrueReviews trueReviews = new TrueReviews();

        trueReviews.totalElements = totalElements;
        trueReviews.totalPages = totalPages;
        trueReviews.numberOfElements = numberOfElements;
        trueReviews.page = page + 1;

        if (page == 0 && primaryReview != null)
        {
            trueReviews.setPrimaryReview(primaryReview.getTrueReview());
        }

        List<TrueReview> trueReviewList = new ArrayList<>();

        if (content != null)
        {
            for (TrueReviewData trueReviewData : content)
            {
                trueReviewList.add(trueReviewData.getTrueReview());
            }
        }

        trueReviews.setTrueReviewList(trueReviewList);

        return trueReviews;
    }
}
