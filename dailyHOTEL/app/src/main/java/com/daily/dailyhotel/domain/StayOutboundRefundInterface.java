package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

import io.reactivex.Observable;

public interface StayOutboundRefundInterface
{
    // Stay Outbound 환불 상세
    Observable<StayOutboundRefundDetail> getStayOutboundRefundDetail(int bookingIndex);

    // Stay Outbound 환불
    Observable<String> getStayOutboundRefund(int bookingIndex, String refundType, String cancelReasonType, String reasons);
}
