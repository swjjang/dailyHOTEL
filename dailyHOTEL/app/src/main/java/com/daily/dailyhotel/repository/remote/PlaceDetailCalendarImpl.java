package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.PlaceDetailCalendarInterface;
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

public class PlaceDetailCalendarImpl implements PlaceDetailCalendarInterface
{
    private Context mContext;

    public PlaceDetailCalendarImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<List<String>> getGourmetUnavailableDates(int placeIndex, int dateRange, boolean reverse)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetUnavailableDates(placeIndex, dateRange, reverse) //
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
    public Observable<List<String>> getStayUnavailableDates(int placeIndex, int dateRange, boolean reverse)
    {
        return DailyMobileAPI.getInstance(mContext).getStayUnavailableDates(placeIndex, dateRange, reverse) //
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
}
