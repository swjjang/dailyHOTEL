package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.WishResult;

import io.reactivex.Observable;

public interface GourmetInterface
{
    Observable<GourmetDetail> getGourmetDetail(int gourmetIndex, GourmetBookDateTime gourmetBookDateTime);

    Observable<Boolean> getGourmetHasCoupon(int gourmetIndex, GourmetBookDateTime gourmetBookDateTime);

    Observable<WishResult> addGourmetWish(int gourmetIndex);

    Observable<WishResult> removeGourmetWish(int gourmetIndex);
}
