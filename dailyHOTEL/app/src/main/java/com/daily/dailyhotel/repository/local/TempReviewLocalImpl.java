package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.database.Cursor;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.TempReviewInterface;
import com.daily.dailyhotel.repository.local.model.TempReviewList;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2018. 2. 28..
 */

public class TempReviewLocalImpl implements TempReviewInterface
{
    private Context mContext;

    public TempReviewLocalImpl(Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<Boolean> addTempReview(int reservationIndex, String serviceType, String startDate, String endDate, String scoreQuestion, String pickQuestion, String comment)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (reservationIndex <= 0 || DailyTextUtils.isTextEmpty(startDate))
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);
                dailyDb.addTempReview(reservationIndex, serviceType //
                    , startDate, endDate, scoreQuestion, pickQuestion, comment);
                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ArrayList<String>> getTempReview(int reservationIndex, String serviceType, String startDate, String endDate)
    {
        return Observable.defer(new Callable<ObservableSource<ArrayList<String>>>()
        {
            @Override
            public ObservableSource<ArrayList<String>> call() throws Exception
            {
                if (reservationIndex <= 0 || DailyTextUtils.isTextEmpty(startDate))
                {
                    return Observable.just(new ArrayList<>());
                }

                Cursor cursor = null;

                ArrayList<String> list = new ArrayList<>();

                try
                {
                    DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);

                    cursor = dailyDb.getTempReview(reservationIndex, serviceType, startDate, endDate);

                    cursor.moveToFirst();

                    String score = cursor.getString(cursor.getColumnIndex(TempReviewList.SCORE_QUESTION));
                    String pick = cursor.getString(cursor.getColumnIndex(TempReviewList.PICK_QUESTION));
                    String comment = cursor.getString(cursor.getColumnIndex(TempReviewList.COMMENT));

                    ExLog.d("sam : " + score + " \n , " + pick + " \n , " + comment);

                    list.add(DailyTextUtils.isTextEmpty(score) ? "" : score);
                    list.add(DailyTextUtils.isTextEmpty(pick) ? "" : pick);
                    list.add(DailyTextUtils.isTextEmpty(comment) ? "" : comment);

                } catch (Exception e)
                {
                    ExLog.d(e.getMessage());
                } finally
                {
                    if (cursor != null)
                    {
                        cursor.close();
                    }
                }

                DailyDbHelper.getInstance().close();

                return Observable.just(list);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteTempReview(int reservationIndex, String serviceType, String startDate)
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (reservationIndex <= 0 || DailyTextUtils.isTextEmpty(startDate))
                {
                    return Observable.just(false);
                }

                DailyDb dailyDb = DailyDbHelper.getInstance().open(mContext);
                dailyDb.deleteTempReview(reservationIndex, serviceType, startDate);
                DailyDbHelper.getInstance().close();

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }
}
