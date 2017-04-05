package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;

import io.reactivex.Observable;

public interface ConfigInterface
{
    Observable setVerified(boolean verify);

    Observable<Boolean> isVerified();
}
