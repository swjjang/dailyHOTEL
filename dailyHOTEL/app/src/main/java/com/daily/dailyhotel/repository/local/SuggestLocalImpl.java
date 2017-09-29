package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.StayObRecentlySuggestColumns;
import com.daily.dailyhotel.domain.SuggestLocalInterface;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 9. 29..
 */
public class SuggestLocalImpl implements SuggestLocalInterface
{
    private Context mContext;

    public SuggestLocalImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable addSuggestDb(Suggest suggest)
    {
        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                if (suggest == null)
                {
                    observer.onNext(true);
                    observer.onComplete();
                    return;
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                dailyDb.addStayObRecentlySuggest(suggest.id, suggest.name, suggest.city, suggest.country //
                    , suggest.countryCode, suggest.categoryKey, suggest.display, suggest.latitude //
                    , suggest.longitude, true);

                DailyDbHelper.getInstance().close();

                observer.onNext(true);
                observer.onComplete();
            }
        };

        return observable.subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Suggest> getRecentlySuggest()
    {
        return Observable.defer(new Callable<ObservableSource<Suggest>>()
        {
            @Override
            public ObservableSource<Suggest> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                Suggest suggest = null;
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

                        suggest = new Suggest(id, name, city, country, countryCode, categoryKey, display, latitude, longitude);
                    }

                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    suggest = null;
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

                return Observable.just(suggest);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Suggest>> getRecentlySuggestList()
    {
        return Observable.defer(new Callable<ObservableSource<List<Suggest>>>()
        {
            @Override
            public ObservableSource<List<Suggest>> call() throws Exception
            {
                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                ArrayList<Suggest> suggestList = null;
                Cursor cursor = null;
                try
                {
                    cursor = dailyDb.getStayObRecentlySuggestList(DailyDb.MAX_RECENT_PLACE_COUNT);

                    if (cursor == null)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    int size = cursor.getCount();
                    if (size == 0)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    suggestList = new ArrayList<>();

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

                        suggestList.add(new Suggest(id, name, city, country, countryCode, categoryKey, display, latitude, longitude));
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

                if (suggestList == null)
                {
                    suggestList = new ArrayList<>();
                }

                return Observable.just(suggestList);
            }
        }).subscribeOn(Schedulers.io());
    }
}
