package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.PaymentTypeEasy;

@JsonObject
public class PaymentTypeEasyData
{
    @JsonField(name = "reservationId")
    public int reservationId;

    @JsonField(name = "result")
    public String result;

    public PaymentTypeEasyData()
    {

    }

    public PaymentTypeEasy getPaymentTypeEasy()
    {
        PaymentTypeEasy paymentTypeEasy = new PaymentTypeEasy();
        paymentTypeEasy.reservationId = reservationId;
        paymentTypeEasy.result = result;
        return paymentTypeEasy;
    }
}
