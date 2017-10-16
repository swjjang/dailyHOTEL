package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyInterface;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.model.RecentlyPlacesData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONArray;
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
    public Observable<StayOutbounds> getStayOutboundRecentlyList(int numberOfResults)
    {
        return Observable.defer(new Callable<ObservableSource<StayOutbounds>>()
        {
            @Override
            public ObservableSource<StayOutbounds> call() throws Exception
            {
                String hotelIds = RecentlyPlaceUtil.getDbTargetIndices(mContext, Constants.ServiceType.OB_STAY, RecentlyPlaceUtil.MAX_RECENT_PLACE_COUNT);

                if (DailyTextUtils.isTextEmpty(hotelIds) == true)
                {
                    return Observable.just(new StayOutbounds()).subscribeOn(Schedulers.io());
                }

                return DailyMobileAPI.getInstance(mContext).getStayOutboundRecentlyList(hotelIds, numberOfResults).map(new Function<BaseDto<StayOutboundsData>, StayOutbounds>()
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
    public Observable<ArrayList<RecentlyPlace>> getInboundRecentlyList(int maxSize, @NonNull Constants.ServiceType... serviceTypes)
    {
        return Observable.defer(new Callable<ObservableSource<ArrayList<RecentlyPlace>>>()
        {
            @Override
            public ObservableSource<ArrayList<RecentlyPlace>> call() throws Exception
            {
                ArrayList<RecentlyDbPlace> list = RecentlyPlaceUtil.getDbRecentlyTypeList(mContext, serviceTypes);
                if (list == null || list.size() == 0)
                {
                    return Observable.just(new ArrayList<RecentlyPlace>()).subscribeOn(Schedulers.io());
                }

                JSONObject recentJsonObject = getRecentlyJSONObject(list, maxSize);

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

    JSONObject getRecentlyJSONObject(ArrayList<RecentlyDbPlace> list, int maxSize)
    {
        JSONObject recentlyJsonObject = new JSONObject();

        try
        {
            JSONArray recentlyJsonArray = RecentlyPlaceUtil.getDbRecentlyJsonArray(list, maxSize);
            recentlyJsonObject.put("keys", recentlyJsonArray);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return recentlyJsonObject;
    }
}
