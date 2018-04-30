package com.daily.dailyhotel.repository.remote;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.CalendarInterface;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 7. 12..
 */

public class CalendarImpl extends BaseRemoteImpl implements CalendarInterface
{
    @Override
    public Observable<List<Integer>> getGourmetUnavailableDates(int gourmetIndex, int dateRange, boolean reverse)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}/unavailableDates" //
            : "NDYkMTEkNDQkMTE0JDg5JDExMSQxMDgkMzQkMjYkNSQxMDEkMjgkMTA4JDQ2JDExMyQxMSQ=$MUEzQEjA3MzLBMFMjM1NUU0Q0U0RWSEYwN0Y5RJDA5REFGMZkFZGM0SZFNEY1RUY1NTI0NkU1NjVFODA3QzMwQ0NBRTJDRTOFBQTJFNTBZBQUVQCOTZkyFRUZABQzUOwMDRBNUUwMzA5MEQ0$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(gourmetIndex));

        return mDailyMobileService.getGourmetUnavailableDates(Crypto.getUrlDecoderEx(URL, urlParams), dateRange, reverse) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<String>, List<Integer>>()
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
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}/unavailableCheckinDates" //
            : "MjIkMTAwJDM1JDg3JDM0JDEyNiQyNCQ5NyQ0NiQ3OSQxMjUkNzYkNiQyOSQ3NCQ0OCQ=$OTMxQTTQ0MkI1NUUwNjBERjYMAzRkFY0NTM0MTEBVBQjVFMEEMQwMzc5NTNEQUMxODA1NDIzMTIOwQUML3ODIUxNUUzMzBCMPUNCMkZDNQjE5QkFEFODJBMzkwQzBEMkNGMMTI3NITZEQkM5$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(placeIndex));

        return mDailyMobileService.getStayUnavailableCheckInDates(Crypto.getUrlDecoderEx(URL, urlParams), dateRange, reverse) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<String>, List<String>>()
            {
                @Override
                public List<String> apply(@NonNull BaseListDto<String> stringBaseListDto) throws Exception
                {
                    List<String> unavailableDateList;

                    if (stringBaseListDto != null)
                    {
                        if (stringBaseListDto.msgCode == 100 && stringBaseListDto.data != null)
                        {
                            unavailableDateList = stringBaseListDto.data;

                            if (unavailableDateList == null)
                            {
                                unavailableDateList = new ArrayList<>();
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

    /**
     * @param placeIndex
     * @param dateRange   : 오늘로 부터 몇일까지 볼지
     * @param checkInDate
     * @return
     */
    @Override
    public Observable<List<String>> getStayAvailableCheckOutDates(int placeIndex, int dateRange, String checkInDate)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}/availableCheckoutDates" //
            : "MTMkMzgkODIkMjYkNDMkMTEyJDUyJDkwJDc2JDE0JDEkNDQkMzckMTUkMTM5JDEyNyQ=$QUjM1OUFBRDRBNCNVTlDOUUwOTY3QZzhEMUI0OHDc5MWjVGCIRDJDMzU0DQjhGMjg0MUY5MUUyMUZDQURVEQkM3NjY4XRjI1TN0U2RkFENjk4RDUxMDhGMjICwMUQyOMTY2RDlDRDYzNMkVD$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(placeIndex));

        return mDailyMobileService.getStayAvailableCheckOutDates(Crypto.getUrlDecoderEx(URL, urlParams), dateRange, checkInDate) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<String>, List<String>>()
            {
                @Override
                public List<String> apply(@NonNull BaseListDto<String> stringBaseListDto) throws Exception
                {
                    List<String> availableDateList;

                    if (stringBaseListDto != null)
                    {
                        if (stringBaseListDto.msgCode == 100 && stringBaseListDto.data != null)
                        {
                            availableDateList = new ArrayList<>(stringBaseListDto.data);
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
            });
    }
}
