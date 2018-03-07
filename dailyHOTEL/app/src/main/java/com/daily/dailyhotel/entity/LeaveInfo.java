package com.daily.dailyhotel.entity;

import java.util.List;

/**
 * Created by android_sam on 2018. 2. 19..
 */

public class LeaveInfo
{
    public int msgCode;

    public String msg;

    public boolean activeReward;

    public List<LeaveReason> leaveReasonList;

    public String policy;

    public List<String> policyList;

    //    public RewardCardData rewardCard;

    public int rewardStickerCount;
}
