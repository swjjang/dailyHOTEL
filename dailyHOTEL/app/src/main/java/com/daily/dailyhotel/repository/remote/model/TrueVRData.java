package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.TrueVR;

/**
 * Created by android_sam on 2017. 2. 15..
 */
@JsonObject
public class TrueVRData
{
    @JsonField(name = "name")
    public String name;

    @JsonField(name = "type")
    public String type;

    @JsonField(name = "typeIdx")
    public int typeIdx;

    @JsonField(name = "url")
    public String url;

    public TrueVRData()
    {
    }

    public TrueVR getTrueVR()
    {
        TrueVR trueVR = new TrueVR();

        trueVR.name = name;
        trueVR.type = type;
        trueVR.typeIndex = typeIdx;
        trueVR.url = url;

        return trueVR;
    }
}
