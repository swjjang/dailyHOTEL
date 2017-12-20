package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOldWaitingDeposit;

/**
 * Created by android_sam on 2017. 12. 19..
 */
@JsonObject
public class StayOldWaitingDepositData
{
    @JsonField(name = "msg1")
    public String message1; // "msg1": "입금 순서대로 예약이 확정되며\n객실 예약이 조기에 마감될 수 있습니다.\n위의 계좌번호는 10분간만 유효하며\n10분이 지나면 입금이 불가합니다.\n입금자명과 예약자명이 달라도 입금가능합니다.",

    @JsonField(name = "msg2")
    public String message2; // "msg2": "자동으로 예약이 완료됩니다.",

    @JsonField(name = "reservation")
    public StayOldWaitingDepositReservationData reservation; // "reservation": Object

    public StayOldWaitingDeposit getWaitingDeposit()
    {
        StayOldWaitingDeposit waitDeposit = new StayOldWaitingDeposit();

        waitDeposit.message1 = this.message1;
        waitDeposit.message2 = this.message2;
        waitDeposit.reservation = this.reservation.getReservation();

        return waitDeposit;
    }
}
