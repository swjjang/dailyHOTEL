package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class NotificationData
{
    @JsonField(name = "serverDate")
    public String serverDate;

    public NotificationData()
    {

    }
}
