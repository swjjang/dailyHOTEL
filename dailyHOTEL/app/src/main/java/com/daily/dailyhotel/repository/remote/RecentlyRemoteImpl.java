package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyInterface;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.local.model.RecentlyRealmObject;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by android_sam on 2017. 6. 14..
 */

public class RecentlyRemoteImpl implements RecentlyInterface
{
    private Context mContext;

    public RecentlyRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayOutbounds> getStayOutboundRecentlyList(int numberOfResults, boolean useRealm)
    {
        String hotelIds = RecentlyPlaceUtil.getDbTargetIndices(mContext, Constants.ServiceType.OB_STAY, RecentlyPlaceUtil.MAX_RECENT_PLACE_COUNT);

        if (useRealm == true)
        {
            String oldHotelIds = RecentlyPlaceUtil.getRealmTargetIndices(Constants.ServiceType.OB_STAY, RecentlyPlaceUtil.MAX_RECENT_PLACE_COUNT);
            if (DailyTextUtils.isTextEmpty(oldHotelIds) == false)
            {
                if (DailyTextUtils.isTextEmpty(hotelIds) == false)
                {
                    hotelIds += ",";
                }

                hotelIds += oldHotelIds;
            }
        }

        return DailyMobileAPI.getInstance(mContext).getStayOutboundRecentlyList(hotelIds, numberOfResults).map(new Function<BaseDto<StayOutboundsData>, StayOutbounds>()
        {
            @Override
            public StayOutbounds apply(@NonNull BaseDto<StayOutboundsData> stayOutboundsDataBaseDto) throws Exception
            {
                StayOutbounds stayOutbounds = null;

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
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<RecentlyPlace>> getInboundRecentlyList(int maxSize, boolean useRealm, @NonNull Constants.ServiceType... serviceTypes)
    {
        JSONObject recentJsonObject = new JSONObject();

        try
        {
            ArrayList<RecentlyDbPlace> list = RecentlyPlaceUtil.getDbRecentlyTypeList(mContext, serviceTypes);

            ArrayList<Integer> indexList = RecentlyPlaceUtil.getDbRecentlyIndexList(mContext, serviceTypes);

            if (useRealm == true)
            {
                ArrayList<RecentlyRealmObject> realmList = RecentlyPlaceUtil.getRealmRecentlyTypeList(serviceTypes);
                if (realmList != null)
                {
                    for (RecentlyRealmObject realmObject : realmList)
                    {
                        int index = realmObject.index;
                        if (indexList.contains(index) == true)
                        {
                            continue;
                        }

                        RecentlyDbPlace place = new RecentlyDbPlace();
                        place.savingTime = realmObject.savingTime;
                        place.englishName = realmObject.englishName;
                        place.imageUrl = realmObject.imageUrl;
                        place.index = index;
                        place.name = realmObject.name;

                        try
                        {
                            place.serviceType = Constants.ServiceType.valueOf(realmObject.serviceType);
                        } catch (Exception e)
                        {
                            continue;
                        }

                        list.add(place);
                    }
                }
            }

            JSONArray recentJsonArray = RecentlyPlaceUtil.getDbRecentlyJsonArray(list, maxSize);
            recentJsonObject.put("keys", recentJsonArray);

        } catch (Exception e)
        {
            ExLog.d(e.toString());
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
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
