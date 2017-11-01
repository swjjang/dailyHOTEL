package com.daily.dailyhotel.entity;

import java.util.List;

public class RewardHistoryDetail extends Configurations
{
    public int rewardStickerCount;
    public String expiredAt;

    private List<RewardHistory> mRewardHistoryList;

    public void setRewardHistoryList(List<RewardHistory> rewardHistoryList)
    {
        this.mRewardHistoryList = rewardHistoryList;
    }

    public List<RewardHistory> getRewardHistoryList()
    {
        return mRewardHistoryList;
    }
}
