package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.StayOutboundReceipt;

import io.reactivex.Observable;

public interface StayOutboundReceiptInterface
{
    // 영수증 화면
    Observable<StayOutboundReceipt> getReceipt(Context context, int bookingIndex);

    // 영수증 이메일로 신청
    Observable<String> getEmailReceipt(Context context, int bookingIndex, String email);
}
