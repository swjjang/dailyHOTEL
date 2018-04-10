package com.daily.dailyhotel.repository.local;

import android.content.Context;

import com.daily.dailyhotel.domain.ConfigInterface;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigLocalImpl implements ConfigInterface
{
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
    public Observable setVerified(Context context, boolean verify)
    {
        return Observable.just(verify).subscribeOn(Schedulers.io()).doOnNext(isVerify -> DailyPreference.getInstance(context).setVerification(isVerify))//
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> isVerified(Context context)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                return Observable.just(DailyPreference.getInstance(context).isVerification());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable clear(Context context)
    {
        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                DailyPreference.getInstance(context).clear();
                DailyUserPreference.getInstance(context).clear();

                // 임시 저장된 리뷰 전체 삭제
                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);
                dailyDb.deleteAllTempReview();
                DailyDbHelper.getInstance().close();

                observer.onNext(true);
                observer.onComplete();
            }
        };

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
