package com.daily.dailyhotel.repository.local;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyLocalInterface;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.local.model.RecentlyList;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
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

    @Override
    public Observable<ArrayList<CarouselListItem>> sortCarouselListItemList(ArrayList<CarouselListItem> actualList, Constants.ServiceType... serviceTypes)
    {
        if (actualList == null || actualList.size() == 0)
        {
            return Observable.just(new ArrayList<>());
        }

        return getRecentlyTypeList(serviceTypes).flatMap(new Function<ArrayList<RecentlyDbPlace>, ObservableSource<ArrayList<CarouselListItem>>>()
        {
            @Override
            public ObservableSource<ArrayList<CarouselListItem>> apply(@NonNull ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
            {
                Collections.sort(recentlyDbPlaces, new Comparator<RecentlyDbPlace>()
                {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public int compare(RecentlyDbPlace o1, RecentlyDbPlace o2)
                    {
                        return Long.compare(o1.savingTime, o2.savingTime);
                    }
                });

                Collections.reverse(recentlyDbPlaces);

                if (recentlyDbPlaces == null || recentlyDbPlaces.size() == 0)
                {
                    return Observable.just(actualList);
                }

                ArrayList<Integer> expectedList = new ArrayList<>();
                for (RecentlyDbPlace recentlyDbPlace : recentlyDbPlaces)
                {
                    expectedList.add(recentlyDbPlace.index);
                }

                Collections.sort(actualList, new Comparator<CarouselListItem>()
                {
                    @Override
                    public int compare(CarouselListItem item1, CarouselListItem item2)
                    {
                        Integer position1 = expectedList.indexOf(getCarouselListItemIndex(item1));
                        Integer position2 = expectedList.indexOf(getCarouselListItemIndex(item2));
                        return position1.compareTo(position2);
                    }
                });

                return Observable.just(actualList);
            }
        }).subscribeOn(Schedulers.io());
    }

    private static int getCarouselListItemIndex(CarouselListItem carouselListItem)
    {
        int index = -1;
        switch (carouselListItem.mType)
        {
            case CarouselListItem.TYPE_RECENTLY_PLACE:
            {
                RecentlyPlace item = carouselListItem.getItem();
                index = item.index;
                break;
            }

            case CarouselListItem.TYPE_IN_STAY:
            {
                Stay item = carouselListItem.getItem();
                index = item.index;
                break;
            }

            case CarouselListItem.TYPE_OB_STAY:
            {
                StayOutbound item = carouselListItem.getItem();
                index = item.index;
                break;
            }

            case CarouselListItem.TYPE_GOURMET:
            {
                Gourmet item = carouselListItem.getItem();
                index = item.index;
                break;
            }
        }

        return index;
    }
}
