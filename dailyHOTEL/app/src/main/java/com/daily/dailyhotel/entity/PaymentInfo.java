package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2017. 11. 29..
 */

public class PaymentInfo
{
    public int bonusAmount; // (integer): 적립금사용금액 ,
    public int couponAmount; // (integer): 쿠폰사용금액 ,
    public String paidAt; // (string): 결제일 ,
    public int paymentAmount; // (integer): 사용자결제액 ,
    public int price; // (integer): 총금액
}
