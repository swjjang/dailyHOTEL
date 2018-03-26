package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.LoganSquare;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.StayObRecentlySuggestColumns;
import com.daily.dailyhotel.domain.SuggestLocalInterface;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.repository.local.model.GourmetSuggestData;
import com.daily.dailyhotel.repository.local.model.StayIbRecentlySuggestList;
import com.daily.dailyhotel.repository.local.model.StaySuggestData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundSuggestData;
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

                String suggestString;
                try
                {
                    suggestString = LoganSquare.serialize(stayOutboundSuggest.getSuggestData());
                } catch (Exception e)
                {
                    suggestString = null;
                }

                if (DailyTextUtils.isTextEmpty(suggestString))
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                dailyDb.addStayObRecentlySuggest(stayOutboundSuggest.id, suggestString, keyword, true);

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

                        String suggestString = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.SUGGEST));

                        StayOutboundSuggestData stayOutboundSuggestData = LoganSquare.parse(suggestString, StayOutboundSuggestData.class);
                        stayOutboundSuggest = stayOutboundSuggestData.getSuggests();
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

                        String suggestString = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.SUGGEST));

                        StayOutboundSuggestData stayOutboundSuggestData = LoganSquare.parse(suggestString, StayOutboundSuggestData.class);
                        StayOutboundSuggest stayOutboundSuggest = stayOutboundSuggestData.getSuggests();
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
    public Observable<Boolean> addRecentlyGourmetSuggest(GourmetSuggest gourmetSuggest, String keyword)
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

                if (gourmetSuggest.getSuggestType() == GourmetSuggest.SuggestType.UNKNOWN)
                {
                    return Observable.just(false);
                }

                String suggestString;
                try
                {
                    suggestString = LoganSquare.serialize(gourmetSuggest.getSuggestData());
                } catch (Exception e)
                {
                    suggestString = null;
                }

                if (DailyTextUtils.isTextEmpty(suggestString))
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                String type = null;
                String displayName = null;

                try
                {
                    switch (gourmetSuggest.getSuggestType())
                    {
                        case GOURMET:
                        {
                            type = GourmetSuggest.SuggestType.GOURMET.name();
                            GourmetSuggest.Gourmet gourmet = (GourmetSuggest.Gourmet) gourmetSuggest.getSuggestItem();
                            displayName = gourmet.name;
                            break;
                        }

                        case AREA_GROUP:
                        {
                            type = GourmetSuggest.SuggestType.AREA_GROUP.name();
                            GourmetSuggest.AreaGroup areaGroup = (GourmetSuggest.AreaGroup) gourmetSuggest.getSuggestItem();
                            displayName = areaGroup.getDisplayName();
                            break;
                        }

                        case LOCATION:
                        {
                            type = GourmetSuggest.SuggestType.LOCATION.name();
                            GourmetSuggest.Location location = (GourmetSuggest.Location) gourmetSuggest.getSuggestItem();
                            displayName = location.name;
                            break;
                        }

                        case DIRECT:
                        {
                            type = GourmetSuggest.SuggestType.DIRECT.name();
                            GourmetSuggest.Direct direct = (GourmetSuggest.Direct) gourmetSuggest.getSuggestItem();
                            displayName = direct.name;
                            break;
                        }

                        default:
                            return Observable.just(false);
                    }

                    dailyDb.addGourmetRecentlySuggest(type, displayName, suggestString, keyword);
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
    public Observable<List<GourmetSuggest>> getRecentlyGourmetSuggestList(int maxCount)
    {
        return Observable.defer(new Callable<ObservableSource<List<GourmetSuggest>>>()
        {
            @Override
            public ObservableSource<List<GourmetSuggest>> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                ArrayList<GourmetSuggest> gourmetSuggestList = null;
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

                        String suggestString = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.SUGGEST));

                        GourmetSuggestData gourmetSuggestData = LoganSquare.parse(suggestString, GourmetSuggestData.class);
                        GourmetSuggest staySuggest = gourmetSuggestData.getSuggest();
                        staySuggest.menuType = GourmetSuggest.MenuType.RECENTLY_SEARCH;

                        gourmetSuggestList.add(staySuggest);
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
    public Observable<Boolean> deleteRecentlyGourmetSuggest(GourmetSuggest gourmetSuggest)
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

                String type = gourmetSuggest.getSuggestType().name();
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
    public Observable<Boolean> addRecentlyStaySuggest(StaySuggest staySuggest, String keyword)
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

                if (staySuggest.getSuggestType() == StaySuggest.SuggestType.UNKNOWN)
                {
                    return Observable.just(false);
                }

                String suggestString;
                try
                {
                    suggestString = LoganSquare.serialize(staySuggest.getSuggestData());
                } catch (Exception e)
                {
                    suggestString = null;
                }

                if (DailyTextUtils.isTextEmpty(suggestString))
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                String type = null;
                String displayName = null;

                try
                {
                    switch (staySuggest.getSuggestType())
                    {
                        case STATION:
                        {
                            type = StaySuggest.SuggestType.STATION.name();

                            StaySuggest.Station station = (StaySuggest.Station) staySuggest.getSuggestItem();
                            displayName = station.getDisplayName();
                            break;
                        }

                        case STAY:
                        {
                            type = StaySuggest.SuggestType.STAY.name();
                            StaySuggest.Stay stay = (StaySuggest.Stay) staySuggest.getSuggestItem();
                            displayName = stay.name;
                            break;
                        }

                        case AREA_GROUP:
                        {
                            type = StaySuggest.SuggestType.AREA_GROUP.name();
                            StaySuggest.AreaGroup areaGroup = (StaySuggest.AreaGroup) staySuggest.getSuggestItem();
                            displayName = areaGroup.getDisplayName();
                            break;
                        }

                        case LOCATION:
                        {
                            type = StaySuggest.SuggestType.LOCATION.name();
                            StaySuggest.Location location = (StaySuggest.Location) staySuggest.getSuggestItem();
                            displayName = location.name;
                            break;
                        }

                        case DIRECT:
                        {
                            type = StaySuggest.SuggestType.DIRECT.name();
                            StaySuggest.Direct direct = (StaySuggest.Direct) staySuggest.getSuggestItem();
                            displayName = direct.name;
                            break;
                        }

                        default:
                            return Observable.just(false);
                    }

                    dailyDb.addStayIbRecentlySuggest(type, displayName, suggestString, keyword);
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
    public Observable<List<StaySuggest>> getRecentlyStaySuggestList(int maxCount)
    {
        return Observable.defer(new Callable<ObservableSource<List<StaySuggest>>>()
        {
            @Override
            public ObservableSource<List<StaySuggest>> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                ArrayList<StaySuggest> staySuggestList = null;
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

                        String suggestString = cursor.getString(cursor.getColumnIndex(StayIbRecentlySuggestList.SUGGEST));

                        StaySuggestData staySuggestData = LoganSquare.parse(suggestString, StaySuggestData.class);
                        StaySuggest staySuggest = staySuggestData.getSuggest();
                        staySuggest.menuType = StaySuggest.MenuType.RECENTLY_SEARCH;

                        staySuggestList.add(staySuggest);
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
    public Observable<Boolean> deleteRecentlyStaySuggest(StaySuggest staySuggest)
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

                String type = staySuggest.getSuggestType().name();
                String name = staySuggest.getText1();
                ExLog.d("sam : type : " + type + " , name : " + name);

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
