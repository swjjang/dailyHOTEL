package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class TripAdvisorData
{
    @JsonField(name = "tripAdvisorRating")
    public float tripAdvisorRating;

    @JsonField(name = "tripAdvisorReviewCount")
    public int tripAdvisorReviewCount;

    public TripAdvisorData()
    {

    }
}
