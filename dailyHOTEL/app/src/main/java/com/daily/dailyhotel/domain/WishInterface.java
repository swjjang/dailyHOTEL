package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayWish;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.WishResult;

import java.util.List;

import io.reactivex.Observable;

public interface WishInterface
{
    Observable<List<StayWish>> getStayWishList();

    Observable<WishResult> addStayWish(int wishIndex);

    Observable<WishResult> removeStayWish(int wishIndex);

    Observable<List<StayOutbound>> getStayOutboundWishList();

    Observable<WishResult> addStayOutboundWish(int wishIndex);

    Observable<WishResult> removeStayOutboundWish(int wishIndex);

}
