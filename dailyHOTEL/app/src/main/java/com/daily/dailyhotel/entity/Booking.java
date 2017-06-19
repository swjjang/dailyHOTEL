package com.daily.dailyhotel.entity;

public class Booking
{
    public static final int COMPLETED_PAYMENT = 10;
    public static final int WAIT_PAYMENT = 20;

    public int index;
    public String imageUrl;
    public int statusPayment;
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

    public Booking()
    {

    }
}