package com.daily.dailyhotel.screen.stay.outbound.detail;

import android.app.Dialog;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;

import io.reactivex.Observable;

public interface StayOutboundDetailViewInterface extends BaseViewInterface
{
    Observable<Boolean> showRoomList(boolean animation);

    Observable<Boolean> hideRoomList(boolean animation);

    void setStayDetail(StayBookDateTime stayBookDateTime, People people, StayOutboundDetail stayOutboundDetail);

    Observable<Boolean> getSharedElementTransition();

    void setInitializedImage(String url);

    void setInitializedTransLayout(String name, String url, boolean callFromMap);

    void setSharedElementTransitionEnabled(boolean enabled);

    void setBottomButtonLayout(int status);

    void setDetailImageCaption(String caption);

    void setPriceType(StayOutboundDetailPresenter.PriceType priceType);

    void setPeopleText(String peopleText);

    void setCalendarText(String peopleText);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showNavigatorDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void scrollTop();
}
