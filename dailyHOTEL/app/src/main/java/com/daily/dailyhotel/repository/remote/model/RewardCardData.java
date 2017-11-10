package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class RewardCardData
{
    @JsonField(name = "expiredAt")
    public String expiredAt;

    @JsonField(name = "rewardStickerCount")
    public int rewardStickerCount;

    public RewardCardData()
    {

    }
}
