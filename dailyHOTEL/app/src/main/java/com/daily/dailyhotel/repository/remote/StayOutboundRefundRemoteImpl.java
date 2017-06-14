package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.StayOutboundRefundInterface;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundDetailData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class StayOutboundRefundRemoteImpl implements StayOutboundRefundInterface
{
    private Context mContext;

    public StayOutboundRefundRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayOutboundRefundDetail> getStayOutboundRefundDetail(int bookingIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundRefundDetail(bookingIndex).map(new Function<BaseDto<StayOutboundRefundDetailData>, StayOutboundRefundDetail>()
        {
            @Override
            public StayOutboundRefundDetail apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundRefundDetailData> stayOutboundRefundDetailDataBaseDto) throws Exception
            {
                StayOutboundRefundDetail stayOutboundRefundDetail = null;

                if (stayOutboundRefundDetailDataBaseDto != null)
                {
                    if (stayOutboundRefundDetailDataBaseDto.msgCode == 100 && stayOutboundRefundDetailDataBaseDto.data != null)
                    {
                        stayOutboundRefundDetail = stayOutboundRefundDetailDataBaseDto.data.getStayOutboundBookingDetail();
                    } else
                    {
                        throw new BaseException(stayOutboundRefundDetailDataBaseDto.msgCode, stayOutboundRefundDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayOutboundRefundDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> getStayOutboundRefund(int bookingIndex, String refundType, String cancelReasonType, String reasons)
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundRefund(bookingIndex, refundType, cancelReasonType, reasons).map(new Function<BaseDto<StayOutboundRefundData>, String>()
        {
            @Override
            public String apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundRefundData> stayOutboundRefundDataBaseDto) throws Exception
            {
                String message = null;

                if (stayOutboundRefundDataBaseDto != null)
                {
                    if (stayOutboundRefundDataBaseDto.msgCode == 100 && stayOutboundRefundDataBaseDto.data != null)
                    {
                        message = stayOutboundRefundDataBaseDto.data.message;
                    } else
                    {
                        throw new BaseException(stayOutboundRefundDataBaseDto.msgCode, stayOutboundRefundDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return message;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
