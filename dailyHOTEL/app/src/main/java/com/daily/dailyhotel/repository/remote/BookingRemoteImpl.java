package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.BookingInterface;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.repository.remote.model.BookingData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class BookingRemoteImpl implements BookingInterface
{
    private Context mContext;

    public BookingRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<List<Booking>> getStayOutboundBookingList()
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundBookingList().map(new Function<BaseListDto<BookingData>, List<Booking>>()
        {
            @Override
            public List<Booking> apply(@io.reactivex.annotations.NonNull BaseListDto<BookingData> bookingDataBaseListDto) throws Exception
            {
                List<Booking> bookingList = null;

                if (bookingDataBaseListDto != null)
                {
                    if (bookingDataBaseListDto.msgCode == 100 && bookingDataBaseListDto.data != null)
                    {
                        for (BookingData bookingData : bookingDataBaseListDto.data)
                        {
                            bookingList.add(bookingData.getBooking());
                        }
                    } else
                    {
                        throw new BaseException(bookingDataBaseListDto.msgCode, bookingDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return bookingList;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Booking>> getBookingList()
    {
        return DailyMobileAPI.getInstance(mContext).getBookingList().map(new Function<BaseListDto<BookingData>, List<Booking>>()
        {
            @Override
            public List<Booking> apply(@io.reactivex.annotations.NonNull BaseListDto<BookingData> bookingDataBaseListDto) throws Exception
            {
                List<Booking> bookingList = null;

                if (bookingDataBaseListDto != null)
                {
                    if (bookingDataBaseListDto.msgCode == 100 && bookingDataBaseListDto.data != null)
                    {
                        for (BookingData bookingData : bookingDataBaseListDto.data)
                        {
                            bookingList.add(bookingData.getBooking());
                        }
                    } else
                    {
                        throw new BaseException(bookingDataBaseListDto.msgCode, bookingDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return bookingList;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> getStayOutboundHideBooking(int reservationIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundHideBooking(reservationIndex).map(new Function<BaseDto, Boolean>()
        {
            @Override
            public Boolean apply(@io.reactivex.annotations.NonNull BaseDto baseDto) throws Exception
            {
                boolean result = false;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        result = true;
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return result;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
