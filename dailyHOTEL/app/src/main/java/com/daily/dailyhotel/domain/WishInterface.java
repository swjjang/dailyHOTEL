package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.WishCount;
import com.daily.dailyhotel.entity.WishResult;

import java.util.List;

import io.reactivex.Observable;

public interface WishInterface
{
    Observable<List<RecentlyPlace>> getHomeWishList();

    Observable<List<Stay>> getStayWishList();

    Observable<WishResult> addStayWish(int wishIndex);

    Observable<WishResult> removeStayWish(int wishIndex);

    Observable<List<StayOutbound>> getStayOutboundWishList(Context context);

    Observable<List<StayOutbound>> getStayOutboundWishList(Context context, int maxCount);

    Observable<WishResult> addStayOutboundWish(Context context, int wishIndex);

    Observable<WishResult> removeStayOutboundWish(Context context, int wishIndex);

    Observable<WishCount> getWishCount();

    Observable<Integer> getStayOutboundWishCount(Context context);
}
