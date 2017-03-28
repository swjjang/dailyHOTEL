package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class PlaceReviews
{
    @JsonField
    public List<PlaceReview> content;

    @JsonField
    public int totalElements;

    @JsonField
    public int totalPages;

    @JsonField
    public int numberOfElements;

    @JsonField(name = "number")
    public int page;

    public PlaceReviews()
    {
    }
}
