package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayOutboundReceipt;

import io.reactivex.Observable;

public interface StayOutboundReceiptInterface
{
    // 영수증 화면
    Observable<StayOutboundReceipt> getStayOutboundReceipt(int bookingIndex);

    // 영수증 이메일로 신청
    Observable<String> getStayOutboundEmailReceipt(int bookingIndex, String email);
}
