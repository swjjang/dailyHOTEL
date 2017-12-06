package com.daily.dailyhotel.screen.booking.detail.stay;

import android.app.Dialog;
import android.location.Location;
import android.widget.ScrollView;

import com.daily.dailyhotel.base.BaseBlurViewInterface;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.entity.RefundPolicy;
import com.daily.dailyhotel.entity.StayBookingDetail;

import java.util.ArrayList;

import io.reactivex.Observable;

public interface StayBookingDetailInterface extends BaseBlurViewInterface
{
    void setBookingDetailToolbar();

    void setBookingDetailMapToolbar();

    void setBookingDetail(StayBookingDetail stayBookingDetail);

    void setRemindDate(String commonDateTime, StayBookingDetail stayBookingDetail);

    void setTimeInformation(StayBookingDetail stayBookingDetail);

    void setHiddenBookingVisible(int bookingState);

    void setReviewButtonLayout(String reviewStatusType);

    void setRefundPolicyInformation(boolean isVisibleRefundPolicy, boolean readyForRefund, RefundPolicy refundPolicy);

    Observable<Long> getLocationAnimation();

    void setMyLocation(Location location);

    boolean isExpandedMap();

    Observable<Boolean> expandMap(double latitude, double longitude);

    Observable<Boolean> collapseMap();

    void showConciergeDialog(String frontPhone1, String frontPhone2, String currentDateTime, Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void showRefundCallDialog(Dialog.OnDismissListener listener);

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  고메  추천  ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    void setRecommendGourmetLayoutVisible(boolean isVisible);

    ArrayList<CarouselListItem> getRecommendGourmetData();

    void setRecommendGourmetData(ArrayList<CarouselListItem> list);

    void setRecommendGourmetButtonAnimation(boolean isVisible);

    void onAfterScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt);

    void setDepositStickerCardVisible(boolean visible);

    void setDepositStickerCard(String titleText, int nights);
}
