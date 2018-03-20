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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String RECENT_PLACE_DELIMITER = ",";

    Context mContext;

    public RecentlyLocalImpl(Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<Boolean> addRecentlyItem(Constants.ServiceType serviceType, int index //
        , String name, String englishName, String imageUrl, String areaGroupName, boolean isUpdateDate)
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
                dailyDb.addRecentlyPlace(serviceType, index, name, englishName, imageUrl, areaGroupName, isUpdateDate);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteRecentlyItem(Constants.ServiceType serviceType, int index)
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
    public Observable<Boolean> clearRecentlyItems(Constants.ServiceType serviceType)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (serviceType == null)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);
                dailyDb.deleteAllRecentlyItem(serviceType);

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
                            recentlyDbPlace.regionName = cursor.getString(cursor.getColumnIndex(RecentlyList.REGION_NAME));

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
                        return Long.compare(o2.savingTime, o1.savingTime);
                    }
                });

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

    @Override
    public Observable<String> getTargetIndices(Constants.ServiceType serviceType, final int maxSize)
    {
        return Observable.defer(new Callable<ObservableSource<String>>()
        {
            @Override
            public ObservableSource<String> call() throws Exception
            {
                int maxCount = maxSize;

                if (serviceType == null || maxCount <= 0)
                {
                    return Observable.just("");
                }

                StringBuilder builder = new StringBuilder();

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                Cursor cursor = null;

                try
                {
                    cursor = dailyDb.getRecentlyPlaces(-1, serviceType);

                    if (cursor == null || cursor.getCount() == 0 || maxCount <= 0)
                    {
                        return Observable.just("");
                    }

                    int size = cursor.getCount();
                    if (maxCount > size)
                    {
                        maxCount = size;
                    }

                    for (int i = 0; i < maxCount; i++)
                    {
                        cursor.moveToPosition(i);

                        int index = cursor.getInt(cursor.getColumnIndex(RecentlyList.PLACE_INDEX));

                        if (i != 0)
                        {
                            builder.append(RECENT_PLACE_DELIMITER);
                        }

                        builder.append(index);
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

                return Observable.just(builder.toString());
            }
        });
    }

    @Override
    public Observable<JSONObject> getRecentlyJSONObject(int maxSize, Constants.ServiceType... serviceTypes)
    {
        return getRecentlyTypeList(serviceTypes).flatMap(new Function<ArrayList<RecentlyDbPlace>, ObservableSource<JSONObject>>()
        {
            @Override
            public ObservableSource<JSONObject> apply(@NonNull ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
            {
                JSONObject recentlyJsonObject = new JSONObject();

                if (recentlyDbPlaces == null || recentlyDbPlaces.size() == 0)
                {
                    return Observable.just(recentlyJsonObject).subscribeOn(Schedulers.io());
                }

                try
                {
                    JSONArray recentlyJsonArray = getRecentlyJsonArray(recentlyDbPlaces, maxSize);
                    recentlyJsonObject.put("keys", recentlyJsonArray);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                return Observable.just(recentlyJsonObject);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ArrayList<Integer>> getRecentlyIndexList(Constants.ServiceType... serviceTypes)
    {
        return Observable.defer(new Callable<ObservableSource<ArrayList<Integer>>>()
        {
            @Override
            public ObservableSource<ArrayList<Integer>> call() throws Exception
            {
                if (serviceTypes == null || serviceTypes.length == 0)
                {
                    return Observable.just(new ArrayList<>());
                }

                ArrayList<Integer> indexList = new ArrayList<>();

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                Cursor cursor = null;

                try
                {
                    cursor = dailyDb.getRecentlyPlaces(-1, serviceTypes);

                    if (cursor == null || cursor.getCount() == 0)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    int size = cursor.getCount();
                    if (DailyDb.MAX_RECENT_PLACE_COUNT < size)
                    {
                        size = DailyDb.MAX_RECENT_PLACE_COUNT;
                    }

                    for (int i = 0; i < size; i++)
                    {
                        cursor.moveToPosition(i);

                        int index = cursor.getInt(cursor.getColumnIndex(RecentlyList.PLACE_INDEX));

                        indexList.add(index);
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

                return Observable.just(indexList);
            }
        }).subscribeOn(Schedulers.io());
    }

    static int getCarouselListItemIndex(CarouselListItem carouselListItem)
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

    JSONArray getRecentlyJsonArray(ArrayList<RecentlyDbPlace> list, int maxSize)
    {
        JSONArray jsonArray = new JSONArray();

        if (list == null || list.size() == 0 || maxSize == 0)
        {
            // dummy Data 생성
            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put("serviceType", "HOTEL");
                jsonObject.put("idx", 0);

                jsonArray.put(jsonObject);
            } catch (JSONException e)
            {
                ExLog.d(e.getMessage());
            }

            return jsonArray;
        }

        int size = list.size();

        if (maxSize > size)
        {
            maxSize = size;
        }

        for (int i = 0; i < maxSize; i++)
        {
            JSONObject jsonObject = new JSONObject();

            RecentlyDbPlace recentlyDbPlace = list.get(i);

            try
            {
                String serviceTypeString = recentlyDbPlace.serviceType.name();

                if (Constants.ServiceType.HOTEL.name().equalsIgnoreCase(serviceTypeString) == true //
                    || Constants.ServiceType.GOURMET.name().equalsIgnoreCase(serviceTypeString) == true)
                {
                    int index = recentlyDbPlace.index;

                    jsonObject.put("serviceType", serviceTypeString);
                    jsonObject.put("idx", index);

                    jsonArray.put(jsonObject);
                }
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        }

        return jsonArray;
    }
}
