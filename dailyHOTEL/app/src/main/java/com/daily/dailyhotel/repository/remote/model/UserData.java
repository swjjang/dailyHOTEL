package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.User;

@JsonObject
public class UserData
{
    @JsonField(name = "userIdx")
    public int userIdx;

    @JsonField(name = "email")
    public String email;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "phone")
    public String phone;

    @JsonField(name = "recommender")
    public int recommender;

    @JsonField(name = "referralCode")
    public String referralCode;

    @JsonField(name = "verifiedAt")
    public String verifiedAt;

    @JsonField(name = "phoneVerifiedAt")
    public String phoneVerifiedAt;

    @JsonField(name = "userType")
    public String userType;

    @JsonField(name = "birthday")
    public String birthday;

    @JsonField(name = "verified")
    public boolean verified;

    @JsonField(name = "phoneVerified")
    public boolean phoneVerified;

    @JsonField(name = "agreedBenefit")
    public boolean agreedBenefit;

    @JsonField(name = "agreedAt")
    public String agreedAt;

    @JsonField(name = "dataRetentionInMonth")
    public int dataRetentionInMonth;

    public UserData()
    {

    }

    public User getUser()
    {
        User user = new User();
        user.index = userIdx;

        if (DailyTextUtils.isTextEmpty(email) == false)
        {
            user.email = email.trim();
        }

        user.name = name;
        user.phone = phone;
        user.recommender = recommender;
        user.referralCode = referralCode;
        user.verifiedAt = verifiedAt;
        user.phoneVerifiedAt = phoneVerifiedAt;
        user.userType = userType;
        user.birthday = birthday;
        user.verified = verified;
        user.phoneVerified = phoneVerified;
        user.agreedBenefit = agreedBenefit;
        user.agreedAt = agreedAt;
        user.dataRetentionInMonth = dataRetentionInMonth;

        return user;
    }
}
