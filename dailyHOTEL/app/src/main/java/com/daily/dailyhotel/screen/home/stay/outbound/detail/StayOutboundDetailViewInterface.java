package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.app.Dialog;

import com.daily.dailyhotel.base.BaseBlurViewInterface;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundRoom;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public interface StayOutboundDetailViewInterface extends BaseBlurViewInterface
{
    Observable<Boolean> showRoomList(boolean animation);

    Observable<Boolean> hideRoomList(boolean animation);

    void setStayDetail(StayBookDateTime stayBookDateTime, People people, StayOutboundDetail stayOutboundDetail);

    void updateBookDateTime(StayBookDateTime stayBookDateTime);

    void updatePeople(People people);

    void setRewardVisible(boolean visible, boolean hasRecommandList);

    void setRewardNonMember(String titleText, String optionText, int campaignFreeNights, String descriptionText);

    void setRewardMember(String titleText, String optionText, int nights, String descriptionText);

    Observable<Boolean> getSharedElementTransition(int gradientType);

    void setInitializedImage(String url);

    void setInitializedTransLayout(String name, String englishName, String url);

    void setTransitionVisible(boolean visible);

    void setSharedElementTransitionEnabled(boolean enabled, int gradientType);

    void setBottomButtonLayout(int status);

    void setPriceType(StayOutboundDetailPresenter.PriceType priceType);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void scrollTop();

    void setWishCount(int count);

    void setWishSelected(boolean selected);

    void showWishTooltip();

    void hideWishTooltip();

    void setRecommendAroundVisible(boolean visible);

    void setRecommendAroundList(ArrayList<CarouselListItem> list, StayBookDateTime stayBookDateTime);

    void startCampaignStickerAnimation();

    void stopCampaignStickerAnimation();

    void setRoomList(StayBookDateTime stayBookDateTime, List<StayOutboundRoom> roomList);

    void setRoomActiveReward(boolean activeReward);
}
