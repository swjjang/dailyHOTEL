package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.GourmetPayment;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.StayPayment;
import com.daily.dailyhotel.entity.StayRefundPolicy;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;

public interface PaymentInterface
{
    // Stay Outbound 결제 정보를 가져온다.
    Observable<StayOutboundPayment> getStayOutboundPayment(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people, String vendorType);

    Observable<StayPayment> getStayPayment(StayBookDateTime stayBookDateTime, int roomIndex);

    Observable<GourmetPayment> getGourmetPayment(GourmetCart gourmetCart);

    // 간편 결제 카드 리스트를 얻어온다.
    Observable<List<Card>> getEasyCardList();

    Observable<PaymentResult> getStayOutboundPaymentTypeEasy(int index, JSONObject jsonObject);

    Observable<PaymentResult> getStayOutboundPaymentTypeBonus(int index, JSONObject jsonObject);

    Observable<String> getStayOutboundHasDuplicatePayment(int index, JSONObject jsonObject);

    Observable<PaymentResult> getStayPaymentTypeEasy(JSONObject jsonObject);

    Observable<PaymentResult> getStayPaymentTypeBonus(JSONObject jsonObject);

    Observable<StayRefundPolicy> getStayRefundPolicy(StayBookDateTime stayBookDateTime, int stayIndex, int roomIndex);

    Observable<String> getStayHasDuplicatePayment(StayBookDateTime stayBookDateTime);

    Observable<PaymentResult> getGourmetPaymentTypeEasy(JSONObject jsonObject);

    Observable<PaymentResult> getGourmetPaymentTypeBonus(JSONObject jsonObject);
}
