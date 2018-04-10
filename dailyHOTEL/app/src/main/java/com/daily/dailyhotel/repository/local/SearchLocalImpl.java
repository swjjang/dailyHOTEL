package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.database.Cursor;

import com.bluelinelabs.logansquare.LoganSquare;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.SearchLocalInterface;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.repository.local.model.GourmetSearchResultHistory;
import com.daily.dailyhotel.repository.local.model.SearchResultHistoryList;
import com.daily.dailyhotel.repository.local.model.StayObSearchResultHistory;
import com.daily.dailyhotel.repository.local.model.StaySearchResultHistory;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public class SearchLocalImpl implements SearchLocalInterface
{
    @Override
    public Observable<Boolean> addStayIbSearchResultHistory(Context context, CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StaySuggest suggest)
    {
        return Observable.defer(new Callable<ObservableSource<? extends Boolean>>()
        {
            @Override
            public ObservableSource<? extends Boolean> call() throws Exception
            {
                if (commonDateTime == null || stayBookDateTime == null || suggest == null)
                {
                    return Observable.just(false);
                }

                String suggestString;
                try
                {
                    suggestString = LoganSquare.serialize(suggest.getSuggestData());
                } catch (Exception e)
                {
                    suggestString = null;
                }

                if (DailyTextUtils.isTextEmpty(suggestString))
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.HOTEL.name();
                String displayName = suggest.getSuggestType() == StaySuggest.SuggestType.LOCATION //
                    ? context.getString(R.string.label_search_suggest_type_location_item_format, suggest.getText1()) //
                    : suggest.getText1();
                String startDate = stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
                String endDate = stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);

                dailyDb.addSearchResultHistory(serviceType, displayName, commonDateTime.dailyDateTime, startDate, endDate, suggestString, 0, null);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<StaySearchResultHistory>> getStayIbSearchResultHistoryList(Context context, CommonDateTime commonDateTime, int maxCount)
    {
        return Observable.defer(new Callable<ObservableSource<List<StaySearchResultHistory>>>()
        {
            @Override
            public ObservableSource<List<StaySearchResultHistory>> call() throws Exception
            {
                if (commonDateTime == null)
                {
                    return Observable.just(new ArrayList<>());
                }

                List<StaySearchResultHistory> list = new ArrayList<>();

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.HOTEL.name();

                Cursor cursor = null;

                try
                {
                    cursor = dailyDb.getSearchResultHistoryList(serviceType, commonDateTime.dailyDateTime, maxCount);

                    if (cursor == null)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    for (int i = 0; i < cursor.getCount(); i++)
                    {
                        cursor.moveToPosition(i);

                        String startDate = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.START_DATE));
                        String endDate = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.END_DATE));
                        String suggest = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.SUGGEST));

                        StaySearchResultHistory searchResultHistory = new StaySearchResultHistory(startDate, endDate, suggest);
                        list.add(searchResultHistory);
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

                return Observable.just(list);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteStayIbSearchResultHistory(Context context, StaySuggest suggest)
    {
        return Observable.defer(new Callable<ObservableSource<? extends Boolean>>()
        {
            @Override
            public ObservableSource<? extends Boolean> call() throws Exception
            {
                if (suggest == null)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.HOTEL.name();
                String displayName = suggest.getSuggestType() == StaySuggest.SuggestType.LOCATION //
                    ? context.getString(R.string.label_search_suggest_type_location_item_format, suggest.getText1()) //
                    : suggest.getText1();

                dailyDb.deleteSearchResultHistory(serviceType, displayName);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> addGourmetSearchResultHistory(Context context, CommonDateTime commonDateTime, GourmetBookDateTime gourmetBookDateTime, GourmetSuggest suggest)
    {
        return Observable.defer(new Callable<ObservableSource<? extends Boolean>>()
        {
            @Override
            public ObservableSource<? extends Boolean> call() throws Exception
            {
                if (commonDateTime == null || gourmetBookDateTime == null || suggest == null)
                {
                    return Observable.just(false);
                }

                String suggestString;
                try
                {
                    suggestString = LoganSquare.serialize(suggest.getSuggestData());
                } catch (Exception e)
                {
                    suggestString = null;
                }

                if (DailyTextUtils.isTextEmpty(suggestString))
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.GOURMET.name();
                String displayName = suggest.getSuggestType() == GourmetSuggest.SuggestType.LOCATION //
                    ? context.getString(R.string.label_search_suggest_type_location_item_format, suggest.getText1()) //
                    : suggest.getText1();
                String startDate = gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT);

                dailyDb.addSearchResultHistory(serviceType, displayName, commonDateTime.dailyDateTime, startDate, null, suggestString, 0, null);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<GourmetSearchResultHistory>> getGourmetSearchResultHistoryList(Context context, CommonDateTime commonDateTime, int maxCount)
    {
        return Observable.defer(new Callable<ObservableSource<List<GourmetSearchResultHistory>>>()
        {
            @Override
            public ObservableSource<List<GourmetSearchResultHistory>> call() throws Exception
            {
                if (commonDateTime == null)
                {
                    return Observable.just(new ArrayList<>());
                }

                List<GourmetSearchResultHistory> list = new ArrayList<>();

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.GOURMET.name();

                Cursor cursor = null;

                try
                {
                    cursor = dailyDb.getSearchResultHistoryList(serviceType, commonDateTime.dailyDateTime, maxCount);

                    if (cursor == null)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    for (int i = 0; i < cursor.getCount(); i++)
                    {
                        cursor.moveToPosition(i);

                        String startDate = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.START_DATE));
                        String suggest = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.SUGGEST));

                        GourmetSearchResultHistory searchResultHistory = new GourmetSearchResultHistory(startDate, suggest);
                        list.add(searchResultHistory);
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

                return Observable.just(list);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteGourmetSearchResultHistory(Context context, GourmetSuggest suggest)
    {
        return Observable.defer(new Callable<ObservableSource<? extends Boolean>>()
        {
            @Override
            public ObservableSource<? extends Boolean> call() throws Exception
            {
                if (suggest == null)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.GOURMET.name();
                String displayName = suggest.getSuggestType() == GourmetSuggest.SuggestType.LOCATION //
                    ? context.getString(R.string.label_search_suggest_type_location_item_format, suggest.getText1()) //
                    : suggest.getText1();

                dailyDb.deleteSearchResultHistory(serviceType, displayName);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> addStayObSearchResultHistory(Context context, CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StayOutboundSuggest suggest, People people)
    {
        return Observable.defer(new Callable<ObservableSource<? extends Boolean>>()
        {
            @Override
            public ObservableSource<? extends Boolean> call() throws Exception
            {
                if (commonDateTime == null || stayBookDateTime == null || suggest == null)
                {
                    return Observable.just(false);
                }

                String suggestString;
                try
                {
                    suggestString = LoganSquare.serialize(suggest.getSuggestData());
                } catch (Exception e)
                {
                    suggestString = null;
                }

                if (DailyTextUtils.isTextEmpty(suggestString))
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.OB_STAY.name();
                String displayName = suggest.latitude != 0 && suggest.longitude != 0 //
                    ? context.getString(R.string.label_search_suggest_type_location_item_format, suggest.display) //
                    : suggest.display;
                String startDate = stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
                String endDate = stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);
                int adultCount = people.numberOfAdults;
                String childList = people.getChildAgeList() == null ? null : people.getChildAgeList().toString();

                dailyDb.addSearchResultHistory(serviceType, displayName, commonDateTime.dailyDateTime, startDate, endDate, suggestString, adultCount, childList);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<StayObSearchResultHistory>> getStayObSearchResultHistoryList(Context context, CommonDateTime commonDateTime, int maxCount)
    {
        return Observable.defer(new Callable<ObservableSource<List<StayObSearchResultHistory>>>()
        {
            @Override
            public ObservableSource<List<StayObSearchResultHistory>> call() throws Exception
            {
                if (commonDateTime == null)
                {
                    return Observable.just(new ArrayList<>());
                }

                List<StayObSearchResultHistory> list = new ArrayList<>();

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.OB_STAY.name();

                Cursor cursor = null;

                try
                {
                    cursor = dailyDb.getSearchResultHistoryList(serviceType, commonDateTime.dailyDateTime, maxCount);

                    if (cursor == null)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    for (int i = 0; i < cursor.getCount(); i++)
                    {
                        cursor.moveToPosition(i);

                        String startDate = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.START_DATE));
                        String endDate = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.END_DATE));
                        String suggest = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.SUGGEST));
                        int adultCount = cursor.getInt(cursor.getColumnIndex(SearchResultHistoryList.ADULT_COUNT));
                        String childAgeList = cursor.getString(cursor.getColumnIndex(SearchResultHistoryList.CHILD_AGE_LIST));

                        StayObSearchResultHistory searchResultHistory = new StayObSearchResultHistory(startDate, endDate, suggest, adultCount, childAgeList);
                        list.add(searchResultHistory);
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

                return Observable.just(list);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteStayObSearchResultHistory(Context context, StayOutboundSuggest suggest)
    {
        return Observable.defer(new Callable<ObservableSource<? extends Boolean>>()
        {
            @Override
            public ObservableSource<? extends Boolean> call() throws Exception
            {
                if (suggest == null)
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

                String serviceType = Constants.ServiceType.OB_STAY.name();
                String displayName = suggest.latitude != 0 && suggest.longitude != 0 //
                    ? context.getString(R.string.label_search_suggest_type_location_item_format, suggest.display) //
                    : suggest.display;

                dailyDb.deleteSearchResultHistory(serviceType, displayName);

                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }
}
