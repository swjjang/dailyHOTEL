package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2017. 12. 7..
 */

public class GourmetReceipt
{
    public int couponAmount; // (integer, optional),
    public int gourmetReservationIdx; // (integer, optional),
    public int price; // (integer, optional)
    public String paidAt; // (string, optional),
    public int paymentAmount; // (integer, optional),
    public String paymentType; // (string, optional) = ['신용카드', '계좌이체', '휴대폰 결제', '적립금 전액결제', '쿠폰 전액결제', '신용/체크카드 간편결제', '신용/체크카드 일반결제', '수기수수료', '수기결제'],
    public String restaurantAddress; // (string, optional),
    public String restaurantName; // (string, optional),
    public String sday; // (string, optional),
    public int ticketCount; // (integer, optional),
    public String userName; // (string, optional),
    public String userPhone; // (string, optional)
    public String notice; // (string, optional)
}
