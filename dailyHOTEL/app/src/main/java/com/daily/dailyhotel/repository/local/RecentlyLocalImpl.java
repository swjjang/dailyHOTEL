package com.daily.dailyhotel.repository.local;

import android.content.Context;

import com.daily.dailyhotel.domain.RecentlyLocalInterface;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.twoheart.dailyhotel.util.Constants;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public class RecentlyLocalImpl implements RecentlyLocalInterface
{
    private Context mContext;

    public RecentlyLocalImpl(Context context)
    {
        mContext = context;
    }

    @Override
    public Observable addRecentlyItem(Constants.ServiceType serviceType, int index //
        , String name, String englishName, String imageUrl, boolean isUpdateDate)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (serviceType == null || index <= 0)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);
                dailyDb.addRecentlyPlace(serviceType, index, name, englishName, imageUrl, isUpdateDate);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable deleteRecentlyItem(Constants.ServiceType serviceType, int index)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (serviceType == null || index <= 0)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);
                dailyDb.deleteRecentlyItem(serviceType, index);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }
}
