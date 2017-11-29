package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.TicketInfo;

/**
 * Created by android_sam on 2017. 11. 29..
 */
@JsonObject
public class TicketInfoData
{
    @JsonField(name = "count")
    public int count; // (integer): 개수 ,

    @JsonField(name = "idx")
    public int index; // (integer): 티켓IDX ,

    @JsonField(name = "name")
    public String name; // (string): 티켓이름 ,

    @JsonField(name = "subTotalPrice")
    public int subTotalPrice; // (integer): 티켓총가격

    public TicketInfo getTicketInfo()
    {
        TicketInfo ticketInfo = new TicketInfo();

        ticketInfo.count = this.count;
        ticketInfo.index = this.index;
        ticketInfo.name = this.name;
        ticketInfo.subTotalPrice = this.subTotalPrice;

        return ticketInfo;
    }
}
