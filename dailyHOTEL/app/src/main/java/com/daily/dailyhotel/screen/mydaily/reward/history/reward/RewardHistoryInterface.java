package com.daily.dailyhotel.screen.mydaily.reward.history.reward;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;

import java.util.List;

public interface RewardHistoryInterface extends BaseDialogViewInterface
{
    void setStickerValidityText(String text);

    void setRewardHistoryList(List<ObjectItem> list);
}
