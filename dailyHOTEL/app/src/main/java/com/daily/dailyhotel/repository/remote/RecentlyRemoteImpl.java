package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyInterface;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.repository.local.model.RecentlyRealmObject;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.HomePlaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.realm.RealmResults;

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
    public Observable<StayOutbounds> getStayOutboundRecentlyList(int numberOfResults)
    {
        String hotelIds = RecentlyPlaceUtil.getTargetIndices(RecentlyPlaceUtil.ServiceType.OB_STAY, numberOfResults);

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
    public Observable<List<HomePlace>> getHomeRecentlyList(int maxSize)
    {
        RealmResults<RecentlyRealmObject> realmResults = RecentlyPlaceUtil.getRecentlyTypeList(RecentlyPlaceUtil.ServiceType.IB_STAY, RecentlyPlaceUtil.ServiceType.GOURMET);
        if (realmResults == null || realmResults.size() == 0)
        {
            // TODO : 최근 본 업장 개수가 없을때 처리 추가 필요
        }

        JSONArray recentJsonArray = RecentlyPlaceUtil.getRecentlyJsonArray(realmResults, maxSize);
        JSONObject recentJsonObject = new JSONObject();

        try
        {
            recentJsonObject.put("keys", recentJsonArray);
        } catch (JSONException e)
        {
            ExLog.d(e.getMessage());
        }

        return DailyMobileAPI.getInstance(mContext).getHomeRecentList(recentJsonObject).map(new Function<BaseDto<HomePlaces>, List<HomePlace>>()
        {
            @Override
            public List<HomePlace> apply(@NonNull BaseDto<HomePlaces> homePlacesBaseDto) throws Exception
            {
                List<HomePlace> homePlaceList = null;

                if (homePlacesBaseDto != null)
                {
                    if (homePlacesBaseDto.msgCode == 100 && homePlacesBaseDto.data != null)
                    {
                        homePlaceList = homePlacesBaseDto.data.getHomePlaceList();
                        if (homePlaceList == null || homePlaceList.size() == 0)
                        {
                           homePlaceList = new ArrayList<HomePlace>();
                        }
                    } else
                    {
                        throw new BaseException(homePlacesBaseDto.msgCode, homePlacesBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return homePlaceList;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
