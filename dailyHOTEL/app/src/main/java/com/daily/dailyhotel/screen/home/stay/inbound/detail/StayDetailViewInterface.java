package com.daily.dailyhotel.screen.home.stay.inbound.detail;

import android.app.Dialog;
import android.view.View;
import android.widget.CheckBox;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDetail;

import io.reactivex.Observable;

public interface StayDetailViewInterface extends BaseDialogViewInterface
{
    Observable<Boolean> showRoomList(boolean animation);

    Observable<Boolean> hideRoomList(boolean animation);

    void setStayDetail(StayBookDateTime stayBookDateTime, StayDetail stayDetail, int trueReviewCount);

    Observable<Boolean> getSharedElementTransition(int gradientType);

    void setInitializedImage(String url);

    void setInitializedTransLayout(String name, String url);

    void setTransitionVisible(boolean visible);

    void setSharedElementTransitionEnabled(boolean enabled, int gradientType);

    void setBottomButtonLayout(int status);

    void setDetailImageCaption(String caption);

    void setPriceType(StayDetailPresenter.PriceType priceType);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void scrollTop();

    void setWishCount(int count);

    void setWishSelected(boolean selected);

    Observable<Boolean> showWishView(boolean myWish);

    void setTrueVRVisible(boolean visible);

    void showTrueVRDialog(CheckBox.OnCheckedChangeListener checkedChangeListener, View.OnClickListener positiveListener
        , View.OnClickListener negativeListener, Dialog.OnCancelListener onCancelListener, Dialog.OnDismissListener onDismissListener);
}
