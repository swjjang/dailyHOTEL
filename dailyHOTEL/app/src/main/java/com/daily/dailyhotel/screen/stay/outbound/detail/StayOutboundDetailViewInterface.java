package com.daily.dailyhotel.screen.stay.outbound.detail;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.StayOutboundDetail;

public interface StayOutboundDetailViewInterface extends BaseViewInterface
{
    void showRoomList();

    void hideRoomList();

    void setStayDetail(StayOutboundDetail stayOutboundDetail);
}
