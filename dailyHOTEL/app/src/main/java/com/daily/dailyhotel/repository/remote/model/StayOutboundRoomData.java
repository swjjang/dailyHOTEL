package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundRoom;

/**
 * Created by android_sam on 2018. 3. 23..
 */
@JsonObject
public class StayOutboundRoomData
{
    @JsonField(name = "rateKey")
    public String rateKey;

    @JsonField(name = "roomTypeCode")
    public String roomTypeCode;

    @JsonField(name = "rateCode")
    public String rateCode;

    @JsonField(name = "roomName")
    public String roomName;

    @JsonField(name = "roomBedTypeId")
    public int roomBedTypeId;

    @JsonField(name = "base")
    public int base;

    @JsonField(name = "total")
    public int total;

    @JsonField(name = "baseNightly")
    public int baseNightly;

    @JsonField(name = "nightly")
    public int nightly;

    @JsonField(name = "quotedOccupancy")
    public int quotedOccupancy;

    @JsonField(name = "rateOccupancyPerRoom")
    public int rateOccupancyPerRoom;

    @JsonField(name = "promotion")
    public boolean promotion;

    @JsonField(name = "promotionDescription")
    public String promotionDescription;

    @JsonField(name = "nonRefundable")
    public boolean nonRefundable;

    @JsonField(name = "nonRefundableDescription")
    public String nonRefundableDescription;

    @JsonField(name = "valueAddName")
    public String valueAddName;

    @JsonField(name = "vendorType")
    public String vendorType;

    @JsonField(name = "provideRewardSticker")
    public boolean provideRewardSticker;

    public StayOutboundRoomData()
    {

    }

    public StayOutboundRoom getStayOutboundRoom()
    {
        StayOutboundRoom stayOutboundRoom = new StayOutboundRoom();
        stayOutboundRoom.rateKey = rateKey;
        stayOutboundRoom.roomTypeCode = roomTypeCode;
        stayOutboundRoom.rateCode = rateCode;
        stayOutboundRoom.roomName = roomName;
        stayOutboundRoom.roomBedTypeId = roomBedTypeId;
        stayOutboundRoom.base = base;
        stayOutboundRoom.total = total;
        stayOutboundRoom.baseNightly = baseNightly;
        stayOutboundRoom.nightly = nightly;
        stayOutboundRoom.quotedOccupancy = quotedOccupancy;
        stayOutboundRoom.rateOccupancyPerRoom = rateOccupancyPerRoom;
        stayOutboundRoom.promotion = promotion;
        stayOutboundRoom.promotionDescription = promotionDescription;
        stayOutboundRoom.nonRefundable = nonRefundable;
        stayOutboundRoom.nonRefundableDescription = nonRefundableDescription;
        stayOutboundRoom.valueAddName = valueAddName;
        stayOutboundRoom.vendorType = vendorType;
        stayOutboundRoom.provideRewardSticker = provideRewardSticker;

        return stayOutboundRoom;
    }
}
