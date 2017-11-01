package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RewardHistory;
import com.daily.dailyhotel.entity.RewardHistoryDetail;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class RewardHistoryDetailData
{
    @JsonField(name = "rewardCard")
    public RewardCardData rewardCard;

    @JsonField(name = "histories")
    public List<RewardHistoryData> histories;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public RewardHistoryDetailData()
    {

    }

    public RewardHistoryDetail getRewardHistoryDetail()
    {
        RewardHistoryDetail rewardHistoryDetail = new RewardHistoryDetail();

        rewardHistoryDetail.expiredAt = rewardCard.expiredAt;
        rewardHistoryDetail.rewardStickerCount = rewardCard.rewardStickerCount;

        if (histories != null && histories.size() > 0)
        {
            List<RewardHistory> rewardStickerList = new ArrayList<>();

            for (RewardHistoryData rewardHistoryData : histories)
            {
                rewardStickerList.add(rewardHistoryData.getRewardHistory());
            }

            rewardHistoryDetail.setRewardHistoryList(rewardStickerList);
        }

        if (configurations != null)
        {
            rewardHistoryDetail.activeReward = configurations.activeReward;
        }

        return rewardHistoryDetail;
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
    static class RewardHistoryData
    {
        @JsonField(name = "aggregationId")
        public String aggregationId;

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

        public RewardHistory getRewardHistory()
        {
            RewardHistory rewardHistory = new RewardHistory();

            rewardHistory.aggregationId = aggregationId;
            rewardHistory.couponPrice = couponAmount;
            rewardHistory.expiredStickerCount = expiredStickerCount;

            rewardHistory.type = RewardHistory.Type.valueOf(historyType);

            switch (rewardHistory.type)
            {
                case EVENT_STICKER:
                case PUBLISHED_COUPON:
                case RESERVATION_STICKER:
                    rewardHistory.date = createdAt;
                    break;

                case EXPIRED_STICKER:
                    rewardHistory.date = expiredAt;
                    break;
            }

            rewardHistory.reservationName = reservationName;
            rewardHistory.nights = roomNights;
            rewardHistory.serviceType = RewardHistory.ServiceType.valueOf(serviceType);

            return rewardHistory;
        }
    }
}
