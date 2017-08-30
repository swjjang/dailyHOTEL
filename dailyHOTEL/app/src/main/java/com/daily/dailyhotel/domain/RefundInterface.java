package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Refund;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

import io.reactivex.Observable;

public interface RefundInterface
{
    // Stay Outbound 환불 상세
    Observable<StayOutboundRefundDetail> getStayOutboundRefundDetail(int bookingIndex);

    // Stay Outbound 환불
    Observable<String> getStayOutboundRefund(int bookingIndex, String refundType, String cancelReasonType, String reasons);

    // 환불
    Observable<Refund> getRefund(String aggregationId, String bankAccount, String bankCode);
}
