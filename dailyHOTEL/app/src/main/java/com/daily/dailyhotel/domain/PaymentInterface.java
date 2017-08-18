package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.DomesticGuest;
import com.daily.dailyhotel.entity.OverseasGuest;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.StayPayment;
import com.daily.dailyhotel.entity.StayRefundPolicy;

import java.util.List;

import io.reactivex.Observable;

public interface PaymentInterface
{
    // Stay Outbound 결제 정보를 가져온다.
    Observable<StayOutboundPayment> getStayOutboundPayment(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people);

    Observable<StayPayment> getStayPayment(StayBookDateTime stayBookDateTime, int roomIndex);

    // 간편 결제 카드 리스트를 얻어온다.
    Observable<List<Card>> getEasyCardList();

    Observable<PaymentResult> getStayOutboundPaymentTypeEasy(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people//
        , boolean usedBonus, int bonus, OverseasGuest guest, int totalPrice, String billingKey);

    Observable<PaymentResult> getStayOutboundPaymentTypeBonus(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people//
        , boolean usedBonus, int bonus, OverseasGuest guest, int totalPrice);

    Observable<PaymentResult> getStayPaymentTypeEasy(StayBookDateTime stayBookDateTime, int roomIndex//
        , boolean usedBonus, int bonus, boolean usedCoupon, String couponCode, DomesticGuest guest, int totalPrice, String transportation, String billingKey);

    Observable<PaymentResult> getStayPaymentTypeBonus(StayBookDateTime stayBookDateTime, int roomIndex//
        , boolean usedBonus, int bonus, boolean usedCoupon, String couponCode, DomesticGuest guest, int totalPrice, String transportation);

    Observable<StayRefundPolicy> getStayRefundPolicy(StayBookDateTime stayBookDateTime, int stayIndex, int roomIndex);
}
