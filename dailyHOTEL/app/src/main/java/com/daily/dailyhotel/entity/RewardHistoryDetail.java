package com.daily.dailyhotel.entity;

import java.util.List;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class RewardHistoryDetail
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
