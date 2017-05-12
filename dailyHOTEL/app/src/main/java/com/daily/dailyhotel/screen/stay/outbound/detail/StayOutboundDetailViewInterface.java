package com.daily.dailyhotel.screen.stay.outbound.detail;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;

import io.reactivex.Observable;

public interface StayOutboundDetailViewInterface extends BaseViewInterface
{
    void showRoomList();

    void hideRoomList();

    void setStayDetail(StayBookDateTime stayBookDateTime, StayOutboundDetail stayOutboundDetail);

    Observable<Boolean> getSharedElementTransition();
}
