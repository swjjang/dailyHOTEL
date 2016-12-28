/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * VolleyHttpClient
 * <p>
 * 네트워크 이미지 처리 및 네트워크 처리 작업을 담당하는 외부 라이브러리 Vol
 * ley를 네트워크 처리 작업을 목적으로 사용하기 위해 설정하는 유틸 클래스이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.network;

import android.content.Context;

import com.twoheart.dailyhotel.network.factory.TagCancellableCallAdapterFactory.ExecutorCallbackCall;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyMobileAPI implements IDailyNetwork
{
    private static DailyMobileAPI mInstance;
    private DailyMobileService mDailyMobileService;

    public static synchronized DailyMobileAPI getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyMobileAPI(context);
        }

        return mInstance;
    }

    private DailyMobileAPI(Context context)
    {
        mDailyMobileService = RetrofitHttpClient.getInstance(context).getService();
    }

    public void cancelAll(Context context)
    {
        RetrofitHttpClient.getInstance(context).cancelAll();
    }

    public void cancelAll(Context context, final String tag)
    {
        if (Util.isTextEmpty(tag) == true)
        {
            return;
        }

        RetrofitHttpClient.getInstance(context).cancelAll(tag);
    }

    @Override
    public void requestStatusServer(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "http://status.dailyhotel.kr/status/health/check"//
            : "MzUkMTI2JDM2JDIkNjEkNzEkOTMkOTAkMTA5JDYwJDU4JDkxJDEwOCQzOSQxMTkkNTMk$MTTc5NDRENTlBQUUxMzNDRDFEMDY5NTZGNTQHFzVNEFGNEY4RDlBOVEE4Q0VWDRWUBNDNEI2NkUTzN0MyMEYzRTg2MTVCJQRkJBGQTgzMkM5MTEBGQkJEwOEOIxOTlENjIwNzE1REZFQZTIx$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStatusServer(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestCommonVersion(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/version"//
            : "NDIkMzAkNTQkNTgkNjckODAkNjIkOTAkODckMzckMyQ2NCQ1NSQyOSQyMSQyMiQ=$NTJCFOURFNjU4QjdDMzRCDSMEY4OUYyMNTOk5NjdCIMTQ1NjRg2QjE4MUQLxHQUZJFRTXPIzM0VUEQkNGRDlBREVZEQTgxFNjIMyRg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCommonVersion(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestCommonDateTime(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/datetime"//
            : "NzgkMTUkMjckNTUkNjEkNjckNjckNDUkMTAkMjYkMTckMTkkNjckNTQkNjgkNDYk$ODE1MDI0NzMZGREQAJ0IMDFBNkIzTNTLUwNDAzMzY3MzQ0IMzZXEMkUT0RTZEMNkZGRUDAJE4MTkDSxOTVEMjBBQjRFQzMVDN0VDOA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCommonDateTime(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserProfile(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/profile"//
            : "NzMkNTEkMzYkNTkkNzckNjQkMTQkMjkkNTIkNTkkODckOSQ5NyQ5JDg5JDEk$MRUY4NUFGMRYjU0MjNI0Q0YyNjYyMjdCKMEQ5M0U5MMEY5NDQyQjcwNFTEC5NTKRCQS0ZFNPEU3RjFCOEMwMWOURDQJHjBEQTI4NRQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserProfile(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserBonus(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/session/bonus/all"//
            : "MjIkMSQ4MiQ2NCQ4NiQ0NCQ3OCQ5JDgzJDQyJDYwJDAkNDkkOTckNjkkMzkk$YNUkE4ODVEQRkIxMDVBQjRDMUIFFNjQ5NDA3NkUT5RUMF0RTKVCGMTEzMUM4NDMPwOEUwNETEc4QkE1NUVGMTCIyQBTQ2RODGU2XQQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserBonus(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserInformationUpdate(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/users/profile"//
            : "MzEkNTAkNzMkMzAkMzgkNDQkMTckMzIkNjMkOTIkNjAkNzMkOCQ1MiQ1JDM3JA==$RTZGMNDc1TMjhGQTA2QXzM3MTQ3MzY1OTTPVJMFNjJBXOUVGQXTY5NJjg2MUzg5NCDQ3WNDdGQUFFCRjdDOEVODODQ5MTk5MjcO0OA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserInformationUpdate(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserProfileBenefit(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/profile/benefit"//
            : "NDUkODAkMjkkMjEkMzMkMzMkMzEkODgkMzgkNzUkOTMkNzgkMjMkOTYkNTQkODck$N0M1N0ZCQzE4ODgxQ0Y2QWTTEzMjBCOCCTRBYDRTDE4RDhGMDkyMXzLY0NjcxNDM3NEVCMDE2QTc3RRjPdDREZFWODUT2RBjACG5Rg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserProfileBenefit(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserCheckEmail(String tag, String userEmail, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/check/email_auth"//
            : "ODAkOSQxOSQ1OCQ3NSQ2NiQ1OCQzNiQ5MSQzNCQzOSQ3OCQ2JDc4JDU4JDg3JA==$MzAwMkIQwOCDU5MDRERTQI3MDkyODZCNDBGWQkFJVFQUFCMDcyMjJENDI3ENTY0RPMDU0NDEU0OTgyOHDCVDMMzMZDOTk2BRDQU0NQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserCheckEmail(Crypto.getUrlDecoderEx(URL), userEmail);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserChangePassword(String tag, String userEmail, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/change_pw"//
            : "NDMkNDEkMjAkMTQkNiQ0MSQzOSQzMCQ0NyQ0MyQ0MCQ0NyQyNCQwJDUzJDE2JA==$VMjM3MUWE5M0VBMzRJAwRjM3LMVjM3Q0RIGNjc3OEJBPUNTIHUV3MGPWDUB=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserChangePassword(Crypto.getUrlDecoderEx(URL), userEmail);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserInformationForPayment(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/information"//
            : "NTQkNTMkNzckMTgkODIkODEkMTgkNjYkMzQkNTYkODIkNzYkNzQkMzckNTQkMjMk$ODAzNUVCRjAyNDIwMzPClCNATc5ODc2OEEzIN0JU1MjVCMUQwNEMzOTTY2NEMUSIzRDA5MjWAyNkRTBNQDE1NUZVPFNBUEMzQTFDMg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserInformationForPayment(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserUpdateInformationForSocial(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/update/fb_user"//
            : "NjUkNzMkMzUkNzAkNzQkNTIkNSQ2OSQzMiQ5MiQ4MyQzMyQ1MyQ0NCQxNSQyMSQ=$RDdBNAzNEMDA5NjVE4QTAI3NzUzODk4NTUSUwMURHBQjUzTRDZCRTBENS0QT0QTdFNDFGMEU4OZAThDVNEMB5QNkIQzOUJFOEUN3QQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserUpdateInformationForSocial(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserBillingCardList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/info"//
            : "NDIkOCQ1NSQ4NyQ4NyQ4MCQxMzIkOTIkMTMwJDU2JDE2JDQyJDY4JDU5JDEzMCQ3MyQ=$QzdFNkE5NNjgzM0JIFMjZFRjlCQjY4OEQ3NkI5NDdDKRjUMxNDkzNTk1MTBWjkzQkE5NELNDNQ0RFOENGRDAwMGEE5MTE3UARDYFGQjEzMzMxRjVDMDA2MjVEQzBGMTgxREYJGNDMK3MTGI2$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserBillingCardList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserDeleteBillingCard(String tag, String billkey, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/del"//
            : "NTkkODYkOTMkMTEyJDMyJDI3JDMwJDYzJDI1JDEwNiQzOSQyOCQxMzYkODgkMzUkMTgk$MTBDN0VBRjZFRkE4ODAA2OEYyNKDcQS2NSkZNESMEVRBNjhDNzczMjE1OUM3QjIxMzYVPzRjk1RDQ1QzgzMDlENzI3QNjIxQDTU2NkFOBMzhCOTBg3RjkzRDQ3RITg5RDE0Q0QxNjgyUNURF$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserDeleteBillingCard(Crypto.getUrlDecoderEx(URL), billkey);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayList(String tag, Map<String, Object> queryMap, List<String> bedTypeList, List<String> luxuryList, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales"//
            : "NzEkOSQ1MyQ1MiQ2OCQ3MyQ3MSQ4MCQ4MCQ4OSQ3MiQ3NiQyJDUwJDM1JDEwJA==$ODWg1NUYzOPWTg1ODczQzU2ODM0N0M5RDVDNDDRBNTNCMjAzOTVEQNDYUyPRDAxNjc2QkI4RPDBGQDNVPjkM1RJMUE0RTYzNNTdCQg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayList(Crypto.getUrlDecoderEx(URL), queryMap, bedTypeList, luxuryList);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStaySearchAutoCompleteList(String tag, String date, int stays, String term, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales/search/auto_complete"//
            : "NzUkMTkkNTEkOTYkMjAkNTMkMjckMTE0JDg2JDEzMSQzMSQ3NCQzMyQxMDQkNTIkNzck$MzlGNTlBM0QwRjQ4NkZVWFNzBDMEERBWOETVFOTY2RUNCQUNBQkNQDNDQPRBQUM5MjU1OEJFOTU0FGNEQ4NkERwQTU0MMTcxOTU2N0RFMGREFFMDEwMjVGMDgK2QjNCQzczMjEzMjCZCMDU2$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStaySearchAutoCompleteList(Crypto.getUrlDecoderEx(URL), date, stays, term);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayRegionList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/region"//
            : "MjMkNjQkMjEkMCQ2MCQ1MiQ0NCQzMiQzMSQyMiQ3MSQ4NiQ2OCQxMyQ0NyQ2OCQ=$PRUM3NTRGQzA5RMEVBMjZFNPQEEN0MTgzYMVzcyQ0VERDUzOOJDQyRTQ1NYzkxNkM0MBNEUG1RUTFOGMDExRDVEMEMExRTEwMDExNw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayRegionList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayPaymentInformation(String tag, int roomIndex, String date, int nights, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/detail"//
            : "NzckMjIkMTIkNzkkMjYkODEkNTAkNTEkNjUkMzUkOCQ4MyQzMSQyNSQxNCQ5NCQ=$NTZEMEQ4EQ0EyIRMzY0N0IwQzOMYxZMjICwNTQzQMEI5QTVFOEE3RDEIB1MjRFQzZBNzgxVM0ExQzFGODc0NUER4OKNFThOGMzJBNA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayPaymentInformation(Crypto.getUrlDecoderEx(URL), roomIndex, date, nights);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayDetailInformation(String tag, int index, String date, int nights, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}"//
            : "NjMkNjckODgkNTMkNTckNjMkNCQ2JDM3JDc3JDI4JDQ5JDY2JDI0JDQyJDM2JA==$NjRDWNS0ExQUY4NjNGMzM4QkXRFNDZlDM0Y3KRDIK5RIUUxOUY1RWjNBMUY5MPEI0OMjkG0NPDNIDREPJERXjNCNDcxMTlGMUQ2OQB==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayDetailInformation(Crypto.getUrlDecoderEx(URL, urlParams), date, nights);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayPayment(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/session/easy"//
            : "NjMkMjMkOTAkMTMwJDY3JDUwJDI3JDY2JDUwJDk2JDMkMjckMzAkOTIkNzAkMzQk$RkEL4QkUwQzY3RjA0MUI0M0MJyRKTFXI3OTDQ5N0RGREM4MEU2RjdBAMMTg5MDE4NEI0RTZJAQCQGTlFOTA2REU3N0RBMDYlCRTA1VYRDc2Qjg3RTY4NzE1MjM5RjQ5RkFDQTZDRDE0MzMJz$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayPayment(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestBookingList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/booking/list"//
            : "OCQ5OCQ4NCQ3NyQ4NiQxMjkkNzMkMTIxJDcxJDExOCQ5NyQ2MCQ2MyQyNSQzNSQyNSQ=$RjYxMjU1INzFDQ0ZCRTRBMzRFCLMzY0QTQ0ONTQ3QURENDMyQjM0NDczQzczOURYFNGTQ2RTNGRTPQ5XNzNDPNzNDNkQWD4NzA0M0YBzNkRFNODVFRUU4RDQ1Q0IOyRDcZ2QzkwNTlDFN0M2$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestBookingList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetBookingDetailInformation(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/fnb/{fnbReservationIdx}"//
            : "ODQkNDQkNDckODEkMTIyJDE0JDQ2JDAkMTckMjQkNzEkMzEkMTAzJDQxJDEyNiQxMTYk$NNzVFQUVFRDlBRTYkO0NzQxOWDJGNjAZ4MEI2RDBBTREZCNURGLZQzUYyQkNBQjJGMDU5RDJERQjAwMkFDODE0OTIOyNzMxTOEUwQjNEQREVDNTAyMzIJ1RUI0Q0U0OTDBDOEXY2RTIzRkQ2$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{fnbReservationIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetBookingDetailInformation(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetReceipt(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/fnb/{reservationIdx}/receipt"//
            : "MSQ5MCQxMTckNjgkNzAkNTIkNzMkNzIkNDIkNDckMjUkNSQxNiQyOCQ3MSQ5MyQ=$NL0Y0BQTM2RkRCRTSRBRURFMzQ1VYQTE5NzBFODc1MjZFQWjUyRSENERTMU3NzNDMUEzMUUVwQ0VPFWIRJDEwRDBGQUEyQQzc1QzI5ROkZFQTgwOEU0QkYwNjhFM0ExMjMKwMzE0NEVBQkVF$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetReceipt(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetHiddenBooking(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/hidden"//
            : "MTEzJDQ4JDM1JDEzJDI4JDY5JDkzJDcxJDEzMSQ5NiQxNSQxMzckMTIwJDUzJDU4JDk0JA==$N0U1RjY2MjIzQPzRkyOEVEQzQ0RkED4Mjg4RDEW5RTM3MzkwRTZGZQQTBEBMTczQzNDQUMwNFUJIxQkE1NkZGOUJGODY3QRzBFMNS0RCMkNFNDgxRERCNTZDQ0EX1NFEI3RjNBQzYE4QzOM2$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetHiddenBooking(Crypto.getUrlDecoderEx(URL), index);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetAccountInformation(String tag, String tid, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/vbank/account/info"//
            : "MjUkNzckMzkkODQkNzQkMTA4JDExMSQxMDUkODMkMyQ1MCQ0NyQxMDQkNDckNTUkMzYk$OUUSxNjYxRDI3OUQ0OEY2MTE4MLzU1RkFCNETYxNzVk2QkNDPHOTJTFQVzU3N0I0NjIyMzI3MDYxQzAzIQTI0UMDQH5MMzZENDI1RTEyODkX4N0IyXMDUX1RAUQzOUUyN0RCQUNGRDU2NTJB$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetAccountInformation(Crypto.getUrlDecoderEx(URL), tid);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetRegionList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "gourmet/region/v1/list"//
            : "NzMkNiQ1OCQ4MCQxJDI3JDMxJDg4JDI0JDQzJDYkNjQkNTAkMjAkMzQkNzgk$QZTgxRUUGQ0RkFFRDZDNAzBFQjZAyQVzAwINNTY2OTExMTZlERjAC5NzZCMThDQzNFMGBTk4NkU3QzPhCMDgwQQUNFOEUFEMJ0Y1Mg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetRegionList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetList(String tag, Map<String, Object> queryMap, List<String> categoryList, List<String> timeList, List<String> luxuryList, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/sales"//
            : "NjYkNjMkMzEkNzYkMzckODEkODUkNCQ2NyQ5NiQ2MSQxMyQ0MSQ0MCQ5MSQ2MCQ=$N0M0VNTRCQUIxYMDIzRDdEQTJBODI3QjZFCOEE4NEQVTdBMUVDOUM3QzlDOTRg1MzLBERDYEOzRTKNBQzk2QYUFBIMDAMGwRjNBQw=U=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetList(Crypto.getUrlDecoderEx(URL), queryMap, categoryList, timeList, luxuryList);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetSearchAutoCompleteList(String tag, String date, String term, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/sales/search/auto_complete"//
            : "MTI0JDE5JDQ3JDY2JDY0JDExMiQyNiQyNiQ3OSQyNiQxMDUkODIkNTckNDAkMTIyJDEk$RDDU0NTk3RTIwNEZBQTEZ4QTlBQNPSjcyRUZCRjBBTN0JGMUFBQ0UM0MUY0IQjhEM0NCMUCIzJODdERUQwNDzFZGM0U5MUE3NTE0NUI0RENGRTTlEMTZDRkJBMMMUUwNDlGMjBCNkExRNjU5$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetSearchAutoCompleteList(Crypto.getUrlDecoderEx(URL), date, term);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetDetailInformation(String tag, int index, String date, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}"//
            : "MTckNjMkNTQkNyQ2NSQ1MyQyNSQyMiQ0JDk0JDYxJDM1JDgkNjckMzckMTAxJA==$QkJCSMjBPVENkQ3RTU4MTjkyDOTQVyODZBQjSZFBNTMwMDI3MDQ5N0RDMDYGxRFDFg4NZEI4NTDXM2OUNGRUQ0QTY4N0MyNjVEMRgJ==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetDetailInformation(Crypto.getUrlDecoderEx(URL, urlParams), date);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetPaymentInformation(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/ticket/payment/info"//
            : "MzkkOTYkOTIkMTI5JDEwOSQzNyQxMjgkMTA3JDQ3JDY0JDMxJDEzMyQxNiQwJDk0JDEzOCQ=$KMzM4MzgyRkFFOTFBINUZCRkQxRjA5MjIEyRkFEOODYDwMkMwMKjk1RjkxRjNDMDM1MJTc1QjZCMjJCREFEQzk3NDdGMkUCzMDgVzNDZEGRkE1QThWDNkQM1MzNBRjEyMjQwQUUYH1DNkIQw$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetPaymentInformation(Crypto.getUrlDecoderEx(URL), index);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetCheckTicket(String tag, int index, String day, int count, String time, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/session/ticket/sell/check"//
            : "MTAyJDUzJDEyMiQ0JDI5JDExNiQ5MSQ2OCQ3OSQxMTIkODEkNjckMTA2JDExNSQxMjYkOTIk$OTAwONUU3MDE1MzkxMUI4NDJEMjRFMOTUxQTIyMUUwOUFDQ0M5NTU5QBjBEMEI4OUMzMOZDY0NjdFQjFYBUNzlEODUxNEDRCEMEMyRDFGRENJGRULI3NXSzhFODQ1NQDzMxRUI1BMzM1MzAy$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetCheckTicket(Crypto.getUrlDecoderEx(URL), index, day, count, time);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetPayment(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/payment/session/easy"//
            : "NzgkMzMkMzYkMzAkNjIkNzMkNSQzOSQxMyQ1MyQyNiQyMiQ0NiQzNSQ2OCQ4NiQ=$NjA0NKzIxMDNETRkUwMTNBNMjAzZM0E2NDQNhGNPjcME3NDKk3MkVEQkIP5QzA2NjYxRIjNADRTg4RUU0RATIzTNTc3MzCMwNjg5Qw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetPayment(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestDepositWaitDetailInformation(String tag, String tid, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/account/{tid}"//
            : "MTE5JDg1JDk2JDEzJDIyJDgkMzgkMTUkMTE1JDExMSQ2MiQ2JDE3JDEzMSQxMjYkMTQwJA==$MEQ3Q0OU5XRDlFOFKJUI5RkE0MOjI2MzdDMzFGOEIS0QTlGOUExQjREM0JCNkVGRVDVFNDVBRTZGNTFGNkRGNkZBRTFBMVDhEODdFRTVSCQkI3RjcxKRDQ3DRTQ3QTDBBNEYFJ1RkMwQKjA4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{tid}", tid);

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDepositWaitDetailInformation(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayBookingDetailInformation(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/hotel/{hotelReservationIdx}"//
            : "MTI3JDExMyQxMTckMCQ4NiQ4JDE1JDU4JDExOSQxMzAkMzUkMTkkNDUkNDEkMTIwJDk3JA==$FMjIxNzUN4MzVDRPUFECOTBDNjg0MjdGQUU0ERDBDFQkRCENEVEREVDQ0ZFRDUWzN0MwRDhDMDFGNDdDRTUxODQzMDQ3OHTdDWMkQ1NDE2REIxNjJDMkJCQTgX3ONJEQyPOEYzNDJE2QkQF4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelReservationIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayBookingDetailInformation(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayHiddenBooking(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/mine/hidden"//
            : "MSQ3NiQzJDQ5JDcwJDEzJDE2JDI0JDg4JDY3JDU2JDkyJDE4JDc4JDY3JDI3JA==$MBkWE0QTNGNjIFzRKTAE2RkE5TQBTEzNEJCNTA1QjVDNDY4NEZGRUQL1N0NNDM0RFN0UZ1RNUYzNEYM1YQ0RCMjBPFNDJGYRNjc4MA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayHiddenBooking(Crypto.getUrlDecoderEx(URL), index);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayReceipt(String tag, String index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/receipt"//
            : "NTIkMzckNDYkNDQkNjEkMzIkNzMkNDQkMjUkODQkODMkMiQ2NiQyNCQ4NyQ3MSQ=$ODPIyNDhGMzU1QTQzNzRBQjgR1QD0E2MTJBLNzRFRWDVDRkNQY0NRTEzRTk1PNzY3OTODUzZRDU1RjlTEMzUxRDCFFSGMzNDNzM0QQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayReceipt(Crypto.getUrlDecoderEx(URL), index);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestEventList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/daily/event/list"//
            : "NDgkNzMkODckNjkkNDgkNzEkNjckOTAkNTYkNSQxJDcxJDM2JDcxJDYwJDgk$QUUZFMLkXMwMTE5RDRFM0Y3NTgxQjI0QkZENjXY2RUExOEFCNzZDZHNTkxNTPWM1NjRDNzM4MFUXjZGPWMjE2LNkJCNjZFNTUwKRNg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestEventList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestEventNCouponNNoticeNewCount(String tag, String eventLatestDate, String couponLatestDate, String noticeLatestDate, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/new"//
            : "NzAkODAkNzYkMjMkMzckMyQ4MyQ2NCQyNyQ1MiQ0JDU3JDcxJDI1JDMyJDYyJA==$RURSICQTU3RkZGMEUwNUZCOEEPP1RFDQY4MTlERUQyXMDI2QzgwNEQ1FMzIPwQGzYzNjFGRLDdUGQTdDM0KU3NjUB5MzEVEDOERGMw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestEventNCouponNNoticeNewCount(Crypto.getUrlDecoderEx(URL), eventLatestDate, couponLatestDate, noticeLatestDate);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestEventPageUrl(String tag, int eventIndex, String store, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/daily/event/page"//
            : "MTckNTUkNTUkMTYkMzEkNzQkNjQkNjIkNjMkODckNzkkNjkkMzckNjQkNjYkNDAk$N0I2N0FBQ0QwQ0U1KQLzI5NDk0REE5NRzM1N0LM4FRkNFQUI5MzQ5NkYyMUGQMyNUPYADBSCRKDU0NzZBMKDDM2QTZDM0UQwMTdDNQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestEventPageUrl(Crypto.getUrlDecoderEx(URL), eventIndex, store);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestEventBannerList(String tag, String place, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "event/v1/banner"//
            : "MTckMyQzNiQzNiQzNyQyNiQyMiQxNSQ0JDckNiQxOSQxMCQzMiQ1MyQxOCQ=$NkZQXFOMGzNIwQjNCQVTLSUzODTNGWNzUK2BNkEzRkRCRDRUMVFRUQF4NzY=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestEventBannerList(Crypto.getUrlDecoderEx(URL), place);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestDailyUserVerification(String tag, String phone, boolean force, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/myself/phones/verification/start"//
            : "MTMkNDckODYkNTYkNTQkMTI4JDk0JDU4JDEwMiQxMTQkMTA4JDY4JDE5JDQ4JDcxJDU1JA==$NDkxNUNFQjlENREUwNEERBMDJCMjJGRjdDODI3MDUyRkZFRTUCdEOTMAxHNzTZNEQzg4REQRS2RTY4QTU0M0RDMzI4QkJEINzgyOLTdDQzUX4QzJDRODk4NTXg3RkQyQjY2MEY3QzNYCNDhG$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDailyUserVerification(Crypto.getUrlDecoderEx(URL), phone, force);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestDailyUserUpdatePhoneNumber(String tag, String phone, String code, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/myself/phones/verification/check"//
            : "OTYkNTMkMTE0JDEwMCQyMyQyOCQ3MSQ5OCQ4NSQzNyQ2MyQxMzIkODYkMTAkNTQkODMk$RkMyQkVGODTJEN0NENzAzQUIAyNDkA5NTM4ODUT2REU4NjY1REU0MjTI0RGjM5RjRDERTJFQzcwMRTQxMDIZ4MjU2ZNUjE3MzdFRUJFNDWdFGRDIMxRThERTg4QTAyPRkY5NDVFMCjVEQUU5$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDailyUserUpdatePhoneNumber(Crypto.getUrlDecoderEx(URL), phone, code);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestSignupValidation(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/normal/validation"//
            : "MTA5JDYwJDU4JDk5JDgwJDU2JDk1JDI3JDg3JDU4JDckNTYkNTckNCQ1NSQ2MyQ=$QzlFLMDUI4MzVCQ0NDQTZGQkI1QzMGwMEZGMjA0NUQzRjhBMDJBMzY5IMjANVERQYMjPFCCNEE3MzMxNDgwRUJGODBVCRDWBGNDRGMjYZyRjIxRUkJCRDk1QkFENIjQ1REM3MTA3MzgwQjkz$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestSignupValidation(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestDailyUserSignupVerfication(String tag, String signupKey, String phone, boolean force, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/normal/phones/verification/start"//
            : "MTU4JDc5JDE1NCQ0MCQ1MiQxMDAkMTQzJDQ5JDkyJDE1NSQxNjUkMTM4JDE0MyQ4MSQ3MSQ5OCQ=$ODJDQkRDNDFGMjZDMkEzQUMzNTNFQzJGREY1NzA2JNkQ3RjcwZNDkC1MDAwNTNCNUE5QjAwLQjI4MDZBQzGdMDQUM0OTdBAQzIX4RkQ1RVTg2OEFEMTM1QTIxMkU2RUU5RTQxNzgwOEVDKNDFEIRjBQDMDEzQTE5NQjIyQWjBFVNUPFCODI5MDQxNEI=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDailyUserSignupVerification(Crypto.getUrlDecoderEx(URL), signupKey, phone, force);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestDailyUserSignup(String tag, String signupKey, String code, String phone, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/normal/phones/verification/check"//
            : "MjEkMCQxNyQ1NiQxMTAkMzIkNyQxMzEkMTQ1JDQ1JDE0MiQ4MCQ0NSQ0OCQxNzMkMTc0JA==$BQzZCQ0IJCQzg1RTQ3QNEFBMXTA1QTQ2QGkNDMzlFQjQyTHMNThGMEYyMDc3MI0UwMDlCNTc0NDFEMUNBMTkI1QTBFNTgxOUUxOTI2ODY0Q0VFQTRDQjRU4NzFCNkQyMENENEMxAMzE4ODVERYEYxOSDhBNjNBMEYwODFEMzNDRUULSwOEEzNjk5MzA=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDailyUserSignup(Crypto.getUrlDecoderEx(URL), signupKey, code, phone);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestFacebookUserSignup(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/facebook" : "ODAkNDQkNTckNDgkNTAkODUkMjQkMTIkNDMkMTUkNDckNyQzOSQ1OCQyMCQ1MSQ=$NTAyNUFCDRDEwQRDCc2MWzE1MjQzUNzZDMTI0MTkKwQjFCNHzBRSDIM0MPzZKMjA5RDU1EQTYyMUI0Q0Y3MUVFRDNCOUNERNODIzNQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserSignup(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestKakaoUserSignup(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/kakao_talk"//
            : "MzQkMTIkNjUkNDQkMjAkODMkNzckMjQkNzUkNjQkMzckNyQzMSQ4OCQ0OCQ5OSQ=$RTdDQ0ZRCMzg1ENTZBQUJRDNjPVGQzUFyQUE1RjXSg2ODI4RITMc2OTU1ODg4RjhGNjYS3RUUHzREU1MYDcxYMDNBBNzFNFMTk4JMA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserSignup(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestDailyUserLogin(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/normal"//
            : "MCQ3NSQyJDgxJDg1JDYkNzckMjIkNTkkODAkNzkkNzkkNzkkODUkNTIkMzgk$GMI0Q2DOEZDQTM2QzBBQTlNDM0JCQTk4MzgwOEMYyQUREOUFCMzEzSOTcxMEET0Q0RFMDdFMDQ1NTk4ODRAAYTJClEMjXU4QCjIwNw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserLogin(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestFacebookUserLogin(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/facebook"//
            : "NDQkNzckMjgkMzIkNjIkNTIkNTkkNDckNzkkNzckNTMkNzMkNTckODckNDkkMzAk$MTk3OTE5MkU4MDE5NjQyNTY1RUQ3TRLjVVBRkNDNkM0NDdCLAMTTFBQKRzhTENzZNEM0UEwNEQyNVjAzOTHAzSODNJFNMzRFNDg4MQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserLogin(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestKakaoUserLogin(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/kakao_talk" : "MzkkMjUkNDkkODAkNjQkNjckNTIkNTkkNzQkMiQ3NCQ0MyQzNyQyNCQzNiQyJA==$NkTCNGODU5RkI4Q0RCNzA1ODVYFMOTE4N0JEMC0MJ3NjBECGQkI2QTIMyRGTcxMDZZERDU2OJUYF0Q0WMKyN0E2NDcxNRzU5RTM4MQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserLogin(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    /**
     * /api/v3/users/coupons
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    @Override
    public void requestCouponList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons"//
            : "NTUkNzUkNDQkMTIkMCQxNyQ4MiQ4NCQzNyQ0MCQ0OCQxNyQyNSQzMSQ2JDEwMiQ=$XMzE5MMTc3MTVEJQjBUSCQkM1QLUQ5NkWY5MUYzQUEQwWRDA5N0UA0MQUU0RDNDM0RXFRTE3RUJBRkY1RUM3NTUZGCNKzk5QTRDRA=B=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    /**
     * /api/v3/users/coupons
     * 결제화면에서 사용되는 자신이 소유한 Coupon List
     *
     * @param tag
     * @param hotelIdx
     * @param roomIdx
     * @param checkIn  ISO-8601
     * @param checkOut ISO-8601
     * @param listener
     */
    @Override
    public void requestCouponList(String tag, int hotelIdx, int roomIdx, String checkIn, String checkOut, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/coupons"//
            : "MjkkMTckODAkNTkkNTEkMzAkMyQ0MiQ2NyQzMiQ0OCQ2NCQ1NCQ3OSQxJDUxJA==$MBDgCxMDk2MDFENTI1QUkFGRkFDREY5OFCNDhGQUQ5NzFM4NTOUC1NjAQzMMjZCRDU2JQKjNCZOTYyOTQT4NDQwRjY5RDPM2N0ZCQQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponList(Crypto.getUrlDecoderEx(URL), hotelIdx, roomIdx, checkIn, checkOut);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestCouponList(String tag, int ticketIdx, int countOfTicket, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/payment/coupons"//
            : "MTAkNDEkNTgkNDckODIkODIkNzMkODMkMSQzJDcxJDY1JDUyJDMxJDM1JDkyJA==$OWDEMzRDNFMTXU4MjM2Q0E5OTYxREU0AMUZIFMEQxNzdCNRkEzOGEUH5NTQzNUE2VNjIMzQzRBMP0QwQCjdFREVGNzPQKMgyNjY0MQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponList(Crypto.getUrlDecoderEx(URL), ticketIdx, countOfTicket);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestCouponHistoryList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/history"//
            : "NTgkMjgkMzMkNTYkNzEkNTYkNDQkODYkMzAkNTAkMjEkMTkkOSQzMCQyOSQyOCQ=$ODlCNTQ1OOTA3NjczNkJVEQCjRBQSjNFOEXOXDMyPMzM5ODE4RTOBEMEZFGRjA1RTFZg5MjXk2QTVGNUJCDMzMzMzI4ODJJCNzY0Mg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponHistoryList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestNoticeAgreement(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/agreement/confirm"//
            : "NTYkNjgkMTkkNyQ4JDI4JDUyJDE4JDc5JDg1JDY0JDg0JDY5JDc5JDYxJDk0JA==$ODdCM0QIL5RDY2RTRFNQjlFGRDhBRATk5ODlCMkEyQjVBQjYxRjc2YQkRENzUEzTOVUExMAjZENzkD1NVDEE3RTNFBHQjNRCOUYyNA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestNoticeAgreement(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestNoticeAgreementResult(String tag, boolean isAgree, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/agreement/result"//
            : "NjgkOSQ4NSQ3NiQ3MCQ0NyQ1MCQzMiQ5NCQxNiQ5JDE3JDQxJDQ4JDk1JDUk$QTcxNVjkwRTOUEyOURMYBQTcyQzlBOTBCOUIS1OTg2VQ0JBREUI5NDTEwYRDA5M0RCNzAxMzM0ODIzXVNzc5NzRM3NjUxNEUODxMQQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestNoticeAgreementResult(Crypto.getUrlDecoderEx(URL), isAgree);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestBenefitMessage(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/benefit"//
            : "MTUkODEkMjIkMzMkMSQ3MiQ0MCQ1MSQ2OCQ3NSQ1NCQzNiQzNiQyOCQ3MCQxMDIk$MMTREQkE5QTVFNEQAwMTdERLTE3RMEU1MkJXBLWQjY3HNDc1OTExQ0LI1HRjI3RERBRjU3YNURU0N0VCTNQkY4Q0Q0QjBDBNENDNQ=T=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestBenefitMessage(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestDownloadCoupon(String tag, String userCouponCode, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/download"//
            : "MzMkNTYkMTgkODUkNTMkMzUkOTAkNTQkNyQyMyQxNiQyMCQ4MSQ2MiQ3NCQxMyQ=$QUE2NzVCFMUU5RNEFKCMjSQE3MOjUyRkFBOTVDMGCzlFN0M3QzkzM0VGMTAQO3QVzBMFMTJGMDhFDN0MxMUMHzNDc5RDY4NDLU5YNA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDownloadCoupon(Crypto.getUrlDecoderEx(URL), userCouponCode);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestDownloadEventCoupon(String tag, String couponCode, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/download"//
            : "NTckMTUkMjckNzEkNDQkNyQxNCQ1JDE3JDE3JDcwJDk1JDM1JDU1JDckNDEk$NUY0MNjKEOwMDIwQZTXWlOCNjRBQUE2ODFc1SNUMwXNzY4MTA4OESIxMUQEwRUQ4OEEwQGTdGRQjMzQUFFUN0M5OEI5OTczNDVGZRg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDownloadEventCoupon(Crypto.getUrlDecoderEx(URL), couponCode);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestHasCoupon(String tag, int placeIndex, String date, int nights, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}/coupons/exist"//
            : "MzckNjgkNzAkNzgkMTE1JDEyNiQyMiQxMiQ5NiQ1NSQxMDAkNTckMzEkNTUkMTA4JDE4JA==$ODk3NDIwMTAwHODNFNWDk4QTPQ1MkQ0MKTVDNjYyQVkM3OTk2MzVCRjIVwAOOENGQzg1MTI1OTEA5YNzE1NzdVEREVDRkJERUVFODMg0DNDcwTQTNBNzY1RkY0MjRKFM0ExNkVENWkI5Mjkx$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(placeIndex));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestHasCoupon(Crypto.getUrlDecoderEx(URL, urlParams), date, nights);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestHasCoupon(String tag, int placeIndex, String date, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}/coupons/exist"//
            : "NzEkNTUkMTAxJDc4JDExNyQ1MSQxOSQzNyQ0NiQyJDEzMCQxMDgkMzEkMTIwJDc5JDE5JA==$MTMExNTdFQkU1QjUxNUUQB0MzQ3QjkxRVTJEOEQ0TNzAzOUFDCNTg5NTlBBNUJZCODY0RjY0NkM1MTQNSwNzMxUNDI3MDU2MjE1NDcwRERCNDYTCxMkNBQzM3RCDY4OKDU4QjUxVNDQ2MkIz$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(placeIndex));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestHasCoupon(Crypto.getUrlDecoderEx(URL, urlParams), date);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestCouponList(String tag, int placeIndex, String date, int nights, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}/coupons"//
            : "NDAkMzIkMTkkMjMkMTAkNzgkODckNiQyOCQxNyQ4NyQyMyQwJDExJDE2JDc4JA==$YMEQxMDTIyOITBEyFQ0MMwOEICMwOTKdETNTgwMzE3GMkRGNEYwKQURBNDg1NzAzOTlFRDFDNEFDQzXhBREI2OLDNCRUAIxQWTI2Mg==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(placeIndex));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponList(Crypto.getUrlDecoderEx(URL, urlParams), date, nights);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestCouponList(String tag, int placeIndex, String date, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}/coupons"//
            : "NTkkNTQkMTA0JDkxJDExNCQ4JDEyMiQxNiQxMjUkMTIzJDI0JDExJDEwNCQxMTMkMTMyJDEwMSQ=$MzI0MTk1POUBM3OEUE1NjQ5RTFAwMDJBOTNGNjVDOEVCOUYxNzU3OEQxNzLkwNTEDwMzE1MTVCRTZFMkY3RDAyM0RBM0JCNHTE2ODXRCRPTkyM0WU4QMkU0Q0UFEMjZGPJQCzFA1RTE5Q0Q4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(placeIndex));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponList(Crypto.getUrlDecoderEx(URL, urlParams), date);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestRegisterKeywordCoupon(String tag, String keyword, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/keyword"//
            : "NzUkMzEkMjAkOCQyJDAkNDkkNjEkMiQxOSQ5MCQ2MiQxMiQxNiQzMCQxNSQ=$NNTzJk1NTVDRKNDBdUGMzQX1RkMxFNjTU4OUY2NUEXzRUVCRTk1REUyUOEU0REY0QjHNLFRUIzQzc3RTlCMTRBQUMT0QzNFNNTNGNg==$\n";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestRegisterKeywordCoupon(Crypto.getUrlDecoderEx(URL), keyword);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUpdateBenefitAgreement(String tag, boolean isAgree, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/benefit"//
            : "ODckNDIkMzUkNzYkMzAkNjEkOTAkNTgkNTIkODEkNDckOSQ2MiQzMiQ3NyQ2OSQ=$NjdCRTNCQBTczOUY4RTJGMzY5RDA2NEWTRCMTkX5NDA3NEOE1HRDNBNVjc3NzMAA1NkAQRyNTgwNjBWDNEY3REIXM1NkRCREQ4MVQ=K=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUpdateBenefitAgreement(Crypto.getUrlDecoderEx(URL), isAgree);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestUserTracking(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/tracking"//
            : "MzkkMzEkNTIkNjUkNDckMzUkOTAkMTIkODEkNDEkNDEkNDckOTYkMTckNjEkMTAk$MjAxNkUyMTYk5QRDMzXQjk4RkYwOTRCMzMYwRkLRGMjHKlWBQPTdDMXDkxQkTNQBNzAzMDEyMjQgwMjg0M0VCMUNU2Qzk3OTNCOWQw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserTracking(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestNoticeList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/notices"//
            : "MjAkMTYkMzYkNzEkMzYkMzUkNTckMTkkMTkkNjAkNDAkOCQyNSQxMyQ0NiQ0NCQ=$RTg4MDE1MRDMxROTY5RNkLOFBECMjQ4NzEzQjczNCzSPHEYL3QzhBNUJDODhGM0ZGZINEFCMTdCQUNEQUNGGM0YzODA2MjRFRDBFQw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestNoticeList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestReceiptByEmail(String tag, String placeType, String reservationIdx, String email, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/reservations/{kind}/{reservationIdx}/receipts" : "ODQkOTgkMTY5JDckNTYkNjkkNTkkOTUkNjQkMTczJDEyOCQ3NiQ0NCQ0NCQ3OSQxMTEk$OUIxOEUQwMUJFREVGQkMwOENEOEE5MkY5REY2NjE0QTcFC2REU0RDk2MzEVyOZURCMXDQ1Q0UOzRUEPYzOENFMzZGREMwNQUEwQUHFDNkVDQRzIQxRjFBNzJGQUQwQTJCMDg5NOEU2NTc1QzgyRkE1QkFCQ0E0NzQ0ODlBRTYzN0ZGMjhFMYzFMDMDQ=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{kind}", placeType);
        urlParams.put("{reservationIdx}", reservationIdx);

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestReceiptByEmail(Crypto.getUrlDecoderEx(URL, urlParams), email);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestWishListCount(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes"//
            : "MzgkMTUkMzIkMTgkNDAkNDEkMzMkMjkkNDMkMTQkMjIkMTkkNDUkNTAkMjgkMzIk$NTBFODQwNDgzNTPAWzNFF0IWxQUIL2OEMER4NzBCWCMjMyNIALIkOEVCNDU=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestWishListCount(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestWishList(String tag, String placeType, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes/{type}"//
            : "NiQzMSQ5JDM4JDkxJDQzJDg5JDc1JDQ3JDc1JDEkNTUkNjAkNTUkNDMkMjYk$MJjgzRDYY1IMzVDNTNERDQ3QzYA5RTEzQ0DI0NURYDM0RQRyRkSU4NzUxGWMDQzRMTU0OTYwRkVBRTQyQDTWUzODM3OURGNUYwIQg=F=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", placeType);

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestWishList(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestAddWishList(String tag, String placeType, int placeIndex, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes/{type}/add/{itemIdx}"//
            : "MTIkMTI3JDM5JDk3JDExOSQzOSQ3MSQxMDAkMTI3JDc4JDk1JDU3JDEwMCQxMzEkOTMkMTA5JA==$MjgzRDY1MzVDYNTNERDQ3QzY5RTEzQ0I0NURDM0RIQyRkVFRTJEN0I0MjWA5MkRCNDcxMUU1IRDNGMUDZDRUQyOTMxMTUR1RDJdFMRUNYWEQjAA3OURCMTgxMjVFNjYzFOTFGGSNkMzMUDI1$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", placeType);
        urlParams.put("{itemIdx}", Integer.toString(placeIndex));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestAddWishList(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestRemoveWishList(String tag, String placeType, int placeIndex, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes/{type}/remove/{itemIdx}" : "MjEkMzgkNzQkMTE4JDYwJDQyJDQ2JDExMyQxMDEkMzQkMTM1JDExNSQxMzMkOTgkNDAkODgk$ODFCMzNDMDREN0VCNUFDOXEVEMjA3RjAzMWDc4RJMjhGFQkRVBN0M5MTA4M0REMjCNDMjkyMDVEMkEzUQkYxMjcwQOEM2M0Q5MDdGBOEMO2MUU2QTM1RTFWDFMjZENjAM1NTg2NzLAyNVkVD$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", placeType);
        urlParams.put("{itemIdx}", Integer.toString(placeIndex));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestRemoveWishList(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestPolicyRefund(String tag, int placeIndex, int ticketIndex, String dateCheckIn, String dateCheckOut, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/policy_refund"//
            : "NjYkMzYkMzIkNTgkMjEkMjQkNDEkODgkNTQkNTgkNzckNDEkNTAkMzUkNzUkMzEk$RjFBOTM0MjJFODlCNkJFRTTlVCRTIxQBTE3RYMUZCCOGCUJDQTYwQNTdEMPkE4JRDUyRNkYyOTU1ZNUYYxNMjM2RTlBMDQxNOTI0Qg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestPolicyRefund(Crypto.getUrlDecoderEx(URL), placeIndex, ticketIndex, dateCheckIn, dateCheckOut);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestPolicyRefund(String tag, int hotelReservationIdx, String transactionType, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/hotel/policy_refund"//
            : "MTAkMTEzJDkxJDkxJDYwJDckNiQ3MCQxJDcyJDIxJDQ4JDEyNyQxMDYkMzIkNDEk$OMEZGRELUMyRjLlGMTBCNAkUzNTJFRTQTyRUYxMDAT2ODQyOUMZ2QTIzNDkxN0NDRTBPGMzNCOUCSNCOEUxNTRCMUE2RDE3N0VGODXQBGMEIS5OTBGNTM0NUJEM0U1GMTQV0RTc2Njk4RjM3$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestPolicyRefund(Crypto.getUrlDecoderEx(URL), hotelReservationIdx, transactionType);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestRefund(String tag, int hotelIdx, String dateCheckIn, String transactionType, int hotelReservationIdx//
        , String reasonCancel, String accountHolder, String bankAccount, String bankCode, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/refund"//
            : "MTQkNzUkNDMkODUkMzkkNzMkMTQkNjIkMzMkMTYkODIkODkkNzAkMzIkMjkkNDMk$Nzg0NTMxOUNGOUOMFE1RDE2NjRFRTRM1OOThXBOUQ2RNDIhBQ0KEyRjIxMjcwNTMyOTANFQ0ME3RDNBNzFE3MzHRQzNTIC0NjEQ3OA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestRefund(Crypto.getUrlDecoderEx(URL), hotelIdx, dateCheckIn, transactionType//
            , hotelReservationIdx, reasonCancel, accountHolder, bankAccount, bankCode);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestBankList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/bank"//
            : "NjEkMTIkNzkkNDMkMTgkNjMkMzkkMzgkNDgkMjgkMzMkNjEkNzckNjAkNjEkMjQk$RDlCRUJDNUY0ROUM5MODA1MjDY5REHQzNTAE4RTM2CNWEZGRkUYR4NzQ4MUQxMBRKDY2RDkwQNG0RFNETE2MkU0OTNENFTY3Nzc0Mg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestBankList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestStayReviewInformation(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/hotel/question"//
            : "NzQkMTIkNDkkMiQ0OSQ4NyQ2NCQ4OSQzOCQyNSQxNyQ3NiQzMSQzNCQ0OSQxNyQ=$MUZVFMEQ4OUMxSNTUTPwNUIxQzVVDNUJXFRZTVCQjQyFMUNCODXg4RDZTBQMENBMTc0RTcxTMjUyM0Q1KRDFFMDXBGMTU2MEKNNEMg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestReviewInformation(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestGourmetReviewInformation(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/gourmet/question"//
            : "NDUkMTIkNjUkODckNDAkNzQkNzUkNzckNjckNTYkNDMkMTYkNTQkOTkkODckMTYk$QzgxQkVEQjBGONDdTWEMDI4NENBOEMwNkIyQUIxQUQOwNITlGNBTczMIjBGMSTM1QTdCNDNOUCMTg4ODRHZAFMjRIDQzE3REI3ENQI==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestReviewInformation(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    /**
     * 예약내역 상세 - 리뷰 정보
     *
     * @param tag
     * @param reserveIdx
     * @param listener
     */
    @Override
    public void requestStayReviewInformation(String tag, int reserveIdx, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/hotel/{reserveIdx}/question"//
            : "MTE4JDExNiQ5NCQ3OSQ5NiQ3NiQ5MyQ3NCQyMSQ2OCQxMDkkODgkNzgkMzkkNzgkMTMyJA==$NzcwQkMxNEVBQTZFODNGNPDJCQkExRkZFNDVERDHhCNzA5MDlDRTZDRUE4NzUwQzI1QTBWGRTJEREZQIEzXNTQOyN0VCFRjhDNUYQ2Q0XUM1MUFDQY0E2MzE1N0NFNjJBSRjNFNFMzhGMjYw$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(reserveIdx));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestReviewInformation(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    /**
     * 예약내역 상세 - 리뷰 정보
     *
     * @param tag
     * @param reserveIdx
     * @param listener
     */
    @Override
    public void requestGourmetReviewInformation(String tag, int reserveIdx, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/gourmet/{reserveIdx}/question"//
            : "ODckMTEyJDEwOCQ3NCQ5NCQxMzEkMTE5JDg1JDIxJDMzJDEyOSQyMCQxMTQkMTIzJDUyJDc3JA==$MDVEQjk0RDhFNEU2NjI2HMJEI5RjlGRUNFVQ0JGNzI5QkNERjRCMITlBMUJEQzE1QUJDNzQ1NUQxMUTRQ1MjFEQUFBROTcI0NkU4TMEVDNzdDQjE0MzQWCyRTgQ1NJkLQ2QzZDGMTUyQ0QRG$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(reserveIdx));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestReviewInformation(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestAddReviewInformation(String tag, JSONObject jsonObject, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/add"//
            : "NiQ0OSQ2MCQ3MCQ1MiQ0MCQ1NiQzJDE5JDQyJDIyJDc1JDc1JDQyJDUkMTYk$M0NXBMMDSY5ODkwNI0E2NBzVACQzczQTVFN0JBNzNGRDGIVO3RTNGRjVBSQ0AQzJRjEyNEZEzNkIyOEVTNCEN0JFQjUyN0I4NzcwQw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestAddReviewInformation(Crypto.getUrlDecoderEx(URL), jsonObject);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    @Override
    public void requestAddReviewDetailInformation(String tag, JSONObject jsonObject, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/add/detail"//
            : "NDUkMzgkMTYkMzckOTAkMjQkNjckNjAkNjUkNjkkODkkODIkODYkNSQ3OCQzMCQ=$RjU4ME0IyMUIyMkJGSNTVCMzMF3MzBSCM0UxMTNFEODLQzNUE4MDUFERjI5NTEB3M0ISyMDOCc3NjE4FMkVDMYzg3NMjc5KQjE3NwR==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestAddReviewDetailInformation(Crypto.getUrlDecoderEx(URL), jsonObject);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }
}
