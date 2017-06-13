package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayOutboundReceipt;
import com.daily.dailyhotel.entity.StayOutboundRefund;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

import io.reactivex.Observable;

public interface StayOutboundReceiptInterface
{
    // 영수증 화면
    Observable<StayOutboundReceipt> getStayOutboundReceipt(int bookingIndex);

    // 영수증 이메일로 신청
    Observable<Boolean> getStayOutboundEmailReceipt(int bookingIndex);
}
