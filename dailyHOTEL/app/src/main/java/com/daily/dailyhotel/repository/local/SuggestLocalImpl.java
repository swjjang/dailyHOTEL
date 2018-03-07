package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.StayObRecentlySuggestColumns;
import com.daily.dailyhotel.domain.SuggestLocalInterface;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 9. 29..
 */
public class SuggestLocalImpl implements SuggestLocalInterface
{
    Context mContext;

    public SuggestLocalImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<Boolean> addStayOutboundSuggestDb(StayOutboundSuggest stayOutboundSuggest, String keyword)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (stayOutboundSuggest == null)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                dailyDb.addStayObRecentlySuggest(stayOutboundSuggest.id, stayOutboundSuggest.name, stayOutboundSuggest.city, stayOutboundSuggest.country //
                    , stayOutboundSuggest.countryCode, stayOutboundSuggest.categoryKey, stayOutboundSuggest.display, stayOutboundSuggest.latitude //
                    , stayOutboundSuggest.longitude, keyword, true);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<StayOutboundSuggest> getRecentlyStayOutboundSuggest()
    {
        return Observable.defer(new Callable<ObservableSource<StayOutboundSuggest>>()
        {
            @Override
            public ObservableSource<StayOutboundSuggest> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                StayOutboundSuggest stayOutboundSuggest = null;
                Cursor cursor = null;

                try
                {
                    cursor = dailyDb.getStayObRecentlySuggestList(1);

                    if (cursor != null && cursor.getCount() > 0)
                    {
                        cursor.moveToFirst();

                        long id = cursor.getLong(cursor.getColumnIndex(StayObRecentlySuggestColumns._ID));
                        String name = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.NAME));
                        String city = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.CITY));
                        String country = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.COUNTRY));
                        String countryCode = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.COUNTRY_CODE));
                        String categoryKey = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.CATEGORY_KEY));
                        String display = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.DISPLAY));
                        double latitude = cursor.getDouble(cursor.getColumnIndex(StayObRecentlySuggestColumns.LATITUDE));
                        double longitude = cursor.getDouble(cursor.getColumnIndex(StayObRecentlySuggestColumns.LONGITUDE));

                        stayOutboundSuggest = new StayOutboundSuggest(id, name, city, country, countryCode, categoryKey, display, latitude, longitude);
                        stayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_RECENTLY_SEARCH;
                    }

                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    stayOutboundSuggest = null;
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

                return Observable.just(stayOutboundSuggest);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<StayOutboundSuggest>> getRecentlyStayOutboundSuggestList(int maxCount)
    {
        final int maxSize = maxCount < 1 ? DailyDb.MAX_RECENT_PLACE_COUNT : maxCount;

        return Observable.defer(new Callable<ObservableSource<List<StayOutboundSuggest>>>()
        {
            @Override
            public ObservableSource<List<StayOutboundSuggest>> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                ArrayList<StayOutboundSuggest> stayOutboundSuggestList = null;
                Cursor cursor = null;
                try
                {
                    cursor = dailyDb.getStayObRecentlySuggestList(maxSize);

                    if (cursor == null)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    int size = cursor.getCount();
                    if (size == 0)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    stayOutboundSuggestList = new ArrayList<>();

                    for (int i = 0; i < size; i++)
                    {
                        cursor.moveToPosition(i);

                        long id = cursor.getLong(cursor.getColumnIndex(StayObRecentlySuggestColumns._ID));
                        String name = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.NAME));
                        String city = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.CITY));
                        String country = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.COUNTRY));
                        String countryCode = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.COUNTRY_CODE));
                        String categoryKey = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.CATEGORY_KEY));
                        String display = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.DISPLAY));
                        double latitude = cursor.getDouble(cursor.getColumnIndex(StayObRecentlySuggestColumns.LATITUDE));
                        double longitude = cursor.getDouble(cursor.getColumnIndex(StayObRecentlySuggestColumns.LONGITUDE));

                        StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest(id, name, city, country, countryCode, categoryKey, display, latitude, longitude);
                        stayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_RECENTLY_SEARCH;

                        stayOutboundSuggestList.add(stayOutboundSuggest);
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

                if (stayOutboundSuggestList == null)
                {
                    stayOutboundSuggestList = new ArrayList<>();
                }

                return Observable.just(stayOutboundSuggestList);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<String> getRecentlyStayOutboundSuggestKeyword(final long id)
    {
        return Observable.defer(new Callable<ObservableSource<String>>()
        {
            @Override
            public ObservableSource<String> call() throws Exception
            {
                if (id <= 0)
                {
                    return Observable.just("");
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                String keyword = null;

                try
                {
                    keyword = dailyDb.getStayObRecentlySuggestKeyword(id);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                DailyDbHelper.getInstance().close();

                if (DailyTextUtils.isTextEmpty(keyword) == true)
                {
                    keyword = "";
                }

                return Observable.just(keyword);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteAllRecentlyStayOutboundSuggest()
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                try
                {
                    dailyDb.deleteAllStayObRecentlySuggest();
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteRecentlyStayOutboundSuggest(long id)
    {
        return Observable.defer(new Callable<Observable<Boolean>>()
        {
            @Override
            public Observable<Boolean> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                try
                {
                    dailyDb.deleteStayObRecentlySuggest(id);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> addRecentlyGourmetSuggest(GourmetSuggestV2 gourmetSuggest, String keyword)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (gourmetSuggest == null)
                {
                    return Observable.just(false);
                }

                GourmetSuggestV2.SuggestItem suggestItem = gourmetSuggest.suggestItem;
                if (suggestItem == null)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                try
                {
                    if (suggestItem instanceof GourmetSuggestV2.Gourmet)
                    {
                        GourmetSuggestV2.Gourmet gourmet = (GourmetSuggestV2.Gourmet) suggestItem;
                        GourmetSuggestV2.Province province = gourmet.province;
                        GourmetSuggestV2.Area area = province != null ? province.area : null;

                        String type = GourmetSuggestV2.Gourmet.class.getSimpleName();
                        int provinceIndex = province == null ? 0 : province.index;
                        String provinceName = province == null ? null : province.name;
                        int areaIndex = area == null ? 0 : area.index;
                        String areaName = area == null ? null : area.name;

                        dailyDb.addGourmetRecentlySuggest(type, gourmet.name, gourmet.index //
                            , provinceIndex, provinceName, areaIndex, areaName, null //
                            , 0, 0, keyword);
                    } else if (suggestItem instanceof GourmetSuggestV2.Province)
                    {
                        GourmetSuggestV2.Province province = (GourmetSuggestV2.Province) suggestItem;
                        GourmetSuggestV2.Area area = province.area;

                        String type = GourmetSuggestV2.Province.class.getSimpleName();
                        int areaIndex = area == null ? 0 : area.index;
                        String areaName = area == null ? null : area.name;

                        dailyDb.addGourmetRecentlySuggest(type, province.name, 0 //
                            , province.index, province.name, areaIndex, areaName, null //
                            , 0, 0, keyword);
                    } else if (suggestItem instanceof GourmetSuggestV2.Location)
                    {
                        GourmetSuggestV2.Location location = (GourmetSuggestV2.Location) suggestItem;
                        String type = GourmetSuggestV2.Location.class.getSimpleName();

                        dailyDb.addGourmetRecentlySuggest(type, location.name, 0 //
                            , 0, null, 0, null, location.address //
                            , location.latitude, location.longitude, keyword);
                    } else if (suggestItem instanceof GourmetSuggestV2.Direct)
                    {
                        GourmetSuggestV2.Direct direct = (GourmetSuggestV2.Direct) suggestItem;
                        String type = GourmetSuggestV2.Direct.class.getSimpleName();

                        dailyDb.addGourmetRecentlySuggest(type, direct.name, 0 //
                            , 0, null, 0, null, null //
                            , 0, 0, keyword);
                    } else
                    {
                        return Observable.just(false);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                return Observable.just(true);
            }
        });
    }

    @Override
    public Observable<List<GourmetSuggestV2>> getRecentlyGourmetSuggestList(int maxCount)
    {
        return Observable.defer(new Callable<ObservableSource<List<GourmetSuggestV2>>>()
        {
            @Override
            public ObservableSource<List<GourmetSuggestV2>> call() throws Exception
            {
                return null;
            }
        });
    }

    @Override
    public Observable<Boolean> deleteRecentlyGourmetSuggest(GourmetSuggestV2 gourmetSuggest)
    {
        return Observable.defer(new Callable<Observable<Boolean>>()
        {
            @Override
            public Observable<Boolean> call() throws Exception
            {
                if (gourmetSuggest == null)
                {
                    return Observable.just(false);
                }

                if (gourmetSuggest.suggestItem == null)
                {
                    return Observable.just(false);
                }

                String type = gourmetSuggest.suggestItem.getClass().getSimpleName();
                String name = gourmetSuggest.suggestItem.name;
                ExLog.d("sam : type : " + type + " , name : " + name);

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                try
                {
                    dailyDb.deleteGourmetRecentlySuggest(type, name);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }
}
