package com.daily.dailyhotel.domain;

import io.reactivex.Observable;

public interface SnsInterface
{
    void initialize();

    void onRegister();

    void logOut();
}
