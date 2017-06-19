package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.CommonDateTime;

import io.reactivex.Observable;

public interface CommonInterface
{
    Observable<CommonDateTime> getCommonDateTime();
}
