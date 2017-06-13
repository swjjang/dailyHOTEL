package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.PaymentResult;

@JsonObject
public class PaymentResultData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "result")
    public String result;

    public PaymentResultData()
    {

    }

    public PaymentResult getPaymentTypeEasy()
    {
        PaymentResult paymentResult = new PaymentResult();
        paymentResult.bookingIndex = reservationIdx;
        paymentResult.result = result;
        return paymentResult;
    }
}
