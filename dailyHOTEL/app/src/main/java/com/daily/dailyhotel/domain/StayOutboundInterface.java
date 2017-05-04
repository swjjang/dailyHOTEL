package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.RoomInformation;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutbounds;

import java.util.ArrayList;

import io.reactivex.Observable;

public interface StayOutboundInterface
{
    Observable<StayOutbounds> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, Persons persons, String cacheKey, String cacheLocation);

    Observable<StayOutboundDetail> getStayOutBoundDetail(int index, StayBookDateTime stayBookDateTime
        , String countryCode, String city, Persons persons);

    Observable<RoomInformation> getStayOutBoundRoom(int index, StayBookDateTime stayBookDateTime
        , String countryCode, String city, Persons persons);
}
