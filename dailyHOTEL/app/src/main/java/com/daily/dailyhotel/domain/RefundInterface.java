package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.RefundPolicy;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

import io.reactivex.Observable;

public interface RefundInterface
{
    // Stay Outbound 환불 상세
    Observable<StayOutboundRefundDetail> getStayOutboundRefundDetail(int bookingIndex);

    // Stay Outbound 환불
    Observable<String> getStayOutboundRefund(int bookingIndex, String refundType, String cancelReasonType, String reasons);

    // 환불
    Observable<String> getRefund(String aggregationId, int reservationIndex, String reason, String serviceType);

    // 계좌이체 환불
    Observable<String> getRefund(String aggregationId, int reservationIndex, String reason, String serviceType//
        , String accountHolder, String accountNumber, String bankCode);

    Observable<RefundPolicy> getStayRefundPolicy(int reservationIndex, String transactionType);
}
