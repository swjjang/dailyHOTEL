package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.Guest;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;

import java.util.List;

import io.reactivex.Observable;

public interface PaymentInterface
{
    // Stay Outbound 결제 정보를 가져온다.
    Observable<StayOutboundPayment> getStayOutboundPayment(StayBookDateTime stayBookDateTime, int index//
    , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people);

    // 간편 결제 카드 리스트를 얻어온다.
    Observable<List<Card>> getSimpleCardList();

    Observable<PaymentResult> getPaymentTypeEasy(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people//
        , boolean usedBonus, Guest guest, int totalPrice);

    Observable<PaymentResult> getPaymentTypeBonus(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people//
        , boolean usedBonus, Guest guest, int totalPrice);
}
