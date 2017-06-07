package com.daily.dailyhotel.entity;

import java.util.ArrayList;

public class StayOutboundBookingDetail
{
    public static final String STATUS_NO_CHARGE_REFUND = "NO_CHARGE_REFUND"; // 무료 환불
    public static final String STATUS_SURCHARGE_REFUND = "SURCHARGE_REFUND"; // 부분 환불
    public static final String STATUS_NRD = "NRD";
    public static final String STATUS_WAIT_REFUND = "WAIT_REFUND";
    public static final String STATUS_NONE = "NONE";

    public int stayIndex;
    public int bookingIndex;
    public String name;
    public String roomName;
    public String address;
    public String guestFirstName;
    public String guestLastName;
    public String guestEmail;
    public String guestPhone;
    public String paymentDate;

    public double latitude;
    public double longitude;

    public int price;
    public int bonus;
    public int total;

    private People mPeople;

    public boolean readyForRefund;
    public String refundComment;
    public String refundPolicy;

    public String checkInDate;
    public String checkInTime;
    public String checkOutDate;
    public String checkOutTime;

    public StayOutboundBookingDetail()
    {

    }

    public People getPeople()
    {
        return mPeople;
    }
}
