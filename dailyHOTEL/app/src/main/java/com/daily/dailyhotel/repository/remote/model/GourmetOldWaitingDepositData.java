package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GourmetOldWaitingDeposit;

/**
 * Created by android_sam on 2017. 12. 19..
 */

@JsonObject
public class GourmetOldWaitingDepositData
{
    @JsonField(name = "amt")
    public int depositWaitingAmount; // 실(총) 입금금액(VAT포함) "amt": "70800",

    @JsonField(name = "time")
    public String expireTime; // 입금기한 - 시간 "time": "14:36:00",

    @JsonField(name = "bank_name")
    public String bankName; // 은행명 "bank_name": "신한(통합)은행",

    @JsonField(name = "price")
    public int totalPrice; // 결제(입금) 금액 "price": 70800,

    @JsonField(name = "name")
    public String accountHolder; // 예금주 "name": "（주）데일리",

    @JsonField(name = "account_num")
    public String accountNumber; // 계좌번호 "account_num": "56211992987722",

    @JsonField(name = "coupon_amount")
    public int couponAmount; // 쿠폰 사용 금액 "coupon_amount": 0,

    @JsonField(name = "date")
    public String expireDate; // 입금 기한 - 날짜 "date": "2017\/12\/19",

    @JsonField(name = "msg1")
    public String message1; // "msg1": "입금 순서대로 예약이 확정되며\n상품 예약이 조기에 마감될 수 있습니다.\n위의 계좌번호는 10분간만 유효하며\n10분이 지나면 입금이 불가합니다.\n입금자명과 예약자명이 달라도 입금가능합니다.",

    @JsonField(name = "msg2")
    public String message2; // "msg2": "자동으로 예약이 완료됩니다."

    public GourmetOldWaitingDeposit getWaitingDeposit()
    {
        GourmetOldWaitingDeposit waitingDeposit = new GourmetOldWaitingDeposit();

        waitingDeposit.depositWaitingAmount = this.depositWaitingAmount;
        waitingDeposit.expireTime = this.expireTime;
        waitingDeposit.bankName = this.bankName;
        waitingDeposit.totalPrice = this.totalPrice;
        waitingDeposit.accountHolder = this.accountHolder;
        waitingDeposit.accountNumber = this.accountNumber;
        waitingDeposit.couponAmount = this.couponAmount;
        waitingDeposit.expireDate = this.expireDate;
        waitingDeposit.message1 = this.message1;
        waitingDeposit.message2 = this.message2;

        return waitingDeposit;
    }
}
