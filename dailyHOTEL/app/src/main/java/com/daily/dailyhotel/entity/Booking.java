package com.daily.dailyhotel.entity;

import com.twoheart.dailyhotel.util.Constants;

public class Booking
{
    public int index;
    public String imageUrl;
    public PaymentType paymentType;
    public String placeName;
    public PlaceType placeType;
    public String checkInDateTime;
    public String checkOutDateTime;
    public boolean readyForRefund;
    public String comment;
    public String tid;

    // 내부 사용 변수
    public int remainingDays;
    public boolean isUsed;

    public enum PlaceType
    {
        STAY,
        GOURMET,
        STAY_OUTBOUND
    }

    public enum PaymentType
    {
        CARD,
        VIRTUAL,
        PHONE,
        BONUS,
        COUPON,
        VIRTUAL_WAIT,
    }

    public Booking()
    {

    }
}