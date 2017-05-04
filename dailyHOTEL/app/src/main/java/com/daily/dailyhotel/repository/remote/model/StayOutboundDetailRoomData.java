package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundDetail;

import java.util.List;

@JsonObject
public class StayOutboundDetailRoomData
{
    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "policy")
    public String policy;

    @JsonField(name = "roomTypeCode")
    public String roomTypeCode;

    @JsonField(name = "rateCode")
    public int rateCode;

    @JsonField(name = "rateDescription")
    public String rateDescription;

    @JsonField(name = "roomTypeDescription")
    public String roomTypeDescription;

    @JsonField(name = "propertyId")
    public int propertyId;

    @JsonField(name = "checkInInstructions")
    public String checkInInstructions;

    @JsonField(name = "specialCheckInInstructions")
    public String specialCheckInInstructions;

    @JsonField(name = "smokingPreferences")
    public String smokingPreferences;



    public StayOutboundDetailRoomData()
    {

    }

    public StayOutboundDetail getStayOutboundDetail()
    {
        StayOutboundDetail stayOutboundDetail = new StayOutboundDetail();

        return stayOutboundDetail;
    }
}
