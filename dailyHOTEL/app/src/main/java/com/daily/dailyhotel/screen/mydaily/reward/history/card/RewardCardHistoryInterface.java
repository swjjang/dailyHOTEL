package com.daily.dailyhotel.screen.mydaily.reward.history.card;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.RewardCardHistory;

import java.util.List;

public interface RewardCardHistoryInterface extends BaseDialogViewInterface
{
    void setRewardCardHistoryList(List<ObjectItem> rewardCardHistoryList);
}
