package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.domain.RefundInterface;
import com.daily.dailyhotel.entity.Refund;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.daily.dailyhotel.repository.remote.model.RefundData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundDetailData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class RefundRemoteImpl implements RefundInterface
{
    private Context mContext;

    public RefundRemoteImpl(@NonNull Context context)
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

    @Override
    public Observable<Refund> getRefund(String aggregationId, String bankAccount, String bankCode)
    {
        return DailyMobileAPI.getInstance(mContext).getRefund(aggregationId, bankAccount, bankCode).map(new Function<BaseDto<RefundData>, Refund>()
        {
            @Override
            public Refund apply(@io.reactivex.annotations.NonNull BaseDto<RefundData> refundDataBaseDto) throws Exception
            {
                Refund refund = new Refund();

                if (refundDataBaseDto != null)
                {
                    refund.msgCode = refundDataBaseDto.msgCode;

                    // msgCode 1013: 환불 요청 중 실패한 것으로 messageFromPg를 사용자에게 노출함.
                    // msgCode 1014: 무료 취소 횟수를 초과한 것으로 msg 내용을 사용자에게 노출함.
                    // msgCode 1015: 환불 수동 스위치 ON일 경우
                    switch (refundDataBaseDto.msgCode)
                    {
                        case 1014:
                            refund.message = refundDataBaseDto.msg;
                            break;

                        case 1013:
                        case 1015:
                        default:
                            if (refundDataBaseDto.data != null)
                            {
                                refund.message = refundDataBaseDto.data.messageFromPg;
                                refund.readyForRefund = refundDataBaseDto.data.readyForRefund;
                            }

                            if (DailyTextUtils.isTextEmpty(refund.message) == true)
                            {
                                refund.message = refundDataBaseDto.msg;
                            }
                            break;
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return refund;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
