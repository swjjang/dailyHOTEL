package com.daily.dailyhotel.screen.mydaily.reward;

import android.util.Pair;

import com.daily.base.BaseDialogViewInterface;

import java.util.List;

import io.reactivex.Observable;

public interface RewardInterface extends BaseDialogViewInterface
{
    void setLoginVisible(boolean visible);

    void setTitleMessage(String message);

    void setDescriptionMessage(String message);

    void setStickerList(List<String> stickerList);

    void setCampaignFreeStickerCount(int count);

    void startCampaignStickerAnimation();

    void stopCampaignStickerAnimation();

    void setStickerValidityVisible(boolean visible);

    void setStickerValidityText(String message);

    void setIssueCouponCount(int count);

    void setRewardHistoryEnabled(boolean enabled);

    void setGuideTitleMessage(String message);

    void setGuideDescriptionMessage(String message);

    void setOthersGuideList(List<Pair<String, String>> guideList);

    void setNotificationVisible(boolean visible);

    void setIssueCouponVisible(boolean visible);

    void setIssueCouponEnabled(boolean enabled);

    Observable<Boolean> openIssueCouponAnimation();

    Observable<Boolean> closeIssueCouponAnimation();

    boolean isOpenedIssueCoupon();

    void setIssueCouponAnimation(boolean visible);

    void setRewardCardHistoryVisible(boolean visible);
}
