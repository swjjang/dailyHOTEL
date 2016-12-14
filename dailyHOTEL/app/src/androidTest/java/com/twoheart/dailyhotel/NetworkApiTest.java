package com.twoheart.dailyhotel;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetMainNetworkController;
import com.twoheart.dailyhotel.screen.hotel.list.StayListNetworkController;
import com.twoheart.dailyhotel.screen.hotel.list.StayMainNetworkController;
import com.twoheart.dailyhotel.screen.main.MainNetworkController;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyAssert;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2016. 12. 12..
 */

public class NetworkApiTest extends ApplicationTest
{
    protected static final String TAG = NetworkApiTest.class.getSimpleName();

    protected String mAuthorization;
    protected SaleTime mSaleTime;

    public NetworkApiTest()
    {
        super();
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        mSaleTime = new SaleTime();
        mAuthorization = null;

        ignore_testLoginByDailyUser();
        ignore_testDailyDateTime();
    }

    public void ignore_testLoginByDailyUser()
    {
        if (Util.isTextEmpty(mAuthorization) == false)
        {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("email", DailyHotelJsonRequest.getUrlDecoderEx(Const.TEST_EMAIL));
        params.put("pw", DailyHotelJsonRequest.getUrlDecoderEx(Const.TEST_PASSWORD));
        params.put("social_id", "0");
        params.put("user_type", Constants.DAILY_USER);

        DailyNetworkAPI.getInstance(application).requestDailyUserSignin(TAG, params, new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                ExLog.d("Url : " + url);
                try
                {
                    int msgCode = response.getInt("msg_code");
                    DailyAssert.assertEquals(0, msgCode);

                    if (msgCode == 0)
                    {
                        JSONObject dataJSONObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(dataJSONObject);

                        boolean isLogin = dataJSONObject.getBoolean("is_signin");
                        DailyAssert.assertTrue(isLogin);

                        JSONObject tokenJSONObject = response.getJSONObject("token");
                        DailyAssert.assertNotNull(tokenJSONObject);
                        if (isLogin == true)
                        {
                            DailyPreference.getInstance(application).setLastestCouponTime("");

                            String accessToken = tokenJSONObject.getString("access_token");
                            String tokenType = tokenJSONObject.getString("token_type");
                            DailyAssert.assertNotNull(accessToken);
                            DailyAssert.assertNotNull(tokenType);

                            JSONObject userJSONObject = dataJSONObject.getJSONObject("user");
                            DailyAssert.assertNotNull(userJSONObject);

                            String userIndex = userJSONObject.getString("idx");
                            DailyAssert.assertNotNull(userIndex);

                            String email = userJSONObject.getString("email");
                            String name = userJSONObject.getString("name");
                            DailyAssert.assertNotNull(email);
                            DailyAssert.assertNotNull(name);

                            String recommender = userJSONObject.getString("rndnum");
                            String userType = userJSONObject.getString("userType");
                            DailyAssert.assertEquals(Constants.DAILY_USER, userType);
                            //        String phoneNumber = userJSONObject.getString("phone");
                            String birthday = userJSONObject.getString("birthday");

                            mAuthorization = String.format("%s %s", tokenType, accessToken);

                            DailyPreference.getInstance(application).setAuthorization(mAuthorization);
                            DailyPreference.getInstance(application).setUserInformation(userType, email, name, birthday, recommender);
                            return;
                        }
                    }

                    // 로그인이 실패한 경우
                    String msg = response.getString("msg");

                    if (Util.isTextEmpty(msg) == true)
                    {
                        msg = application.getResources().getString(R.string.toast_msg_failed_to_login);
                    }

                    DailyAssert.fail(msg);
                } catch (Exception e)
                {
                    DailyAssert.fail(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                DailyAssert.fail(volleyError);
            }
        });
    }

    public void ignore_testDailyDateTime()
    {
        if (mSaleTime != null)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestCommonDateTime(TAG, new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msgCode");
                    DailyAssert.assertEquals(100, msgCode);

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(dataJSONObject);

                        long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                        DailyAssert.assertNotNull(currentDateTime);
                        DailyAssert.assertNotNull(dailyDateTime);

                        if (mSaleTime == null)
                        {
                            mSaleTime = new SaleTime();
                        }

                        mSaleTime.setCurrentTime(currentDateTime);
                        mSaleTime.setDailyTime(dailyDateTime);
                        mSaleTime.setOffsetDailyDay(0);
                    } else
                    {
                        String message = response.getString("msg");
                        DailyAssert.fail(message);
                    }
                } catch (Exception e)
                {
                    DailyAssert.fail(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                DailyAssert.fail(volleyError);
            }
        });
    }

    public void testMainNetworkController()
    {
        MainNetworkController mainNetworkController = new MainNetworkController(this.application, TAG, new MainNetworkController.OnNetworkControllerListener()
        {
            @Override
            public void updateNewEvent(boolean isNewEvent, boolean isNewCoupon, boolean isNewNotices)
            {

            }

            @Override
            public void onReviewGourmet(Review review)
            {

            }

            @Override
            public void onReviewHotel(Review review)
            {

            }

            @Override
            public void onCheckServerResponse(String title, String message)
            {

            }

            @Override
            public void onAppVersionResponse(String currentVersion, String forceVersion)
            {

            }

            @Override
            public void onConfigurationResponse()
            {

            }

            @Override
            public void onNoticeAgreement(String message, boolean isFirstTimeBuyer)
            {

            }

            @Override
            public void onNoticeAgreementResult(String agreeMessage, String cancelMessage)
            {

            }

            @Override
            public void onCommonDateTime(long currentDateTime, long openDateTime, long closeDateTime)
            {

            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                DailyAssert.fail(volleyError);
            }

            @Override
            public void onError(Exception e)
            {
                DailyAssert.fail(e);
            }

            @Override
            public void onErrorPopupMessage(int msgCode, String message)
            {
                DailyAssert.fail(message);
            }

            @Override
            public void onErrorToastMessage(String message)
            {
                DailyAssert.fail(message);
            }
        });

        mainNetworkController.requestCheckServer();

        mainNetworkController.requestVersion();

        mainNetworkController.requestCommonDatetime();

        mainNetworkController.requestUserInformation();

        mainNetworkController.requestNoticeAgreement();

        mainNetworkController.requestNoticeAgreementResult(true);

        mainNetworkController.requestNoticeAgreementResult(false);

        mainNetworkController.requestReviewGourmet();
    }

    public void testStayMainNetworkController()
    {
        StayMainNetworkController stayMainNetworkController = new StayMainNetworkController(application, TAG, new StayMainNetworkController.OnNetworkControllerListener()
        {
            @Override
            public void onDateTime(long currentDateTime, long dailyDateTime)
            {
                if (mSaleTime == null)
                {
                    mSaleTime = new SaleTime();
                }

                mSaleTime.setCurrentTime(currentDateTime);
                mSaleTime.setDailyTime(dailyDateTime);
                mSaleTime.setOffsetDailyDay(0);
            }

            @Override
            public void onEventBanner(List<EventBanner> eventBannerList)
            {
                DailyAssert.assertNotNull(eventBannerList);
            }

            @Override
            public void onRegionList(List<Province> provinceList, List<Area> areaList)
            {

            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                DailyAssert.fail(volleyError);
            }

            @Override
            public void onError(Exception e)
            {
                DailyAssert.fail(e);
            }

            @Override
            public void onErrorPopupMessage(int msgCode, String message)
            {
                DailyAssert.fail("error : msgCode=" + msgCode + ", message=" + message);
            }

            @Override
            public void onErrorToastMessage(String message)
            {
                DailyAssert.fail("error : " + message);
            }
        });

        stayMainNetworkController.requestDateTime();

        stayMainNetworkController.requestRegionList();

        stayMainNetworkController.requestEventBanner();
    }

    public void testStayListNetworkController()
    {
        StayListNetworkController stayListNetworkController//
            = new StayListNetworkController(application, TAG, new StayListNetworkController.OnNetworkControllerListener()
        {
            @Override
            public void onStayList(ArrayList<Stay> list, int page)
            {
                DailyAssert.assertNull(list);
                DailyAssert.assertNotSame(0, list.size());
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                DailyAssert.fail(volleyError);
            }

            @Override
            public void onError(Exception e)
            {
                DailyAssert.fail(e);
            }

            @Override
            public void onErrorPopupMessage(int msgCode, String message)
            {
                DailyAssert.fail("error : msgCode=" + msgCode + ", message=" + message);
            }

            @Override
            public void onErrorToastMessage(String message)
            {
                DailyAssert.fail("error : " + message);
            }
        });

//        StayParams params = new StayParams()
//        stayListNetworkController.requestStayList(params);
    }

    public void testGourmetMainNetworkController()
    {
        GourmetMainNetworkController gourmetMainNetworkController//
            = new GourmetMainNetworkController(application, TAG, new GourmetMainNetworkController.OnNetworkControllerListener()
        {
            @Override
            public void onDateTime(long currentDateTime, long dailyDateTime)
            {
                if (mSaleTime == null)
                {
                    mSaleTime = new SaleTime();
                }

                mSaleTime.setCurrentTime(currentDateTime);
                mSaleTime.setDailyTime(dailyDateTime);
                mSaleTime.setOffsetDailyDay(0);
            }

            @Override
            public void onEventBanner(List<EventBanner> eventBannerList)
            {
                DailyAssert.assertNotNull(eventBannerList);
            }

            @Override
            public void onRegionList(List<Province> provinceList, List<Area> areaList)
            {

            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                DailyAssert.fail(volleyError);
            }

            @Override
            public void onError(Exception e)
            {
                DailyAssert.fail(e);
            }

            @Override
            public void onErrorPopupMessage(int msgCode, String message)
            {
                DailyAssert.fail("error : msgCode=" + msgCode + ", message=" + message);
            }

            @Override
            public void onErrorToastMessage(String message)
            {
                DailyAssert.fail("error : " + message);
            }
        });

        gourmetMainNetworkController.requestDateTime();


        gourmetMainNetworkController.requestEventBanner();
    }
}
