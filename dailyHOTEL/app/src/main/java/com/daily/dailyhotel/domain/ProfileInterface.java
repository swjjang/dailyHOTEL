package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;

import io.reactivex.Observable;

public interface ProfileInterface
{
    Observable<User> getProfile();

    Observable<UserBenefit> getBenefit();

    void setVerified(boolean verify);

    boolean isVerified();
}
