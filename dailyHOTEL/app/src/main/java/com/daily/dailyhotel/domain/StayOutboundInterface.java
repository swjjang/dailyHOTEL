package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public interface StayOutboundInterface
{
    Observable<List<StayOutbound>> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, int numberOfAdults, ArrayList<String> childList, String cacheKey, String cacheLocation);

    Observable<StayOutbound> getStayOutBoundDetail(StayBookDateTime stayBookDateTime, String countryCode, String city//
        , int numberOfAdults, ArrayList<String> childList);
}
