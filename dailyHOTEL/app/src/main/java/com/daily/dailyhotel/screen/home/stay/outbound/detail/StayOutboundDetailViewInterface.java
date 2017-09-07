package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.app.Dialog;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;

import io.reactivex.Observable;

public interface StayOutboundDetailViewInterface extends BaseDialogViewInterface
{
    Observable<Boolean> showRoomList(boolean animation);

    Observable<Boolean> hideRoomList(boolean animation);

    void setStayDetail(StayBookDateTime stayBookDateTime, People people, StayOutboundDetail stayOutboundDetail);

    Observable<Boolean> getSharedElementTransition();

    void setInitializedImage(String url);

    void setInitializedTransLayout(String name, String url);

    void setSharedElementTransitionEnabled(boolean enabled, int gradientType);

    void setBottomButtonLayout(int status);

    void setDetailImageCaption(String caption);

    void setPriceType(StayOutboundDetailPresenter.PriceType priceType);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void scrollTop();
}
