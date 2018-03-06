package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.LeaveInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2018. 2. 19..
 */
@JsonObject
public class LeaveInfoData
{
    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    @JsonField(name = "leaveReasons")
    public List<LeaveReasonData> leaveReasonList;

    @JsonField(name = "policy")
    public String policy;

    @JsonField(name = "rewardCard")
    public RewardCardData rewardCard;

    public LeaveInfo getLeaveInfo()
    {
        LeaveInfo info = new LeaveInfo();

        if (configurations != null)
        {
            info.activeReward = configurations.activeReward;
        }

        info.leaveReasonList = new ArrayList<>();
        if (leaveReasonList != null && leaveReasonList.size() > 0)
        {
            for (LeaveReasonData data : leaveReasonList)
            {
                info.leaveReasonList.add(data.getLeaveReason());
            }
        }

        info.policy = this.policy;
        info.policyList = getPolicyList();

        if (rewardCard == null)
        {
            info.rewardStickerCount = 0;
        } else
        {
            info.rewardStickerCount = rewardCard.rewardStickerCount;
        }

        return info;
    }

    private List<String> getPolicyList()
    {
        List<String> list = new ArrayList<>();

        if (DailyTextUtils.isTextEmpty(policy) == true)
        {
            return list;
        }

        String[] split = policy.split("-");
        for (String policyString : split)
        {
            if (DailyTextUtils.isTextEmpty(policyString) == true)
            {
                continue;
            }

            if (policyString.endsWith("\n") == true)
            {
                if (policyString.length() > 2)
                {
                    policyString = policyString.substring(0, policyString.length() - 2);
                } else
                {
                    policyString = "";
                }
            }

            list.add(policyString.trim());
        }

        return list;
    }
}
