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

    private List<RewardSticker> mRewardStickerList;

    public void setRewardStickerList(List<RewardSticker> rewardStickerList)
    {
        mRewardStickerList = rewardStickerList;
    }

    public List<RewardSticker> getRewardStickerList()
    {
        return mRewardStickerList;
    }
}
