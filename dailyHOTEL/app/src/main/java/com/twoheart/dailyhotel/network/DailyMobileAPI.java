package com.twoheart.dailyhotel.network;

import android.content.Context;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.repository.remote.model.RecentlyPlacesData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.factory.TagCancellableCallAdapterFactory.ExecutorCallbackCall;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetWishItem;
import com.twoheart.dailyhotel.network.model.Holiday;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.PlaceReviews;
import com.twoheart.dailyhotel.network.model.PlaceWishItems;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.network.model.Status;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayWishItem;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.network.model.TrueVRParams;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyMobileAPI
{
    // DailyHOTEL Reservation Controller WebAPI URL
    // api/hotel/v1/payment/session/common
    private String URL_WEBAPI_HOTEL_V1_PAYMENT_SESSION_COMMON = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/session/common" : "ODUkNDMkOCQxMDgkNDYkMjckNjEkOTYkMzEkNDckNTIkMTMkODUkOTEkNzkkMzUk$MkNCQ0MyQQjYzMN0U3OTlEQkREMjPU5MHThYDMEE1NjM2QzgKM2SRkZI1MkI3OTNGNJEExQzBEMkIzMzYBEQkY4SOTU2QJjk3NKjhFQTCM4MEQwRjg1RjFBOTDQ3MEZCNTJBOTAwRjI0ODZC$";

    // api/fnb/payment/session/common
    private String URL_WEBAPI_FNB_PAYMENT_SESSION_COMMON = Constants.UNENCRYPTED_URL ? "api/fnb/payment/session/common" : "MjEkNzgkMjUkMzgkNzgkMTkkNDYkNjAkMyQ1MyQzMCQ1MSQxMSQ3OSQ5NyQ5OSQ=$OEER1OEYxMjElEMzhFNTZHCOVEIwMNzJVCQjU4MTI4ZNkY2NDVIxTMjZNDNUJCMTlEGRDczNzNCREM1SOEQ0MDFUBNNkFDRjUE2DMA==$";

    // Register Credit Card URL
    private String URL_REGISTER_CREDIT_CARD = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/register" : "NTYkNjckNjkkMzQkOTMkNjQkMTI3JDgxJDkzJDExMCQxMTQkODgkMTIwJDgkNDQkNjUk$RjQ4MjE3LNTFBODVCQzVEQTExQTc2QTMwRDNMxRDYxOUOQyRTdCMjU4MkFGMOEZEOBDJBFNUNFYBMzM2RUY1DREU1NzGZGQUNOFMAjdBQkRDMUUyNDPY1MVjU0NWDhCNkFFQUM2OREY2QkU5$";

    private static DailyMobileAPI mInstance;
    private DailyMobileService mDailyMobileService;
    private Context mContext;

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
        mContext = context;

        mDailyMobileService = RetrofitHttpClient.getInstance().getService();
    }

    public void cancelAll(Context context, final String tag)
    {
        if (DailyTextUtils.isTextEmpty(tag) == true)
        {
            return;
        }

        RetrofitHttpClient.getInstance().cancelAll(tag);
    }

    public void requestStatusServer(String tag, Object listener)
    {
        final String URL;

        if (Constants.DEBUG == true)
        {
            URL = Constants.UNENCRYPTED_URL ? "https://dev-status.dailyhotel.kr"//
                : "MyQyMCQ5MSQxMTMkMTAwJDM1JDE3JDY5JDEyMCQxMDkkMTI4JDEyNyQyMCQxNCQxMjQkMiQ=$MTPEYxOTU1NTBBOFTQ5YREYFZCNzlCMEVENjNFNLUUwREY4N0IzRkFDN0M0NjIxRTFGNDk5MR0M2M0QzNjU0MUEyMDlFRDdDQOkYzQTAyMUkNDNjRMzMkJEOEHNBYBODgxRGDKg4RkMzRjc0$";
        } else
        {
            URL = Constants.UNENCRYPTED_URL ? "https://prod-status.dailyhotel.kr"//
                : "NTQkMTEzJDU4JDc2JDExMiQxMiQzNyQxMzMkMTIkMzUkNDAkMTM3JDExMiQxMjkkMyQ0NyQ=$M0QF2MDM1NjIzGARUYwNEQ0RjczNjQ2OTRGNMTA1GHMDc5RJjNBOEJCOTYxNjLNDOEDgzQjZFNDQxQkI3NDKJEODQyNUU5RTNDQTEwOTZGRkEwNEIwCN0JDMLDc1ANTgzRDKk1MkRCRTUSI3$";
        }

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStatusServer(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<Status>>) listener);
    }

    public void requestHappyTalkCategory(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "https://customer.happytalk.io/public_v1/chat_v4/get_category/?site_id="//
            : "MTg0JDE0NSQxNzYkNTIkMTU0JDg2JDE1MSQxNjckMTI5JDIwMSQ0NyQ5NCQ5MyQxNzgkMTQ3JDk1JA==$QzhGNjA2QTU5QkU1RkVFNUFFMkY5RkYxMzgxMzlCMjg3NDhQGMTEyLQjUxMkMyRTIzN0U4NTc2NTU1RTk3RkU5MQTI5MzCUNCzODFBOEVCNUVGQ0MwQTUxRDUyRDc0NDk2NzcY4MTREQTczMkJENLkU4QEkEyKQUVFZMTNEMUU3RkKNEN0FBPQjdGMUZQFNjFGQ0U2BQjYxRDFGFMjNCQkE2RjU4N0UwQTA1Qw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestHappyTalkCategory(Crypto.getUrlDecoderEx(URL) + HappyTalkCategoryDialog.SITE_ID);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestCommonVersion(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/version"//
            : "NDIkMzAkNTQkNTgkNjckODAkNjIkOTAkODckMzckMyQ2NCQ1NSQyOSQyMSQyMiQ=$NTJCFOURFNjU4QjdDMzRCDSMEY4OUYyMNTOk5NjdCIMTQ1NjRg2QjE4MUQLxHQUZJFRTXPIzM0VUEQkNGRDlBREVZEQTgxFNjIMyRg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCommonVersion(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestCommonDateTime(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/datetime"//
            : "NzgkMTUkMjckNTUkNjEkNjckNjckNDUkMTAkMjYkMTckMTkkNjckNTQkNjgkNDYk$ODE1MDI0NzMZGREQAJ0IMDFBNkIzTNTLUwNDAzMzY3MzQ0IMzZXEMkUT0RTZEMNkZGRUDAJE4MTkDSxOTVEMjBBQjRFQzMVDN0VDOA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCommonDateTimeRefactoring(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<TodayDateTime>>) listener);
    }

    public void requestUserProfile(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/profile"//
            : "NzMkNTEkMzYkNTkkNzckNjQkMTQkMjkkNTIkNTkkODckOSQ5NyQ5JDg5JDEk$MRUY4NUFGMRYjU0MjNI0Q0YyNjYyMjdCKMEQ5M0U5MMEY5NDQyQjcwNFTEC5NTKRCQS0ZFNPEU3RjFCOEMwMWOURDQJHjBEQTI4NRQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserProfile(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestUserBonus(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "user/session/bonus/all"//
            : "MjIkMSQ4MiQ2NCQ4NiQ0NCQ3OCQ5JDgzJDQyJDYwJDAkNDkkOTckNjkkMzkk$YNUkE4ODVEQRkIxMDVBQjRDMUIFFNjQ5NDA3NkUT5RUMF0RTKVCGMTEzMUM4NDMPwOEUwNETEc4QkE1NUVGMTCIyQBTQ2RODGU2XQQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserBonus(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestUserInformationUpdate(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/users/profile"//
            : "MzEkNTAkNzMkMzAkMzgkNDQkMTckMzIkNjMkOTIkNjAkNzMkOCQ1MiQ1JDM3JA==$RTZGMNDc1TMjhGQTA2QXzM3MTQ3MzY1OTTPVJMFNjJBXOUVGQXTY5NJjg2MUzg5NCDQ3WNDdGQUFFCRjdDOEVODODQ5MTk5MjcO0OA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserInformationUpdate(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestUserProfileBenefit(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/profile/benefit"//
            : "NDUkODAkMjkkMjEkMzMkMzMkMzEkODgkMzgkNzUkOTMkNzgkMjMkOTYkNTQkODck$N0M1N0ZCQzE4ODgxQ0Y2QWTTEzMjBCOCCTRBYDRTDE4RDhGMDkyMXzLY0NjcxNDM3NEVCMDE2QTc3RRjPdDREZFWODUT2RBjACG5Rg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserProfileBenefit(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestUserChangePassword(String tag, String email, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/users/password/email"//
            : "MjgkODgkNzIkMTckOCQ1JDc1JDU0JDYwJDYkNDkkNDYkNDUkNyQ5NSQ2NyQ=$RDE2MLRNjFBRMTM2QjZDMJjk2MDQ1QzVBSOUE0NzJCMjM0BOXUVEPRjMwNTTA3NzgD3VMTMyOTU1N0E0Q0ILN4OUM5QzY2REYI4NQ=W=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserChangePassword(Crypto.getUrlDecoderEx(URL), email);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<Object>>) listener);
    }

    public void requestUserBillingCardList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/info"//
            : "NDIkOCQ1NSQ4NyQ4NyQ4MCQxMzIkOTIkMTMwJDU2JDE2JDQyJDY4JDU5JDEzMCQ3MyQ=$QzdFNkE5NNjgzM0JIFMjZFRjlCQjY4OEQ3NkI5NDdDKRjUMxNDkzNTk1MTBWjkzQkE5NELNDNQ0RFOENGRDAwMGEE5MTE3UARDYFGQjEzMzMxRjVDMDA2MjVEQzBGMTgxREYJGNDMK3MTGI2$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserBillingCardList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestUserDeleteBillingCard(String tag, String billkey, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/del"//
            : "NTkkODYkOTMkMTEyJDMyJDI3JDMwJDYzJDI1JDEwNiQzOSQyOCQxMzYkODgkMzUkMTgk$MTBDN0VBRjZFRkE4ODAA2OEYyNKDcQS2NSkZNESMEVRBNjhDNzczMjE1OUM3QjIxMzYVPzRjk1RDQ1QzgzMDlENzI3QNjIxQDTU2NkFOBMzhCOTBg3RjkzRDQ3RITg5RDE0Q0QxNjgyUNURF$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserDeleteBillingCard(Crypto.getUrlDecoderEx(URL), billkey);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayRegionList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/region"//
            : "MjMkNjQkMjEkMCQ2MCQ1MiQ0NCQzMiQzMSQyMiQ3MSQ4NiQ2OCQxMyQ0NyQ2OCQ=$PRUM3NTRGQzA5RMEVBMjZFNPQEEN0MTgzYMVzcyQ0VERDUzOOJDQyRTQ1NYzkxNkM0MBNEUG1RUTFOGMDExRDVEMEMExRTEwMDExNw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayRegionList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayPaymentInformation(String tag, int roomIndex, String date, int nights, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/detail"//
            : "NzckMjIkMTIkNzkkMjYkODEkNTAkNTEkNjUkMzUkOCQ4MyQzMSQyNSQxNCQ5NCQ=$NTZEMEQ4EQ0EyIRMzY0N0IwQzOMYxZMjICwNTQzQMEI5QTVFOEE3RDEIB1MjRFQzZBNzgxVM0ExQzFGODc0NUER4OKNFThOGMzJBNA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayPaymentInformation(Crypto.getUrlDecoderEx(URL), roomIndex, date, nights);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayDetailInformation(String tag, int index, String date, int nights, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}"//
            : "NjMkNjckODgkNTMkNTckNjMkNCQ2JDM3JDc3JDI4JDQ5JDY2JDI0JDQyJDM2JA==$NjRDWNS0ExQUY4NjNGMzM4QkXRFNDZlDM0Y3KRDIK5RIUUxOUY1RWjNBMUY5MPEI0OMjkG0NPDNIDREPJERXjNCNDcxMTlGMUQ2OQB==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayDetailInformation(Crypto.getUrlDecoderEx(URL, urlParams), date, nights);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<StayDetailParams>>) listener);
    }

    public void requestStayPayment(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/session/easy"//
            : "NjMkMjMkOTAkMTMwJDY3JDUwJDI3JDY2JDUwJDk2JDMkMjckMzAkOTIkNzAkMzQk$RkEL4QkUwQzY3RjA0MUI0M0MJyRKTFXI3OTDQ5N0RGREM4MEU2RjdBAMMTg5MDE4NEI0RTZJAQCQGTlFOTA2REU3N0RBMDYlCRTA1VYRDc2Qjg3RTY4NzE1MjM5RjQ5RkFDQTZDRDE0MzMJz$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayPayment(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestBookingList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/booking/list"//
            : "OCQ5OCQ4NCQ3NyQ4NiQxMjkkNzMkMTIxJDcxJDExOCQ5NyQ2MCQ2MyQyNSQzNSQyNSQ=$RjYxMjU1INzFDQ0ZCRTRBMzRFCLMzY0QTQ0ONTQ3QURENDMyQjM0NDczQzczOURYFNGTQ2RTNGRTPQ5XNzNDPNzNDNkQWD4NzA0M0YBzNkRFNODVFRUU4RDQ1Q0IOyRDcZ2QzkwNTlDFN0M2$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestBookingList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetReservationDetail(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/fnb/{fnbReservationIdx}"//
            : "ODQkNDQkNDckODEkMTIyJDE0JDQ2JDAkMTckMjQkNzEkMzEkMTAzJDQxJDEyNiQxMTYk$NNzVFQUVFRDlBRTYkO0NzQxOWDJGNjAZ4MEI2RDBBTREZCNURGLZQzUYyQkNBQjJGMDU5RDJERQjAwMkFDODE0OTIOyNzMxTOEUwQjNEQREVDNTAyMzIJ1RUI0Q0U0OTDBDOEXY2RTIzRkQ2$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{fnbReservationIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetReservationDetail(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetHiddenBooking(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/hidden"//
            : "MTEzJDQ4JDM1JDEzJDI4JDY5JDkzJDcxJDEzMSQ5NiQxNSQxMzckMTIwJDUzJDU4JDk0JA==$N0U1RjY2MjIzQPzRkyOEVEQzQ0RkED4Mjg4RDEW5RTM3MzkwRTZGZQQTBEBMTczQzNDQUMwNFUJIxQkE1NkZGOUJGODY3QRzBFMNS0RCMkNFNDgxRERCNTZDQ0EX1NFEI3RjNBQzYE4QzOM2$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetHiddenBooking(Crypto.getUrlDecoderEx(URL), index);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetAccountInformation(String tag, String tid, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/vbank/account/info"//
            : "MjUkNzckMzkkODQkNzQkMTA4JDExMSQxMDUkODMkMyQ1MCQ0NyQxMDQkNDckNTUkMzYk$OUUSxNjYxRDI3OUQ0OEY2MTE4MLzU1RkFCNETYxNzVk2QkNDPHOTJTFQVzU3N0I0NjIyMzI3MDYxQzAzIQTI0UMDQH5MMzZENDI1RTEyODkX4N0IyXMDUX1RAUQzOUUyN0RCQUNGRDU2NTJB$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetAccountInformation(Crypto.getUrlDecoderEx(URL), tid);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetRegionList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/gourmet/region"//
            : "MTckNjIkMjUkNjAkNTIkNzEkNjckMzEkNjgkMiQ0MSQ4MyQ2OSQ0MiQyNyQ3NyQ=$REVYwRDJCM0NGRTNERZjJFQzMxXZRTI1MRzg1NzJENIIjlDMUFFM0ZDQkIM1RjJBNkAY0QPEzZXRGGQzZU4QUFDHOENGOTE2NDk3Mg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetRegionList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetList(String tag, Map<String, Object> queryMap, List<String> categoryList, List<String> timeList, List<String> luxuryList, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/sales"//
            : "NjYkNjMkMzEkNzYkMzckODEkODUkNCQ2NyQ5NiQ2MSQxMyQ0MSQ0MCQ5MSQ2MCQ=$N0M0VNTRCQUIxYMDIzRDdEQTJBODI3QjZFCOEE4NEQVTdBMUVDOUM3QzlDOTRg1MzLBERDYEOzRTKNBQzk2QYUFBIMDAMGwRjNBQw=U=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetList(Crypto.getUrlDecoderEx(URL), queryMap, categoryList, timeList, luxuryList);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetDetailInformation(String tag, int index, String date, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}"//
            : "MTckNjMkNTQkNyQ2NSQ1MyQyNSQyMiQ0JDk0JDYxJDM1JDgkNjckMzckMTAxJA==$QkJCSMjBPVENkQ3RTU4MTjkyDOTQVyODZBQjSZFBNTMwMDI3MDQ5N0RDMDYGxRFDFg4NZEI4NTDXM2OUNGRUQ0QTY4N0MyNjVEMRgJ==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetDetailInformation(Crypto.getUrlDecoderEx(URL, urlParams), date);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<GourmetDetailParams>>) listener);
    }

    public void requestGourmetPaymentInformation(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/ticket/payment/info"//
            : "MzkkOTYkOTIkMTI5JDEwOSQzNyQxMjgkMTA3JDQ3JDY0JDMxJDEzMyQxNiQwJDk0JDEzOCQ=$KMzM4MzgyRkFFOTFBINUZCRkQxRjA5MjIEyRkFEOODYDwMkMwMKjk1RjkxRjNDMDM1MJTc1QjZCMjJCREFEQzk3NDdGMkUCzMDgVzNDZEGRkE1QThWDNkQM1MzNBRjEyMjQwQUUYH1DNkIQw$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetPaymentInformation(Crypto.getUrlDecoderEx(URL), index);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetCheckTicket(String tag, int index, String day, int count, String time, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/sale/session/ticket/sell/check"//
            : "MTAyJDUzJDEyMiQ0JDI5JDExNiQ5MSQ2OCQ3OSQxMTIkODEkNjckMTA2JDExNSQxMjYkOTIk$OTAwONUU3MDE1MzkxMUI4NDJEMjRFMOTUxQTIyMUUwOUFDQ0M5NTU5QBjBEMEI4OUMzMOZDY0NjdFQjFYBUNzlEODUxNEDRCEMEMyRDFGRENJGRULI3NXSzhFODQ1NQDzMxRUI1BMzM1MzAy$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetCheckTicket(Crypto.getUrlDecoderEx(URL), index, day, count, time);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetPayment(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/payment/session/easy"//
            : "NzgkMzMkMzYkMzAkNjIkNzMkNSQzOSQxMyQ1MyQyNiQyMiQ0NiQzNSQ2OCQ4NiQ=$NjA0NKzIxMDNETRkUwMTNBNMjAzZM0E2NDQNhGNPjcME3NDKk3MkVEQkIP5QzA2NjYxRIjNADRTg4RUU0RATIzTNTc3MzCMwNjg5Qw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetPayment(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

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

    public void requestStayReservationDetail(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/hotel/{hotelReservationIdx}"//
            : "MTI3JDExMyQxMTckMCQ4NiQ4JDE1JDU4JDExOSQxMzAkMzUkMTkkNDUkNDEkMTIwJDk3JA==$FMjIxNzUN4MzVDRPUFECOTBDNjg0MjdGQUU0ERDBDFQkRCENEVEREVDQ0ZFRDUWzN0MwRDhDMDFGNDdDRTUxODQzMDQ3OHTdDWMkQ1NDE2REIxNjJDMkJCQTgX3ONJEQyPOEYzNDJE2QkQF4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelReservationIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayReservationDetail(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayHiddenBooking(String tag, int index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/mine/hidden"//
            : "MSQ3NiQzJDQ5JDcwJDEzJDE2JDI0JDg4JDY3JDU2JDkyJDE4JDc4JDY3JDI3JA==$MBkWE0QTNGNjIFzRKTAE2RkE5TQBTEzNEJCNTA1QjVDNDY4NEZGRUQL1N0NNDM0RFN0UZ1RNUYzNEYM1YQ0RCMjBPFNDJGYRNjc4MA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayHiddenBooking(Crypto.getUrlDecoderEx(URL), index);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayReceipt(String tag, String index, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/receipt"//
            : "NTIkMzckNDYkNDQkNjEkMzIkNzMkNDQkMjUkODQkODMkMiQ2NiQyNCQ4NyQ3MSQ=$ODPIyNDhGMzU1QTQzNzRBQjgR1QD0E2MTJBLNzRFRWDVDRkNQY0NRTEzRTk1PNzY3OTODUzZRDU1RjlTEMzUxRDCFFSGMzNDNzM0QQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayReceipt(Crypto.getUrlDecoderEx(URL), index);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestEventList(String tag, String store, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/events/history"//
            : "MTAkMjgkODUkMjckNDIkNjckODkkODQkODYkNyQ5NiQ2NSQ0MSQ0OCQyMyQ1NSQ=$RjY1NTdDCNkTIwOTc2QUQyMAjFGRTTEC5NUI0RDJDNIkQT1REAU3MEVFCRkRDQUNFREUyERDJUDMTMwRkY0QjgzNTQMzZNTFQYDRAS==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestEventList(Crypto.getUrlDecoderEx(URL), store);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseListDto<Event>>) listener);
    }

    public void requestEventNCouponNNoticeNewCount(String tag, String eventLatestDate, String couponLatestDate, String noticeLatestDate, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/new"//
            : "NzAkODAkNzYkMjMkMzckMyQ4MyQ2NCQyNyQ1MiQ0JDU3JDcxJDI1JDMyJDYyJA==$RURSICQTU3RkZGMEUwNUZCOEEPP1RFDQY4MTlERUQyXMDI2QzgwNEQ1FMzIPwQGzYzNjFGRLDdUGQTdDM0KU3NjUB5MzEVEDOERGMw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestEventNCouponNNoticeNewCount(Crypto.getUrlDecoderEx(URL), eventLatestDate, couponLatestDate, noticeLatestDate);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestDailyUserVerification(String tag, String phone, boolean force, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/myself/phones/verification/start"//
            : "MTMkNDckODYkNTYkNTQkMTI4JDk0JDU4JDEwMiQxMTQkMTA4JDY4JDE5JDQ4JDcxJDU1JA==$NDkxNUNFQjlENREUwNEERBMDJCMjJGRjdDODI3MDUyRkZFRTUCdEOTMAxHNzTZNEQzg4REQRS2RTY4QTU0M0RDMzI4QkJEINzgyOLTdDQzUX4QzJDRODk4NTXg3RkQyQjY2MEY3QzNYCNDhG$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDailyUserVerification(Crypto.getUrlDecoderEx(URL), phone, force);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestDailyUserUpdatePhoneNumber(String tag, String phone, String code, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/myself/phones/verification/check"//
            : "OTYkNTMkMTE0JDEwMCQyMyQyOCQ3MSQ5OCQ4NSQzNyQ2MyQxMzIkODYkMTAkNTQkODMk$RkMyQkVGODTJEN0NENzAzQUIAyNDkA5NTM4ODUT2REU4NjY1REU0MjTI0RGjM5RjRDERTJFQzcwMRTQxMDIZ4MjU2ZNUjE3MzdFRUJFNDWdFGRDIMxRThERTg4QTAyPRkY5NDVFMCjVEQUU5$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDailyUserUpdatePhoneNumber(Crypto.getUrlDecoderEx(URL), phone, code);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestSignupValidation(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/users/signup/normal/validation"//
            : "MzQkNjQkMTEyJDEyJDYyJDYyJDI1JDE2JDM4JDIxJDEwMyQxMDUkMiQxMiQ5NSQyOSQ=$QjBQyQzlFRTgN2TNzgX4ODAJwMkRBOGODAwMEE2QTZZFCRDNBRjQzODkxMEE0NjFDMEZBPDNTEB3NTkxMzAzNjhENTU2OEZBPNTYxRkVENjHMG4MkVBNDQ1OUVGRjTBGNkUyMUIzMTZDNDg1$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestSignupValidation(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestDailyUserSignupVerfication(String tag, String signupKey, String phone, boolean force, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/normal/phones/verification/start"//
            : "MTU4JDc5JDE1NCQ0MCQ1MiQxMDAkMTQzJDQ5JDkyJDE1NSQxNjUkMTM4JDE0MyQ4MSQ3MSQ5OCQ=$ODJDQkRDNDFGMjZDMkEzQUMzNTNFQzJGREY1NzA2JNkQ3RjcwZNDkC1MDAwNTNCNUE5QjAwLQjI4MDZBQzGdMDQUM0OTdBAQzIX4RkQ1RVTg2OEFEMTM1QTIxMkU2RUU5RTQxNzgwOEVDKNDFEIRjBQDMDEzQTE5NQjIyQWjBFVNUPFCODI5MDQxNEI=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDailyUserSignupVerification(Crypto.getUrlDecoderEx(URL), signupKey, phone, force);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestDailyUserSignup(String tag, String signupKey, String code, String phone, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/normal/phones/verification/check"//
            : "MjEkMCQxNyQ1NiQxMTAkMzIkNyQxMzEkMTQ1JDQ1JDE0MiQ4MCQ0NSQ0OCQxNzMkMTc0JA==$BQzZCQ0IJCQzg1RTQ3QNEFBMXTA1QTQ2QGkNDMzlFQjQyTHMNThGMEYyMDc3MI0UwMDlCNTc0NDFEMUNBMTkI1QTBFNTgxOUUxOTI2ODY0Q0VFQTRDQjRU4NzFCNkQyMENENEMxAMzE4ODVERYEYxOSDhBNjNBMEYwODFEMzNDRUULSwOEEzNjk5MzA=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDailyUserSignup(Crypto.getUrlDecoderEx(URL), signupKey, code, phone);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestFacebookUserSignup(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/facebook" : "ODAkNDQkNTckNDgkNTAkODUkMjQkMTIkNDMkMTUkNDckNyQzOSQ1OCQyMCQ1MSQ=$NTAyNUFCDRDEwQRDCc2MWzE1MjQzUNzZDMTI0MTkKwQjFCNHzBRSDIM0MPzZKMjA5RDU1EQTYyMUI0Q0Y3MUVFRDNCOUNERNODIzNQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserSignup(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestKakaoUserSignup(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signup/kakao_talk"//
            : "MzQkMTIkNjUkNDQkMjAkODMkNzckMjQkNzUkNjQkMzckNyQzMSQ4OCQ0OCQ5OSQ=$RTdDQ0ZRCMzg1ENTZBQUJRDNjPVGQzUFyQUE1RjXSg2ODI4RITMc2OTU1ODg4RjhGNjYS3RUUHzREU1MYDcxYMDNBBNzFNFMTk4JMA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserSignup(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestDailyUserLogin(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/normal"//
            : "MCQ3NSQyJDgxJDg1JDYkNzckMjIkNTkkODAkNzkkNzkkNzkkODUkNTIkMzgk$GMI0Q2DOEZDQTM2QzBBQTlNDM0JCQTk4MzgwOEMYyQUREOUFCMzEzSOTcxMEET0Q0RFMDdFMDQ1NTk4ODRAAYTJClEMjXU4QCjIwNw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserLogin(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestFacebookUserLogin(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/facebook"//
            : "NDQkNzckMjgkMzIkNjIkNTIkNTkkNDckNzkkNzckNTMkNzMkNTckODckNDkkMzAk$MTk3OTE5MkU4MDE5NjQyNTY1RUQ3TRLjVVBRkNDNkM0NDdCLAMTTFBQKRzhTENzZNEM0UEwNEQyNVjAzOTHAzSODNJFNMzRFNDg4MQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserLogin(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestKakaoUserLogin(String tag, Map<String, String> params, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/signin/kakao_talk" : "MzkkMjUkNDkkODAkNjQkNjckNTIkNTkkNzQkMiQ3NCQ0MyQzNyQyNCQzNiQyJA==$NkTCNGODU5RkI4Q0RCNzA1ODVYFMOTE4N0JEMC0MJ3NjBECGQkI2QTIMyRGTcxMDZZERDU2OJUYF0Q0WMKyN0E2NDcxNRzU5RTM4MQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserLogin(Crypto.getUrlDecoderEx(URL), params);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    /**
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    public void requestCouponList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons"//
            : "NTUkNzUkNDQkMTIkMCQxNyQ4MiQ4NCQzNyQ0MCQ0OCQxNyQyNSQzMSQ2JDEwMiQ=$XMzE5MMTc3MTVEJQjBUSCQkM1QLUQ5NkWY5MUYzQUEQwWRDA5N0UA0MQUU0RDNDM0RXFRTE3RUJBRkY1RUM3NTUZGCNKzk5QTRDRA=B=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    /**
     * 결제화면에서 사용되는 자신이 소유한 Coupon List
     *
     * @param tag
     * @param hotelIdx
     * @param roomIdx
     * @param checkIn  ISO-8601
     * @param checkOut ISO-8601
     * @param listener
     */
    public void requestCouponList(String tag, int hotelIdx, int roomIdx, String checkIn, String checkOut, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/coupons"//
            : "MjkkMTckODAkNTkkNTEkMzAkMyQ0MiQ2NyQzMiQ0OCQ2NCQ1NCQ3OSQxJDUxJA==$MBDgCxMDk2MDFENTI1QUkFGRkFDREY5OFCNDhGQUQ5NzFM4NTOUC1NjAQzMMjZCRDU2JQKjNCZOTYyOTQT4NDQwRjY5RDPM2N0ZCQQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponList(Crypto.getUrlDecoderEx(URL), hotelIdx, roomIdx, checkIn, checkOut);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestCouponList(String tag, int ticketIdx, int countOfTicket, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/payment/coupons"//
            : "MTAkNDEkNTgkNDckODIkODIkNzMkODMkMSQzJDcxJDY1JDUyJDMxJDM1JDkyJA==$OWDEMzRDNFMTXU4MjM2Q0E5OTYxREU0AMUZIFMEQxNzdCNRkEzOGEUH5NTQzNUE2VNjIMzQzRBMP0QwQCjdFREVGNzPQKMgyNjY0MQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponList(Crypto.getUrlDecoderEx(URL), ticketIdx, countOfTicket);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestCouponHistoryList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/history"//
            : "NTgkMjgkMzMkNTYkNzEkNTYkNDQkODYkMzAkNTAkMjEkMTkkOSQzMCQyOSQyOCQ=$ODlCNTQ1OOTA3NjczNkJVEQCjRBQSjNFOEXOXDMyPMzM5ODE4RTOBEMEZFGRjA1RTFZg5MjXk2QTVGNUJCDMzMzMzI4ODJJCNzY0Mg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestCouponHistoryList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestNoticeAgreement(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/agreement/confirm"//
            : "NTYkNjgkMTkkNyQ4JDI4JDUyJDE4JDc5JDg1JDY0JDg0JDY5JDc5JDYxJDk0JA==$ODdCM0QIL5RDY2RTRFNQjlFGRDhBRATk5ODlCMkEyQjVBQjYxRjc2YQkRENzUEzTOVUExMAjZENzkD1NVDEE3RTNFBHQjNRCOUYyNA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestNoticeAgreement(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestNoticeAgreementResult(String tag, boolean isAgree, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/agreement/result"//
            : "NjgkOSQ4NSQ3NiQ3MCQ0NyQ1MCQzMiQ5NCQxNiQ5JDE3JDQxJDQ4JDk1JDUk$QTcxNVjkwRTOUEyOURMYBQTcyQzlBOTBCOUIS1OTg2VQ0JBREUI5NDTEwYRDA5M0RCNzAxMzM0ODIzXVNzc5NzRM3NjUxNEUODxMQQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestNoticeAgreementResult(Crypto.getUrlDecoderEx(URL), isAgree);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestBenefitMessage(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/benefit"//
            : "MTUkODEkMjIkMzMkMSQ3MiQ0MCQ1MSQ2OCQ3NSQ1NCQzNiQzNiQyOCQ3MCQxMDIk$MMTREQkE5QTVFNEQAwMTdERLTE3RMEU1MkJXBLWQjY3HNDc1OTExQ0LI1HRjI3RERBRjU3YNURU0N0VCTNQkY4Q0Q0QjBDBNENDNQ=T=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestBenefitMessage(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestDownloadCoupon(String tag, String couponCode, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/download"//
            : "MzMkNTYkMTgkODUkNTMkMzUkOTAkNTQkNyQyMyQxNiQyMCQ4MSQ2MiQ3NCQxMyQ=$QUE2NzVCFMUU5RNEFKCMjSQE3MOjUyRkFBOTVDMGCzlFN0M3QzkzM0VGMTAQO3QVzBMFMTJGMDhFDN0MxMUMHzNDc5RDY4NDLU5YNA==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDownloadCoupon(Crypto.getUrlDecoderEx(URL), couponCode);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestDownloadEventCoupon(String tag, String couponCode, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/download"//
            : "NTckMTUkMjckNzEkNDQkNyQxNCQ1JDE3JDE3JDcwJDk1JDM1JDU1JDckNDEk$NUY0MNjKEOwMDIwQZTXWlOCNjRBQUE2ODFc1SNUMwXNzY4MTA4OESIxMUQEwRUQ4OEEwQGTdGRQjMzQUFFUN0M5OEI5OTczNDVGZRg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestDownloadEventCoupon(Crypto.getUrlDecoderEx(URL), couponCode);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

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

    public void requestHasVRList(String tag, Constants.PlaceType placeType, int placeIndex, String type, Object listener)
    {
        final String URL;
        Map<String, String> urlParams = new HashMap<>();

        if (Constants.PlaceType.HOTEL == placeType)
        {
            URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}/vr-list"//
                : "NDgkMzIkNiQzNyQzNiQ3NyQ2MCQ4OSQ4NiQ4MyQ3JDQ1JDQxJDAkNjQkNTkk$JRkNGMEUXRCM0NBQ0EyMDdBRkY5QTJGOTgzTMUXYFwVNjkxBNTk3NDE1LQzPREMEQEQ3Mzc5ODc3MzJFNEIwCMDM4SQjFTEOESY4Mg==$";

            urlParams.put("{hotelIdx}", Integer.toString(placeIndex));
        } else
        {
            URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}/vr-list"//
                : "MzckNDckNzgkNTQkNzMkMTAzJDk0JDc2JDExOSQzMCQ2NiQ2NiQ5NyQxMzgkMCQ4NyQ=$IQ0ExNUQ1OTRBNUVDNkY2MjhFNkQ4OTWg0QURCRITk3RkFFNjBQzMTA0ONkFBRkYwRDHZdEODI1OTQg3IODZCBRIDE3QzgzQkYxTMSUQzOUYzMEEZGRkJCRkVFMUJHBQTk5NDQ4N0JEMNENG$";

            urlParams.put("{restaurantIdx}", Integer.toString(placeIndex));
        }

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestHasVRList(Crypto.getUrlDecoderEx(URL, urlParams), type);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseListDto<TrueVRParams>>) listener);
    }

    public void requestRegisterKeywordCoupon(String tag, String keyword, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/keyword"//
            : "NzUkMzEkMjAkOCQyJDAkNDkkNjEkMiQxOSQ5MCQ2MiQxMiQxNiQzMCQxNSQ=$NNTzJk1NTVDRKNDBdUGMzQX1RkMxFNjTU4OUY2NUEXzRUVCRTk1REUyUOEU0REY0QjHNLFRUIzQzc3RTlCMTRBQUMT0QzNFNNTNGNg==$\n";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestRegisterKeywordCoupon(Crypto.getUrlDecoderEx(URL), keyword);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestUpdateBenefitAgreement(String tag, boolean isAgree, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v1/notice/benefit"//
            : "ODckNDIkMzUkNzYkMzAkNjEkOTAkNTgkNTIkODEkNDckOSQ2MiQzMiQ3NyQ2OSQ=$NjdCRTNCQBTczOUY4RTJGMzY5RDA2NEWTRCMTkX5NDA3NEOE1HRDNBNVjc3NzMAA1NkAQRyNTgwNjBWDNEY3REIXM1NkRCREQ4MVQ=K=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUpdateBenefitAgreement(Crypto.getUrlDecoderEx(URL), isAgree);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestUserTracking(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/tracking"//
            : "MzkkMzEkNTIkNjUkNDckMzUkOTAkMTIkODEkNDEkNDEkNDckOTYkMTckNjEkMTAk$MjAxNkUyMTYk5QRDMzXQjk4RkYwOTRCMzMYwRkLRGMjHKlWBQPTdDMXDkxQkTNQBNzAzMDEyMjQgwMjg0M0VCMUNU2Qzk3OTNCOWQw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestUserTracking(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestNoticeList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/common/notices"//
            : "MjAkMTYkMzYkNzEkMzYkMzUkNTckMTkkMTkkNjAkNDAkOCQyNSQxMyQ0NiQ0NCQ=$RTg4MDE1MRDMxROTY5RNkLOFBECMjQ4NzEzQjczNCzSPHEYL3QzhBNUJDODhGM0ZGZINEFCMTdCQUNEQUNGGM0YzODA2MjRFRDBFQw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestNoticeList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

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

    public void requestWishListCount(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/wishes"//
            : "MzgkMTUkMzIkMTgkNDAkNDEkMzMkMjkkNDMkMTQkMjIkMTkkNDUkNTAkMjgkMzIk$NTBFODQwNDgzNTPAWzNFF0IWxQUIL2OEMER4NzBCWCMjMyNIALIkOEVCNDU=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestWishListCount(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayWishList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/wishes/{serviceType}"//
            : "MTEkMzEkNDQkNDkkMTAkMzkkMjQkNjAkMTIkMzkkOTckOTgkNjckNTUkMTckNTYk$NTNDNkVEQjUVIWCMTNlGN0Q1QUSFCRjBGNEHM4QUQUyMMzY0RTCVDREYYDJGNzlGQGzBCMEERCMDFFOUFDNEJFOTI3MDZCODk2MA=GN=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{serviceType}", "HOTEL");

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayWishList(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<PlaceWishItems<StayWishItem>>>) listener);
    }

    public void requestGourmetWishList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/wishes/{serviceType}"//
            : "MTEkMzEkNDQkNDkkMTAkMzkkMjQkNjAkMTIkMzkkOTckOTgkNjckNTUkMTckNTYk$NTNDNkVEQjUVIWCMTNlGN0Q1QUSFCRjBGNEHM4QUQUyMMzY0RTCVDREYYDJGNzlGQGzBCMEERCMDFFOUFDNEJFOTI3MDZCODk2MA=GN=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{serviceType}", "GOURMET");

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestGourmetWishList(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<PlaceWishItems<GourmetWishItem>>>) listener);
    }

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

    public void requestPolicyRefund(String tag, int placeIndex, int ticketIndex, String dateCheckIn, String dateCheckOut, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/policy_refund"//
            : "NjYkMzYkMzIkNTgkMjEkMjQkNDEkODgkNTQkNTgkNzckNDEkNTAkMzUkNzUkMzEk$RjFBOTM0MjJFODlCNkJFRTTlVCRTIxQBTE3RYMUZCCOGCUJDQTYwQNTdEMPkE4JRDUyRNkYyOTU1ZNUYYxNMjM2RTlBMDQxNOTI0Qg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestPolicyRefund(Crypto.getUrlDecoderEx(URL), placeIndex, ticketIndex, dateCheckIn, dateCheckOut);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestPolicyRefund(String tag, int hotelReservationIdx, String transactionType, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/reservation/hotel/policy_refund"//
            : "MTAkMTEzJDkxJDkxJDYwJDckNiQ3MCQxJDcyJDIxJDQ4JDEyNyQxMDYkMzIkNDEk$OMEZGRELUMyRjLlGMTBCNAkUzNTJFRTQTyRUYxMDAT2ODQyOUMZ2QTIzNDkxN0NDRTBPGMzNCOUCSNCOEUxNTRCMUE2RDE3N0VGODXQBGMEIS5OTBGNTM0NUJEM0U1GMTQV0RTc2Njk4RjM3$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestPolicyRefund(Crypto.getUrlDecoderEx(URL), hotelReservationIdx, transactionType);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

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

    public void requestBankList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/bank"//
            : "NjEkMTIkNzkkNDMkMTgkNjMkMzkkMzgkNDgkMjgkMzMkNjEkNzckNjAkNjEkMjQk$RDlCRUJDNUY0ROUM5MODA1MjDY5REHQzNTAE4RTM2CNWEZGRkUYR4NzQ4MUQxMBRKDY2RDkwQNG0RFNETE2MkU0OTNENFTY3Nzc0Mg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestBankList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayReviewInformation(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/hotel/question"//
            : "NzQkMTIkNDkkMiQ0OSQ4NyQ2NCQ4OSQzOCQyNSQxNyQ3NiQzMSQzNCQ0OSQxNyQ=$MUZVFMEQ4OUMxSNTUTPwNUIxQzVVDNUJXFRZTVCQjQyFMUNCODXg4RDZTBQMENBMTc0RTcxTMjUyM0Q1KRDFFMDXBGMTU2MEKNNEMg==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestReviewInformation(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestGourmetReviewInformation(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/gourmet/question"//
            : "NDUkMTIkNjUkODckNDAkNzQkNzUkNzckNjckNTYkNDMkMTYkNTQkOTkkODckMTYk$QzgxQkVEQjBGONDdTWEMDI4NENBOEMwNkIyQUIxQUQOwNITlGNBTczMIjBGMSTM1QTdCNDNOUCMTg4ODRHZAFMjRIDQzE3REI3ENQI==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestReviewInformation(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayOutboundReviewInformation(String tag, Object listener)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/reservations/reviewable-question"//
            : "MTI4JDczJDE1MiQyJDE5JDkwJDE1NCQzNCQxNzgkMzMkOTEkNzYkOTUkOTgkNSQxNjQk$RjFgyQOUNCNjlCOUQ1RkLYwNjc5MEM2RDdDGLMUREQTFDRjA4RTIzMzI2MjMwRjFEMzA0MzNBN0U4EME0NCOUJGNkIyNkGNXLEQTzFDODQxQ0QxOTNCMzAyRkRBNTc2MTg2OTkyRTY4ENjAzNzgwOEMyOEFBOEZGMETKBMxNTU5MzU1NDNBRjMzQjMc=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayOutboundReviewInformation(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API));
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

    public void requestAddReviewInformation(String tag, JSONObject jsonObject, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/add"//
            : "NiQ0OSQ2MCQ3MCQ1MiQ0MCQ1NiQzJDE5JDQyJDIyJDc1JDc1JDQyJDUkMTYk$M0NXBMMDSY5ODkwNI0E2NBzVACQzczQTVFN0JBNzNGRDGIVO3RTNGRjVBSQ0AQzJRjEyNEZEzNkIyOEVTNCEN0JFQjUyN0I4NzcwQw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestAddReviewInformation(Crypto.getUrlDecoderEx(URL), jsonObject);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayOutboundAddReviewInformation(String tag, int index, JSONObject jsonObject, Object listener)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/reservations/{reserveIdx}/review-write"//
            : "ODUkODIkOTMkMTYkODkkNTckMTEwJDEwNiQyNSQxMTUkMyQxMyQxMzUkMjIkMTgxJDE0NiQ=$N0UI1MUI3OEQ5HMTc5JMzNRDQzkwVRkYzREI3RkIyRTk5NUE2MjQwOUY2RUIxCOUYzOEVCNDMwMDdDRkRCNTVGQ0UZFQF0EVDRjUT3NjU1OTYxEQUI1JNTBU3M0IzNjAyRENFREVEGNUJGQzZCGRUIxMTdEOUUzNkQ3OEI0QzhCMDM5NDk1RjcA0QkU=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayOutboundAddReviewInformation(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams), jsonObject);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestAddReviewDetailInformation(String tag, JSONObject jsonObject, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/add/detail"//
            : "NDUkMzgkMTYkMzckOTAkMjQkNjckNjAkNjUkNjkkODkkODIkODYkNSQ3OCQzMCQ=$RjU4ME0IyMUIyMkJGSNTVCMzMF3MzBSCM0UxMTNFEODLQzNUE4MDUFERjI5NTEB3M0ISyMDOCc3NjE4FMkVDMYzg3NMjc5KQjE3NwR==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestAddReviewDetailInformation(Crypto.getUrlDecoderEx(URL), jsonObject);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayOutboundAddReviewDetailInformation(String tag, int index, JSONObject jsonObject, Object listener)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/reservations/{reserveIdx}/review-detail-write"//
            : "MiQ5MiQxNDEkMTI4JDEwMiQ2NCQzNCQ0NCQzNiQxMDckMTYkMTM5JDQ3JDI1JDEzMyQyJA==$MzQHU3QzgxMDJCNDUQyNzMzMzUKzRUY3NDVGMDkIQxMEREOEQBY2RTVFRDBBNDREQTExNkFCERjYxQjRGOERCREE4QTlFRDYwM0IT5NzZGN0RDVAM0Y0NUJGNkY0MTQ0MTE5M0FFGMzPlCOUDIwMjAyNDYW1NzQ1MUNERUFERjNEQUY4M0Q4NDM4Qjc=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayOutboundAddReviewDetailInformation(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams), jsonObject);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    /**
     * @param tag
     * @param startDay yyyy-MM-dd
     * @param endDay   yyyy-MM-dd
     * @param listener
     */
    public void requestHoliday(String tag, String startDay, String endDay, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/calendar"//
            : "MzYkMzYkMiQyMSQzMSQzNSQ0NyQzMSQzNSQyOCQyJDMkMjUkMzMkMzgkNTgk$NzAQZAyMjQ3RTkxRTI4NjE2DMIzdERTWIKzQADUjFIDONDAzAWREQ5QSTET=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestHoliday(Crypto.getUrlDecoderEx(URL), startDay, endDay, true);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseListDto<Holiday>>) listener);
    }

    public void requestHomeEvents(String tag, String store, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/events"//
            : "NTQkODckNzQkMiQzNCQxNiQzOCQ0OSQ5MyQ3MiQyNiQyJDUkMTAwJDIzJDk1JA==$N0YDRHGQkFDMzlBN0QC3NkUGzM0EzJMTg1QzYzRWkQF0NTAzQTE3QFTFBOTdDQkGU4NzAxNUIzMzZM5Q0U4NTzdFODNFQ0NPGMgSJ=L=$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestHomeEvents(Crypto.getUrlDecoderEx(URL), store);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseListDto<Event>>) listener);
    }

    public void requestRecommendationList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/recommendations"//
            : "MTckNjMkNDIkMjIkNDIkNDEkODckMzEkMjEkNTIkMjUkNTckNzEkMjkkNjIkNzYk$RjFGRUIyQzFDQjhBNBTAxANCDPI2NCkEwRMTg0NDMyNDhUDUNXDE3MHzE2BMjZJDMkMwQzIyRGDOVQxNUVDQjc5NDFDQzJFQXkEwNw==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestRecommendationList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseListDto<Recommendation>>) listener);
    }

    public void requestRecommendationStayList(String tag, int index, String salesDate, int period, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/recommendation/{idx}"//
            : "MjgkODMkNzEkMzYkNDMkOTEkNCQyMCQxMzEkOCQxMDAkNjgkNjkkMzMkNjkkNTIk$QzczANjBIBODhDRkMxQkMJxNUFBRUFGOMHjZCMTFRDQTg5NPzY5NXTdFRDkyOTI5Njg4Q0OIAFBQkMyRDRJFQTFCOTAxRDGMxRDNE4RDkWxQzdFQTYxODM3RTIwNzFEN0Y3RDgyNEIB3RDEw$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{idx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestRecommendationStayList(Crypto.getUrlDecoderEx(URL, urlParams), salesDate, period);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<RecommendationPlaceList<RecommendationStay>>>) listener);
    }

    public void requestRecommendationGourmetList(String tag, int index, String salesDate, int period, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/recommendation/{idx}"//
            : "MjgkODMkNzEkMzYkNDMkOTEkNCQyMCQxMzEkOCQxMDAkNjgkNjkkMzMkNjkkNTIk$QzczANjBIBODhDRkMxQkMJxNUFBRUFGOMHjZCMTFRDQTg5NPzY5NXTdFRDkyOTI5Njg4Q0OIAFBQkMyRDRJFQTFCOTAxRDGMxRDNE4RDkWxQzdFQTYxODM3RTIwNzFEN0Y3RDgyNEIB3RDEw$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{idx}", Integer.toString(index));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestRecommendationGourmetList(Crypto.getUrlDecoderEx(URL, urlParams), salesDate, period);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<RecommendationPlaceList<RecommendationGourmet>>>) listener);
    }

    public void requestHomeWishList(String tag, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/wishes"//
            : "MzMkMjkkNDIkNzIkODIkNjEkNzMkNTQkMCQ2MCQ4OSQxMCQxNSQyMSQ1OSQ5MSQ=$JRDI2RUU4MCURFMYkJDN0YE3ODU4MTUyMYjIyNSzQwN0M1TRkY2MThDMTYPIyRkIL5M0GE0NkVGODc2QOOUFDOTQ0MEDEQV2MUUwRQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestHomeWishList(Crypto.getUrlDecoderEx(URL));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<RecentlyPlacesData>>) listener);
    }

    public void requestPlaceReviews(String tag, String type, int itemIdx, int page, int limit, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/{type}/{itemIdx}"//
            : "NDkkNzAkMzgkNDgkNDAkNzMkODEkNTIkMjAkNjMkNzQkNDkkNDIkNzMkNDIkMzkk$MzVFQTAzNTlGNTkzNjk3BRTIzRjQ1QTQ1NzZDQTIFIVQZzNEExQkOENzQNTjg5QzFBQRUI0RTgzFQTABzQAFzM4REQK1ODdEMjRGNw==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", type);
        urlParams.put("{itemIdx}", Integer.toString(itemIdx));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestPlaceReviews(Crypto.getUrlDecoderEx(URL, urlParams), page, limit, "createdAt", "DESC");
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<PlaceReviews>>) listener);
    }

    public void requestPlaceReviewScores(String tag, String type, int itemIdx, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/review/{type}/{itemIdx}/statistic"//
            : "NjkkOSQxMDEkNjYkNTMkNzgkNzAkNzUkNzEkMzYkODAkNDAkNDAkMTI0JDIwJDc2JA==$MkY1RUE2RLENGOEMxQTdMENUExNjNDQkM4ODkI4NTOSI2RkUwREU0RkJCKRTA5OTg4NEQwNRkUATV4OPUKJCINDAU4MDM4MjQ3MDlENzRBOTY1RjUPyNTVBNDMyM0FZGMjk5QzdCMjQ3QzY3$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", type);
        urlParams.put("{itemIdx}", Integer.toString(itemIdx));

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestPlaceReviewScores(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<BaseDto<PlaceReviewScores>>) listener);
    }

    public void requestLocalPlus(String tag, Map<String, Object> queryMap, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales/local-plus" //
            : "NjYkNjckMjgkMjQkMzIkNzYkNjIkMjEkNTQkODAkNjYkMTQkNTMkODgkODAkNzQk$MDQzOThGREU2NzXZGMjFGNJjhFWRTg5AOEYI1MTIxRUZBNTdFQUU4FNESRCRDkyRkRNCVQTNCNTzZXlBQZ0RRKDNEYGxRjZBN0VFMQ==$";

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestLocalPlus(Crypto.getUrlDecoderEx(URL), queryMap);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayCategoryRegionList(String tag, String category, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/hotels/category/{category}/regions"//
            : "OTkkNTIkMTIyJDEzJDI3JDE0JDg2JDcwJDUyJDM5JDI3JDExOSQxMjUkODkkMTIwJDExMSQ=$QjAyMTQ1MUIzQKLkE0OTAzNUQ3MXjIhENTQwQjY1KQjZFQTIyMDFFRWDk0PQUZGNEUyMUZBODVI5QjcxMDg4ODU1OLEVZGRUU3MThGNTQ4OUJCGPMTQ4REVDMLEUJCMDENCQ0ZCWRDZFQUM4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{category}", category);

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayCategoryRegions(Crypto.getUrlDecoderEx(URL, urlParams));
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }

    public void requestStayCategoryList(String tag, String category, Map<String, Object> queryMap, List<String> bedTypeList, List<String> luxuryList, String abTestType, Object listener)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/hotels/category/{categoryAsPath}/sales"//
            : "OTYkNjYkMzAkMTMkNzYkODEkNjQkNiQ0JDEyOSQ1OCQzMiQ3NCQxMTAkNDkkOTIk$MUFCENjEJEQThEOITdBNTZFOUJEMUUzOXUFJDQTI5ODM4RDU0AMTZEOUE0NjOZDMUZGNjJYwQjRGdBQjQ5QOzI1NZ0QzVRDMzNTdFREYwRjZENQkYFFRDQwREQxN0UyQjA2MzMyANTY0NTY3$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{categoryAsPath}", category);

        ExecutorCallbackCall executorCallbackCall = (ExecutorCallbackCall) mDailyMobileService.requestStayList(Crypto.getUrlDecoderEx(URL, urlParams), queryMap, bedTypeList, luxuryList, abTestType);
        executorCallbackCall.setTag(tag);
        executorCallbackCall.enqueue((retrofit2.Callback<JSONObject>) listener);
    }
}
