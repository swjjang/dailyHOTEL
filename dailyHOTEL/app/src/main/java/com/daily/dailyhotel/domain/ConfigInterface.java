package com.daily.dailyhotel.domain;

import android.content.Context;

import io.reactivex.Observable;

public interface ConfigInterface
{
    Observable<Boolean> isLogin();

    Observable setVerified(Context context, boolean verify);

    Observable<Boolean> isVerified(Context context);

    Observable clear(Context context);
}
