package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.PaymentInfo;

/**
 * Created by android_sam on 2017. 11. 29..
 */
@JsonObject
public class PaymentInfoData
{
    @JsonField(name = "bonusAmount")
    public int bonusAmount; // (integer): 적립금사용금액 ,

    @JsonField(name = "couponAmount")
    public int couponAmount; // (integer): 쿠폰사용금액 ,

    @JsonField(name = "paidAt")
    public String paidAt; // (string): 결제일 ,

    @JsonField(name = "paymentAmount")
    public int paymentAmount; // (integer): 사용자결제액 ,

    @JsonField(name = "price")
    public int price; // (integer): 총금액

    public PaymentInfo getPaymentInfo()
    {
        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.bonusAmount = this.bonusAmount;
        paymentInfo.couponAmount = this.couponAmount;
        paymentInfo.paidAt = this.paidAt;
        paymentInfo.paymentAmount = this.paymentAmount;
        paymentInfo.price = this.price;

        return paymentInfo;
    }
}
