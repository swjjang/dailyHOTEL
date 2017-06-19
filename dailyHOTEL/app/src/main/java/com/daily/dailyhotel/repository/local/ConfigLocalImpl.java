package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.domain.ConfigInterface;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
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
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                return Observable.just(DailyHotel.isLogin());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable setVerified(boolean verify)
    {
        return Observable.just(verify).subscribeOn(Schedulers.io()).doOnNext(isVerify -> DailyPreference.getInstance(mContext).setVerification(isVerify))//
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> isVerified()
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                return Observable.just(DailyPreference.getInstance(mContext).isVerification());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable clear()
    {
        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                DailyPreference.getInstance(mContext).clear();
                DailyUserPreference.getInstance(mContext).clear();

                observer.onNext(true);
                observer.onComplete();
            }
        };

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
