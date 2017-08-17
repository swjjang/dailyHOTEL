package com.daily.dailyhotel.entity;

import java.util.List;

public class StayPayment
{
    public static final String VISIT_TYPE_NONE = "NONE"; // 아무것도 표시하지 않음
    public static final String VISIT_TYPE_PARKING = "PARKING"; // 도보/주차 표시
    public static final String VISIT_TYPE_NO_PARKING = "NO_PARKING"; // 주차 불가능

    private static final String NRD = "nrd";

    public boolean soldOut;
    public String checkInDate;
    public String checkOutDate;
    public String refundType;
    public int totalPrice; // 결재할 총금액
    public int discountPrice; // 할인 총금액(보너스, 쿠폰)
    public String businessName;
    public String transportation;
    public boolean parking;
    public boolean noParking;

    public StayPayment()
    {

    }

    public boolean isNRD()
    {
        return NRD.equalsIgnoreCase(refundType) == true;
    }
}