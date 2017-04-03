package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.User;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public interface ProfileInterface
{
    Observable<User> getProfile();
}
