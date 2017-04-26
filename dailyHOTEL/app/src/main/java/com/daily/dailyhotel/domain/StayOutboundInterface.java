package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutBound;
import com.daily.dailyhotel.entity.Suggest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public interface StayOutboundInterface
{
    Observable<List<StayOutBound>> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode, String city//
    , int numberOfAdults, ArrayList<String> childList);

    Observable<List<StayOutBound>> getStayOutBoundDetail(StayBookDateTime stayBookDateTime, String countryCode, String city//
        , int numberOfAdults, ArrayList<String> childList);
}
