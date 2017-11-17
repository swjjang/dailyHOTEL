package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.domain.CartInterface;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.storage.preference.DailyCartPreference;

import org.json.JSONObject;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CartLocalImpl implements CartInterface
{
    private Context mContext;

    public CartLocalImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<GourmetCart> getGourmetCart()
    {
        return Observable.defer(new Callable<ObservableSource<GourmetCart>>()
        {
            @Override
            public ObservableSource<GourmetCart> call() throws Exception
            {
                String jsonObjectString = DailyCartPreference.getInstance(mContext).getGourmetCart();

                if (DailyTextUtils.isTextEmpty(jsonObjectString) == true || "{}".equalsIgnoreCase(jsonObjectString) == true)
                {
                    return Observable.just(new GourmetCart());
                }

                GourmetCart gourmetCart = new GourmetCart(new JSONObject(jsonObjectString));

                return Observable.just(gourmetCart);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> setGourmetCart(GourmetCart gourmetCart)
    {
        if (gourmetCart == null)
        {
            return Observable.defer(new Callable<ObservableSource<Boolean>>()
            {
                @Override
                public ObservableSource<Boolean> call() throws Exception
                {
                    DailyCartPreference.getInstance(mContext).setGourmetCart(null);

                    return Observable.just(true);
                }
            }).subscribeOn(Schedulers.io());
        } else
        {
            return Observable.just(gourmetCart).map(new Function<GourmetCart, Boolean>()
            {
                @Override
                public Boolean apply(GourmetCart gourmetCart) throws Exception
                {
                    String jsonObjectString = gourmetCart.toJSONObject().toString();

                    if (DailyTextUtils.isTextEmpty(jsonObjectString) == true || "{}".equalsIgnoreCase(jsonObjectString) == true)
                    {
                        DailyCartPreference.getInstance(mContext).setGourmetCart(null);
                    } else
                    {
                        DailyCartPreference.getInstance(mContext).setGourmetCart(gourmetCart.toJSONObject().toString());
                    }

                    return true;
                }
            }).subscribeOn(Schedulers.io());
        }
    }

    @Override
    public Observable<Boolean> hasGourmetCart()
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                String jsonObjectString = DailyCartPreference.getInstance(mContext).getGourmetCart();

                return Observable.just(DailyTextUtils.isTextEmpty(jsonObjectString) == false && "{}".equalsIgnoreCase(jsonObjectString) == false);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Integer> getGourmetCartTotalCount()
    {
        return Observable.defer(new Callable<ObservableSource<Integer>>()
        {
            @Override
            public ObservableSource<Integer> call() throws Exception
            {
                String jsonObjectString = DailyCartPreference.getInstance(mContext).getGourmetCart();

                if (DailyTextUtils.isTextEmpty(jsonObjectString) == true || "{}".equalsIgnoreCase(jsonObjectString) == true)
                {
                    return Observable.just(0);
                }

                GourmetCart gourmetCart = new GourmetCart(new JSONObject(jsonObjectString));

                return Observable.just(gourmetCart.getTotalCount());
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> clearGourmetCart()
    {
        return Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                DailyCartPreference.getInstance(mContext).setGourmetCart(null);

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());
    }
}
