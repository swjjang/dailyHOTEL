package com.daily.dailyhotel.entity;

public class StayOutboundBookingDetail
{
    public int stayIndex;
    public int bookingIndex;
    public String name;
    public String roomName;
    public String address;
    public String guestFirstName;
    public String guestLastName;
    public String guestEmail;
    public String guestPhone;

    public double latitude;
    public double longitude;

    public int paymentPrice;
    public int bonus;
    public int totalPrice;
    public double fee;

    private People mPeople;

    public RefundType refundStatus;
    public String refundPolicy;

    public String checkInDate;
    public String checkInTime;
    public String checkOutDate;
    public String checkOutTime;

    public PaymentType paymentType;
    public String paymentDate;

    public enum PaymentType
    {
        CREDIT_CARD,
        VBANK,
        MOBILE_PHONE,
        BONUS,
        COUPON,
        ONE_CLICK,
        CREDIT_INICIS,
        CREDIT_KCP
    }

    public enum RefundType
    {
        FULL,
        PARTIAL,
        NRD,
        TIMEOVER,
    }

    public StayOutboundBookingDetail()
    {

    }

    public People getPeople()
    {
        return mPeople;
    }

    public void setPeople(People people)
    {
        mPeople = people;
    }
}
