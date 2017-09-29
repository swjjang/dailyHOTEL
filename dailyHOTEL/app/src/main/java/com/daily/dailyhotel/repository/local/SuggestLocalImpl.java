package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.domain.SuggestLocalInterface;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;

import io.reactivex.Observable;
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
}
