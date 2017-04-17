package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.domain.ConfigInterface;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigLocalImpl implements ConfigInterface
{
    private Context mContext;

    public ConfigLocalImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<Boolean> isLogin()
    {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> emitter.onNext(DailyHotel.isLogin()))//
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable setVerified(boolean verify)
    {
        return Observable.just(verify).doOnNext(isVerify -> DailyPreference.getInstance(mContext).setVerification(isVerify))//
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> isVerified()
    {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> emitter.onNext(DailyPreference.getInstance(mContext).isVerification()))//
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable clear()
    {
        return Observable.empty().doOnNext(object ->
        {
            DailyPreference.getInstance(mContext).clear();
            DailyUserPreference.getInstance(mContext).clear();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
