package com.daily.dailyhotel.entity;

public class Booking
{
    public static final int PAYMENT_COMPLETED = 10;
    public static final int PAYMENT_WAITING = 20;

    public static final int BOOKING_STATE_NONE = 0;
    public static final int BOOKING_STATE_WAITING_REFUND = 1;
    public static final int BOOKING_STATE_BEFORE_USE = 2;
    public static final int BOOKING_STATE_AFTER_USE = 3;

    public int index;
    public String imageUrl;
    public int statePayment;
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
    public int bookingState;

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