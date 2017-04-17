package com.daily.dailyhotel.domain;

import io.reactivex.Observable;

public interface ConfigInterface
{
    Observable<Boolean> isLogin();

    Observable setVerified(boolean verify);

    Observable<Boolean> isVerified();

    Observable clear();
}
