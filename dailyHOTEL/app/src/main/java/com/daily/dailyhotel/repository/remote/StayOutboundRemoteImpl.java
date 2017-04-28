package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.BaseException;
import com.daily.dailyhotel.domain.StayOutboundInterface;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.twoheart.dailyhotel.network.DailyMobileAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class StayOutboundRemoteImpl implements StayOutboundInterface
{
    private Context mContext;

    public StayOutboundRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayOutbounds> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, Persons persons, String cacheKey, String cacheLocation)
    {
        final int numberOfRooms = 1;
        final int numberOfResults = 200;

        /// 디폴트 인자들
        final String apiExperience = "PARTNER_MOBILE_APP";
        final String locale = "ko_KR";
        final String sort = "DEFAULT";

        int numberOfAdults = persons.numberOfAdults;
        int numberOfChildren = 0;
        String childAges = null;

        List<String> childList = persons.getChildList();

        if (childList != null)
        {
            numberOfChildren = childList.size();

            if (numberOfChildren > 0)
            {
                for (String age : childList)
                {
                    if (childAges == null)
                    {
                        childAges = age;
                    } else
                    {
                        childAges += "," + age;
                    }
                }
            }
        }

        return DailyMobileAPI.getInstance(mContext).getStayOutBoundList(stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")//
            , stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")//
            , numberOfAdults, numberOfChildren, childAges, numberOfRooms, countryCode, city//
            , numberOfResults, cacheKey, cacheLocation, apiExperience, locale, sort).map((stayOutboundDataBaseDto) ->
        {
            StayOutbounds stayOutbounds = null;

            if (stayOutboundDataBaseDto != null)
            {
                if (stayOutboundDataBaseDto.msgCode == 100 && stayOutboundDataBaseDto.data != null)
                {
                    stayOutbounds = stayOutboundDataBaseDto.data.getStayOutboundList();
                } else
                {
                    throw new BaseException(stayOutboundDataBaseDto.msgCode, stayOutboundDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return stayOutbounds;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StayOutbound> getStayOutBoundDetail(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, int numberOfAdults, ArrayList<String> childList)
    {
        return null;
    }
}
