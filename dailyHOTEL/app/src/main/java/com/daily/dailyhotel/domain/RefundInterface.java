package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.Bank;
import com.daily.dailyhotel.entity.OldRefund;
import com.daily.dailyhotel.entity.RefundPolicy;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

import java.util.List;

import io.reactivex.Observable;

public interface RefundInterface
{
    // Stay Outbound 환불 상세
    Observable<StayOutboundRefundDetail> getStayOutboundRefundDetail(Context context, int bookingIndex);

    // Stay Outbound 환불
    Observable<String> getStayOutboundRefund(Context context, int bookingIndex, String refundType, String cancelReasonType, String reasons);

    // 환불
    Observable<String> getRefund(String aggregationId, int reservationIndex, String reason, String serviceType);

    // 계좌이체 환불
    Observable<String> getRefund(String aggregationId, int reservationIndex, String reason, String serviceType//
        , String accountHolder, String accountNumber, String bankCode);

    // 기존 reservationIndex 용 환불 - 계좌이체 포함
    @Deprecated
    Observable<OldRefund> getRefund(int hotelIdx, String dateCheckIn, String transactionType, int hotelReservationIdx//
        , String reasonCancel, String accountHolder, String bankAccount, String bankCode);

    Observable<RefundPolicy> getStayRefundPolicy(int reservationIndex, String transactionType);

    // 은행 목록
    Observable<List<Bank>> getBankList();
}
