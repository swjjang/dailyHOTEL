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

    @JsonField(name = "checkoutDate")
    public String checkoutDate;

    @JsonField(name = "email")
    public String email;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "phone")
    public String phone;

    @JsonField(name = "roomName")
    public String roomName;

    @JsonField(name = "paymentTypeName")
    public String paymentTypeName;

    @JsonField(name = "paymentDate")
    public String paymentDate;

    @JsonField(name = "total")
    public int total;

    @JsonField(name = "paymentAmount")
    public int paymentAmount;

    @JsonField(name = "bonus")
    public int bonus;

    @JsonField(name = "description")
    public String description;

    @JsonField(name = "reservationRooms")
    public int reservationRooms;

    public StayOutboundReceiptData()
    {

    }

    public StayOutboundReceipt getStayOutboundReceipt()
    {
        StayOutboundReceipt stayOutboundReceipt = new StayOutboundReceipt();

        stayOutboundReceipt.index = reservationIdx;
        stayOutboundReceipt.paymentTypeName = paymentTypeName;
        stayOutboundReceipt.placeName = hotelName;
        stayOutboundReceipt.placeAddress = hotelAddress;
        stayOutboundReceipt.paymentDate = paymentDate;
        stayOutboundReceipt.discountPrice = paymentAmount;
        stayOutboundReceipt.totalPrice = total;
        stayOutboundReceipt.bonus = bonus;
        stayOutboundReceipt.checkInDate = checkinDate;
        stayOutboundReceipt.checkOutDate = checkoutDate;
        stayOutboundReceipt.comment = description;
        stayOutboundReceipt.userName = name;
        stayOutboundReceipt.userPhone = phone;
        stayOutboundReceipt.userEmail = email;
        stayOutboundReceipt.roomCount = reservationRooms;

        return stayOutboundReceipt;
    }
}
