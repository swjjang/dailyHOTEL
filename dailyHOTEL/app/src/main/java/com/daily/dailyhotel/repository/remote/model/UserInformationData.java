package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.UserSimpleInformation;

@JsonObject
public class UserInformationData
{
    @JsonField(name = "userIdx")
    public int userIdx;

    @JsonField(name = "email")
    public String email;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "phone")
    public String phone;

    @JsonField(name = "bonus")
    public int bonus;

    public UserInformationData()
    {

    }

    public UserSimpleInformation getUserInformation()
    {
        UserSimpleInformation userSimpleInformation = new UserSimpleInformation();
        userSimpleInformation.index = userIdx;
        userSimpleInformation.email = email;
        userSimpleInformation.name = name;
        userSimpleInformation.phone = phone;
        userSimpleInformation.bonus = bonus;

        return userSimpleInformation;
    }
}
