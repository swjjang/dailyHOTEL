package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.entity.WishResult;

import java.util.List;

import io.reactivex.Observable;

public interface StayOutboundInterface
{
    Observable<StayOutbounds> getList(StayBookDateTime stayBookDateTime, long geographyId//
        , String geographyType, People people, StayOutboundFilters stayOutboundFilters, int numberOfResults, String cacheKey, String cacheLocation, String customerSessionId);

    Observable<StayOutbounds> getList(StayBookDateTime stayBookDateTime, double latitude, double longitude, float radius//
        , People people, StayOutboundFilters stayOutboundFilters, int numberOfResults, boolean mapScreen, String cacheKey, String cacheLocation, String customerSessionId);

    Observable<StayOutboundDetail> getDetailInformation(int index, StayBookDateTime stayBookDateTime, People people);

    Observable<List<StayOutboundRoom>> getDetailRoomList(int index, StayBookDateTime stayBookDateTime, People people);

    Observable<StayOutbounds> getRecommendAroundList(int index, StayBookDateTime stayBookDateTime, People people);

    Observable<WishResult> addWish(int stayIndex);

    Observable<WishResult> removeWish(int stayIndex);
}
