package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.domain.RecentlyInterface;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.repository.remote.model.RecentlyPlacesData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 6. 14..
 */

public class RecentlyRemoteImpl implements RecentlyInterface
{
    Context mContext;

    public RecentlyRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayOutbounds> getStayOutboundRecentlyList(String targetIndices, int numberOfResults)
    {
        if (DailyTextUtils.isTextEmpty(targetIndices) == true)
        {
            return Observable.just(new StayOutbounds()).subscribeOn(Schedulers.io());
        }

        return Observable.defer(new Callable<ObservableSource<StayOutbounds>>()
        {
            @Override
            public ObservableSource<StayOutbounds> call() throws Exception
            {
                return DailyMobileAPI.getInstance(mContext).getStayOutboundRecentlyList(targetIndices, numberOfResults).map(new Function<BaseDto<StayOutboundsData>, StayOutbounds>()
                {
                    @Override
                    public StayOutbounds apply(@NonNull BaseDto<StayOutboundsData> stayOutboundsDataBaseDto) throws Exception
                    {
                        StayOutbounds stayOutbounds;

                        if (stayOutboundsDataBaseDto != null)
                        {
                            if (stayOutboundsDataBaseDto.msgCode == 100 && stayOutboundsDataBaseDto.data != null)
                            {
                                stayOutbounds = stayOutboundsDataBaseDto.data.getStayOutboundList();

                                if (stayOutbounds == null)
                                {
                                    stayOutbounds = new StayOutbounds();
                                }
                            } else
                            {
                                throw new BaseException(stayOutboundsDataBaseDto.msgCode, stayOutboundsDataBaseDto.msg);
                            }
                        } else
                        {
                            throw new BaseException(-1, null);
                        }

                        return stayOutbounds;
                    }
                }).subscribeOn(Schedulers.io());
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ArrayList<RecentlyPlace>> getInboundRecentlyList(JSONObject recentJsonObject)
    {
        return Observable.defer(new Callable<ObservableSource<ArrayList<RecentlyPlace>>>()
        {
            @Override
            public ObservableSource<ArrayList<RecentlyPlace>> call() throws Exception
            {
                if (recentJsonObject == null)
                {
                    return Observable.just(new ArrayList<RecentlyPlace>()).subscribeOn(Schedulers.io());
                }

                return DailyMobileAPI.getInstance(mContext).getInboundRecentlyList(recentJsonObject).map(new Function<BaseDto<RecentlyPlacesData>, ArrayList<RecentlyPlace>>()
                {
                    @Override
                    public ArrayList<RecentlyPlace> apply(@NonNull BaseDto<RecentlyPlacesData> placesBaseDto) throws Exception
                    {
                        ArrayList<RecentlyPlace> recentlyPlaceList = new ArrayList<>();

                        if (placesBaseDto != null)
                        {
                            if (placesBaseDto.msgCode == 100 && placesBaseDto.data != null)
                            {
                                List<RecentlyPlace> list = placesBaseDto.data.getRecentlyPlaceList();

                                if (list != null && list.size() > 0)
                                {
                                    recentlyPlaceList.addAll(list);
                                }
                            } else
                            {
                                throw new BaseException(placesBaseDto.msgCode, placesBaseDto.msg);
                            }
                        } else
                        {
                            throw new BaseException(-1, null);
                        }

                        return recentlyPlaceList;
                    }
                }).subscribeOn(Schedulers.io());
            }
        }).subscribeOn(Schedulers.io());
    }
}
