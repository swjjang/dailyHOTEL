package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutbounds;

import io.reactivex.Observable;

public interface StayOutboundInterface
{
    Observable<StayOutbounds> getList(StayBookDateTime stayBookDateTime, long geographyId//
        , String geographyType, People people, StayOutboundFilters stayOutboundFilters, String cacheKey, String cacheLocation);

    Observable<StayOutboundDetail> getDetail(int index, StayBookDateTime stayBookDateTime, People people);

    Observable<StayOutbounds> getRecommendAroundList(int index, StayBookDateTime stayBookDateTime, People people);
}
