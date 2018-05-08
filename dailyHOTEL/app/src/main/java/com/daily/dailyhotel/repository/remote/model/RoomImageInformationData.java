package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RoomImageInformation;

@JsonObject
public class RoomImageInformationData
{
    @JsonField(name = "description")
    public String description;

    @JsonField(name = "primary")
    public boolean primary;

    @JsonField(name = "url")
    public String url;

    public RoomImageInformation getRoomImageInformation()
    {
        RoomImageInformation roomImageInformation = new RoomImageInformation();

        roomImageInformation.description = description;
        roomImageInformation.primary = primary;
        roomImageInformation.url = url;

        return roomImageInformation;
    }

}
