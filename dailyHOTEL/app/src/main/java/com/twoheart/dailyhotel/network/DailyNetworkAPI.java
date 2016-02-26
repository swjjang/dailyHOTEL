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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class DailyNetworkAPI implements IDailyNetwork
{
    // DailyHOTEL Reservation Controller WebAPI URL
    // api/hotel/v1/payment/session/common
    public static final String URL_WEBAPI_HOTEL_V1_PAYMENT_SESSION_COMMON = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/session/common" : "MjAkNiQ0NiQ0NSQ0MyQ=$RTQ2MEIMxRTFGRjUyMUIxXMzVDRERBQzBBOThFNkE5NYUJWBINzJCRjMxMTM2MUYwNEEzMkU0OURBNzVBRTg4NDU0NkYzRkI2REJFQThERjAxNUJBNTUxNURBOENGQ0IyQjVG$";

    // api/fnb/payment/session/common
    public static final String URL_WEBAPI_FNB_PAYMENT_SESSION_COMMON = Constants.UNENCRYPTED_URL ? "api/fnb/payment/session/common" : "NzIkMTAkMzMkNDEkNTYk$NjlERDU2NDLQyQUZEQzczMEU0NUNDNjBCIRDdCNzYD0OTE5QkMwQTUxQUTVGNEE1RjE1REI2RUJBANzk2QjVFNDM1Ng==$";

    // Register Credit Card URL
    public static final String URL_REGISTER_CREDIT_CARD = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/register" : "MTA4JDgyJDY3JDM1JDgk$NjE5NTkxFODMxQTRCM0RFNzIzNjRCQjc2RThJGQzQxRDRCQkNEQjk5N0U4ODhBMUM5MUYU3RTlGMzY3ODA3NEVUzREQyQjM2MzEwN0VFNzA5ODQ2GMTgwNTVFODA5NzE2MzRE$";

    // DailyHOTEL Site Controller WebAPI URL
    // A/B Test
    // api/abtest/testcase
    public static final String URL_WEBAPI_ABTEST_TESTCASE = Constants.UNENCRYPTED_URL ? "api/abtest/testcase" : "NTYkMzgkOSQ3NyQ4MSQ=$QTc1QzU3QP0VBMkUyQ0RDMjA4RUZFQUEwRjBCOEOY1MkYwNzg4OEI4MEZDBMzAwRjExRkM4N0VBRUFRGMHDYxMkM3QQ==$";

    // api/abtest/kakao/consult/feedback
    public static final String URL_WEBAPI_ABTEST_KAKAO_CONSULT_FEEDBACK = Constants.UNENCRYPTED_URL ? "api/abtest/kakao/consult/feedback" : "NTEkMjgkMTEwJDQ3JDQ0JA==$QTUxRjgwNzIyNDY1MjQ2ODJGMTdDIMUU4QTRCOTc3QTEP3MDTc5OTMG4RTc1M0NGRUIzNkNBOUJBQUJCOTg4OTU5MjBCNzg4MEZFODk5M0VFRTgxZODMyMDU3NjlGQUYxMzkw$";


    private static DailyNetworkAPI mInstance;
    private RequestQueue mQueue;

    public static synchronized DailyNetworkAPI getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new DailyNetworkAPI();
        }

        return mInstance;
    }

    private DailyNetworkAPI()
    {
        mQueue = VolleyHttpClient.getRequestQueue();
    }

    public void cancelAll()
    {
        mQueue.cancelAll(new RequestQueue.RequestFilter()
        {
            @Override
            public boolean apply(Request<?> request)
            {
                return true;
            }
        });
    }

    public void cancelAll(final Object tag)
    {
        mQueue.cancelAll(new RequestQueue.RequestFilter()
        {
            @Override
            public boolean apply(Request<?> request)
            {
                Request<?> cancelRequest = (Request<?>) request;

                if (cancelRequest != null && cancelRequest.getTag() != null && cancelRequest.getTag().equals(tag))
                {
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void requestCheckServer(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "http://status.dailyhotel.kr/status/health/check" : "NDYkMTE0JDU3JDQ5JDYxJA==$NkQ5QUVEMTQ3RjRBNjBGMURGNUMwQUE0RkE0QzkyRkQxNzSQxYRDM4MjM2UM0VMzNUUxMDc0QzVDQzVCRjQyQjBFQ0U1RkM2RTYyODMyMTEwODhEQzc2ODDdEQzI0OUJEMjkz$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.GET, new StringBuilder(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestCommonVer(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "common/ver_dual" : "MjIkMzckNDMkMTgkMTYk$QjlDRjI3N0NBNUM1UNjMZBOTNBFMTZGNUY0RTdEXNTY1RUjA=$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestCommonReview(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/common/code/review" : "MzkkODAkNDQkNjUkNzYk$RTM5MjQ0MjFEMjczNEY1RDA1OEJFMTJGRDUwQUQPzQUYOwMzJCNEMyRjFDQkFERDcQzMEQyMkQ3ONUFGODUZ2QUEyQw==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestCommonDatetime(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/common/datetime" : "MTQkNzMkNzUkMSQzNyQ=$OCEY4MTlBNEY1NjHZFQ0FGNkFFQkNEOThEQkRODNTRFMTc1MzA5NTk5OTE4ODM2ODEzREEzREEzNRNjZDNTU1QTlBNw==$";
        Map<String, String> params = new HashMap<String, String>();
        params.put("timeZone", "Asia/Seoul");

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserLogout(Object tag, DailyHotelStringResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/logout/mobile" : "MjgkNjIkMiQ4MSQzMyQ=$NjIU3RDBBQUEyRjIxMTZEQkFCQjY2NRkZCBRTZCM0RDOTM2M0EwNDhBNURBOTg1MQjYwODAzNkM4NjYwM0CRGRENEOQ==$";

        mQueue.add(new DailyHotelStringRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestUserInformation(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/session/myinfo" : "MjQkNjgkNjUkOTAkNCQ=$RDUxBMkY5MUI3MTU5OTY1RUUyGRDE1QjgzQjI0OEY0REY5Q0JFNzgxRDBFQjdEMURGCQ0MNzN0U0RjRGM0RFQjVCMA=D=$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestUserBonus(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/session/bonus/all" : "NjEkODIkNTQkNjYkNjQk$NkE0NzIwMzJGNUIxNEM1MTYzODAxNkFCMkEzMkY1RDMzQzRFMjNBQTDk0RDNGMCTIIxHNDA2MUI0QkNEMDYxOUDJBRQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestUserAlive(Object tag, DailyHotelStringResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/alive" : "MzAkMTQkMzckNDMkMTck$QzNENDQ1NTk0MzEg1GOURFOUYxQkM4MEIZFRjVBEQTU3BQjE=$";

        mQueue.add(new DailyHotelStringRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestUserInformationUpdate(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/update" : "MjQkMjkkMzAkMiQ4JA==$QTCNBRjRIFMUZGRUQwMEQzRDM2ZMjM2FCRTIzRjZDREZCRDA=$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserCheckEmail(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/check/email_auth" : "NCQ4MCQ0MyQyNiQ4OCQ=$MzY0XM0YwNTgwNzYwMjJBOEQyMQEIxMDM3MDQ1RjBGMjUZGQzJERjU5OTAwQzQ2OEM2REJGMzc3RTI3REIUwNEYzGMQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserChangePassword(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/change_pw" : "MjMkMzAkMyQxNCQzMiQ=$QzcB0MkU1RjExOATlDMjU5OTBKCMkU4NIK0IwMUVCNTZENjI=$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserRegisterNotification(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "notification/v1/register" : "MTkkNTckMiQ0OSQxMCQ=$MTBEyNUM1QNUY2OEFDOUZYBRTA2Mjg4NDc1RjkwRDMwQ0Q5MUEAyRjU2NTcwTMkZEMjgxMTc1NDlBMEY0MTlDNzI5QQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserUpdateNotification(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "notification/v1/update" : "NjUkOCQxMSQ2NCQzNSQ=$NUI4QTUzARTZk2Q0NBQjJCM0I4MEY4MzE5OLTJGQkMzRUI1MUI0RUFFNzE3NkI5MkQU2RXTBGQjM5NTIyMDE2ODIxQg==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserInformationEx(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/information/omission" : "NjkkMTgkNTQkNDUkNTUk$RDhERTZBNkRGMUJENTXVFQTdFQTc5NzFCMTZBQjFFMTMyXNkRGMkUyNWJzRBRjI4NkVBNUMzMKkQ2NDEwOTAyM0ZFNw==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/signin" : "MzIkMzQkMTYkMzAkNDEk$RkNGRTZDQzdGNjI3UNjlCQzExQzY5MNkEyFRGDUyMQDdGMUE=$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserSignup(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/signup" : "NCQzJDM3JDUkMzEk$MDBLCFWQUI1OTE5QjY2MzE3MDJGMkYzRM0U0ODII2MkZDN0M=$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserInformationForPayment(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/information" : "MzIkNTEkNTkkMzEkMTkk$NkQ5N0RBQTA3NzQ5MUZZEMzQ3QUE0OTIRwEMDI2NjhCMDJBNTdFNERZDNzdBMKjlGNDkxMDYyRURBRTQ3QjBFNTRERg==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestUserUpdateInformationForSocial(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/update/fb_user" : "NzIkMjgkMTkkNjYkMiQ=$NTJEyNTA1MEM4Qzk1NTJYBQ0E0NkREQRUE2RTAwNURCNTY3REJBQzA2MjI4QzIyQ0U0VRUM1M0Y5JN0ZGNTJDMzNCNQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestUserBillingCardList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/info" : "NjIkNjkkNTAkMzgkMCQ=$XMUVCNTA1RUJERjVGQkE5NTM0QUM2RkI1MTAxOEQVFQjk3N0UzNTIIzQkRFNTg5RTQUyRDhCNMURCQjBENTIzQUU0N0RGRTgwMEYxREMwOUJCOTM2RDczNEY5OEE0NEU3RTdG$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestUserDeleteBillingCard(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/del" : "NyQyNyQxMTEkODIkMTEzJA==$MDQ2RUMO3QzJEMTNBQjI4MDAzRDLczQzM1REY1NjIxNTgzODZCMjYwNzc1RjA3NEEyRDI2REM2QjkxNDZBMMzZBMDBCNzVGNTk2MzM0Mjg3MDg0QEHTlBQzEwRDg1QjlDNjc5$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestHotelList(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "hotel/sale/v2/list" : "MTQkNDUkODQkMzUkMjIk$NDA3MDMzRUNFQkYQ5NTA3MUjg5MDEzNjU1QkVQ1NTlGNTZEVMDgyREY2NkQ4Njc4Njk4NThBMTQ5RkUzRDFDQTJA4MQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestHotelRegionList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "hotel/region/v2/list" : "NzQkODIkMjkkMTckMzAk$MEU3Qjk1MjNBNzQ1QCTVGMTUwOUZCRVTUM5RTM1MjZBQzhGOEY2OEMyMDNBMjM2QjczMjM0QjY1RjUg1QTlFRWjZCMQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestHotelPaymentInformation(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/detail" : "ODEkNzEkMzMkMTYkMyQ=$QkUG5QkI4MDA0QjAxRM0MyN0JEMTFCNzM1OHUIyRkUxNjUyMDFBODcxNUE1MzI5QTc3OTdEOUMFwOTE2NDY3RVjE5NQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestHotelDetailInformation(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/sale/detail" : "ODUkMjUkNTAkMzAkODIk$MzRDNzU3QjM1OEE0MTEyQkJCQIzhDRUEMzOEYyQUQyOTBBMjlERYjJEN0Q0QUNCRUZGNkRGQkE0MUJCOUQOwQkFBROg==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestHotelPayment(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/session/easy" : "OTAkOTckMzckODEkNzQk$MzY2NzZEMEI4QUVBNzdCQjRGOUE3OUU3NjQxOBDhCMDAyQjMwNTBBRTIwMzZERTQyMkMwOTk5OADkzOTE3URjgyNDhFODVhFMzAxGODYzMEE3RjA4QUMzQjQ4RUNGRUQ0MDBD$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestGourmetDetailRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/rating/msg/update" : "NTMkMjckMTI2JDE5JDExNSQ=$NDVENTEzNEJDMzg4MDBSCOUYzNzQJyMDY4MTE0QzcwRjgxMzgxRTI0RLjg5NEI0QUIyNUI3MEJFMDEyQjdBQjYzNzlDMUNDQkI0ODNDRENBQjlDQTBEHQTU1MjQ4MDdBZQUQx$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestBookingList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/booking/list" : "NiQ5NCQ2NyQ5NSQ3MiQ=$Q0U0MjAc0OUM0RkY4RjU3NTVDNTBFQzJGQUNFMTVDRkJCQUJBOTI0MTI3MzU4RTIyMDVkzOTTlCQjAyNzdGMENFNUFCMDVFQSGTgwRUE0QTc1OTFCMTczNDQyMjlFNERCRDZB$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetBookingDetailInformation(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/booking/detail" : "OTEkNDMkNjEkNCQ0NSQ=$Mzk1LREFENEREODg4Qzg1QzE2NDBDMDFGMTFEMERCRDgBLwMTdGOUJDQkU3RkFGWRTg4NjVFODQ3NkI5NzJDMzMzNTEzNjVJGMTNENUM2RTExMkI5NjNEREREOUZEMTg0REM0$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetReceipt(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/booking/receipt" : "MTA3JDk4JDUyJDc1JDc0JA==$NTdEMjBFRDA2Q0I0RDc0OTI2REMzNjYxRUU2RTdCOTFBNUUwRkY4ERTMxQTI0N0Y2MTExQ0I2QIzHYzMDUwRkE5OTkwQjVEMkNGREWY2ODhGNkQG2MzgwMzFGMUNDQThDMDdF$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetIsExistRating(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/rating/exist" : "MzQkMzQkOTYkMTA3JDEwMCQ=$NkMwNzBFMDI1ODg3NEYyRDQwMEJEQjE5REKZE2QzM1MUMzRjlDRENDODZBREUwQjEwNzkzNUZDMDRDNDFGQ0I0RjlGMzMwNjVEzQQjRBNzFCQNzA0MkY3N0FCODAyNUQ1MjND$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/rating/update" : "MTQkNzgkOTIkMTEzJDEwMCQ=$QzEzMzAwQ0VFMjZkxQzZDQkM2QjM0Q0QzRkJEQTNEMTY4MzA1MzFGRDdCMDQyOUNFMzlCNUIyRjQzNBEZCMzREMUMwMjGdGN0UzOPDAyQUNEQjZDRjGYxRDk2QkNFQzY1NzY1$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestGourmetHiddenBooking(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/hidden" : "MTI1JDI0JDIkNDQkNzck$QzXFGQzQwNTRCRERFQ0UxRjczZQjI2QTI2M0ZFNzExMkZY5NEYwRjdDMUMxNDhFMzgyM0FFNDUxRUHEzNzAzMUUyRUFDMTUyMTM0MDRDMkEyNDJDN0Q4OTE3OUFCNTM4RVTYy$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestGourmetAccountInformation(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/vbank/account/info" : "MCQxMDQkOTIkODIkMTAk$FRDRCRTU2MFDExMjk5Njg2MEZCMzYwQTU4QzZENzg2RTdEQkFGNzUyM0Q2NjkxOTU3M0Y1OEZBQURENDY0QUzVFMjc2NDJZGMURDNkVCRUYDzOEExQjdCRDU5RDVBQUVBMEI0$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetRegionList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "gourmet/region/v1/list" : "NzAkOSQzMCQ5JDY5JA==$MkQ5QkIwRZYkNGOUU0ODJCQ0ZFMzVGNRjgzMzNENTNEQkY0QTc0MkI0QTM4RDBFNDUyQURIwMDPg3NzExQ0YwMzlBNg==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetList(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "gourmet/sale/v2/list" : "MTQkMjgkNTgkNTEkMzAk$NjAxMDJFODkyMzWMxMjAzQUM2N0UC2TMDkwREQ1REU2RjdCOEI5OCUYxMEJEAQzRGRjNBNzEwQUVBNUYxRjg1MkQ1NA==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetDetailInformation(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/restaurant/info" : "MjkkMzYkNDckMjUkODMk$M0FCMTY0Qjk2RDU1NTRFNTc1ROURDRVEFFOTEVyMUFDQTkwNJkExRTkwMzBGNUQ5MTgyQUI4MzJGNEQ2MTBUEOEY2OQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetPaymentInformation(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/ticket/payment/info" : "NTkkNDYkMTAzJDc1JDIk$NzDc3MjA1MTg5QjgwMTg5QkFCOENEMDFBRUY5NTlGNzY4QjTEzMUNCMTBGOUMD1NkM1MzRBOTZFMRjY4MjAwNjlDMDQ3OUNEREIyQTQyMEEZCQ0IwOTgxNkFGMzMwNUYyOTg0$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetCheckTicket(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/session/ticket/sell/check" : "NzckMTA3JDYxJDEyNCQ0MyQ=$MjczMEVDNkQ2ODQzRDE3ODYzRjZCQzMwNzc0NjU0RkYYxMUY0RTYyOTA0QzkwQXTFGNTUxN0M2NjQwNCDQ3ODlCNEZEMEM5RkMwMjQ3OUM2NzJMyNkJCQUU1MTVFRSkZGODRE$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestGourmetPayment(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/payment/session/easy" : "NTEkNDMkMTAkMjAkODYk$QzBFMTI4ODUlBMzAzRDYS3NzU3MEU3NDFEQ0VDQzY1NEJOGRjNDMUNTBNkMzQzkyRUM2RkI5ODVGNUNCRjU0OEWUzOA==$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestDepositWaitDetailInformation(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "reserv/mine/detail" : "NjQkNTIkNjAkNjEkODUk$NzdCMkYzMzAzRUIzRjg4MkM3ODBGRjIzQTkxNzYzMTFEMTZFQ0NDMN0RFMzgMR5M0FBHRjBCMjA5QjUxRkE1RUEI2Qg==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestBonus(Object tag, DailyHotelStringResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "reserv/bonus" : "MjYkMzIkMTYkMjckMjMk$MkM1NTUyNzJERjg5IQzgxOTLg1RUDVY0MTcJ3OUMzQ0U3NjI=$";

        mQueue.add(new DailyHotelStringRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestHotelRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/satisfaction_rating/update" : "MTExJDQ5JDIzJDU4JDg5JA==$NTg0NUMwNTE5QURGQzU1QTRJCRDIwN0UzODY3QTY0MjI3ODY1QCUI4QTk2DMTc2N0EyNzQzMTg4OTUxOTFFQ0Y2NzDQzNjdDOTlBMzYyMDYzNjE1NkFMDNkEzMDdFQ0NGQzg2$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestHotelIsExistRating(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/satisfaction_rating/exist" : "NTUkNjYkODMkMjAkMTE3JA==$QkI0RTFFRDgxODg5RDhEXMTIzOENBQzA5MTVDN0E0MTA4N0FEMTk1ODMQ1MDA4Nzk5MVjhDNDA0QUNCMDEwMZjY0RTQ4RDIzMjc5N0RCOEZENUE0M0M2NO0FFQkE2MTRGOTQw$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestHotelBookingDetailInformation(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/detail" : "MSQ4MCQ1OSQ5JDYwJA==$NVDY2OTY3XM0FBODdEM0UxNDVDODlEQzhGM0RDRUZGMkYzRTRGQzczRjcyOTZQhBQzVDRDM0NDkxMDgxNkMZwRTkyRg==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestHotelHiddenBooking(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/mine/hidden" : "NyQ1OCQzMiQyMyQzOCQ=$RkI3OTZDGQ0UzOEYzQ0U2RkZMzQ0ZFRTNDGRkQOxNERFMjc4NDYzMkQ0MEE1RFDI5Q0NDMDk5NjEzMTEyRkIyNUIzRQ==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestHotelReceipt(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/receipt" : "OCQ3NiQ3OSQyNSQ2NyQ=$MDgxRDE5WRDExNDUzRjAzNTg1KNDIyMEVCQTlGMDE5NjE5RjVEMjYxMEFBRTRCQzQ4RRTFBMDU0NjFADNCEUxMkI4Qg==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestHotelDetailRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/satisfaction_rating/msg/update" : "MTI1JDg2JDEyNCQ3JDU1JA==$RTI1RTRGENDA0NkVDMjcyNjVEQzAwRUIxQTc4NDU1RDhBRUMzRkQwN0ZI1OUJENjY4MDZBOUVGMzFDRTJFOTQ1QTTAxMTExQjhDNkM2NEYzQjZGNzEwNjUxMTdDOEEA2MH0I0$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SESSION_SERVER).append(URL).toString(), params, listener, errorListener));
    }

    @Override
    public void requestEventList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/daily/event/list" : "NDYkMTEkNzkkNzAkNjck$NkUyMEQyRjZTDMjMzNUZFRDJERkVBQUYzRTFGQTgxMzY2MUHY1NDA5MTUyRTAxRjk0MJEZCPMzUzMUZDMD0IzMDREQw==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestEventNewCount(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/daily/event/count" : "NDQkNDIkNjMkODYkMzYk$Mjg4QzY4OEFERDc1RjAzMzE5MDQwNDVGNjVDQNzcyNETI3EMkJDNEIxRTZDMEM1RGDFCNTIxNDczRTc4OTlDNEFIFOA==$";

        mQueue.add(new DailyHotelJsonRequest(tag, Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestEventPageUrl(Object tag, String params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/daily/event/page" : "MyQzNCQ3NyQyOSQ5MCQ=$MURDFQ0UwQkFGRDJCOTBFMDhDRTg0DMjhGOVTdBQzhERTVBQjU2Q0EyMkQ0M0NBODBGNEU1MTQ4RkUM2M0QxQzFBQwT==$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).append(params).toString(), null, listener, errorListener));
    }

    @Override
    public void requestCompanyInformation(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/common/company_info" : "NiQzMCQxOSQyNCQyMCQ=$OTMyN0LEyMjYzNTA5NEDDY0NkXY3QTQwQDzJDMUU0MzkzODQ0RTVCNTJFMjYxMTdBMkRFQjY4NTcxNzIxQzM2RTdFMQ==$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), null, listener, errorListener));
    }

    @Override
    public void requestEventBannerList(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "event/v1/banner" : "MjUkNyQ1JDQwJDE2JA==$MzdCQPzlAGQkRCNDZNGNzE2NEY4OLEU1MjMzMkQ3NFzU3Mjk=$";

        mQueue.add(new DailyHotelJsonRequest(Request.Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL).toString(), params, listener, errorListener));
    }
}
