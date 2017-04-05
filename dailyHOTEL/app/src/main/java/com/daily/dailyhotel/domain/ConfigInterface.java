package com.daily.dailyhotel.domain;

import io.reactivex.Observable;

public interface ConfigInterface
{
    Observable setVerified(boolean verify);

    Observable<Boolean> isVerified();
}
