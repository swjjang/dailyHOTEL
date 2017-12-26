package com.daily.dailyhotel.entity;

import java.util.List;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class RewardDetail extends Configurations
{
    public int availableRewardCouponCount;
    public int rewardStickerCount;
    public String expiredAt;
    public boolean hasRewardHistory;
    public boolean hasRewardCardHistory;

    private List<String> mRewardStickerList;

    public void setRewardStickerList(List<String> rewardStickerList)
    {
        mRewardStickerList = rewardStickerList;
    }

    public List<String> getRewardStickerList()
    {
        return mRewardStickerList;
    }
}
