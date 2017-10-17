package com.daily.dailyhotel.screen.mydaily.reward;

import com.daily.base.BaseDialogViewInterface;

import io.reactivex.Observable;

public interface RewardInterface extends BaseDialogViewInterface
{
    void setLoginVisible(boolean visible);

    void setTitleMessage(String message);

    void setDescriptionMessage(String message);

    void setSticker(int nights, boolean visible, boolean enabled);

    void setIssueCouponCount(int count);

    void setRewardHistoryEnabled(boolean enabled);

    void setGuideTitleMessage(String message);

    void setGuideDescriptionMessage(String message);

    void setNotificationVisible(boolean visible);

    void setIssueCouponVisible(boolean visible);

    void setIssueCouponEnabled(boolean enabled);

    Observable<Boolean> openIssueCouponAnimation();

    Observable<Boolean> closeIssueCouponAnimation();

    boolean isOpenedIssueCoupon();
}
