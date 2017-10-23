package com.daily.dailyhotel.entity;

import java.util.LinkedHashMap;
import java.util.List;

public class GourmetBookingDetail
{
    public int reservationIndex;
    public int gourmetIndex;
    public int userIndex;
    public int userCouponIndex;
    public int regionProvinceIndex;
    public String gourmetName;
    public String gourmetAddress;
    public String addressSummary;
    public String phone1;
    public String phone2;
    public String phone3;
    public String category;
    public String categorySub;
    public String arrivalDateTime;
    public double latitude;
    public double longitude;
    public String guestEmail;
    public String guestName;
    public String guestPhone;
    public String ticketName;
    public int ticketCount;
    public String pamentDateTime;
    public int discountTotal;
    public int priceTotal;
    public int couponAmount;
    public String reviewStatusType;
    public boolean overseas;
    public String cancelDateTime;

    private LinkedHashMap<String, List<String>> mDescriptionMap;

    public GourmetBookingDetail()
    {

    }

    public LinkedHashMap<String, List<String>> getDescriptionMap()
    {
        return mDescriptionMap;
    }

    public void setDescriptionMap(LinkedHashMap<String, List<String>> specificationMap)
    {
        mDescriptionMap = specificationMap;
    }
}
