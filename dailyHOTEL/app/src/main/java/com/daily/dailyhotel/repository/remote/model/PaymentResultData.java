package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.PaymentResult;

@JsonObject
public class PaymentResultData
{
    // 국내 결제 결과.
    @JsonField(name = "aggregationId")
    public String aggregationId;

    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "result")
    public String result;

    public PaymentResultData()
    {

    }

    public PaymentResult getPaymentResult()
    {
        PaymentResult paymentResult = new PaymentResult();
        paymentResult.bookingIndex = reservationIdx;
        paymentResult.aggregationId = aggregationId;
        paymentResult.result = result;
        return paymentResult;
    }
}
