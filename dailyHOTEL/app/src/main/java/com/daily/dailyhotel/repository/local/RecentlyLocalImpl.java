package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.database.Cursor;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyLocalInterface;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.local.model.RecentlyList;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
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

    @Override
    public Observable<ArrayList<RecentlyDbPlace>> getRecentlyTypeList(Constants.ServiceType... serviceTypes)
    {
        return Observable.defer(new Callable<ObservableSource<? extends ArrayList<RecentlyDbPlace>>>()
        {
            @Override
            public ObservableSource<? extends ArrayList<RecentlyDbPlace>> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                Cursor cursor = null;

                ArrayList<RecentlyDbPlace> recentlyList = new ArrayList<>();

                try
                {
                    cursor = dailyDb.getRecentlyPlaces(-1, serviceTypes);

                    if (cursor == null || cursor.getCount() == 0)
                    {
                        return Observable.just(recentlyList);
                    }

                    for (int i = 0; i < cursor.getCount(); i++)
                    {
                        cursor.moveToPosition(i);

                        try
                        {
                            RecentlyDbPlace recentlyDbPlace = new RecentlyDbPlace();
                            recentlyDbPlace.index = cursor.getInt(cursor.getColumnIndex(RecentlyList.PLACE_INDEX));
                            recentlyDbPlace.name = cursor.getString(cursor.getColumnIndex(RecentlyList.NAME));
                            recentlyDbPlace.englishName = cursor.getString(cursor.getColumnIndex(RecentlyList.ENGLISH_NAME));
                            recentlyDbPlace.savingTime = cursor.getLong(cursor.getColumnIndex(RecentlyList.SAVING_TIME));

                            Constants.ServiceType serviceType;

                            try
                            {
                                serviceType = Constants.ServiceType.valueOf(cursor.getString(cursor.getColumnIndex(RecentlyList.SERVICE_TYPE)));
                            } catch (Exception e)
                            {
                                serviceType = null;
                            }

                            recentlyDbPlace.serviceType = serviceType;
                            recentlyDbPlace.imageUrl = cursor.getString(cursor.getColumnIndex(RecentlyList.IMAGE_URL));

                            recentlyList.add(recentlyDbPlace);
                        } catch (Exception e)
                        {
                            ExLog.w("index : " + i + " , e : " + e.toString());
                        }
                    }

                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                } finally
                {
                    try
                    {
                        if (cursor != null)
                        {
                            cursor.close();
                        }
                    } catch (Exception e)
                    {
                    }
                }

                DailyDbHelper.getInstance().close();

                return Observable.just(recentlyList);
            }
        }).subscribeOn(Schedulers.io());
    }
}
