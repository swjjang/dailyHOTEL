package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2017. 12. 19..
 */

public class GourmetOldWaitingDeposit
{
    public int depositWaitingAmount; // 실(총) 입금금액(VAT포함) "amt": "70800",
    public String expireTime; // 입금기한 - 시간 "time": "14:36:00",
    public String bankName; // 은행명 "bank_name": "신한(통합)은행",
    public int totalPrice; // 결제(입금) 금액 "price": 70800,
    public String accountHolder; // 예금주 "name": "（주）데일리",
    public String accountNumber; // 계좌번호 "account_num": "56211992987722",
    public int couponAmount; // 쿠폰 사용 금액 "coupon_amount": 0,
    public String expireDate; // 입금 기한 - 날짜 "date": "2017\/12\/19",
    public String message1; // "msg1": "입금 순서대로 예약이 확정되며\n상품 예약이 조기에 마감될 수 있습니다.\n위의 계좌번호는 10분간만 유효하며\n10분이 지나면 입금이 불가합니다.\n입금자명과 예약자명이 달라도 입금가능합니다.",
    public String message2; // "msg2": "자동으로 예약이 완료됩니다."
}
