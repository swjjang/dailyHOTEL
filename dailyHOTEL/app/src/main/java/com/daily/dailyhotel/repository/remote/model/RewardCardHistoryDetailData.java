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
    @JsonField(name = "rewardCards")
    public List<RewardCardHistoryData> rewardCards;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public RewardCardHistoryDetailData()
    {

    }

    public RewardCardHistoryDetail getRewardCardHistoryDetail()
    {
        RewardCardHistoryDetail rewardCardHistoryDetail = new RewardCardHistoryDetail();

        if (rewardCards != null && rewardCards.size() > 0)
        {
            List<RewardCardHistory> rewardCardHistoryList = new ArrayList<>();

            for (RewardCardHistoryData rewardCardHistoryData : rewardCards)
            {
                rewardCardHistoryList.add(rewardCardHistoryData.getRewardCardHistory());
            }

            rewardCardHistoryDetail.setRewardCardHistoryList(rewardCardHistoryList);
        }

        if (configurations != null)
        {
            rewardCardHistoryDetail.activeReward = configurations.activeReward;
        }

        return rewardCardHistoryDetail;
    }

    @JsonObject
    static class RewardCardHistoryData
    {
        @JsonField(name = "aggregationId")
        public String aggregationId;

        @JsonField(name = "reservationIdx")
        public int reservationIdx;

        @JsonField(name = "couponAmount")
        public int couponAmount;

        @JsonField(name = "expiredStickerCount")
        public int expiredStickerCount;

        @JsonField(name = "createdAt")
        public String createdAt;

        @JsonField(name = "expiredAt")
        public String expiredAt;

        @JsonField(name = "historyType")
        public String historyType;

        @JsonField(name = "reservationName")
        public String reservationName;

        @JsonField(name = "roomnights")
        public int roomNights;

        @JsonField(name = "serviceType")
        public String serviceType;

        @JsonField(name = "rewardStickerType")
        public String rewardStickerType;

        public RewardCardHistory getRewardCardHistory()
        {
            RewardCardHistory rewardCardHistory = new RewardCardHistory();


            return rewardCardHistory;
        }
    }
}
