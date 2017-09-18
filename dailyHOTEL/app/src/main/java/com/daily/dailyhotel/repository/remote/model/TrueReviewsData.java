package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.entity.ReviewAnswerValue;
import com.daily.dailyhotel.entity.ReviewItem;
import com.daily.dailyhotel.entity.ReviewQuestionItem;
import com.daily.dailyhotel.entity.TrueReview;
import com.daily.dailyhotel.entity.TrueReviews;
import com.twoheart.dailyhotel.network.model.PlaceReview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JsonObject
public class TrueReviewsData
{
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

        return trueReviews;
    }
}
