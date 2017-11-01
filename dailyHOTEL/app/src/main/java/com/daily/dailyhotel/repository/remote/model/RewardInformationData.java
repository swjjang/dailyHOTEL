package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RewardInformation;

@JsonObject
public class RewardInformationData
{
    @JsonField(name = "expiredAt")
    public String expiredAt;

    @JsonField(name = "rewardStickerCount")
    public int rewardStickerCount;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public RewardInformationData()
    {

    }

    public RewardInformation getRewardInformation()
    {
        RewardInformation rewardInformation = new RewardInformation();
        rewardInformation.rewardStickerCount = rewardStickerCount;

        if (configurations != null)
        {
            rewardInformation.activeReward = configurations.activeReward;
        }

        return rewardInformation;
    }
}
