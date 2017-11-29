package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.ReservationInfo;

/**
 * Created by android_sam on 2017. 11. 29..
 */
@JsonObject
public class ReservationInfoData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx; // (integer): 예약IDX

    public ReservationInfo getReservationInfo()
    {
        ReservationInfo reservationInfo = new ReservationInfo();

        reservationInfo.reservationIdx = this.reservationIdx;

        return reservationInfo;
    }
}
