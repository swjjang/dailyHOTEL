package com.daily.dailyhotel.entity;

import java.util.List;

public class RewardCardHistoryDetail extends Configurations
{
    private List<RewardCardHistory> mRewardCardHistoryList;

    public void setRewardCardHistoryList(List<RewardCardHistory> rewardCardHistoryList)
    {
        mRewardCardHistoryList = rewardCardHistoryList;
    }

    public List<RewardCardHistory> getRewardCardHistoryList()
    {
        return mRewardCardHistoryList;
    }
}
