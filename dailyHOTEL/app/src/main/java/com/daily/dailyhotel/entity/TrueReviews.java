package com.daily.dailyhotel.entity;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.repository.remote.model.TrueReviewData;

import java.util.List;

public class TrueReviews
{
    public List<TrueReviewData> content;
    public int totalElements;
    public int totalPages;
    public int numberOfElements;
    public int page;

    public TrueReviews()
    {
    }
}
