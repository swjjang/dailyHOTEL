package com.daily.dailyhotel.entity;

public class BookingCancel
{
    public String aggregationId;
    public long orderSeq;
    public int reservationIdx;
//    public String checkinDate;
//    public String checkoutDate;
//    public String checkinTime;
//    public String checkoutTime;
    public String checkInDateTime;
    public String checkOutDateTime;
    public String cancelDateTime;
    public String imageUrl;
//    public String type;
    public PlaceType placeType;
    public String name;
    public int itemIdx;

    public enum PlaceType
    {
        STAY,
        GOURMET,
        STAY_OUTBOUND
    }

    public BookingCancel()
    {

    }
}