package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.BookingInterface;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.GourmetBookingDetail;
import com.daily.dailyhotel.entity.BookingCancel;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.entity.WaitingDeposit;
import com.daily.dailyhotel.repository.remote.model.BookingData;
import com.daily.dailyhotel.repository.remote.model.BookingHideData;
import com.daily.dailyhotel.repository.remote.model.GourmetBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.BookingCancelData;
import com.daily.dailyhotel.repository.remote.model.StayBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.WaitingDepositData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;

import java.util.ArrayList;
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
                List<Booking> bookingList = new ArrayList<>();

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
                List<Booking> bookingList = new ArrayList<>();

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
    public Observable<Boolean> getStayOutboundHideBooking(int bookingIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundHideBooking(bookingIndex).map(new Function<BaseDto<BookingHideData>, Boolean>()
        {
            @Override
            public Boolean apply(@io.reactivex.annotations.NonNull BaseDto<BookingHideData> bookingHideDataBaseDto) throws Exception
            {
                boolean result;

                if (bookingHideDataBaseDto != null)
                {
                    if (bookingHideDataBaseDto.msgCode == 100 && bookingHideDataBaseDto.data != null)
                    {
                        result = true;
                    } else
                    {
                        throw new BaseException(bookingHideDataBaseDto.msgCode, bookingHideDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return result;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StayOutboundBookingDetail> getStayOutboundBookingDetail(int bookingIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundBookingDetail(bookingIndex).map(new Function<BaseDto<StayOutboundBookingDetailData>, StayOutboundBookingDetail>()
        {
            @Override
            public StayOutboundBookingDetail apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundBookingDetailData> stayOutboundBookingDetailDataBaseDto) throws Exception
            {
                StayOutboundBookingDetail stayOutboundBookingDetail;

                if (stayOutboundBookingDetailDataBaseDto != null)
                {
                    if (stayOutboundBookingDetailDataBaseDto.msgCode == 100 && stayOutboundBookingDetailDataBaseDto.data != null)
                    {
                        stayOutboundBookingDetail = stayOutboundBookingDetailDataBaseDto.data.getStayOutboundBookingDetail();
                    } else
                    {
                        throw new BaseException(stayOutboundBookingDetailDataBaseDto.msgCode, stayOutboundBookingDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayOutboundBookingDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StayBookingDetail> getStayBookingDetail(String aggregationId)
    {
        return DailyMobileAPI.getInstance(mContext).getStayBookingDetail(aggregationId).map(new Function<BaseDto<StayBookingDetailData>, StayBookingDetail>()
        {
            @Override
            public StayBookingDetail apply(@io.reactivex.annotations.NonNull BaseDto<StayBookingDetailData> stayBookingDetailDataBaseDto) throws Exception
            {
                StayBookingDetail stayBookingDetail;

                if (stayBookingDetailDataBaseDto != null)
                {
                    if (stayBookingDetailDataBaseDto.msgCode == 100 && stayBookingDetailDataBaseDto.data != null)
                    {
                        stayBookingDetail = stayBookingDetailDataBaseDto.data.getStayBookingDetail();
                    } else
                    {
                        throw new BaseException(stayBookingDetailDataBaseDto.msgCode, stayBookingDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayBookingDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GourmetBookingDetail> getGourmetBookingDetail(String aggregationId)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetBookingDetail(aggregationId).map(new Function<BaseDto<GourmetBookingDetailData>, GourmetBookingDetail>()
        {
            @Override
            public GourmetBookingDetail apply(@io.reactivex.annotations.NonNull BaseDto<GourmetBookingDetailData> gourmetBookingDetailDataBaseDto) throws Exception
            {
                GourmetBookingDetail gourmetBookingDetail;

                if (gourmetBookingDetailDataBaseDto != null)
                {
                    if (gourmetBookingDetailDataBaseDto.msgCode == 100 && gourmetBookingDetailDataBaseDto.data != null)
                    {
                        gourmetBookingDetail = gourmetBookingDetailDataBaseDto.data.getGourmetBookingDetail();
                    } else
                    {
                        throw new BaseException(gourmetBookingDetailDataBaseDto.msgCode, gourmetBookingDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return gourmetBookingDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<WaitingDeposit> getWaitingDeposit(String aggregationId)
    {
        return DailyMobileAPI.getInstance(mContext).getWaitingDeposit(aggregationId).map(new Function<BaseDto<WaitingDepositData>, WaitingDeposit>()
        {
            @Override
            public WaitingDeposit apply(@io.reactivex.annotations.NonNull BaseDto<WaitingDepositData> waitingDepositDataBaseDto) throws Exception
            {
                WaitingDeposit waitingDeposit;

                if (waitingDepositDataBaseDto != null)
                {
                    if (waitingDepositDataBaseDto.msgCode == 100 && waitingDepositDataBaseDto.data != null)
                    {
                        waitingDeposit = waitingDepositDataBaseDto.data.getWaitingDeposit();
                    } else
                    {
                        throw new BaseException(waitingDepositDataBaseDto.msgCode, waitingDepositDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return waitingDeposit;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<BookingCancel>> getBookingCancelList()
    {
        return DailyMobileAPI.getInstance(mContext).getBookingCancelList().map(new Function<BaseListDto<BookingCancelData>, List<BookingCancel>>()
        {
            @Override
            public List<BookingCancel> apply(@io.reactivex.annotations.NonNull BaseListDto<BookingCancelData> bookingCancelDataBaseListDto) throws Exception
            {
                List<BookingCancel> list = new ArrayList<>();

                if (bookingCancelDataBaseListDto != null)
                {
                    if (bookingCancelDataBaseListDto.msgCode == 100 && bookingCancelDataBaseListDto.data != null)
                    {
                        for (BookingCancelData cancelData : bookingCancelDataBaseListDto.data)
                        {
                            list.add(cancelData.getBookingCancel());
                        }
                    } else
                    {
                        throw new BaseException(bookingCancelDataBaseListDto.msgCode, bookingCancelDataBaseListDto.msg);
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
    public Observable<List<BookingCancel>> getStayOutboundBookingCancelList()
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundBookingCancelList().map(new Function<BaseListDto<BookingCancelData>, List<BookingCancel>>()
        {
            @Override
            public List<BookingCancel> apply(@io.reactivex.annotations.NonNull BaseListDto<BookingCancelData> bookingCancelDataBaseListDto) throws Exception
            {
                List<BookingCancel> list = new ArrayList<>();

                if (bookingCancelDataBaseListDto != null)
                {
                    if (bookingCancelDataBaseListDto.msgCode == 100 && bookingCancelDataBaseListDto.data != null)
                    {
                        for (BookingCancelData cancelData : bookingCancelDataBaseListDto.data)
                        {
                            list.add(cancelData.getBookingCancel());
                        }
                    } else
                    {
                        throw new BaseException(bookingCancelDataBaseListDto.msgCode, bookingCancelDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return list;
            }
        });
    }
}
