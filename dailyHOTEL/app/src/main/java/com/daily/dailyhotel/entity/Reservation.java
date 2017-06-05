package com.daily.dailyhotel.entity;

import com.twoheart.dailyhotel.util.Constants;

public class Reservation
{
    public int reservationIndex;
    public String imageUrl;
    public int payType;
    public String placeName;
    public PlaceType placeType;
    public String checkInDateTime;
    public String checkOutDateTime;
    public boolean readyForRefund;
    public String comment;
    public String tid;

    public int remainingDays;
    public boolean isUsed;

    public enum PlaceType
    {
        STAY,
        GOURMET,
        STAY_OUTBOUND
    }

    public Reservation()
    {

    }
}