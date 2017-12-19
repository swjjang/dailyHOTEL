package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RewardCardHistory;
import com.daily.dailyhotel.entity.RewardCardHistoryDetail;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class RewardCardHistoryDetailData
{
    @JsonField(name = "cardDetails")
    public List<CardDetailsData> cardDetails;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public RewardCardHistoryDetailData()
    {

    }

    public RewardCardHistoryDetail getRewardCardHistoryDetail()
    {
        RewardCardHistoryDetail rewardCardHistoryDetail = new RewardCardHistoryDetail();

        List<RewardCardHistory> rewardCardHistoryList = new ArrayList<>();

        if (cardDetails != null && cardDetails.size() > 0)
        {
            for (CardDetailsData cardDetailsData : cardDetails)
            {
                rewardCardHistoryList.add(cardDetailsData.getRewardCardHistory());
            }
        }

        rewardCardHistoryDetail.setRewardCardHistoryList(rewardCardHistoryList);

        if (configurations != null)
        {
            rewardCardHistoryDetail.activeReward = configurations.activeReward;
        }

        return rewardCardHistoryDetail;
    }

    @JsonObject
    static class CardDetailsData
    {
        @JsonField(name = "rewardCard")
        public RewardCardData rewardCard;

        @JsonField(name = "rewardCouponPublishedAt")
        public String rewardCouponPublishedAt;

        @JsonField(name = "rewardStickerType")
        public String rewardStickerType;

        @JsonField(name = "rewardStickers")
        public List<RewardStickersData> rewardStickers;

        public RewardCardHistory getRewardCardHistory()
        {
            RewardCardHistory rewardCardHistory = new RewardCardHistory();

            if (rewardCard != null)
            {
                rewardCardHistory.createdAtDateTime = rewardCard.createdAt;
                rewardCardHistory.rewardStickerCount = rewardCard.rewardStickerCount;
            }

            rewardCardHistory.rewardCouponPublishedAtDateTime = rewardCouponPublishedAt;

            List<String> stickerList = new ArrayList<>();

            if (rewardStickers != null && rewardStickers.size() > 0)
            {
                for (RewardStickersData rewardStickersData : rewardStickers)
                {
                    stickerList.add(rewardStickersData.rewardStickerType);
                }
            }

            rewardCardHistory.setStickerTypeList(stickerList);

            return rewardCardHistory;
        }
    }

    @JsonObject
    static class RewardCardData
    {
        @JsonField(name = "createdAt")
        public String createdAt;

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
