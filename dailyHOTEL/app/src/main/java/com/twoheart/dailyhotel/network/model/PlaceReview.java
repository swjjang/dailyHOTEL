package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class PlaceReview
{
    @JsonField(name = "userId")
    public String email;

    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "createdAt")
    public String createdAt; // ISO-8601

    @JsonIgnore
    public boolean isMore; // 더 읽어보기 체크

    public PlaceReview()
    {
    }
}
