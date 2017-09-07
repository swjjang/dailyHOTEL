package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class ExistCouponsData
{
    @JsonField(name = "existCoupons")
    public boolean existCoupons;

    public ExistCouponsData()
    {

    }
}
