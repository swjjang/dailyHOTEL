package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.StayOutboundInterface;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.twoheart.dailyhotel.network.DailyMobileAPI;

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

        List<String> childAgeList = persons.getChildAgeList();

        if (childAgeList != null)
        {
            numberOfChildren = childAgeList.size();

            if (numberOfChildren > 0)
            {
                for (String age : childAgeList)
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
    public Observable<StayOutbounds> getStayOutBoundList(StayBookDateTime stayBookDateTime, long geographyId//
        , String geographyType, Persons persons, StayOutboundFilters stayOutboundFilters, String cacheKey, String cacheLocation)
    {
        final int numberOfRooms = 1;
        final int numberOfResults = 200;

        /// 디폴트 인자들
        final String apiExperience = "PARTNER_MOBILE_APP";
        final String locale = "ko_KR";
        String sort = "DEFAULT";

        int numberOfAdults = persons.numberOfAdults;
        int numberOfChildren = 0;
        String childAges = null;

        double maxStarRating = 5.0;
        double minStarRating = 1.0;

        List<String> childAgeList = persons.getChildAgeList();

        if (childAgeList != null)
        {
            numberOfChildren = childAgeList.size();

            if (numberOfChildren > 0)
            {
                for (String age : childAgeList)
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

        if (stayOutboundFilters != null)
        {
            sort = stayOutboundFilters.sortType.getValue();

            if (stayOutboundFilters.rating > 0)
            {
                maxStarRating = minStarRating = stayOutboundFilters.rating;
            }

            if(stayOutboundFilters.sortType == StayOutboundFilters.SortType.DISTANCE)
            {
                return DailyMobileAPI.getInstance(mContext).getStayOutBoundList(stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")//
                    , stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")//
                    , numberOfAdults, numberOfChildren, childAges, maxStarRating, minStarRating, numberOfRooms//
                    , geographyId, geographyType, stayOutboundFilters.latitude, stayOutboundFilters.longitude//
                    ,numberOfResults, cacheKey, cacheLocation, apiExperience, locale, sort).map((stayOutboundDataBaseDto) ->
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
        }

        return DailyMobileAPI.getInstance(mContext).getStayOutBoundList(stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")//
            , stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")//
            , numberOfAdults, numberOfChildren, childAges, maxStarRating, minStarRating, numberOfRooms//
            , geographyId, geographyType, numberOfResults, cacheKey, cacheLocation, apiExperience, locale, sort).map((stayOutboundDataBaseDto) ->
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
    public Observable<StayOutbounds> getStayOutBoundList(StayBookDateTime stayBookDateTime, String countryCode//
        , String city, double latitude, double longitude, int searchRadius, StayOutboundFilters filters//
        , Persons persons, String cacheKey, String cacheLocation)
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

        List<String> childAgeList = persons.getChildAgeList();

        if (childAgeList != null)
        {
            numberOfChildren = childAgeList.size();

            if (numberOfChildren > 0)
            {
                for (String age : childAgeList)
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
    public Observable<StayOutboundDetail> getStayOutBoundDetail(int index, StayBookDateTime stayBookDateTime, Persons persons)
    {
        final int numberOfRooms = 1;
        final int numberOfResults = 200;

        /// 디폴트 인자들
        final String apiExperience = "PARTNER_MOBILE_APP";
        final String locale = "ko_KR";

        int numberOfAdults = persons.numberOfAdults;
        int numberOfChildren = 0;
        String childAges = null;

        List<String> childAgeList = persons.getChildAgeList();

        if (childAgeList != null)
        {
            numberOfChildren = childAgeList.size();

            if (numberOfChildren > 0)
            {
                for (String age : childAgeList)
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

        return DailyMobileAPI.getInstance(mContext).getStayOutBoundDetail(index, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")//
            , stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")//
            , numberOfAdults, numberOfChildren, childAges, numberOfRooms//
            , apiExperience, locale).map((stayOutboundDetailDataBaseDto) ->
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
}
