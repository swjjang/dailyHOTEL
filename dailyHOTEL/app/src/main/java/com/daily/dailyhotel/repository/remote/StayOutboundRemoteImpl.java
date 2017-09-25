package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.StayOutboundInterface;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class StayOutboundRemoteImpl implements StayOutboundInterface
{
    private Context mContext;

    public StayOutboundRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayOutbounds> getList(StayBookDateTime stayBookDateTime, long geographyId//
        , String geographyType, People people, StayOutboundFilters stayOutboundFilters, String cacheKey, String cacheLocation)
    {
        JSONObject jsonObject = new JSONObject();

        final int NUMBER_OF_ROOMS = 1;
        final int NUMBER_OF_RESULTS = 200;

        /// 디폴트 인자들
        String sort = "DEFAULT";

        try
        {
            if (DailyTextUtils.isTextEmpty(cacheKey, cacheLocation) == false)
            {
                jsonObject.put("cacheKey", cacheKey);
                jsonObject.put("cacheLocation", cacheLocation);
            }

            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));

            jsonObject.put("geographyId", geographyId);
            jsonObject.put("geographyType", geographyType);

            jsonObject.put("numberOfRooms", NUMBER_OF_ROOMS);
            jsonObject.put("numberOfResults", NUMBER_OF_RESULTS);

            jsonObject.put("rooms", getRooms(new People[]{people}));
            jsonObject.put("filter", getFilter(stayOutboundFilters));

            if (stayOutboundFilters != null)
            {
                sort = stayOutboundFilters.sortType.getValue();

                jsonObject.put("sort", sort);

                if (stayOutboundFilters.sortType == StayOutboundFilters.SortType.DISTANCE)
                {
                    jsonObject.put("latitude", stayOutboundFilters.latitude);
                    jsonObject.put("longitude", stayOutboundFilters.longitude);
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return DailyMobileAPI.getInstance(mContext).getStayOutboundList(jsonObject).map(new Function<BaseDto<StayOutboundsData>, StayOutbounds>()
        {
            @Override
            public StayOutbounds apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundsData> stayOutboundDataBaseDto) throws Exception
            {
                StayOutbounds stayOutbounds;

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
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StayOutboundDetail> getDetail(int index, StayBookDateTime stayBookDateTime, People people)
    {
        JSONObject jsonObject = new JSONObject();

        final int numberOfRooms = 1;

        try
        {
            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));

            jsonObject.put("numberOfRooms", numberOfRooms);
            jsonObject.put("rooms", getRooms(new People[]{people}));
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return DailyMobileAPI.getInstance(mContext).getStayOutboundDetail(index, jsonObject).map((stayOutboundDetailDataBaseDto) ->
        {
            StayOutboundDetail stayOutboundDetail = null;

            if (stayOutboundDetailDataBaseDto != null)
            {
                if (stayOutboundDetailDataBaseDto.msgCode == 100 && stayOutboundDetailDataBaseDto.data != null)
                {
                    stayOutboundDetail = stayOutboundDetailDataBaseDto.data.getStayOutboundDetail();
                } else
                {
                    throw new BaseException(stayOutboundDetailDataBaseDto.msgCode, stayOutboundDetailDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return stayOutboundDetail;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    private JSONArray getRooms(People[] peoples)
    {
        JSONArray roomJSONArray = new JSONArray();

        if (peoples == null || peoples.length == 0)
        {
            return roomJSONArray;
        }

        try
        {
            for (People people : peoples)
            {
                JSONObject roomJSONObject = new JSONObject();
                roomJSONObject.put("numberOfAdults", people.numberOfAdults);

                List<Integer> childAgeList = people.getChildAgeList();

                if (childAgeList != null && childAgeList.size() > 0)
                {
                    JSONArray childJSONArray = new JSONArray();

                    for (int age : childAgeList)
                    {
                        childJSONArray.put(Integer.toString(age));
                    }

                    roomJSONObject.put("numberOfChildren", childAgeList.size());
                    roomJSONObject.put("childAges", childJSONArray);
                } else
                {
                    roomJSONObject.put("numberOfChildren", 0);
                    roomJSONObject.put("childAges", null);
                }

                roomJSONArray.put(roomJSONObject);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return roomJSONArray;
    }

    private JSONObject getFilter(StayOutboundFilters stayOutboundFilters)
    {
        JSONObject filterJSONObject = new JSONObject();

        double maxStarRating = 5.0;
        double minStarRating = 1.0;

        if (stayOutboundFilters != null && stayOutboundFilters.rating > 0)
        {
            minStarRating = stayOutboundFilters.rating;
            maxStarRating = minStarRating + 0.5f;
        }

        try
        {
            filterJSONObject.put("includeSurrounding", false);
            filterJSONObject.put("maxStarRating", maxStarRating);
            filterJSONObject.put("minStarRating", minStarRating);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return filterJSONObject;
    }
}
