package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.entity.WishResult;

import io.reactivex.Observable;

public interface StayOutboundInterface
{
    Observable<StayOutbounds> getList(StayBookDateTime stayBookDateTime, long geographyId//
        , String geographyType, People people, StayOutboundFilters stayOutboundFilters, String cacheKey, String cacheLocation);

    Observable<StayOutbounds> getList(StayBookDateTime stayBookDateTime, double latitude, double longitude, float radius//
        , People people, StayOutboundFilters stayOutboundFilters, int numberOfResults);

    Observable<StayOutboundDetail> getDetail(int index, StayBookDateTime stayBookDateTime, People people);

    Observable<StayOutbounds> getRecommendAroundList(int index, StayBookDateTime stayBookDateTime, People people);

    Observable<WishResult> addWish(int stayIndex);

    Observable<WishResult> removeWish(int stayIndex);
}
