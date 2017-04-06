package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

import java.util.List;

@JsonObject
public class PlaceReviews
{
    @JsonField(name = "content")
    public List<PlaceReview> content;

    @JsonField(name = "totalElements")
    public int totalElements;

    @JsonField(name = "totalPages")
    public int totalPages;

    @JsonField(name = "numberOfElements")
    public int numberOfElements;

    @JsonField(name = "number")
    public int page;

    @JsonIgnore
    public int loadingPage;

    public PlaceReviews()
    {
    }

    @OnJsonParseComplete
    void onParseComplete()
    {
        page++;
        loadingPage = 0;
    }
}
