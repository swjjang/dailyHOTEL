package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.UserInformation;

@JsonObject
public class UserInformationData
{
    @JsonField(name = "user_idx")
    public int userIdx;

    @JsonField(name = "user_email")
    public String email;

    @JsonField(name = "user_name")
    public String name;

    @JsonField(name = "user_phone")
    public String phone;

    @JsonField(name = "user_bonus")
    public int bonus;

    @JsonField(name = "on_session")
    public boolean session;

    public UserInformationData()
    {

    }

    public UserInformation getUserInformation()
    {
        UserInformation userInformation = new UserInformation();
        userInformation.index = userIdx;
        userInformation.email = email;
        userInformation.name = name;
        userInformation.phone = phone;
        userInformation.bonus = bonus;
        userInformation.session = session;

        return userInformation;
    }
}
