package com.daily.dailyhotel.domain;

import android.content.Context;

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
    Observable<StayOutbounds> getList(Context context, StayBookDateTime stayBookDateTime, long geographyId//
        , String geographyType, People people, StayOutboundFilters stayOutboundFilters, int numberOfResults, String cacheKey, String cacheLocation, String customerSessionId);

    Observable<StayOutbounds> getList(Context context, StayBookDateTime stayBookDateTime, double latitude, double longitude, float radius//
        , People people, StayOutboundFilters stayOutboundFilters, int numberOfResults, boolean mapScreen, String cacheKey, String cacheLocation, String customerSessionId);

    Observable<StayOutboundDetail> getDetailInformation(Context context, int index, StayBookDateTime stayBookDateTime, People people);

    Observable<List<StayOutboundRoom>> getDetailRoomList(Context context, int index, StayBookDateTime stayBookDateTime, People people);

    Observable<StayOutbounds> getRecommendAroundList(Context context, int index, StayBookDateTime stayBookDateTime, People people);

    Observable<WishResult> addWish(Context context, int stayIndex);

    Observable<WishResult> removeWish(Context context, int stayIndex);
}
