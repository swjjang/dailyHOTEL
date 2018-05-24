package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RoomImageInformation;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class RoomImagesData
{
    @JsonField(name = "roomIdx")
    public int roomIndex;

    @JsonField(name = "images")
    public List<RoomImageInformationData> imageList;

    public List<RoomImageInformation> getRoomImageList()
    {
        List<RoomImageInformation> list = new ArrayList<>();

        if (imageList != null)
        {
            for (RoomImageInformationData data : imageList)
            {
                list.add(data.getRoomImageInformation());
            }
        }

        return list;
    }
}
