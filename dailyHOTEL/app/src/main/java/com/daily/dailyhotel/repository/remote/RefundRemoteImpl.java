package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RefundInterface;
import com.daily.dailyhotel.entity.Bank;
import com.daily.dailyhotel.entity.OldRefund;
import com.daily.dailyhotel.entity.RefundPolicy;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.daily.dailyhotel.repository.remote.model.BankData;
import com.daily.dailyhotel.repository.remote.model.OldRefundData;
import com.daily.dailyhotel.repository.remote.model.RefundPolicyData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundDetailData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RefundRemoteImpl extends BaseRemoteImpl implements RefundInterface
{
    @Override
    public Observable<StayOutboundRefundDetail> getStayOutboundRefundDetail(Context context, int bookingIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

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
                            stayOutboundRefundDetail = stayOutboundRefundDetailDataBaseDto.data.getStayOutboundRefundDetail();
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
    public Observable<String> getStayOutboundRefund(Context context, int bookingIndex, String refundType, String cancelReasonType, String reasons)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

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

    @Override
    public Observable<OldRefund> getRefund(int hotelIdx, String dateCheckIn, String transactionType //
        , int hotelReservationIdx, String reasonCancel, String accountHolder, String bankAccount, String bankCode)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v2/payment/refund"//
            : "MTQkNzUkNDMkODUkMzkkNzMkMTQkNjIkMzMkMTYkODIkODkkNzAkMzIkMjkkNDMk$Nzg0NTMxOUNGOUOMFE1RDE2NjRFRTRM1OOThXBOUQ2RNDIhBQ0KEyRjIxMjcwNTMyOTANFQ0ME3RDNBNzFE3MzHRQzNTIC0NjEQ3OA==$";

        return mDailyMobileService.getRefund(Crypto.getUrlDecoderEx(API), hotelIdx, dateCheckIn //
            , transactionType, hotelReservationIdx, reasonCancel, accountHolder, bankAccount, bankCode) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<OldRefundData>, OldRefund>()
            {
                @Override
                public OldRefund apply(BaseDto<OldRefundData> oldRefundDataBaseDto) throws Exception
                {
                    OldRefund oldRefund = null;

                    if (oldRefundDataBaseDto != null)
                    {
                        // msgCode 1013: 환불 요청 중 실패한 것으로 messageFromPg를 사용자에게 노출함.
                        // msgCode 1014: 무료 취소 횟수를 초과한 것으로 msg 내용을 사용자에게 노출함.
                        // msgCode 1015: 환불 수동 스위치 ON일 경우
                        switch (oldRefundDataBaseDto.msgCode)
                        {
                            case 1014:
                                oldRefund = new OldRefund();
                                oldRefund.msgCode = oldRefundDataBaseDto.msgCode;
                                oldRefund.messageFromPg = oldRefundDataBaseDto.msg;
                                break;

                            case 1013:
                            case 1015:
                            default:

                                if (oldRefundDataBaseDto.data != null)
                                {
                                    oldRefund = oldRefundDataBaseDto.data.getOldRefund();
                                }

                                if (oldRefund == null)
                                {
                                    oldRefund = new OldRefund();
                                }

                                oldRefund.msgCode = oldRefundDataBaseDto.msgCode;

                                if (DailyTextUtils.isTextEmpty(oldRefund.messageFromPg) == true)
                                {
                                    oldRefund.messageFromPg = oldRefundDataBaseDto.msg;
                                }
                                break;
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return oldRefund;
                }
            });
    }

    @Override
    public Observable<RefundPolicy> getStayRefundPolicy(int reservationIndex, String transactionType)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v2/reservation/hotel/policy_refund"//
            : "MTAkMTEzJDkxJDkxJDYwJDckNiQ3MCQxJDcyJDIxJDQ4JDEyNyQxMDYkMzIkNDEk$OMEZGRELUMyRjLlGMTBCNAkUzNTJFRTQTyRUYxMDAT2ODQyOUMZ2QTIzNDkxN0NDRTBPGMzNCOUCSNCOEUxNTRCMUE2RDE3N0VGODXQBGMEIS5OTBGNTM0NUJEM0U1GMTQV0RTc2Njk4RjM3$";

        return mDailyMobileService.getRefundPolicy(Crypto.getUrlDecoderEx(API), reservationIndex, transactionType) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<RefundPolicyData>, RefundPolicy>()
            {
                @Override
                public RefundPolicy apply(BaseDto<RefundPolicyData> refundPolicyDataBaseDto) throws Exception
                {
                    RefundPolicy refundPolicy;

                    if (refundPolicyDataBaseDto != null)
                    {
                        switch (refundPolicyDataBaseDto.msgCode)
                        {
                            case 100:
                            case 1015:
                            {
                                if (refundPolicyDataBaseDto.data != null)
                                {
                                    refundPolicy = refundPolicyDataBaseDto.data.getRefundPolicy();
                                    refundPolicy.message = refundPolicyDataBaseDto.msg;
                                } else
                                {
                                    throw new BaseException(refundPolicyDataBaseDto.msgCode, refundPolicyDataBaseDto.msg);
                                }
                                break;
                            }

                            default:
                                throw new BaseException(refundPolicyDataBaseDto.msgCode, refundPolicyDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return refundPolicy;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable<List<Bank>> getBankList()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v2/payment/bank"//
            : "NjEkMTIkNzkkNDMkMTgkNjMkMzkkMzgkNDgkMjgkMzMkNjEkNzckNjAkNjEkMjQk$RDlCRUJDNUY0ROUM5MODA1MjDY5REHQzNTAE4RTM2CNWEZGRkUYR4NzQ4MUQxMBRKDY2RDkwQNG0RFNETE2MkU0OTNENFTY3Nzc0Mg==$";


        return mDailyMobileService.getBankList(Crypto.getUrlDecoderEx(API)).subscribeOn(Schedulers.io()) //
            .map(new Function<BaseListDto<BankData>, List<Bank>>()
            {
                @Override
                public List<Bank> apply(BaseListDto<BankData> bankDataBaseListDto) throws Exception
                {
                    List<Bank> bankList = new ArrayList<>();

                    if (bankDataBaseListDto != null)
                    {
                        if (bankDataBaseListDto.msgCode == 100 && bankDataBaseListDto.data != null)
                        {
                            for (BankData bankData : bankDataBaseListDto.data)
                            {
                                bankList.add(bankData.getBank());
                            }
                        } else
                        {
                            throw new BaseException(bankDataBaseListDto.msgCode, bankDataBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return bankList;
                }
            });
    }
}
