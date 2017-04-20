package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.CommonDateTime;

@JsonObject
public class CommonDateTimeData
{
    @JsonField(name = "openDateTime")
    public String openDateTime; // ISO-8601

    @JsonField(name = "closeDateTime")
    public String closeDateTime; // ISO-8601

    @JsonField(name = "currentDateTime")
    public String currentDateTime; // ISO-8601

    @JsonField(name = "dailyDateTime")
    public String dailyDateTime; // ISO-8601

    public CommonDateTimeData()
    {
    }

    public CommonDateTime getCommonDateTime()
    {
        return new CommonDateTime(openDateTime, closeDateTime, currentDateTime, dailyDateTime);
    }
}
