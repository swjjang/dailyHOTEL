package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundReceipt;

@JsonObject
public class StayOutboundReceiptData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "hotelName")
    public String hotelName;

    @JsonField(name = "hotelAddress")
    public String hotelAddress;

    @JsonField(name = "checkinDate")
    public String checkinDate;

    @JsonField(name = "checkinTime")
    public String checkinTime;

    @JsonField(name = "checkoutDate")
    public String checkoutDate;

    @JsonField(name = "checkoutTime")
    public String checkoutTime;

    @JsonField(name = "userEmail")
    public String userEmail;

    @JsonField(name = "userName")
    public String userName;

    @JsonField(name = "userPhone")
    public String userPhone;

    @JsonField(name = "roomName")
    public String roomName;

    @JsonField(name = "paymentType")
    public String paymentType;

    @JsonField(name = "paymentDate")
    public String paymentDate;

    @JsonField(name = "total")
    public int total;

    @JsonField(name = "paymentAmount")
    public int paymentAmount;

    @JsonField(name = "bonus")
    public int bonus;

    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "rooms")
    public int rooms;

    public StayOutboundReceiptData()
    {

    }

    public StayOutboundReceipt getStayOutboundReceipt()
    {
        StayOutboundReceipt stayOutboundReceipt = new StayOutboundReceipt();

        stayOutboundReceipt.index = reservationIdx;
        stayOutboundReceipt.paymentType = paymentType;
        stayOutboundReceipt.placeName = hotelName;
        stayOutboundReceipt.placeAddress = hotelAddress;
        stayOutboundReceipt.paymentDate = paymentDate;
        stayOutboundReceipt.discountPrice = paymentAmount;
        stayOutboundReceipt.totalPrice = total;
        stayOutboundReceipt.bonus = bonus;
        stayOutboundReceipt.checkInDate = checkinDate;
        stayOutboundReceipt.checkOutDate = checkoutDate;
        stayOutboundReceipt.comment = comment;
        stayOutboundReceipt.userName = userName;
        stayOutboundReceipt.userPhone = userPhone;
        stayOutboundReceipt.userEmail = userEmail;
        stayOutboundReceipt.rooms = rooms;

        return stayOutboundReceipt;
    }
}
