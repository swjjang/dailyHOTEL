package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutbounds;

import io.reactivex.Observable;

public interface StayOutboundInterface
{
    Observable<StayOutbounds> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, People people, String cacheKey, String cacheLocation);

    Observable<StayOutbounds> getStayOutBoundList(StayBookDateTime stayBookDateTime, long geographyId//
        , String geographyType, People people, StayOutboundFilters stayOutboundFilters, String cacheKey, String cacheLocation);

    Observable<StayOutboundDetail> getStayOutBoundDetail(int index, StayBookDateTime stayBookDateTime, People people);
}
