package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.StayOutboundInterface;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRoomData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class StayOutboundRemoteImpl extends BaseRemoteImpl implements StayOutboundInterface
{
    @Override
    public Observable<StayOutbounds> getList(Context context, StayBookDateTime stayBookDateTime, long geographyId//
        , String geographyType, People people, StayOutboundFilters stayOutboundFilters, int numberOfResults//
        , String cacheKey, String cacheLocation, String customerSessionId)
    {
        JSONObject jsonObject = new JSONObject();

        final int NUMBER_OF_ROOMS = 1;

        /// 디폴트 인자들
        String sort;

        try
        {
            if (DailyTextUtils.isTextEmpty(cacheKey, cacheLocation, customerSessionId) == false)
            {
                jsonObject.put("cacheKey", cacheKey);
                jsonObject.put("cacheLocation", cacheLocation);
                jsonObject.put("customerSessionId", customerSessionId);
            }

            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));

            jsonObject.put("geographyId", geographyId);
            jsonObject.put("geographyType", geographyType);

            jsonObject.put("numberOfRooms", NUMBER_OF_ROOMS);
            jsonObject.put("numberOfResults", numberOfResults);

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

        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v3/outbound/geographicalid-find-hotels"//
            : "MTE4JDg0JDQ2JDEwNSQzOSQ4MyQxMDckMTE2JDEyNyQyNCQxMjMkMzYkMTEzJDU0JDEyOSQ3NCQ=$QzIwOUJFRDQ4RjFDREY2MzUwQMTRFOUNERUZTCQThZERDM3QjXY5NDRdBMTg3NjY0QUM0NUYyOZDU5QjlERUY1MNTdDTQzc4NDI5MzQ3RTQ3NEIVKwQEkJCMTBczMjcEzNRDNAOwQTI2Mzgy$";

        return mDailyMobileService.getStayOutboundList(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API), jsonObject) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundsData>, StayOutbounds>()
            {
                @Override
                public StayOutbounds apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundsData> stayOutboundDataBaseDto) throws Exception
                {
                    StayOutbounds stayOutbounds;

                    if (stayOutboundDataBaseDto != null)
                    {
                        if (stayOutboundDataBaseDto.msgCode == 100 && stayOutboundDataBaseDto.data != null)
                        {
                            stayOutbounds = stayOutboundDataBaseDto.data.getStayOutbounds();
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
    public Observable<StayOutbounds> getList(Context context, StayBookDateTime stayBookDateTime, double latitude, double longitude, float radius//
        , People people, StayOutboundFilters stayOutboundFilters, int numberOfResults, boolean mapScreen, String cacheKey, String cacheLocation, String customerSessionId)
    {
        JSONObject jsonObject = new JSONObject();

        final int NUMBER_OF_ROOMS = 1;

        /// 디폴트 인자들
        String sort;

        if (mapScreen == true)
        {
            sort = StayOutboundFilters.SortType.RECOMMENDATION.getValue();
        } else
        {
            sort = stayOutboundFilters != null && stayOutboundFilters.sortType != null ? stayOutboundFilters.sortType.getValue() : "DEFAULT";
        }

        try
        {
            if (DailyTextUtils.isTextEmpty(cacheKey, cacheLocation, customerSessionId) == false)
            {
                jsonObject.put("cacheKey", cacheKey);
                jsonObject.put("cacheLocation", cacheLocation);
                jsonObject.put("customerSessionId", customerSessionId);
            }

            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));

            jsonObject.put("numberOfRooms", NUMBER_OF_ROOMS);
            jsonObject.put("numberOfResults", numberOfResults);

            jsonObject.put("rooms", getRooms(new People[]{people}));
            jsonObject.put("filter", getFilter(stayOutboundFilters));

            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);

            int searchRadius = (int) radius;

            final int MIN_RADIUS = 2; // (km)
            final int MAX_RADIUS = 80; // (km)

            jsonObject.put("searchRadius", searchRadius < MIN_RADIUS ? MIN_RADIUS : searchRadius);
            jsonObject.put("searchRadiusUnit", "KM");

            jsonObject.put("sort", sort);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v3/outbound/coordinate-find-hotels"//
            : "NzgkNDEkODckMTkkMTA0JDI2JDM4JDU3JDcwJDEwNCQzNSQzNyQxMjIkMzUkNzgkMTMzJA==$MUIwQURENEVCREE4RDYC0RkJEQWzIzNUJBMGTDPJCYQzdBNZzczMDNEMjRCNMTY3QzAyMUVFMTkVDNKEI3NjRDMUFFDNTZGQFjlCMUVFMjNECMkI1HRjgyMjg3QTSVGMDM5ODNhBRUY4Q0Uy$";

        return mDailyMobileService.getStayOutboundList(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API), jsonObject) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundsData>, StayOutbounds>()
            {
                @Override
                public StayOutbounds apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundsData> stayOutboundDataBaseDto) throws Exception
                {
                    StayOutbounds stayOutbounds;

                    if (stayOutboundDataBaseDto != null)
                    {
                        if (stayOutboundDataBaseDto.msgCode == 100 && stayOutboundDataBaseDto.data != null)
                        {
                            stayOutbounds = stayOutboundDataBaseDto.data.getStayOutbounds();
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
            });
    }

    @Override
    public Observable<StayOutboundDetail> getDetailInformation(Context context, int index, StayBookDateTime stayBookDateTime, People people)
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

        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v3/outbound/hotels/{hotelId}/description"//
            : "MzckODMkNzIkOTAkMTI3JDEyNiQxMDEkMTkkODUkOTkkMCQ5OSQxMDYkNDIkNjMkNzck$NMzc2RjI2N0M5RjY0MENJERDQ4NUE0QzA4NTMzROUYYxMTQyNTE2NkU0RDM1NUECxMTRBMTI4QThHPCRjY0MDJDNzICBENzIW0OUM0RMNjBCRROjAwOTk2NjI0QTEyQjA3OTQwQULMP3QkQ0$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelId}", Integer.toString(index));

        return mDailyMobileService.getStayOutboundDetailInformation(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams)//
            , jsonObject).subscribeOn(Schedulers.io()).map((stayOutboundDetailDataBaseDto) -> {
            StayOutboundDetail stayOutboundDetail;

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
        });
    }

    @Override
    public Observable<List<StayOutboundRoom>> getDetailRoomList(Context context, int index, StayBookDateTime stayBookDateTime, People people)
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

        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v3/outbound/hotels/{hotelId}/rooms"//
            : "MTI0JDk3JDM1JDc1JDEyOCQ4MyQxNCQ4MiQxMDMkODckNjYkMTgkOTMkNzIkNjckNSQ=$QTM0NJUZCQzU1OUHJCRLTQzMzUxNTA4OTEzQURNDQzgxREE1NDY5RjFCQzlCM0MyNENBBNNkU0LQzQzQzABCMzIP1RBEWUxNYUNGNTNBM0NDOOGTMyNENGOUFBRjM0NTRBN0EzMjU5JZOUNB$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelId}", Integer.toString(index));

        return mDailyMobileService.getStayOutboundDetailRoomList(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams), jsonObject) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<StayOutboundRoomData>, List<StayOutboundRoom>>()
            {
                @Override
                public List<StayOutboundRoom> apply(BaseListDto<StayOutboundRoomData> stayOutboundRoomDataBaseListDto) throws Exception
                {
                    List<StayOutboundRoom> list = new ArrayList<>();

                    if (stayOutboundRoomDataBaseListDto != null)
                    {
                        if (stayOutboundRoomDataBaseListDto.msgCode == 100 && stayOutboundRoomDataBaseListDto.data != null)
                        {
                            for (StayOutboundRoomData roomData : stayOutboundRoomDataBaseListDto.data)
                            {
                                list.add(roomData.getStayOutboundRoom());
                            }
                        } else
                        {
                            throw new BaseException(stayOutboundRoomDataBaseListDto.msgCode, stayOutboundRoomDataBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return list;
                }
            });
    }

    @Override
    public Observable<StayOutbounds> getRecommendAroundList(Context context, int index, StayBookDateTime stayBookDateTime, People people)
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

        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/hotels/{hotelId}/recommend-around"//
            : "MTcwJDExMCQ2JDEwNSQxMzAkMTgkMTI5JDEyMyQ1MyQ2OSQ2MiQ0MyQyNiQ1OCQ3MCQ5NyQ=$MzZFOUSI2NkIwNEQwOFDQyNjVDRRjNDRDU1MUQwQjY0RHURDNTQ3RUNHFNAzExOTYCxMDJIEMEXFBQzExNjcyRjdGRDlBNDRCHRDhGNkM1RjIyNDkwDMTdFMjSMwMEMzNTZGBNDc3QA0ZQGMUFCOUZCM0FDOTQ1NkI0NTA0NEJEQzgwRjlDOTRERENU=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelId}", Integer.toString(index));

        return mDailyMobileService.getStayOutboundRecommendAroundList(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams), jsonObject) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundsData>, StayOutbounds>()
            {
                @Override
                public StayOutbounds apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundsData> stayOutboundDataBaseDto) throws Exception
                {
                    StayOutbounds stayOutbounds;

                    if (stayOutboundDataBaseDto != null)
                    {
                        if (stayOutboundDataBaseDto.msgCode == 100 && stayOutboundDataBaseDto.data != null)
                        {
                            stayOutbounds = stayOutboundDataBaseDto.data.getStayOutbounds();
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
    public Observable<WishResult> addWish(Context context, int wishIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/wishitems/{stayIndex}/add"//
            : "OTUkMTIyJDY3JDExOSQ3MSQxMyQ2NCQxMzQkOTQkNTMkNDYkODgkMTExJDYwJDgxJDUk$REEzNQzg2RDJERXkRFMkQ0MzJGNDk5RjQzQjBBNTdDOUYzMGDdGQ0U4DQUMzMFDYwNzQJwNjVXBMUDE4MDAk2NjREOTWExMEEyNEZFFNkRSGNzQ0RDBRFODhCNkQ5NEZCMKDNGNZTEzNTUD1$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(wishIndex));

        return mDailyMobileService.addStayOutboundWish(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    @Override
    public Observable<WishResult> removeWish(Context context, int wishIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/wishitems/{stayIndex}/remove"//
            : "MiQ5NyQ2NSQ5MyQxMzEkOTMkNjgkMTAyJDEyNyQ2NyQxJDEzNyQxMTIkODIkMTQxJDY0JA==$MYzZk4MUZENzU0MDg0NjkwMTk2QzNERUNCRDRCMkY1QkNCMEJFNDEyOTE2MTcwNkCI3BRWUMY0RDk5M0ZCQAUVFREZCQ0Y0MEEHE4Q0E1EMREM2Q0RNDMzNBMkU0N0M2QjkyVQjNFODZIBCD$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(wishIndex));

        return mDailyMobileService.removeStayOutboundWish(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
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
        double minStarRating = 0.0;

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
