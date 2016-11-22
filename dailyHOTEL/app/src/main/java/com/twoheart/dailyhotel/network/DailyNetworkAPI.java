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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DailyNetworkAPI implements IDailyNetwork
{
    public static final String URL_DAILYHOTEL_SERVER = Constants.URL_DAILYHOTEL_SERVER_DEFAULT;

    // DailyHOTEL Reservation Controller WebAPI URL
    // api/hotel/v1/payment/session/common
    public static final String URL_WEBAPI_HOTEL_V1_PAYMENT_SESSION_COMMON = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/session/common" : "ODUkNDMkOCQxMDgkNDYkMjckNjEkOTYkMzEkNDckNTIkMTMkODUkOTEkNzkkMzUk$MkNCQ0MyQQjYzMN0U3OTlEQkREMjPU5MHThYDMEE1NjM2QzgKM2SRkZI1MkI3OTNGNJEExQzBEMkIzMzYBEQkY4SOTU2QJjk3NKjhFQTCM4MEQwRjg1RjFBOTDQ3MEZCNTJBOTAwRjI0ODZC$";

    // api/fnb/payment/session/common
    public static final String URL_WEBAPI_FNB_PAYMENT_SESSION_COMMON = Constants.UNENCRYPTED_URL ? "api/fnb/payment/session/common" : "MjEkNzgkMjUkMzgkNzgkMTkkNDYkNjAkMyQ1MyQzMCQ1MSQxMSQ3OSQ5NyQ5OSQ=$OEER1OEYxMjElEMzhFNTZHCOVEIwMNzJVCQjU4MTI4ZNkY2NDVIxTMjZNDNUJCMTlEGRDczNzNCREM1SOEQ0MDFUBNNkFDRjUE2DMA==$";

    // Register Credit Card URL
    public static final String URL_REGISTER_CREDIT_CARD = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/register" : "NTYkNjckNjkkMzQkOTMkNjQkMTI3JDgxJDkzJDExMCQxMTQkODgkMTIwJDgkNDQkNjUk$RjQ4MjE3LNTFBODVCQzVEQTExQTc2QTMwRDNMxRDYxOUOQyRTdCMjU4MkFGMOEZEOBDJBFNUNFYBMzM2RUY1DREU1NzGZGQUNOFMAjdBQkRDMUUyNDPY1MVjU0NWDhCNkFFQUM2OREY2QkU5$";

    private static DailyNetworkAPI mInstance;
    private RequestQueue mQueue;

    public static synchronized DailyNetworkAPI getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyNetworkAPI(context);
        }

        return mInstance;
    }

    private DailyNetworkAPI(Context context)
    {
        mQueue = VolleyHttpClient.getInstance(context).getRequestQueue();
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
                return request != null && tag != null && tag.equals(request.getTag()) == true;
            }
        });
    }

    @Override
    public void requestCheckServer(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "http://status.dailyhotel.kr/status/health/check" : "MzUkMTI2JDM2JDIkNjEkNzEkOTMkOTAkMTA5JDYwJDU4JDkxJDEwOCQzOSQxMTkkNTMk$MTTc5NDRENTlBQUUxMzNDRDFEMDY5NTZGNTQHFzVNEFGNEY4RDlBOVEE4Q0VWDRWUBNDNEI2NkUTzN0MyMEYzRTg2MTVCJQRkJBGQTgzMkM5MTEBGQkJEwOEOIxOTlENjIwNzE1REZFQZTIx$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestCommonVer(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/version" : "NDIkMzAkNTQkNTgkNjckODAkNjIkOTAkODckMzckMyQ2NCQ1NSQyOSQyMSQyMiQ=$NTJCFOURFNjU4QjdDMzRCDSMEY4OUYyMNTOk5NjdCIMTQ1NjRg2QjE4MUQLxHQUZJFRTXPIzM0VUEQkNGRDlBREVZEQTgxFNjIMyRg==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestCommonReview(Object tag, String type, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/common/code/review" : "MTAkNTgkMjYkNzckMzEkMTkkMTMkMjgkMTMkNTAkMjgkMjUkMTkkNTIkMjkkMTAk$RDIzRDNENkDRI2BBMTU0RNjGNERKTlQDSNBHDI4MLkY0QjM0NUI1Q0LUL2OTM5OTI0RjI4QR0RGQjVFMkYwMEE3NEKE2RDdGRTcwQw==$";

        String params = String.format("?type=%s", type);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestCommonDateTime(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/datetime" : "NzgkMTUkMjckNTUkNjEkNjckNjckNDUkMTAkMjYkMTckMTkkNjckNTQkNjgkNDYk$ODE1MDI0NzMZGREQAJ0IMDFBNkIzTNTLUwNDAzMzY3MzQ0IMzZXEMkUT0RTZEMNkZGRUDAJE4MTkDSxOTVEMjBBQjRFQzMVDN0VDOA==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserProfile(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/profile" : "NzMkNTEkMzYkNTkkNzckNjQkMTQkMjkkNTIkNTkkODckOSQ5NyQ5JDg5JDEk$MRUY4NUFGMRYjU0MjNI0Q0YyNjYyMjdCKMEQ5M0U5MMEY5NDQyQjcwNFTEC5NTKRCQS0ZFNPEU3RjFCOEMwMWOURDQJHjBEQTI4NRQ==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserBonus(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/session/bonus/all" : "MjIkMSQ4MiQ2NCQ4NiQ0NCQ3OCQ5JDgzJDQyJDYwJDAkNDkkOTckNjkkMzkk$YNUkE4ODVEQRkIxMDVBQjRDMUIFFNjQ5NDA3NkUT5RUMF0RTKVCGMTEzMUM4NDMPwOEUwNETEc4QkE1NUVGMTCIyQBTQ2RODGU2XQQ==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserInformationUpdate(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/users/profile" : "MzEkNTAkNzMkMzAkMzgkNDQkMTckMzIkNjMkOTIkNjAkNzMkOCQ1MiQ1JDM3JA==$RTZGMNDc1TMjhGQTA2QXzM3MTQ3MzY1OTTPVJMFNjJBXOUVGQXTY5NJjg2MUzg5NCDQ3WNDdGQUFFCRjdDOEVODODQ5MTk5MjcO0OA==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserProfileBenefit(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/profile/benefit" : "NDUkODAkMjkkMjEkMzMkMzMkMzEkODgkMzgkNzUkOTMkNzgkMjMkOTYkNTQkODck$N0M1N0ZCQzE4ODgxQ0Y2QWTTEzMjBCOCCTRBYDRTDE4RDhGMDkyMXzLY0NjcxNDM3NEVCMDE2QTc3RRjPdDREZFWODUT2RBjACG5Rg==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserCheckEmail(Object tag, String userEmail, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/check/email_auth" : "ODAkOSQxOSQ1OCQ3NSQ2NiQ1OCQzNiQ5MSQzNCQzOSQ3OCQ2JDc4JDU4JDg3JA==$MzAwMkIQwOCDU5MDRERTQI3MDkyODZCNDBGWQkFJVFQUFCMDcyMjJENDI3ENTY0RPMDU0NDEU0OTgyOHDCVDMMzMZDOTk2BRDQU0NQ==$";

        Map<String, String> params = Collections.singletonMap("userEmail", userEmail);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserChangePassword(Object tag, String userEmail, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/change_pw" : "NDMkNDEkMjAkMTQkNiQ0MSQzOSQzMCQ0NyQ0MyQ0MCQ0NyQyNCQwJDUzJDE2JA==$VMjM3MUWE5M0VBMzRJAwRjM3LMVjM3Q0RIGNjc3OEJBPUNTIHUV3MGPWDUB=$";

        Map<String, String> params = Collections.singletonMap("userEmail", userEmail);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserInformationEx(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/information/omission" : "NzUkMTEkMzMkMiQyJDgxJDM3JDY0JDQzJDEzJDMyJDM5JDI4JDYwJDI1JDY1JA==$MDVMQxQzQ3RERMEBOTgzQzRGQFUUyUNzREDQzlDKMYNTg4NDVc4N0U1RTNFMDNk4MNkIzQTIEzMTdCREMzRkE4RTdEBJNDM3MzEyMA==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserInformationForPayment(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/information" : "NTQkNTMkNzckMTgkODIkODEkMTgkNjYkMzQkNTYkODIkNzYkNzQkMzckNTQkMjMk$ODAzNUVCRjAyNDIwMzPClCNATc5ODc2OEEzIN0JU1MjVCMUQwNEMzOTTY2NEMUSIzRDA5MjWAyNkRTBNQDE1NUZVPFNBUEMzQTFDMg==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserUpdateInformationForSocial(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/update/fb_user" : "NjUkNzMkMzUkNzAkNzQkNTIkNSQ2OSQzMiQ5MiQ4MyQzMyQ1MyQ0NCQxNSQyMSQ=$RDdBNAzNEMDA5NjVE4QTAI3NzUzODk4NTUSUwMURHBQjUzTRDZCRTBENS0QT0QTdFNDFGMEU4OZAThDVNEMB5QNkIQzOUJFOEUN3QQ==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserBillingCardList(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/info" : "NDIkOCQ1NSQ4NyQ4NyQ4MCQxMzIkOTIkMTMwJDU2JDE2JDQyJDY4JDU5JDEzMCQ3MyQ=$QzdFNkE5NNjgzM0JIFMjZFRjlCQjY4OEQ3NkI5NDdDKRjUMxNDkzNTk1MTBWjkzQkE5NELNDNQ0RFOENGRDAwMGEE5MTE3UARDYFGQjEzMzMxRjVDMDA2MjVEQzBGMTgxREYJGNDMK3MTGI2$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserDeleteBillingCard(Object tag, String billkey, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/del" : "NTkkODYkOTMkMTEyJDMyJDI3JDMwJDYzJDI1JDEwNiQzOSQyOCQxMzYkODgkMzUkMTgk$MTBDN0VBRjZFRkE4ODAA2OEYyNKDcQS2NSkZNESMEVRBNjhDNzczMjE1OUM3QjIxMzYVPzRjk1RDQ1QzgzMDlENzI3QNjIxQDTU2NkFOBMzhCOTBg3RjkzRDQ3RITg5RDE0Q0QxNjgyUNURF$";

        Map<String, String> params = Collections.singletonMap("billkey", billkey);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestStayList(Object tag, String stayParams, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales" : "NzEkOSQ1MyQ1MiQ2OCQ3MyQ3MSQ4MCQ4MCQ4OSQ3MiQ3NiQyJDUwJDM1JDEwJA==$ODWg1NUYzOPWTg1ODczQzU2ODM0N0M5RDVDNDDRBNTNCMjAzOTVEQNDYUyPRDAxNjc2QkI4RPDBGQDNVPjkM1RJMUE0RTYzNNTdCQg==$";

        String params;

        if (Util.isTextEmpty(stayParams) == false)
        {
            params = "?" + stayParams;
        } else
        {
            return;
        }

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestStaySearchList(Object tag, String stayParams, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales" : "NzAkMCQ0OSQ2NiQ3OSQyMyQzNCQ5NCQ0MCQ2NiQxNiQ2OCQ3JDYyJDE2JDE4JA==$RN0Q4MjFQwNEY0RUXNPZDMDU0MDZlCQ0VFNDJCPRUNDOWEJBRTMxQzM4HRjkxODREEQjFBMKYjVBXNzRGNTUk3RkMD4OTA4NTI1RA=B=$";

        String params;

        if (Util.isTextEmpty(stayParams) == false)
        {
            params = "?" + stayParams;
        } else
        {
            return;
        }

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelSearchAutoCompleteList(Object tag, String date, int stays, String text, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales/search/auto_complete" : "NzUkMTkkNTEkOTYkMjAkNTMkMjckMTE0JDg2JDEzMSQzMSQ3NCQzMyQxMDQkNTIkNzck$MzlGNTlBM0QwRjQ4NkZVWFNzBDMEERBWOETVFOTY2RUNCQUNBQkNQDNDQPRBQUM5MjU1OEJFOTU0FGNEQ4NkERwQTU0MMTcxOTU2N0RFMGREFFMDEwMjVGMDgK2QjNCQzczMjEzMjCZCMDU2$";

        String params = String.format("?dateCheckIn=%s&stays=%d&term=%s", date, stays, URLEncoder.encode(text));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelRegionList(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "hotel/region/v2/list" : "MzEkNjckMTEkMjYkMjkkOSQ3MyQ2MCQ4NyQxNyQyNyQ5NCQyMSQxNyQzJDk5JA==$OTAUwRDlCNWDQExQjAEDxMzVI5NEREBMXDcCwNjcA0RDQ4MkM2N0E2QkVBM0NENzYExNjhBQzhDQzDIExRUUzRTUwMDZFGMzRUFZNw==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelPaymentInformation(Object tag, int roomIndex, String date, int nights, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/detail" : "NzckMjIkMTIkNzkkMjYkODEkNTAkNTEkNjUkMzUkOCQ4MyQzMSQyNSQxNCQ5NCQ=$NTZEMEQ4EQ0EyIRMzY0N0IwQzOMYxZMjICwNTQzQMEI5QTVFOEE3RDEIB1MjRFQzZBNzgxVM0ExQzFGODc0NUER4OKNFThOGMzJBNA==$";

        String params = String.format("?room_idx=%d&checkin_date=%s&nights=%d", roomIndex, date, nights);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelDetailInformation(Object tag, int index, String date, int nights, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}" : "NjMkNjckODgkNTMkNTckNjMkNCQ2JDM3JDc3JDI4JDQ5JDY2JDI0JDQyJDM2JA==$NjRDWNS0ExQUY4NjNGMzM4QkXRFNDZlDM0Y3KRDIK5RIUUxOUY1RWjNBMUY5MPEI0OMjkG0NPDNIDREPJERXjNCNDcxMTlGMUQ2OQB==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(index));

        String params = String.format("?dateCheckIn=%s&stays=%d", date, nights);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelPayment(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/session/easy" : "NjMkMjMkOTAkMTMwJDY3JDUwJDI3JDY2JDUwJDk2JDMkMjckMzAkOTIkNzAkMzQk$RkEL4QkUwQzY3RjA0MUI0M0MJyRKTFXI3OTDQ5N0RGREM4MEU2RjdBAMMTg5MDE4NEI0RTZJAQCQGTlFOTA2REU3N0RBMDYlCRTA1VYRDc2Qjg3RTY4NzE1MjM5RjQ5RkFDQTZDRDE0MzMJz$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetDetailRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/rating/msg/update" : "MTI0JDQyJDEyMSQxMDMkNjIkMTA1JDEyJDM4JDYkNzQkOTkkMTckMTEzJDU2JDkxJDAk$VODg1M0IY0RjJBSNzZPERTg3QkUyRUFDQkRENUYwOSTg3OTEgzMzFGNjZDBM0U4RjZCMZzhDMDJBQDTRDNDQ0QkMxNjAT4ODQ5MzhGNHDYyMjEyQ0WDZCCMzYzRUYxRDU3MjAwFRTdCIQjg3$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestBookingList(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/booking/list" : "OCQ5OCQ4NCQ3NyQ4NiQxMjkkNzMkMTIxJDcxJDExOCQ5NyQ2MCQ2MyQyNSQzNSQyNSQ=$RjYxMjU1INzFDQ0ZCRTRBMzRFCLMzY0QTQ0ONTQ3QURENDMyQjM0NDczQzczOURYFNGTQ2RTNGRTPQ5XNzNDPNzNDNkQWD4NzA0M0YBzNkRFNODVFRUU4RDQ1Q0IOyRDcZ2QzkwNTlDFN0M2$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetBookingDetailInformation(Object tag, int index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/fnb/{fnbReservationIdx}" : "ODQkNDQkNDckODEkMTIyJDE0JDQ2JDAkMTckMjQkNzEkMzEkMTAzJDQxJDEyNiQxMTYk$NNzVFQUVFRDlBRTYkO0NzQxOWDJGNjAZ4MEI2RDBBTREZCNURGLZQzUYyQkNBQjJGMDU5RDJERQjAwMkFDODE0OTIOyNzMxTOEUwQjNEQREVDNTAyMzIJ1RUI0Q0U0OTDBDOEXY2RTIzRkQ2$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{fnbReservationIdx}", Integer.toString(index));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, "", listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetReceipt(Object tag, int index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/booking/receipt" : "NDAkMTI1JDEyNCQxMTMkMCQzOCQ2NCQ1OCQ5NyQyMiQ5NyQxOCQxNiQxMDkkMTAwJDg3JA==$UMEEzRDA4N0U3NUZKGRQjJCRFTNFODREMEQ0RDU0OXTI1TODFCREYxQTcyN0UI5REI5NWTJGQzdDNTdCRDU2N0YJyM0QxNUMwN0QER1ROEY1QjcRxOUJBMzI4RTI4DNjZEQURCNDlHFVNkU4$";

        String params = String.format("?reservation_rec_idx=%d", index);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetIsExistRating(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/rating/exist" : "MCQxNyQzMiQ5NiQxNiQzMiQ4JDEyJDIzJDkkODgkMjgkNzUkMTUkMTA0JDY4JA==$HQTFFMEQSA5NzNUM0OEEQ4INkMI5QVTUyNEM1MPzFhERDREMDEzM0ZCOTFGMEVERjc4NLDc0Qzc0NIEVEQ0VBNUFCODMW3QTMyNjU2QUUQ5RADg3OEMwOTA0OTVBMzQzQzBFNEU4QUQ2OTQ4$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetRating(Object tag, String result, String index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/rating/update" : "NyQ0JDEyMiQ0NyQxMjQkMjUkNjYkMzckMTE5JDExMSQ5OCQxMiQzNyQ3JDc0JDk1JA==$MUNBVNTMMQwM0OI1QkMxNEE0RjAE0MUIzRDI3NRDWg5RDE2NEQ2NHzIzRkY1MDVEQkNFNTCVCMQTYxMzdCQzA1NzFFQ0QyRQkNDNjhCWNTA3MzU2MkM5NLjBCQzZGMXjM0RDMzCLQzgyMTAw$";

        Map<String, String> params = new HashMap<>();
        params.put("rating", result);
        params.put("reservation_rec_idx", index);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetHiddenBooking(Object tag, int index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/hidden" : "MTEzJDQ4JDM1JDEzJDI4JDY5JDkzJDcxJDEzMSQ5NiQxNSQxMzckMTIwJDUzJDU4JDk0JA==$N0U1RjY2MjIzQPzRkyOEVEQzQ0RkED4Mjg4RDEW5RTM3MzkwRTZGZQQTBEBMTczQzNDQUMwNFUJIxQkE1NkZGOUJGODY3QRzBFMNS0RCMkNFNDgxRERCNTZDQ0EX1NFEI3RjNBQzYE4QzOM2$";

        Map<String, String> params = Collections.singletonMap("reservation_rec_idx", Integer.toString(index));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetAccountInformation(Object tag, String tid, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/vbank/account/info" : "MjUkNzckMzkkODQkNzQkMTA4JDExMSQxMDUkODMkMyQ1MCQ0NyQxMDQkNDckNTUkMzYk$OUUSxNjYxRDI3OUQ0OEY2MTE4MLzU1RkFCNETYxNzVk2QkNDPHOTJTFQVzU3N0I0NjIyMzI3MDYxQzAzIQTI0UMDQH5MMzZENDI1RTEyODkX4N0IyXMDUX1RAUQzOUUyN0RCQUNGRDU2NTJB$";

        String params = String.format("?tid=%s", tid);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetRegionList(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "gourmet/region/v1/list" : "NzMkNiQ1OCQ4MCQxJDI3JDMxJDg4JDI0JDQzJDYkNjQkNTAkMjAkMzQkNzgk$QZTgxRUUGQ0RkFFRDZDNAzBFQjZAyQVzAwINNTY2OTExMTZlERjAC5NzZCMThDQzNFMGBTk4NkU3QzPhCMDgwQQUNFOEUFEMJ0Y1Mg==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetList(Object tag, String gourmetParams, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/sales" : "NjYkNjMkMzEkNzYkMzckODEkODUkNCQ2NyQ5NiQ2MSQxMyQ0MSQ0MCQ5MSQ2MCQ=$N0M0VNTRCQUIxYMDIzRDdEQTJBODI3QjZFCOEE4NEQVTdBMUVDOUM3QzlDOTRg1MzLBERDYEOzRTKNBQzk2QYUFBIMDAMGwRjNBQw=U=$";

        String params;

        if (Util.isTextEmpty(gourmetParams) == false)
        {
            params = "?" + gourmetParams;
        } else
        {
            return;
        }

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetSearchAutoCompleteList(Object tag, String date, String text, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/sales/search/auto_complete" : "MTI0JDE5JDQ3JDY2JDY0JDExMiQyNiQyNiQ3OSQyNiQxMDUkODIkNTckNDAkMTIyJDEk$RDDU0NTk3RTIwNEZBQTEZ4QTlBQNPSjcyRUZCRjBBTN0JGMUFBQ0UM0MUY0IQjhEM0NCMUCIzJODdERUQwNDzFZGM0U5MUE3NTE0NUI0RENGRTTlEMTZDRkJBMMMUUwNDlGMjBCNkExRNjU5$";

        String params = String.format("?reserveDate=%s&term=%s", date, URLEncoder.encode(text));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetSearchList(Object tag, String gourmetParams, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/sales" : "MjkkODckNTQkNDckNDkkNjIkMTQkMTAkNjUkOTYkMzkkOTYkNSQ3MSQ1NiQ0JA==$NzI4SQNjJCOUSQwNUYQ1NDg5OUZBQTJEMEkZDRTA0LNjA0RDlGNjZkI4QJkJEREDUxRTLUU1QWzYwMzdBRDJCNTQzMjU4OTdFRgHV=X=$";

        String params;

        if (Util.isTextEmpty(gourmetParams) == false)
        {
            params = "?" + gourmetParams;
        } else
        {
            return;
        }

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetDetailInformation(Object tag, int index, String date, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}" : "MTckNjMkNTQkNyQ2NSQ1MyQyNSQyMiQ0JDk0JDYxJDM1JDgkNjckMzckMTAxJA==$QkJCSMjBPVENkQ3RTU4MTjkyDOTQVyODZBQjSZFBNTMwMDI3MDQ5N0RDMDYGxRFDFg4NZEI4NTDXM2OUNGRUQ0QTY4N0MyNjVEMRgJ==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(index));

        String params = String.format("?dateSale=%s", date);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetPaymentInformation(Object tag, int index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/ticket/payment/info" : "MzkkOTYkOTIkMTI5JDEwOSQzNyQxMjgkMTA3JDQ3JDY0JDMxJDEzMyQxNiQwJDk0JDEzOCQ=$KMzM4MzgyRkFFOTFBINUZCRkQxRjA5MjIEyRkFEOODYDwMkMwMKjk1RjkxRjNDMDM1MJTc1QjZCMjJCREFEQzk3NDdGMkUCzMDgVzNDZEGRkE1QThWDNkQM1MzNBRjEyMjQwQUUYH1DNkIQw$";

        String params = String.format("?sale_reco_idx=%d", index);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetCheckTicket(Object tag, int index, String day, int count, String time, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/session/ticket/sell/check" : "MTAyJDUzJDEyMiQ0JDI5JDExNiQ5MSQ2OCQ3OSQxMTIkODEkNjckMTA2JDExNSQxMjYkOTIk$OTAwONUU3MDE1MzkxMUI4NDJEMjRFMOTUxQTIyMUUwOUFDQ0M5NTU5QBjBEMEI4OUMzMOZDY0NjdFQjFYBUNzlEODUxNEDRCEMEMyRDFGRENJGRULI3NXSzhFODQ1NQDzMxRUI1BMzM1MzAy$";

        String params = String.format("?sale_reco_idx=%d&sday=%s&ticket_count=%d&arrival_time=%s", index, day, count, time);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestGourmetPayment(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/payment/session/easy" : "NzgkMzMkMzYkMzAkNjIkNzMkNSQzOSQxMyQ1MyQyNiQyMiQ0NiQzNSQ2OCQ4NiQ=$NjA0NKzIxMDNETRkUwMTNBNMjAzZM0E2NDQNhGNPjcME3NDKk3MkVEQkIP5QzA2NjYxRIjNADRTg4RUU0RATIzTNTc3MzCMwNjg5Qw==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestDepositWaitDetailInformation(Object tag, String tid, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/account" : "ODAkNjIkMjgkMTIkMzgkMiQ1OCQ1MiQxMSQ4MCQxMCQ4NyQ5NiQ1OSQ3NSQ2JA==$MEJZEMZkFCQKjCc4BRDM3N0Y0N0Q1RjU3DNEI4OUUzDNzhBMTNGQzY4UNUY3INzWE3MTMzQkLUwQPjFBOTFELREE2MXzFCWQ0VCXMg==$";

        String params = String.format("/%s", tid);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelRating(Object tag, String result, String index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/satisfaction_rating/update" : "NDckMzEkNDMkMTA1JDExOSQxMTckNiQ1NyQzNSQ0NiQzNiQxMDUkMTE5JDEwMyQ5JDg0JA==$REE0MDAlBBQTUzMzdCRTU0Q0RFNzlERDcZ2OPDEY0MTc2QjAFc4MURCEMDI2NDTQwMDYxNDM0MDQyNDk5REYDzMkQ5QzU5M0VBQjM4NTkL1RQEY0RUIZDRDY4MPzEzNW0VPFNUVDMkM1REQ3$";

        Map<String, String> params = new HashMap<>();
        params.put("rating", result);
        params.put("reserv_idx", index);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelIsExistRating(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/satisfaction_rating/exist" : "NTEkNDUkMTI1JDEwNyQxMDYkNzUkODckMTI2JDg2JDEyMiQ1NCQxMjkkNzkkODgkNzMkMTI3JA==$MkVCQ0RFMEYwMzVEMUIzNDVEQ0Y4NDc2QkQzMTMxNEE0QX0ZERTYM1JRTQwMzI5QUI1QzNCOTUg5NDjYNwMzJFRDEXUwVRjYyMDUyRUMzQkJBOTQ3FMTzBEMjAzNjUZO1MzBBEVNjcF1RkNC$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelBookingDetailInformation(Object tag, int index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/hotel/{hotelReservationIdx}" : "MTI3JDExMyQxMTckMCQ4NiQ4JDE1JDU4JDExOSQxMzAkMzUkMTkkNDUkNDEkMTIwJDk3JA==$FMjIxNzUN4MzVDRPUFECOTBDNjg0MjdGQUU0ERDBDFQkRCENEVEREVDQ0ZFRDUWzN0MwRDhDMDFGNDdDRTUxODQzMDQ3OHTdDWMkQ1NDE2REIxNjJDMkJCQTgX3ONJEQyPOEYzNDJE2QkQF4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelReservationIdx}", Integer.toString(index));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, "", listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelHiddenBooking(Object tag, int index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/mine/hidden" : "MSQ3NiQzJDQ5JDcwJDEzJDE2JDI0JDg4JDY3JDU2JDkyJDE4JDc4JDY3JDI3JA==$MBkWE0QTNGNjIFzRKTAE2RkE5TQBTEzNEJCNTA1QjVDNDY4NEZGRUQL1N0NNDM0RFN0UZ1RNUYzNEYM1YQ0RCMjBPFNDJGYRNjc4MA==$";

        Map<String, String> params = Collections.singletonMap("idx", Integer.toString(index));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelReceipt(Object tag, String index, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/receipt" : "NTIkMzckNDYkNDQkNjEkMzIkNzMkNDQkMjUkODQkODMkMiQ2NiQyNCQ4NyQ3MSQ=$ODPIyNDhGMzU1QTQzNzRBQjgR1QD0E2MTJBLNzRFRWDVDRkNQY0NRTEzRTk1PNzY3OTODUzZRDU1RjlTEMzUxRDCFFSGMzNDNzM0QQ==$";

        String params = String.format("?reservation_idx=%s", index);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL + params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHotelDetailRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/satisfaction_rating/msg/update" : "MTE2JDEyMSQxNiQxMDQkNTQkMTAkMTEkNjMkMTAkNSQyMiQ5NiQxNCQ3JDQ0JDk5JA==$RUY3MMEEQ0NjPKPAgyQzJDVMDjExNkEzRjdDNEFFNzY0KMDU2MjlDRkVGQUFDNIEE0RTAT4OEVFNjRCMjIxN0MxNEREMjhFOERDANNTFGRDI1RjEzQzlBBMDRFNDVERkNEEODgxXQzFENUZB$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestEventList(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/daily/event/list" : "NDgkNzMkODckNjkkNDgkNzEkNjckOTAkNTYkNSQxJDcxJDM2JDcxJDYwJDgk$QUUZFMLkXMwMTE5RDRFM0Y3NTgxQjI0QkZENjXY2RUExOEFCNzZDZHNTkxNTPWM1NjRDNzM4MFUXjZGPWMjE2LNkJCNjZFNTUwKRNg==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestEventNCouponNNoticeNewCount(Object tag, String eventLatestDate, String couponLatestDate, String noticeLatestDate, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/new" : "NzAkODAkNzYkMjMkMzckMyQ4MyQ2NCQyNyQ1MiQ0JDU3JDcxJDI1JDMyJDYyJA==$RURSICQTU3RkZGMEUwNUZCOEEPP1RFDQY4MTlERUQyXMDI2QzgwNEQ1FMzIPwQGzYzNjFGRLDdUGQTdDM0KU3NjUB5MzEVEDOERGMw==$";

        String params = String.format("?eventLatestDate=%s&couponLatestDate=%s&noticesLatestDate=%s", URLEncoder.encode(eventLatestDate), URLEncoder.encode(couponLatestDate), URLEncoder.encode(noticeLatestDate));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestEventPageUrl(Object tag, int eventIndex, String store, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/daily/event/page" : "MTckNTUkNTUkMTYkMzEkNzQkNjQkNjIkNjMkODckNzkkNjkkMzckNjQkNjYkNDAk$N0I2N0FBQ0QwQ0U1KQLzI5NDk0REE5NRzM1N0LM4FRkNFQUI5MzQ5NkYyMUGQMyNUPYADBSCRKDU0NzZBMKDDM2QTZDM0UQwMTdDNQ==$";

        String params = String.format("?daily_event_idx=%d&store_type=%s", eventIndex, store);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestCompanyInformation(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/common/company_info" : "MjckNzkkMTUkNTIkMjUkMTgkMzQkODkkMzckODIkNTgkNjkkODEkODIkNzYkNDYk$MDU5RDY0QzdCMDMY1QTjk2RjMxJNzMX1MEUFCNMzk3OUQyXQUY0RkMyNjLVZCQzE5MENFQKTIxRkQPwMzlFQWQzMI2OEOZBQkHM0OQ==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestEventBannerList(Object tag, String place, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "event/v1/banner" : "MTckMyQzNiQzNiQzNyQyNiQyMiQxNSQ0JDckNiQxOSQxMCQzMiQ1MyQxOCQ=$NkZQXFOMGzNIwQjNCQVTLSUzODTNGWNzUK2BNkEzRkRCRDRUMVFRUQF4NzY=$";

        Map<String, String> params = Collections.singletonMap("type", place);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestDailyUserVerfication(Object tag, String phone, boolean force, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/myself/phones/verification/start" : "MTMkNDckODYkNTYkNTQkMTI4JDk0JDU4JDEwMiQxMTQkMTA4JDY4JDE5JDQ4JDcxJDU1JA==$NDkxNUNFQjlENREUwNEERBMDJCMjJGRjdDODI3MDUyRkZFRTUCdEOTMAxHNzTZNEQzg4REQRS2RTY4QTU0M0RDMzI4QkJEINzgyOLTdDQzUX4QzJDRODk4NTXg3RkQyQjY2MEY3QzNYCNDhG$";

        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("force_to_proceed", Boolean.toString(force));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestDailyUserUpdatePhoneNumber(Object tag, String phone, String code, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/myself/phones/verification/check" : "OTYkNTMkMTE0JDEwMCQyMyQyOCQ3MSQ5OCQ4NSQzNyQ2MyQxMzIkODYkMTAkNTQkODMk$RkMyQkVGODTJEN0NENzAzQUIAyNDkA5NTM4ODUT2REU4NjY1REU0MjTI0RGjM5RjRDERTJFQzcwMRTQxMDIZ4MjU2ZNUjE3MzdFRUJFNDWdFGRDIMxRThERTg4QTAyPRkY5NDVFMCjVEQUU5$";

        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestSignupValidation(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/normal/validation" : "MTA5JDYwJDU4JDk5JDgwJDU2JDk1JDI3JDg3JDU4JDckNTYkNTckNCQ1NSQ2MyQ=$QzlFLMDUI4MzVCQ0NDQTZGQkI1QzMGwMEZGMjA0NUQzRjhBMDJBMzY5IMjANVERQYMjPFCCNEE3MzMxNDgwRUJGODBVCRDWBGNDRGMjYZyRjIxRUkJCRDk1QkFENIjQ1REM3MTA3MzgwQjkz$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestDailyUserSignupVerfication(Object tag, String signupKey, String phone, boolean force, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/normal/phones/verification/start" : "MTU4JDc5JDE1NCQ0MCQ1MiQxMDAkMTQzJDQ5JDkyJDE1NSQxNjUkMTM4JDE0MyQ4MSQ3MSQ5OCQ=$ODJDQkRDNDFGMjZDMkEzQUMzNTNFQzJGREY1NzA2JNkQ3RjcwZNDkC1MDAwNTNCNUE5QjAwLQjI4MDZBQzGdMDQUM0OTdBAQzIX4RkQ1RVTg2OEFEMTM1QTIxMkU2RUU5RTQxNzgwOEVDKNDFEIRjBQDMDEzQTE5NQjIyQWjBFVNUPFCODI5MDQxNEI=$";

        Map<String, String> params = new HashMap<>();
        params.put("signup_key", signupKey);
        params.put("phone", phone);
        params.put("force_to_proceed", Boolean.toString(force));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestDailyUserSignup(Object tag, String signupKey, String code, String phone, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/normal/phones/verification/check" : "MjEkMCQxNyQ1NiQxMTAkMzIkNyQxMzEkMTQ1JDQ1JDE0MiQ4MCQ0NSQ0OCQxNzMkMTc0JA==$BQzZCQ0IJCQzg1RTQ3QNEFBMXTA1QTQ2QGkNDMzlFQjQyTHMNThGMEYyMDc3MI0UwMDlCNTc0NDFEMUNBMTkI1QTBFNTgxOUUxOTI2ODY0Q0VFQTRDQjRU4NzFCNkQyMENENEMxAMzE4ODVERYEYxOSDhBNjNBMEYwODFEMzNDRUULSwOEEzNjk5MzA=$";

        Map<String, String> params = new HashMap<>();
        params.put("signup_key", signupKey);
        params.put("code", code);
        params.put("phone", phone);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestFacebookUserSignup(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/facebook" : "ODAkNDQkNTckNDgkNTAkODUkMjQkMTIkNDMkMTUkNDckNyQzOSQ1OCQyMCQ1MSQ=$NTAyNUFCDRDEwQRDCc2MWzE1MjQzUNzZDMTI0MTkKwQjFCNHzBRSDIM0MPzZKMjA5RDU1EQTYyMUI0Q0Y3MUVFRDNCOUNERNODIzNQ==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestKakaoUserSignup(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/kakao_talk" : "MzQkMTIkNjUkNDQkMjAkODMkNzckMjQkNzUkNjQkMzckNyQzMSQ4OCQ0OCQ5OSQ=$RTdDQ0ZRCMzg1ENTZBQUJRDNjPVGQzUFyQUE1RjXSg2ODI4RITMc2OTU1ODg4RjhGNjYS3RUUHzREU1MYDcxYMDNBBNzFNFMTk4JMA==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestDailyUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/normal" : "MCQ3NSQyJDgxJDg1JDYkNzckMjIkNTkkODAkNzkkNzkkNzkkODUkNTIkMzgk$GMI0Q2DOEZDQTM2QzBBQTlNDM0JCQTk4MzgwOEMYyQUREOUFCMzEzSOTcxMEET0Q0RFMDdFMDQ1NTk4ODRAAYTJClEMjXU4QCjIwNw==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestFacebookUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/facebook" : "NDQkNzckMjgkMzIkNjIkNTIkNTkkNDckNzkkNzckNTMkNzMkNTckODckNDkkMzAk$MTk3OTE5MkU4MDE5NjQyNTY1RUQ3TRLjVVBRkNDNkM0NDdCLAMTTFBQKRzhTENzZNEM0UEwNEQyNVjAzOTHAzSODNJFNMzRFNDg4MQ==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestKakaoUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/kakao_talk" : "MzkkMjUkNDkkODAkNjQkNjckNTIkNTkkNzQkMiQ3NCQ0MyQzNyQyNCQzNiQyJA==$NkTCNGODU5RkI4Q0RCNzA1ODVYFMOTE4N0JEMC0MJ3NjBECGQkI2QTIMyRGTcxMDZZERDU2OJUYF0Q0WMKyN0E2NDcxNRzU5RTM4MQ==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    /**
     * /api/v3/users/coupons
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    @Override
    public void requestCouponList(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons" : "NTUkNzUkNDQkMTIkMCQxNyQ4MiQ4NCQzNyQ0MCQ0OCQxNyQyNSQzMSQ2JDEwMiQ=$XMzE5MMTc3MTVEJQjBUSCQkM1QLUQ5NkWY5MUYzQUEQwWRDA5N0UA0MQUU0RDNDM0RXFRTE3RUJBRkY1RUM3NTUZGCNKzk5QTRDRA=B=$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
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
    public void requestCouponList(Object tag, int hotelIdx, int roomIdx, String checkIn, String checkOut, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/coupons" : "MjkkMTckODAkNTkkNTEkMzAkMyQ0MiQ2NyQzMiQ0OCQ2NCQ1NCQ3OSQxJDUxJA==$MBDgCxMDk2MDFENTI1QUkFGRkFDREY5OFCNDhGQUQ5NzFM4NTOUC1NjAQzMMjZCRDU2JQKjNCZOTYyOTQT4NDQwRjY5RDPM2N0ZCQQ==$";

        String params = String.format("?hotelIdx=%d&roomIdx=%d&checkIn=%s&checkOut=%s", hotelIdx, roomIdx, URLEncoder.encode(checkIn), URLEncoder.encode(checkOut));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestCouponList(Object tag, int ticketIdx, int countOfTicket, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/payment/coupons" : "MTAkNDEkNTgkNDckODIkODIkNzMkODMkMSQzJDcxJDY1JDUyJDMxJDM1JDkyJA==$OWDEMzRDNFMTXU4MjM2Q0E5OTYxREU0AMUZIFMEQxNzdCNRkEzOGEUH5NTQzNUE2VNjIMzQzRBMP0QwQCjdFREVGNzPQKMgyNjY0MQ==$";

        String params = String.format("?saleIdx=%d&countOfTicket=%d", ticketIdx, countOfTicket);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestCouponHistoryList(Object tag, DailyHotelJsonResponseListener listener)
    {

        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/history" : "NTgkMjgkMzMkNTYkNzEkNTYkNDQkODYkMzAkNTAkMjEkMTkkOSQzMCQyOSQyOCQ=$ODlCNTQ1OOTA3NjczNkJVEQCjRBQSjNFOEXOXDMyPMzM5ODE4RTOBEMEZFGRjA1RTFZg5MjXk2QTVGNUJCDMzMzMzI4ODJJCNzY0Mg==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestNoticeAgreement(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/agreement/confirm" : "NTYkNjgkMTkkNyQ4JDI4JDUyJDE4JDc5JDg1JDY0JDg0JDY5JDc5JDYxJDk0JA==$ODdCM0QIL5RDY2RTRFNQjlFGRDhBRATk5ODlCMkEyQjVBQjYxRjc2YQkRENzUEzTOVUExMAjZENzkD1NVDEE3RTNFBHQjNRCOUYyNA==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestNoticeAgreementResult(Object tag, boolean isAgree, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/agreement/result" : "NjgkOSQ4NSQ3NiQ3MCQ0NyQ1MCQzMiQ5NCQxNiQ5JDE3JDQxJDQ4JDk1JDUk$QTcxNVjkwRTOUEyOURMYBQTcyQzlBOTBCOUIS1OTg2VQ0JBREUI5NDTEwYRDA5M0RCNzAxMzM0ODIzXVNzc5NzRM3NjUxNEUODxMQQ==$";

        String params = String.format("?isAgreed=%s", isAgree ? "true" : "false");

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.PUT, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestBenefitMessage(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/benefit" : "MTUkODEkMjIkMzMkMSQ3MiQ0MCQ1MSQ2OCQ3NSQ1NCQzNiQzNiQyOCQ3MCQxMDIk$MMTREQkE5QTVFNEQAwMTdERLTE3RMEU1MkJXBLWQjY3HNDc1OTExQ0LI1HRjI3RERBRjU3YNURU0N0VCTNQkY4Q0Q0QjBDBNENDNQ=T=$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestDownloadCoupon(Object tag, String userCouponCode, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/download" : "MzMkNTYkMTgkODUkNTMkMzUkOTAkNTQkNyQyMyQxNiQyMCQ4MSQ2MiQ3NCQxMyQ=$QUE2NzVCFMUU5RNEFKCMjSQE3MOjUyRkFBOTVDMGCzlFN0M3QzkzM0VGMTAQO3QVzBMFMTJGMDhFDN0MxMUMHzNDc5RDY4NDLU5YNA==$";

        String params = String.format("?userCouponCode=%s", userCouponCode);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.PUT, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestDownloadEventCoupon(Object tag, String couponCode, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/download" : "NTckMTUkMjckNzEkNDQkNyQxNCQ1JDE3JDE3JDcwJDk1JDM1JDU1JDckNDEk$NUY0MNjKEOwMDIwQZTXWlOCNjRBQUE2ODFc1SNUMwXNzY4MTA4OESIxMUQEwRUQ4OEEwQGTdGRQjMzQUFFUN0M5OEI5OTczNDVGZRg==$";

        String params = String.format("?couponCode=%s", couponCode);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.PUT, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHasCoupon(Object tag, int placeIndex, String date, int nights, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}/coupons/exist" : "MzckNjgkNzAkNzgkMTE1JDEyNiQyMiQxMiQ5NiQ1NSQxMDAkNTckMzEkNTUkMTA4JDE4JA==$ODk3NDIwMTAwHODNFNWDk4QTPQ1MkQ0MKTVDNjYyQVkM3OTk2MzVCRjIVwAOOENGQzg1MTI1OTEA5YNzE1NzdVEREVDRkJERUVFODMg0DNDcwTQTNBNzY1RkY0MjRKFM0ExNkVENWkI5Mjkx$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(placeIndex));

        String params = String.format("?dateCheckIn=%s&stays=%d", date, nights);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestHasCoupon(Object tag, int placeIndex, String date, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}/coupons/exist" : "NzEkNTUkMTAxJDc4JDExNyQ1MSQxOSQzNyQ0NiQyJDEzMCQxMDgkMzEkMTIwJDc5JDE5JA==$MTMExNTdFQkU1QjUxNUUQB0MzQ3QjkxRVTJEOEQ0TNzAzOUFDCNTg5NTlBBNUJZCODY0RjY0NkM1MTQNSwNzMxUNDI3MDU2MjE1NDcwRERCNDYTCxMkNBQzM3RCDY4OKDU4QjUxVNDQ2MkIz$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(placeIndex));

        String params = String.format("?dateSale=%s", date);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestCouponList(Object tag, int placeIndex, String date, int nights, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}/coupons" : "NDAkMzIkMTkkMjMkMTAkNzgkODckNiQyOCQxNyQ4NyQyMyQwJDExJDE2JDc4JA==$YMEQxMDTIyOITBEyFQ0MMwOEICMwOTKdETNTgwMzE3GMkRGNEYwKQURBNDg1NzAzOTlFRDFDNEFDQzXhBREI2OLDNCRUAIxQWTI2Mg==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(placeIndex));

        String params = String.format("?dateCheckIn=%s&stays=%d", date, nights);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestCouponList(Object tag, int placeIndex, String date, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}/coupons" : "NTkkNTQkMTA0JDkxJDExNCQ4JDEyMiQxNiQxMjUkMTIzJDI0JDExJDEwNCQxMTMkMTMyJDEwMSQ=$MzI0MTk1POUBM3OEUE1NjQ5RTFAwMDJBOTNGNjVDOEVCOUYxNzU3OEQxNzLkwNTEDwMzE1MTVCRTZFMkY3RDAyM0RBM0JCNHTE2ODXRCRPTkyM0WU4QMkU0Q0UFEMjZGPJQCzFA1RTE5Q0Q4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(placeIndex));

        String params = String.format("?dateSale=%s", date);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestRegistKeywordCoupon(Object tag, String keyword, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/keyword" : "NzUkMzEkMjAkOCQyJDAkNDkkNjEkMiQxOSQ5MCQ2MiQxMiQxNiQzMCQxNSQ=$NNTzJk1NTVDRKNDBdUGMzQX1RkMxFNjTU4OUY2NUEXzRUVCRTk1REUyUOEU0REY0QjHNLFRUIzQzc3RTlCMTRBQUMT0QzNFNNTNGNg==$\n";

        Map<String, String> params = Collections.singletonMap("keyword", keyword);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUpdateBenefitAgreement(Object tag, boolean isAgree, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/benefit" : "ODckNDIkMzUkNzYkMzAkNjEkOTAkNTgkNTIkODEkNDckOSQ2MiQzMiQ3NyQ2OSQ=$NjdCRTNCQBTczOUY4RTJGMzY5RDA2NEWTRCMTkX5NDA3NEOE1HRDNBNVjc3NzMAA1NkAQRyNTgwNjBWDNEY3REIXM1NkRCREQ4MVQ=K=$";

        String params = String.format("?isAgreed=%b", isAgree);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.PUT, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestUserTracking(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/tracking" : "MzkkMzEkNTIkNjUkNDckMzUkOTAkMTIkODEkNDEkNDEkNDckOTYkMTckNjEkMTAk$MjAxNkUyMTYk5QRDMzXQjk4RkYwOTRCMzMYwRkLRGMjHKlWBQPTdDMXDkxQkTNQBNzAzMDEyMjQgwMjg0M0VCMUNU2Qzk3OTNCOWQw==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestNoticeList(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/notices" : "MjAkMTYkMzYkNzEkMzYkMzUkNTckMTkkMTkkNjAkNDAkOCQyNSQxMyQ0NiQ0NCQ=$RTg4MDE1MRDMxROTY5RNkLOFBECMjQ4NzEzQjczNCzSPHEYL3QzhBNUJDODhGM0ZGZINEFCMTdCQUNEQUNGGM0YzODA2MjRFRDBFQw==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestRecentStayList(Object tag, String stayParams, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales" : "NzEkOSQ1MyQ1MiQ2OCQ3MyQ3MSQ4MCQ4MCQ4OSQ3MiQ3NiQyJDUwJDM1JDEwJA==$ODWg1NUYzOPWTg1ODczQzU2ODM0N0M5RDVDNDDRBNTNCMjAzOTVEQNDYUyPRDAxNjc2QkI4RPDBGQDNVPjkM1RJMUE0RTYzNNTdCQg==$";

        String params;

        if (Util.isTextEmpty(stayParams) == false)
        {
            params = "?" + stayParams;
        } else
        {
            return;
        }

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestRecentGourmetList(Object tag, String gourmetParams, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/sales" : "NjYkNjMkMzEkNzYkMzckODEkODUkNCQ2NyQ5NiQ2MSQxMyQ0MSQ0MCQ5MSQ2MCQ=$N0M0VNTRCQUIxYMDIzRDdEQTJBODI3QjZFCOEE4NEQVTdBMUVDOUM3QzlDOTRg1MzLBERDYEOzRTKNBQzk2QYUFBIMDAMGwRjNBQw=U=$";

        String params;

        if (Util.isTextEmpty(gourmetParams) == false)
        {
            params = "?" + gourmetParams;
        } else
        {
            return;
        }

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestReceiptByEmail(Object tag, String placeType, String reservationIdx, String email, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/reservations/{kind}/{reservationIdx}/receipts" : "ODQkOTgkMTY5JDckNTYkNjkkNTkkOTUkNjQkMTczJDEyOCQ3NiQ0NCQ0NCQ3OSQxMTEk$OUIxOEUQwMUJFREVGQkMwOENEOEE5MkY5REY2NjE0QTcFC2REU0RDk2MzEVyOZURCMXDQ1Q0UOzRUEPYzOENFMzZGREMwNQUEwQUHFDNkVDQRzIQxRjFBNzJGQUQwQTJCMDg5NOEU2NTc1QzgyRkE1QkFCQ0E0NzQ0ODlBRTYzN0ZGMjhFMYzFMDMDQ=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{kind}", placeType);
        urlParams.put("{reservationIdx}", reservationIdx);

        String params = String.format("?emails=%s", URLEncoder.encode(email));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, urlParams, params, listener);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestWishListCount(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes" : "MzgkMTUkMzIkMTgkNDAkNDEkMzMkMjkkNDMkMTQkMjIkMTkkNDUkNTAkMjgkMzIk$NTBFODQwNDgzNTPAWzNFF0IWxQUIL2OEMER4NzBCWCMjMyNIALIkOEVCNDU=$";
        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestWishList(Object tag, Constants.PlaceType placeType, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes/{type}" : "NiQzMSQ5JDM4JDkxJDQzJDg5JDc1JDQ3JDc1JDEkNTUkNjAkNTUkNDMkMjYk$MJjgzRDYY1IMzVDNTNERDQ3QzYA5RTEzQ0DI0NURYDM0RQRyRkSU4NzUxGWMDQzRMTU0OTYwRkVBRTQyQDTWUzODM3OURGNUYwIQg=F=$";
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel");

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, urlParams, "", listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestAddWishList(Object tag, Constants.PlaceType placeType, int placeIndex, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes/{type}/add/{itemIdx}" : "MTIkMTI3JDM5JDk3JDExOSQzOSQ3MSQxMDAkMTI3JDc4JDk1JDU3JDEwMCQxMzEkOTMkMTA5JA==$MjgzRDY1MzVDYNTNERDQ3QzY5RTEzQ0I0NURDM0RIQyRkVFRTJEN0I0MjWA5MkRCNDcxMUU1IRDNGMUDZDRUQyOTMxMTUR1RDJdFMRUNYWEQjAA3OURCMTgxMjVFNjYzFOTFGGSNkMzMUDI1$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel");
        urlParams.put("{itemIdx}", Integer.toString(placeIndex));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, urlParams, "", listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestRemoveWishList(Object tag, Constants.PlaceType placeType, int placeIndex, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes/{type}/remove/{itemIdx}" : "MjEkMzgkNzQkMTE4JDYwJDQyJDQ2JDExMyQxMDEkMzQkMTM1JDExNSQxMzMkOTgkNDAkODgk$ODFCMzNDMDREN0VCNUFDOXEVEMjA3RjAzMWDc4RJMjhGFQkRVBN0M5MTA4M0REMjCNDMjkyMDVEMkEzUQkYxMjcwQOEM2M0Q5MDdGBOEMO2MUU2QTM1RTFWDFMjZENjAM1NTg2NzLAyNVkVD$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel");
        urlParams.put("{itemIdx}", Integer.toString(placeIndex));

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, urlParams, "", listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);
        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestPolicyRefund(Object tag, int placeIndex, int ticketIndex, String dateCheckIn, String transactionType, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/policy_refund" : "NjYkMzYkMzIkNTgkMjEkMjQkNDEkODgkNTQkNTgkNzckNDEkNTAkMzUkNzUkMzEk$RjFBOTM0MjJFODlCNkJFRTTlVCRTIxQBTE3RYMUZCCOGCUJDQTYwQNTdEMPkE4JRDUyRNkYyOTU1ZNUYYxNMjM2RTlBMDQxNOTI0Qg==$";
        String params;

        if (Util.isTextEmpty(transactionType) == true)
        {
            params = String.format("?hotelIdx=%d&roomIdx=%d&dateCheckIn=%s", placeIndex, ticketIndex, URLEncoder.encode(dateCheckIn));
        } else
        {
            params = String.format("?hotelIdx=%d&roomIdx=%d&dateCheckIn=%s&transactionType=%s", placeIndex, ticketIndex, URLEncoder.encode(dateCheckIn), transactionType);
        }

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestPolicyRefund(Object tag, int hotelReservationIdx, String transactionType, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/hotel/policy_refund" : "MTAkMTEzJDkxJDkxJDYwJDckNiQ3MCQxJDcyJDIxJDQ4JDEyNyQxMDYkMzIkNDEk$OMEZGRELUMyRjLlGMTBCNAkUzNTJFRTQTyRUYxMDAT2ODQyOUMZ2QTIzNDkxN0NDRTBPGMzNCOUCSNCOEUxNTRCMUE2RDE3N0VGODXQBGMEIS5OTBGNTM0NUJEM0U1GMTQV0RTc2Njk4RjM3$";

        String params = String.format("?hotelReservationIdx=%d&transactionType=%s", hotelReservationIdx, transactionType);

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, params, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestRefund(Object tag, int hotelIdx, String dateCheckIn, String transactionType, int hotelReservationIdx//
        , String reasonCancel, String accountHolder, String bankAccount, String bankCode, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/refund" : "MTQkNzUkNDMkODUkMzkkNzMkMTQkNjIkMzMkMTYkODIkODkkNzAkMzIkMjkkNDMk$Nzg0NTMxOUNGOUOMFE1RDE2NjRFRTRM1OOThXBOUQ2RNDIhBQ0KEyRjIxMjcwNTMyOTANFQ0ME3RDNBNzFE3MzHRQzNTIC0NjEQ3OA==$";

        Map<String, String> params = new HashMap<>();
        params.put("hotelIdx", Integer.toString(hotelIdx));
        params.put("dateCheckIn", dateCheckIn);
        params.put("transactionType", transactionType);
        params.put("hotelReservationIdx", Integer.toString(hotelReservationIdx));
        params.put("reasonRefund", reasonCancel);

        if (Util.isTextEmpty(accountHolder) == false)
        {
            params.put("accountHolder", accountHolder);
        }

        if (Util.isTextEmpty(bankAccount) == false)
        {
            params.put("bankAccount", bankAccount);
        }

        if (Util.isTextEmpty(bankCode) == false)
        {
            params.put("bankCode", bankCode);
        }

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.POST, URL_DAILYHOTEL_SERVER + URL, params, listener);

        mQueue.add(dailyHotelJsonRequest);
    }

    @Override
    public void requestBankList(Object tag, DailyHotelJsonResponseListener listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/bank" : "NjEkMTIkNzkkNDMkMTgkNjMkMzkkMzgkNDgkMjgkMzMkNjEkNzckNjAkNjEkMjQk$RDlCRUJDNUY0ROUM5MODA1MjDY5REHQzNTAE4RTM2CNWEZGRkUYR4NzQ4MUQxMBRKDY2RDkwQNG0RFNETE2MkU0OTNENFTY3Nzc0Mg==$";

        DailyHotelJsonRequest dailyHotelJsonRequest = new DailyHotelJsonRequest(tag, Request.Method.GET, URL_DAILYHOTEL_SERVER + URL, listener);
        dailyHotelJsonRequest.setIsUsedAccept(true);

        mQueue.add(dailyHotelJsonRequest);
    }
}
