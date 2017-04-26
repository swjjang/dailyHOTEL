package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.BaseException;
import com.daily.dailyhotel.domain.StayOutboundInterface;
import com.daily.dailyhotel.domain.SuggestInterface;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutBound;
import com.daily.dailyhotel.entity.Suggest;
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
    public Observable<List<StayOutBound>> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, int numberOfAdults, ArrayList<String> childList)
    {
        return null;
    }

    @Override
    public Observable<List<StayOutBound>> getStayOutBoundDetail(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, int numberOfAdults, ArrayList<String> childList)
    {
        return null;
    }
}
