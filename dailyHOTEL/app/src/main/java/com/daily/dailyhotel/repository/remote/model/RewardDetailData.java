package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RewardDetail;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class RewardDetailData
{
    @JsonField(name = "availableRewardCouponCount")
    public int availableRewardCouponCount;

    @JsonField(name = "hasRewardHistory")
    public boolean hasRewardHistory;

    @JsonField(name = "hasRewardCardHistory")
    public boolean hasRewardCardHistory;

    @JsonField(name = "rewardCard")
    public RewardCardData rewardCard;

    @JsonField(name = "rewardStickers")
    public List<RewardStickersData> rewardStickers;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public RewardDetailData()
    {

    }

    public RewardDetail getRewardDetail()
    {
        RewardDetail rewardDetail = new RewardDetail();

        rewardDetail.availableRewardCouponCount = availableRewardCouponCount;
        rewardDetail.hasRewardHistory = hasRewardHistory;
        rewardDetail.hasRewardCardHistory = hasRewardCardHistory;

        if (rewardCard != null)
        {
            rewardDetail.expiredAt = rewardCard.expiredAt;
            rewardDetail.rewardStickerCount = rewardCard.rewardStickerCount;
        }

        if (rewardStickers != null && rewardStickers.size() > 0)
        {
            List<String> rewardStickerList = new ArrayList<>();

            for (RewardStickersData rewardStickersData : rewardStickers)
            {
                rewardStickerList.add(rewardStickersData.rewardStickerType);
            }

            rewardDetail.setRewardStickerList(rewardStickerList);
        }

        if (configurations != null)
        {
            rewardDetail.activeReward = configurations.activeReward;
        }

        return rewardDetail;
    }

    @JsonObject
    static class RewardCardData
    {
        @JsonField(name = "expiredAt")
        public String expiredAt;

        @JsonField(name = "rewardStickerCount")
        public int rewardStickerCount;
    }

    @JsonObject
    static class RewardStickersData
    {
        @JsonField(name = "rewardStickerType")
        public String rewardStickerType;
    }
}
