package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.text.TextUtils;

import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyInterface;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.repository.local.model.RecentlyRealmObject;
import com.daily.dailyhotel.repository.remote.model.StayListData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.model.RecentStayParams;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
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
    public Observable<List<HomePlace>> getHomeRecentlyList(int maxSize)
    {
        RealmResults<RecentlyRealmObject> realmResults = RecentlyPlaceUtil.getRecentlyTypeList(RecentlyPlaceUtil.ServiceType.IB_STAY, RecentlyPlaceUtil.ServiceType.GOURMET);
        JSONArray recentJsonArray = RecentlyPlaceUtil.getRecentlyJsonArray(realmResults, maxSize);
        JSONObject recentJsonObject = new JSONObject();

        try
        {
            recentJsonObject.put("keys", recentJsonArray);
        } catch (JSONException e)
        {
            ExLog.d(e.getMessage());
        }

        return DailyMobileAPI.getInstance(mContext).getHomeRecentlyList(recentJsonObject).map(new Function<BaseDto<HomePlaces>, List<HomePlace>>()
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

    @Override
    public Observable<List<Stay>> getStayInboundRecentlyList(StayBookingDay stayBookingDay)
    {
        String targetIndices = RecentlyPlaceUtil.getTargetIndices(RecentlyPlaceUtil.ServiceType.IB_STAY, 10000);

        if (TextUtils.isEmpty(targetIndices) == true)
        {
            targetIndices = "0";
        }

        RecentStayParams recentStayParams = new RecentStayParams();
        recentStayParams.setStayBookingDay(stayBookingDay);
        recentStayParams.setTargetIndices(targetIndices);

        return DailyMobileAPI.getInstance(mContext) //
            .getStayList(recentStayParams.toParamsMap(), recentStayParams.getBedTypeList(), recentStayParams.getLuxuryList()) //
            .map(new Function<BaseDto<StayListData>, List<Stay>>()
            {
                @Override
                public List<Stay> apply(@NonNull BaseDto<StayListData> stayListDataBaseDto) throws Exception
                {
                    List<Stay> stayList = null;

                    if (stayListDataBaseDto != null)
                    {
                        if (stayListDataBaseDto.msgCode == 100 && stayListDataBaseDto.data != null)
                        {
                            stayList = stayListDataBaseDto.data.getStayList();
                            if (stayList == null || stayList.size() == 0)
                            {
                                stayList = new ArrayList<Stay>();
                            }
                        } else
                        {
                            throw new BaseException(stayListDataBaseDto.msgCode, stayListDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return stayList;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }
}
