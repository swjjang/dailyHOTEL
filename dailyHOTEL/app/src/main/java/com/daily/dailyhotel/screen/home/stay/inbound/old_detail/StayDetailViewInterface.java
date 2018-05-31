package com.daily.dailyhotel.screen.home.stay.inbound.old_detail;

import android.app.Dialog;
import android.view.View;
import android.widget.CheckBox;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.TrueAwards;
import com.daily.dailyhotel.entity.old_StayDetail;

import io.reactivex.Observable;

public interface StayDetailViewInterface extends BaseDialogViewInterface
{
    Observable<Boolean> showRoomList(boolean animation);

    Observable<Boolean> hideRoomList(boolean animation);

    void setStayDetail(StayBookDateTime stayBookDateTime, old_StayDetail stayDetail, int trueReviewCount);

    void setRewardVisible(boolean visible);

    void setRewardNonMember(String titleText, String optionText, int campaignFreeNights, String descriptionText);

    void setRewardMember(String titleText, String optionText, int nights, String descriptionText);

    Observable<Boolean> getSharedElementTransition(int gradientType);

    void setInitializedImage(String url);

    void setInitializedTransLayout(String name, String url);

    void setTransitionVisible(boolean visible);

    void setSharedElementTransitionEnabled(boolean enabled, int gradientType);

    void setBottomButtonLayout(int status);

    void setPriceType(StayDetailPresenter.PriceType priceType);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void scrollTop();

    void setWishCount(int count);

    void setWishSelected(boolean selected);

    void showWishTooltip();

    void hideWishTooltip();

    Observable<Boolean> showWishView(boolean myWish);

    void setTrueVRVisible(boolean visible);

    void showTrueVRDialog(CheckBox.OnCheckedChangeListener checkedChangeListener, View.OnClickListener positiveListener, Dialog.OnDismissListener onDismissListener);

    void startCampaignStickerAnimation();

    void stopCampaignStickerAnimation();

    void showTrueAwardsDialog(TrueAwards trueAwards, Dialog.OnDismissListener onDismissListener);
}
