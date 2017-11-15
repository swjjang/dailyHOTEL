package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RefundInterface;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundDetailData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RefundRemoteImpl extends BaseRemoteImpl implements RefundInterface
{
    public RefundRemoteImpl(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public Observable<StayOutboundRefundDetail> getStayOutboundRefundDetail(int bookingIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/hotel-reservations/{reservationIdx}/cancelinfos"//
            : "MTEyJDY2JDExMCQxMjAkMjEkNTMkMTE2JDEyMiQxNSQxNjYkMzAkMTY3JDE1NSQzNSQ3MCQyMiQ=$MzRFOUM4N0ZCODAD4QTNDOHODA3RkE3QMjM4UOEY0RURCMDlDNEEyQTdFONjM3MEExQ0UxQIUSM4Qjc1NzY0MTQ1Nzc0NDgyRTQ2REZFRjU2NkRFNDgzNUjIyJEMzEwCNZEQ5RUMyMzg2Nzk1MjhDMUQ5RUE0QBzlEQzY0MTFGRDXEI3MzNGM0UxQzY=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(bookingIndex));

        return mDailyMobileService.getStayOutboundRefundDetail( //
            Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundRefundDetailData>, StayOutboundRefundDetail>()
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
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/hotel-reservations/{reservationIdx}/cancel"//
            : "NiQxMTIkMTMxJDY5JDg4JDE2MSQ4MiQxMDQkOTYkMjgkOSQzNiQ1OCQ3OSQxMzgkMTUyJA==$NjYxRTFE3DQTk4RkY3N0FEMjc0QjcW1OTgzNFDdGOTY3RTFEODMxRTM5QjCUxMjdBMzQ4MjFDKQjc0NBTY5NDgxEQjYyNzVAyN0ZCBNjI5RDczINzZENEQzNTcV1MTVBMjgwNTQ0OTQY3OBDhDMjIzNTJI3NzI0RkM3MTY5MjNBCRDlDNUQ0ODBDNjA=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(bookingIndex));

        return mDailyMobileService.getStayOutboundRefund( //
            Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams), refundType, cancelReasonType, reasons) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundRefundData>, String>()
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

        final String API = Constants.UNENCRYPTED_URL ? "api/v3/payment/refund"//
            : "ODIkNzIkOCQ0NSQ2MiQ0OCQ1OSQyMCQ3NyQ5MyQ1NiQ4MyQ2OCQ4NCQyMyQxJA==$OXTlDMEM0TNjdDMEIxQjVGDRJjk4NTMxQUZDQzU1RDhGQzdDFMDJI0NzUyJMTM2OMTA4CQITk5OEJFOEUL5BOTRMc1MjIxMEGVBOQg==$";

        return mDailyMobileService.getRefund(Crypto.getUrlDecoderEx(API), jsonObject) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<Object>, String>()
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

        final String API = Constants.UNENCRYPTED_URL ? "api/v3/payment/refund/vbank"//
            : "ODUkNDAkNjIkODUkMjEkMjAkMSQ4JDQ4JDUkMzQkMTUkMTEkMTQkNjAkNTIk$NUkUyBQTcN0WQUKI4SQUEwNThBSONTdFMDU4NTkU5OTgxODdGLQ0UUQ2NDcwMW0QzQzI5NkYzRRkFGNDg4Qjc5NjE5MTU3NjhPDRKQ==$";

        return mDailyMobileService.getRefund(Crypto.getUrlDecoderEx(API), jsonObject) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<Object>, String>()
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
