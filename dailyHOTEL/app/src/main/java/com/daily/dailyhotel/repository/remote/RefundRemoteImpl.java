package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RefundInterface;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundDetailData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import org.json.JSONObject;

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
                StayOutboundRefundDetail stayOutboundRefundDetail;

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
                String message;

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
    public Observable<String> getRefund(String aggregationId, int reservationIndex, String reason, String serviceType)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("aggregationId", aggregationId);
            jsonObject.put("reason", reason);
            jsonObject.put("reservationIdx", reservationIndex);
            jsonObject.put("serviceType", serviceType);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return DailyMobileAPI.getInstance(mContext).getRefund(jsonObject).map(new Function<BaseDto<Object>, String>()
        {
            @Override
            public String apply(@io.reactivex.annotations.NonNull BaseDto<Object> baseDto) throws Exception
            {
                String message;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100)
                    {
                        message = baseDto.msg;
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
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
    public Observable<String> getRefund(String aggregationId, int reservationIndex, String reason, String serviceType//
        , String accountHolder, String accountNumber, String bankCode)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("aggregationId", aggregationId);
            jsonObject.put("reason", reason);
            jsonObject.put("reservationIdx", reservationIndex);
            jsonObject.put("serviceType", serviceType);
            jsonObject.put("accountHolder", accountHolder);
            jsonObject.put("accountNumber", accountNumber);
            jsonObject.put("bankCode", bankCode);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return DailyMobileAPI.getInstance(mContext).getRefundVBank(jsonObject).map(new Function<BaseDto<Object>, String>()
        {
            @Override
            public String apply(@io.reactivex.annotations.NonNull BaseDto<Object> baseDto) throws Exception
            {
                String message;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100)
                    {
                        message = baseDto.msg;
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
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
