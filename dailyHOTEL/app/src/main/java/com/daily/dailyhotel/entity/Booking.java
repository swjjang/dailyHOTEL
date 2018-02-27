package com.daily.dailyhotel.entity;

public class Booking
{
    public static final int PAYMENT_COMPLETED = 10;
    public static final int PAYMENT_WAITING = 20;

    public static final int BOOKING_STATE_NONE = 0;
    public static final int BOOKING_STATE_WAITING_REFUND = 1;
    public static final int BOOKING_STATE_BEFORE_USE = 2;
    public static final int BOOKING_STATE_AFTER_USE = 3;
    public static final int BOOKING_STATE_DEPOSIT_WAITING = 4;
    public static final int BOOKING_STATE_RESERVATION_WAITING = 5;
    public static final int BOOKING_STATE_CANCEL = 6;

    public int reservationIndex;
    public String aggregationId;
    public String imageUrl;
    public int statePayment;
    public String placeName;
    public PlaceType placeType;
    public String checkInDateTime;
    public String checkOutDateTime;
    public boolean readyForRefund;
    public String comment;
    public String tid;
    public boolean availableReview;
    public int placeIndex;
    public boolean waitingForBooking;
    public String reviewStatusType;

    // 내부 사용 변수
    public int remainingDays;
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