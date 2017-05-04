package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundDetail;

import java.util.List;

@JsonObject
public class StayOutboundDetailImageData
{
    @JsonField(name = "name")
    public String name;

    @JsonField(name = "caption")
    public String caption;

    @JsonField(name = "url")
    public String url;

    public StayOutboundDetailImageData()
    {

    }

    public StayOutboundDetail getStayOutboundDetail()
    {
        StayOutboundDetail stayOutboundDetail = new StayOutboundDetail();

        return stayOutboundDetail;
    }
}
