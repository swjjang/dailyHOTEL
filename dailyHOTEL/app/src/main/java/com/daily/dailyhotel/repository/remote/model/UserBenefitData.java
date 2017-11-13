package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.UserBenefit;

@JsonObject
public class UserBenefitData
{
    @JsonField(name = "bonusAmount")
    public int bonusAmount;

    @JsonField(name = "couponTotalCount")
    public int couponTotalCount;

    @JsonField(name = "exceedLimitedBonus")
    public boolean exceedLimitedBonus;

    public UserBenefitData()
    {

    }

    public UserBenefit getUserBenefit()
    {
        UserBenefit userBenefit = new UserBenefit();
        userBenefit.bonusAmount = bonusAmount;
        userBenefit.couponTotalCount = couponTotalCount;
        userBenefit.exceedLimitedBonus = exceedLimitedBonus;

        return userBenefit;
    }
}
