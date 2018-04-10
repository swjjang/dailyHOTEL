package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.GourmetCart;

import io.reactivex.Observable;

public interface CartInterface
{
    Observable<GourmetCart> getGourmetCart(Context context);

    Observable<Boolean> setGourmetCart(Context context, GourmetCart gourmetCart);

    Observable<Boolean> hasGourmetCart(Context context);

    Observable<Integer> getGourmetCartTotalCount(Context context);

    Observable<Boolean> clearGourmetCart(Context context);
}
