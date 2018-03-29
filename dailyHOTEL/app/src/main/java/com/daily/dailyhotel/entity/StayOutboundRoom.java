package com.daily.dailyhotel.entity;

public class StayOutboundRoom
{
    public static final String VENDOR_TYPE_FIT_RUUMS = "F";
    public static final String VENDOR_TYPE_EAN = "E";

    public String rateKey;
    public String roomTypeCode;
    public String rateCode;
    public int roomBedTypeId;
    public String roomName;
    public int base;
    public int total;
    public int baseNightly;
    public int nightly;
    public int quotedOccupancy;
    public int rateOccupancyPerRoom;
    public boolean promotion;
    public String promotionDescription;
    public boolean nonRefundable;
    public String nonRefundableDescription;
    public String policy;
    public String valueAddName;
    public String vendorType;
    public boolean provideRewardSticker;
    public boolean hasCoupon;

    public StayOutboundRoom()
    {

    }
}
