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
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.repository.local.model.GourmetRecentlySuggestList;
import com.daily.dailyhotel.repository.local.model.StayIbRecentlySuggestList;
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

                if (gourmetSuggest.getCategory() == GourmetSuggestV2.Category.UNKNOWN)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                try
                {
                    switch (gourmetSuggest.getCategory())
                    {
                        case GOURMET:
                        {
                            GourmetSuggestV2.Gourmet gourmet = (GourmetSuggestV2.Gourmet) gourmetSuggest.suggestItem;
                            GourmetSuggestV2.AreaGroup areaGroup = gourmet.areaGroup;
                            GourmetSuggestV2.Area area = areaGroup != null ? areaGroup.area : null;

                            String type = GourmetSuggestV2.Category.GOURMET.name();
                            int areaGroupIndex = areaGroup == null ? 0 : areaGroup.index;
                            String areaGroupName = areaGroup == null ? null : areaGroup.name;
                            int areaIndex = area == null ? 0 : area.index;
                            String areaName = area == null ? null : area.name;

                            dailyDb.addGourmetRecentlySuggest(type, gourmet.name //
                                , gourmet.index, gourmet.name //
                                , areaGroupIndex, areaGroupName, areaIndex, areaName //
                                , null, null, 0, 0 //
                                , null, keyword);
                            break;
                        }

                        case AREA_GROUP:
                        {
                            GourmetSuggestV2.AreaGroup areaGroup = (GourmetSuggestV2.AreaGroup) gourmetSuggest.suggestItem;
                            GourmetSuggestV2.Area area = areaGroup.area;

                            String type = GourmetSuggestV2.Category.AREA_GROUP.name();
                            int areaIndex = area == null ? 0 : area.index;
                            String areaName = area == null ? null : area.name;

                            dailyDb.addGourmetRecentlySuggest(type, areaGroup.getDisplayName() //
                                , 0, null //
                                , areaGroup.index, areaGroup.name, areaIndex, areaName //
                                , null, null, 0, 0 //
                                , null, keyword);
                            break;
                        }

                        case LOCATION:
                        {
                            GourmetSuggestV2.Location location = (GourmetSuggestV2.Location) gourmetSuggest.suggestItem;
                            String type = GourmetSuggestV2.Category.LOCATION.name();

                            dailyDb.addGourmetRecentlySuggest(type, location.name //
                                , 0, null //
                                , 0, null, 0, null //
                                , location.name, location.address, location.latitude, location.longitude //
                                , null, keyword);
                            break;
                        }

                        case DIRECT:
                        {
                            GourmetSuggestV2.Direct direct = (GourmetSuggestV2.Direct) gourmetSuggest.suggestItem;
                            String type = GourmetSuggestV2.Category.DIRECT.name();

                            dailyDb.addGourmetRecentlySuggest(type, direct.name //
                                , 0, null //
                                , 0, null, 0, null //
                                , null, null, 0, 0 //
                                , direct.name, keyword);
                            break;
                        }

                        default:
                            return Observable.just(false);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                } finally
                {
                    DailyDbHelper.getInstance().close();
                }

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<GourmetSuggestV2>> getRecentlyGourmetSuggestList(int maxCount)
    {
        return Observable.defer(new Callable<ObservableSource<List<GourmetSuggestV2>>>()
        {
            @Override
            public ObservableSource<List<GourmetSuggestV2>> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                ArrayList<GourmetSuggestV2> gourmetSuggestList = null;
                Cursor cursor = null;
                try
                {
                    cursor = dailyDb.getGourmetRecentlySuggestList(maxCount);

                    if (cursor == null)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    int size = cursor.getCount();
                    if (size == 0)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    gourmetSuggestList = new ArrayList<>();

                    for (int i = 0; i < size; i++)
                    {
                        cursor.moveToPosition(i);

                        String type = cursor.getString(cursor.getColumnIndex(GourmetRecentlySuggestList.TYPE));

                        GourmetSuggestV2.Category category;
                        try
                        {
                            category = GourmetSuggestV2.Category.valueOf(type);
                        } catch (Exception e)
                        {
                            category = GourmetSuggestV2.Category.UNKNOWN;
                        }

                        switch (category)
                        {
                            case GOURMET:
                            {
                                int gourmetIndex = cursor.getInt(cursor.getColumnIndex(GourmetRecentlySuggestList.GOURMET_INDEX));
                                String gourmetName = cursor.getString(cursor.getColumnIndex(GourmetRecentlySuggestList.GOURMET_NAME));
                                int areaGroupIndex = cursor.getInt(cursor.getColumnIndex(GourmetRecentlySuggestList.AREA_GROUP_INDEX));
                                String areaGroupName = cursor.getString(cursor.getColumnIndex(GourmetRecentlySuggestList.AREA_GROUP_NAME));

                                GourmetSuggestV2.Gourmet gourmet = new GourmetSuggestV2.Gourmet();
                                GourmetSuggestV2.AreaGroup areaGroup = new GourmetSuggestV2.AreaGroup();

                                areaGroup.index = areaGroupIndex;
                                areaGroup.name = areaGroupName;
                                areaGroup.area = null;

                                gourmet.index = gourmetIndex;
                                gourmet.name = gourmetName;
                                gourmet.areaGroup = areaGroup;

                                gourmetSuggestList.add(new GourmetSuggestV2(GourmetSuggestV2.MenuType.RECENTLY_SEARCH, gourmet));
                                break;
                            }

                            case AREA_GROUP:
                            {
                                int areaGroupIndex = cursor.getInt(cursor.getColumnIndex(GourmetRecentlySuggestList.AREA_GROUP_INDEX));
                                String areaGroupName = cursor.getString(cursor.getColumnIndex(GourmetRecentlySuggestList.AREA_GROUP_NAME));
                                int areaIndex = cursor.getInt(cursor.getColumnIndex(GourmetRecentlySuggestList.AREA_INDEX));
                                String areaName = cursor.getString(cursor.getColumnIndex(GourmetRecentlySuggestList.AREA_NAME));

                                GourmetSuggestV2.AreaGroup areaGroup = new GourmetSuggestV2.AreaGroup();
                                GourmetSuggestV2.Area area = null;

                                if (areaIndex > 0 && DailyTextUtils.isTextEmpty(areaName) == false)
                                {
                                    area = new GourmetSuggestV2.Area();
                                    area.index = areaIndex;
                                    area.name = areaName;
                                }

                                areaGroup.index = areaGroupIndex;
                                areaGroup.name = areaGroupName;
                                areaGroup.area = area;

                                gourmetSuggestList.add(new GourmetSuggestV2(GourmetSuggestV2.MenuType.RECENTLY_SEARCH, areaGroup));
                                break;
                            }

                            case DIRECT:
                            {
                                String directName = cursor.getString(cursor.getColumnIndex(GourmetRecentlySuggestList.DIRECT_NAME));
                                GourmetSuggestV2.Direct direct = new GourmetSuggestV2.Direct(directName);
                                gourmetSuggestList.add(new GourmetSuggestV2(GourmetSuggestV2.MenuType.RECENTLY_SEARCH, direct));
                                break;
                            }

                            case LOCATION:
                            {
                                String locationName = cursor.getString(cursor.getColumnIndex(GourmetRecentlySuggestList.LOCATION_NAME));
                                String address = cursor.getString(cursor.getColumnIndex(GourmetRecentlySuggestList.ADDRESS));
                                double latitude = cursor.getDouble(cursor.getColumnIndex(GourmetRecentlySuggestList.LATITUDE));
                                double longitude = cursor.getDouble(cursor.getColumnIndex(GourmetRecentlySuggestList.LONGITUDE));

                                GourmetSuggestV2.Location location = new GourmetSuggestV2.Location();
                                location.name = locationName;
                                location.address = address;
                                location.latitude = latitude;
                                location.longitude = longitude;

                                gourmetSuggestList.add(new GourmetSuggestV2(GourmetSuggestV2.MenuType.RECENTLY_SEARCH, location));
                                break;
                            }
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

                if (gourmetSuggestList == null)
                {
                    gourmetSuggestList = new ArrayList<>();
                }

                return Observable.just(gourmetSuggestList);
            }
        }).subscribeOn(Schedulers.io());
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

                String type = gourmetSuggest.getCategory().name();
                String display = gourmetSuggest.getText1();
                ExLog.d("sam : type : " + type + " , display : " + display);

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                try
                {
                    dailyDb.deleteGourmetRecentlySuggest(type, display);
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
    public Observable<Boolean> addRecentlyStaySuggest(StaySuggestV2 staySuggest, String keyword)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (staySuggest == null)
                {
                    return Observable.just(false);
                }

                StaySuggestV2.SuggestItem suggestItem = staySuggest.suggestItem;
                if (suggestItem == null)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                try
                {
                    if (suggestItem instanceof StaySuggestV2.Station)
                    {
                        StaySuggestV2.Station station = (StaySuggestV2.Station) suggestItem;
                        String type = StaySuggestV2.Station.class.getSimpleName();

                        dailyDb.addStayIbRecentlySuggest(type, station.getDisplayName() //
                            , station.index, station.name, station.region, station.line //
                            , 0, null //
                            , 0, null, 0, null //
                            , null, null, 0, 0 //
                            , null, keyword);
                    } else if (suggestItem instanceof StaySuggestV2.Stay)
                    {
                        StaySuggestV2.Stay stay = (StaySuggestV2.Stay) suggestItem;
                        StaySuggestV2.AreaGroup areaGroup = stay.areaGroup;
                        StaySuggestV2.Area area = areaGroup != null ? areaGroup.area : null;

                        String type = StaySuggestV2.Stay.class.getSimpleName();
                        int areaGroupIndex = areaGroup == null ? 0 : areaGroup.index;
                        String areaGroupName = areaGroup == null ? null : areaGroup.name;
                        int areaIndex = area == null ? 0 : area.index;
                        String areaName = area == null ? null : area.name;

                        dailyDb.addStayIbRecentlySuggest(type, stay.name //
                            , 0, null, null, null //
                            , stay.index, stay.name //
                            , areaGroupIndex, areaGroupName, areaIndex, areaName //
                            , null, null, 0, 0 //
                            , null, keyword);
                    } else if (suggestItem instanceof StaySuggestV2.AreaGroup)
                    {
                        StaySuggestV2.AreaGroup areaGroup = (StaySuggestV2.AreaGroup) suggestItem;
                        StaySuggestV2.Area area = areaGroup.area;

                        String type = StaySuggestV2.AreaGroup.class.getSimpleName();
                        int areaIndex = area == null ? 0 : area.index;
                        String areaName = area == null ? null : area.name;

                        dailyDb.addStayIbRecentlySuggest(type, areaGroup.getDisplayName() //
                            , 0, null, null, null //
                            , 0, null //
                            , areaGroup.index, areaGroup.name, areaIndex, areaName //
                            , null, null, 0, 0 //
                            , null, keyword);
                    } else if (suggestItem instanceof StaySuggestV2.Location)
                    {
                        StaySuggestV2.Location location = (StaySuggestV2.Location) suggestItem;
                        String type = StaySuggestV2.Location.class.getSimpleName();

                        dailyDb.addStayIbRecentlySuggest(type, location.name //
                            , 0, null, null, null //
                            , 0, null //
                            , 0, null, 0, null //
                            , location.name, location.address, location.latitude, location.longitude //
                            , null, keyword);
                    } else if (suggestItem instanceof StaySuggestV2.Direct)
                    {
                        StaySuggestV2.Direct direct = (StaySuggestV2.Direct) suggestItem;
                        String type = StaySuggestV2.Direct.class.getSimpleName();

                        dailyDb.addStayIbRecentlySuggest(type, direct.name //
                            , 0, null, null, null //
                            , 0, null //
                            , 0, null, 0, null //
                            , null, null, 0, 0 //
                            , direct.name, keyword);
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
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<StaySuggestV2>> getRecentlyStaySuggestList(int maxCount)
    {
        return Observable.defer(new Callable<ObservableSource<List<StaySuggestV2>>>()
        {
            @Override
            public ObservableSource<List<StaySuggestV2>> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                ArrayList<StaySuggestV2> staySuggestList = null;
                Cursor cursor = null;
                try
                {
                    cursor = dailyDb.getStayIbRecentlySuggestList(maxCount);

                    if (cursor == null)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    int size = cursor.getCount();
                    if (size == 0)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    staySuggestList = new ArrayList<>();

                    for (int i = 0; i < size; i++)
                    {
                        cursor.moveToPosition(i);

                        String type = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.TYPE));

                        if (StaySuggestV2.Station.class.getSimpleName().equalsIgnoreCase(type))
                        {
                            int stationIndex = cursor.getInt(cursor.getColumnIndex(StayIbRecentlySuggestList.STATION_INDEX));
                            String stationName = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.STATION_NAME));
                            String stationRegion = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.STATION_REGION));
                            String stationLine = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.STATION_LINE));

                            StaySuggestV2.Station station = new StaySuggestV2.Station();
                            station.index = stationIndex;
                            station.name = stationName;
                            station.region = stationRegion;
                            station.line = stationLine;

                            staySuggestList.add(new StaySuggestV2(StaySuggestV2.MenuType.RECENTLY_SEARCH, station));

                        } else if (StaySuggestV2.Stay.class.getSimpleName().equalsIgnoreCase(type))
                        {
                            int stayIndex = cursor.getInt(cursor.getColumnIndex(StayIbRecentlySuggestList.STAY_INDEX));
                            String stayName = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.STAY_NAME));
                            int areaGroupIndex = cursor.getInt(cursor.getColumnIndex(StayIbRecentlySuggestList.AREA_GROUP_INDEX));
                            String areaGroupName = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.AREA_GROUP_NAME));

                            StaySuggestV2.Stay stay = new StaySuggestV2.Stay();
                            StaySuggestV2.AreaGroup areaGroup = new StaySuggestV2.AreaGroup();

                            areaGroup.index = areaGroupIndex;
                            areaGroup.name = areaGroupName;
                            areaGroup.area = null;

                            stay.index = stayIndex;
                            stay.name = stayName;
                            stay.areaGroup = areaGroup;

                            staySuggestList.add(new StaySuggestV2(StaySuggestV2.MenuType.RECENTLY_SEARCH, stay));

                        } else if (StaySuggestV2.AreaGroup.class.getSimpleName().equalsIgnoreCase(type))
                        {
                            int areaGroupIndex = cursor.getInt(cursor.getColumnIndex(StayIbRecentlySuggestList.AREA_GROUP_INDEX));
                            String areaGroupName = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.AREA_GROUP_NAME));
                            int areaIndex = cursor.getInt(cursor.getColumnIndex(StayIbRecentlySuggestList.AREA_INDEX));
                            String areaName = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.AREA_NAME));

                            StaySuggestV2.AreaGroup areaGroup = new StaySuggestV2.AreaGroup();
                            StaySuggestV2.Area area = null;

                            if (areaIndex > 0 && DailyTextUtils.isTextEmpty(areaName) == false)
                            {
                                area = new StaySuggestV2.Area();
                                area.index = areaIndex;
                                area.name = areaName;
                            }

                            areaGroup.index = areaGroupIndex;
                            areaGroup.name = areaGroupName;
                            areaGroup.area = area;

                            staySuggestList.add(new StaySuggestV2(StaySuggestV2.MenuType.RECENTLY_SEARCH, areaGroup));

                        } else if (StaySuggestV2.Direct.class.getSimpleName().equalsIgnoreCase(type))
                        {
                            String directName = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.DIRECT_NAME));
                            StaySuggestV2.Direct direct = new StaySuggestV2.Direct(directName);
                            staySuggestList.add(new StaySuggestV2(StaySuggestV2.MenuType.RECENTLY_SEARCH, direct));

                        } else if (StaySuggestV2.Location.class.getSimpleName().equalsIgnoreCase(type))
                        {
                            String locationName = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.LOCATION_NAME));
                            String address = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.ADDRESS));
                            double latitude = cursor.getDouble(cursor.getColumnIndex(StayIbRecentlySuggestList.LATITUDE));
                            double longitude = cursor.getDouble(cursor.getColumnIndex(StayIbRecentlySuggestList.LONGITUDE));

                            StaySuggestV2.Location location = new StaySuggestV2.Location();
                            location.name = locationName;
                            location.address = address;
                            location.latitude = latitude;
                            location.longitude = longitude;

                            staySuggestList.add(new StaySuggestV2(StaySuggestV2.MenuType.RECENTLY_SEARCH, location));
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

                if (staySuggestList == null)
                {
                    staySuggestList = new ArrayList<>();
                }

                return Observable.just(staySuggestList);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteRecentlyStaySuggest(StaySuggestV2 staySuggest)
    {
        return Observable.defer(new Callable<Observable<Boolean>>()
        {
            @Override
            public Observable<Boolean> call() throws Exception
            {
                if (staySuggest == null)
                {
                    return Observable.just(false);
                }

                StaySuggestV2.SuggestItem item = staySuggest.suggestItem;
                if (item == null)
                {
                    return Observable.just(false);
                }

                String type = item.getClass().getSimpleName();
                String name = item.name;
                ExLog.d("sam : type : " + type + " , name : " + name);
                if (item instanceof StaySuggestV2.AreaGroup)
                {
                    StaySuggestV2.AreaGroup areaGroup = (StaySuggestV2.AreaGroup) item;
                    name = areaGroup.getDisplayName();
                } else if (item instanceof StaySuggestV2.Station)
                {
                    StaySuggestV2.Station station = (StaySuggestV2.Station) item;
                    name = station.getDisplayName();
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                try
                {
                    dailyDb.deleteStayIbRecentlySuggest(type, name);
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
