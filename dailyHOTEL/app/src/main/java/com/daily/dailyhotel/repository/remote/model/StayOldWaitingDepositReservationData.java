package com.daily.dailyhotel.repository.remote.model;

import com.daily.dailyhotel.entity.StayOldWaitingDepositReservation;

/**
 * Created by android_sam on 2017. 12. 19..
 */

public class StayOldWaitingDepositReservationData
{
    public String expiredAt; // 입금기한 "validTo": "2017-12-19T14:29:00+09:00",
    public int depositWaitingAmount; // 실(총) 입금금액(VAT포함) "amt": "451000",
    public int couponAmount; // 쿠폰 사용금액 "couponAmount": 0,
    public String accountHolder; // 예금주 "vactName": "（주）데일리",
    public int totalPrice; // 결제(예약) 금액 "price": "451000",
    public String accountNumber; // 계좌번호(입금계좌) "vactNum": "99289014792994",
    public String userName; // "userName": "dh00",
    public String bankName; // "bankName": "국민은행",
    public int bonusAmount; // "bonus": 0

    public StayOldWaitingDepositReservation getReservation()
    {
        StayOldWaitingDepositReservation reservation = new StayOldWaitingDepositReservation();

        reservation.expiredAt = this.expiredAt;
        reservation.depositWaitingAmount = this.depositWaitingAmount;
        reservation.couponAmount = this.couponAmount;
        reservation.accountHolder = this.accountHolder;
        reservation.totalPrice = this.totalPrice;
        reservation.accountNumber = this.accountNumber;
        reservation.userName = this.userName;
        reservation.bankName = this.bankName;
        reservation.bonusAmount = this.bonusAmount;

        return reservation;
    }
}
