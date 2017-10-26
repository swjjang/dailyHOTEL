package com.daily.dailyhotel.entity;

import com.twoheart.dailyhotel.model.Stay;

import java.util.LinkedHashMap;
import java.util.List;

public class StayBookingDetail
{
    public int reservationIndex;
    public int stayIndex;
    public int userIndex;
    public int userCouponIndex;
    public int regionProvinceIndex;
    public String regionDistrictName;
    public String stayName;
    public String stayAddress;
    public String addressSummary;
    public String phone1;
    public String phone2;
    public String phone3;
    public Stay.Grade stayGrade;
    public String checkInDateTime;
    public String checkOutDateTime;
    public double latitude;
    public double longitude;
    public String guestEmail;
    public String guestName;
    public String guestPhone;
    public int roomIndex;
    public String roomName;
    public String guestTransportation;
    public String refundStatus;
    public String paymentDateTime;
    public int priceTotal;
    public int discountTotal;
    public int bonusAmount;
    public int couponAmount;
    public String transactionType;
    public String reviewStatusType;
    public String refundType;
    public boolean overseas;
    public boolean readyForRefund;
    public boolean waitingForBooking;
    public String cancelDateTime;

    private LinkedHashMap<String, List<String>> mSpecificationMap;

    public StayBookingDetail()
    {

    }

    public LinkedHashMap<String, List<String>> getSpecificationMap()
    {
        return mSpecificationMap;
    }

    public void setSpecificationMap(LinkedHashMap<String, List<String>> specificationMap)
    {
        mSpecificationMap = specificationMap;
    }
}
