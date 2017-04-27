package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.BaseException;
import com.daily.dailyhotel.domain.StayOutboundInterface;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
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
    public Observable<List<StayOutbound>> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, int numberOfAdults, ArrayList<String> childList, String cacheKey, String cacheLocation)
    {
        int numberOfChilds = 0;
        String childAges = null;

        if (childList != null && childList.size() > 0)
        {
            numberOfChilds = childList.size();
            childAges = null;

            for (String childAge : childList)
            {
                if (childAges == null)
                {
                    childAges = childAge;
                } else
                {
                    childAges += "^" + childAge;
                }
            }
        }

        final int numberOfRooms = 1;
        final int numberOfResults = 200;

        return DailyMobileAPI.getInstance(mContext).getStayOutBoundList(stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")//
            , stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")//
            , numberOfAdults, numberOfChilds, childAges, numberOfRooms, countryCode, city//
            , numberOfResults, cacheKey, cacheLocation).map((stayOutboundDataBaseDto) ->
        {
            List<StayOutbound> list = null;

            if (stayOutboundDataBaseDto != null)
            {
                if (stayOutboundDataBaseDto.msgCode == 100 && stayOutboundDataBaseDto.data != null)
                {
                    list = stayOutboundDataBaseDto.data.getStayOutboundList();
                } else
                {
                    throw new BaseException(stayOutboundDataBaseDto.msgCode, stayOutboundDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return list;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StayOutbound> getStayOutBoundDetail(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, int numberOfAdults, ArrayList<String> childList)
    {
        return null;
    }
}
