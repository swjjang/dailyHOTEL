package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.CalendarInterface;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by android_sam on 2017. 7. 12..
 */

public class CalendarImpl implements CalendarInterface
{
    private Context mContext;

    public CalendarImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<List<Integer>> getGourmetUnavailableDates(int gourmetIndex, int dateRange, boolean reverse)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetUnavailableDates(gourmetIndex, dateRange, reverse) //
            .map(new Function<BaseListDto<String>, List<Integer>>()
            {
                @Override
                public List<Integer> apply(@NonNull BaseListDto<String> stringBaseListDto) throws Exception
                {
                    List<Integer> unavailableDateList = new ArrayList<>();

                    if (stringBaseListDto != null)
                    {
                        if (stringBaseListDto.msgCode == 100 && stringBaseListDto.data != null)
                        {
                            for (String dayString : stringBaseListDto.data)
                            {
                                unavailableDateList.add(Integer.parseInt(dayString.replaceAll("-", "")));
                            }
                        } else
                        {
                            throw new BaseException(stringBaseListDto.msgCode, stringBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return unavailableDateList;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<String>> getStayUnavailableCheckInDates(int placeIndex, int dateRange, boolean reverse)
    {
        return DailyMobileAPI.getInstance(mContext).getStayUnavailableCheckInDates(placeIndex, dateRange, reverse) //
            .map(new Function<BaseListDto<String>, List<String>>()
            {
                @Override
                public List<String> apply(@NonNull BaseListDto<String> stringBaseListDto) throws Exception
                {
                    List<String> unavailableDateList = null;

                    if (stringBaseListDto != null)
                    {
                        if (stringBaseListDto.msgCode == 100 && stringBaseListDto.data != null)
                        {
                            unavailableDateList = stringBaseListDto.data;

                            if (unavailableDateList == null)
                            {
                                unavailableDateList = new ArrayList<String>();
                            }
                        } else
                        {
                            throw new BaseException(stringBaseListDto.msgCode, stringBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return unavailableDateList;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<String>> getStayAvailableCheckOutDates(int placeIndex, int dateRange, String checkInDate)
    {
        return DailyMobileAPI.getInstance(mContext).getStayAvailableCheckOutDates(placeIndex, dateRange, checkInDate) //
            .map(new Function<BaseListDto<String>, List<String>>()
            {
                @Override
                public List<String> apply(@NonNull BaseListDto<String> stringBaseListDto) throws Exception
                {
                    List<String> availableDateList = null;

                    if (stringBaseListDto != null)
                    {
                        if (stringBaseListDto.msgCode == 100 && stringBaseListDto.data != null)
                        {
                            availableDateList = stringBaseListDto.data;

                            if (availableDateList == null)
                            {
                                availableDateList = new ArrayList<String>();
                            }
                        } else
                        {
                            throw new BaseException(stringBaseListDto.msgCode, stringBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return availableDateList;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }
}
