package com.daily.dailyhotel.screen.stay.outbound.detail;

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

    void setBottomButtonLayout(int status);

    void setDetailImageCaption(String caption);

    void setPriceType(StayOutboundDetailPresenter.PriceType priceType);
}
