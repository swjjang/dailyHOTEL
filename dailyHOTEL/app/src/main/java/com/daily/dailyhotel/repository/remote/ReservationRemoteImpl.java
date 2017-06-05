package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ReservationInterface;
import com.daily.dailyhotel.entity.Reservation;
import com.daily.dailyhotel.repository.remote.model.ReservationData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class ReservationRemoteImpl implements ReservationInterface
{
    private Context mContext;

    public ReservationRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<List<Reservation>> getStayOutBoundReservationList()
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutBoundReservationList().map(new Function<BaseListDto<ReservationData>, List<Reservation>>()
        {
            @Override
            public List<Reservation> apply(@io.reactivex.annotations.NonNull BaseListDto<ReservationData> reservationDataBaseListDto) throws Exception
            {
                List<Reservation> reservationList = null;

                if (reservationDataBaseListDto != null)
                {
                    if (reservationDataBaseListDto.msgCode == 100 && reservationDataBaseListDto.data != null)
                    {
                        for (ReservationData reservationData : reservationDataBaseListDto.data)
                        {
                            reservationList.add(reservationData.getReservation());
                        }
                    } else
                    {
                        throw new BaseException(reservationDataBaseListDto.msgCode, reservationDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return reservationList;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Reservation>> getReservationList()
    {
        return DailyMobileAPI.getInstance(mContext).getReservationList().map(new Function<BaseListDto<ReservationData>, List<Reservation>>()
        {
            @Override
            public List<Reservation> apply(@io.reactivex.annotations.NonNull BaseListDto<ReservationData> reservationDataBaseListDto) throws Exception
            {
                List<Reservation> reservationList = null;

                if (reservationDataBaseListDto != null)
                {
                    if (reservationDataBaseListDto.msgCode == 100 && reservationDataBaseListDto.data != null)
                    {
                        for (ReservationData reservationData : reservationDataBaseListDto.data)
                        {
                            reservationList.add(reservationData.getReservation());
                        }
                    } else
                    {
                        throw new BaseException(reservationDataBaseListDto.msgCode, reservationDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return reservationList;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
