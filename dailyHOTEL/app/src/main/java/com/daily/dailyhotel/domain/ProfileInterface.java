package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.entity.UserTracking;

import io.reactivex.Observable;

public interface ProfileInterface
{
    Observable<User> getProfile();

    Observable<UserBenefit> getBenefit();

    Observable<UserSimpleInformation> getUserSimpleInformation();

    Observable<UserTracking> getTracking();
}
