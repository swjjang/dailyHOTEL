package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GourmetCart;

import io.reactivex.Observable;

public interface CartInterface
{
    Observable<GourmetCart> getGourmetCart();

    Observable<Boolean> setGourmetCart(GourmetCart gourmetCart);

    Observable<Boolean> hasGourmetCart();

    Observable<Integer> getGourmetCartTotalCount();

    Observable<Boolean> clearGourmetCart();
}
