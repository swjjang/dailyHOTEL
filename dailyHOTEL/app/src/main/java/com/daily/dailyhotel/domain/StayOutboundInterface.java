package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutbounds;

import io.reactivex.Observable;

public interface StayOutboundInterface
{
    Observable<StayOutbounds> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, Persons persons, String cacheKey, String cacheLocation);

    Observable<StayOutbounds> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, double latitude, double longitude, int searchRadius, StayOutboundFilters filters//
        , Persons persons, String cacheKey, String cacheLocation);

    Observable<StayOutboundDetail> getStayOutBoundDetail(int index, StayBookDateTime stayBookDateTime, Persons persons);
}
