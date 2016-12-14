package com.twoheart.dailyhotel.network;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.ApplicationTest;
import com.twoheart.dailyhotel.Const;
import com.twoheart.dailyhotel.NetworkApiTest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.User;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyAssert;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by android_sam on 2016. 12. 14..
 */
public class DailyNetworkAPITest extends ApplicationTest
{
    private static final String mNetworkTag = NetworkApiTest.class.getSimpleName();

    private SaleTime mSaleTime;

    // user login
    private String mAuthorization;
    private User mUser;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

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

                            mUser = new User();
                            mUser.setEmail(userJSONObject.getString("email"));
                            mUser.setName(userJSONObject.getString("name"));
                            mUser.setRecommender(userJSONObject.getString("rndnum"));
                            mUser.setType(userJSONObject.getString("userType"));
                            mUser.setPhone(userJSONObject.getString("phone"));
                            mUser.setBirthDay(userJSONObject.getString("birthday"));

                            mAuthorization = String.format("%s %s", tokenType, accessToken);

                            DailyAssert.assertNotNull(mUser.getUserIdx());
                            DailyAssert.assertNotNull(mUser.getName());
                            DailyAssert.assertEquals(Constants.DAILY_USER, mUser.getType());
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

    public void testRequestCheckServer() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msg_code");
                    DailyAssert.assertEquals(200, msgCode);

                    if (msgCode == 200)
                    {
                        JSONObject jsonObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(jsonObject);

                        boolean isSuspend = jsonObject.getBoolean("isSuspend");

                        if (isSuspend == true)
                        {
                            String title = jsonObject.getString("messageTitle");
                            String message = jsonObject.getString("messageBody");

                            DailyAssert.assertNotNull(title);
                            DailyAssert.assertNotNull(message);
                        } else
                        {
                            // 정상임으로 체크 안함
                        }
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
        };

        DailyNetworkAPI.getInstance(mContext).requestCheckServer(mNetworkTag, responseListener);
    }


    public void testRequestCommonVer() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msgCode");
                    DailyAssert.assertEquals(100, msgCode);

                    if (msgCode != 100)
                    {
                        JSONObject dataJSONObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(dataJSONObject);

                        String maxVersionName;
                        String minVersionName;

                        switch (Constants.RELEASE_STORE)
                        {
                            case T_STORE:
                                maxVersionName = dataJSONObject.getString("tstoreMax");
                                minVersionName = dataJSONObject.getString("tstoreMin");
                                break;

                            case PLAY_STORE:
                            default:
                                maxVersionName = dataJSONObject.getString("playMax");
                                minVersionName = dataJSONObject.getString("playMin");
                                break;
                        }

                        DailyAssert.assertNotNull(minVersionName);
                        DailyAssert.assertNotNull(maxVersionName);
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
        };

        DailyNetworkAPI.getInstance(mContext).requestCommonVer(mNetworkTag, responseListener);
    }


    @Deprecated
    public void testRequestCommonReview() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msgCode");
                    DailyAssert.assertEquals(100, msgCode);
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
        };

        DailyNetworkAPI.getInstance(mContext).requestCommonReview(mNetworkTag, "hotel" , responseListener);
    }


    public void testRequestCommonDateTime() throws Exception
    {

    }


    public void testRequestUserProfile() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
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
                        JSONObject jsonObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(jsonObject);

                        final String userIndex = jsonObject.getString("userIdx");
                        final String userType = jsonObject.has("userType") == true ? jsonObject.getString("userType") : AnalyticsManager.ValueType.EMPTY;

                        DailyAssert.assertNotNull(userIndex);
                        DailyAssert.assertNotSame(AnalyticsManager.ValueType.EMPTY, userType);

                    } else
                    {
                        DailyAssert.fail();
                    }
                } catch (Exception e)
                {
                    DailyAssert.fail(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 401)
                {
                    DailyAssert.fail("error Code 401 :: " + volleyError.getMessage());
                } else
                {
                    DailyAssert.fail(volleyError);
                }
            }
        };

        DailyNetworkAPI.getInstance(mContext).requestUserProfile(mNetworkTag, responseListener);
    }


    public void testRequestUserBonus() throws Exception
    {

    }


    public void testRequestUserInformationUpdate() throws Exception
    {

    }


    public void testRequestUserProfileBenefit() throws Exception
    {

    }


    public void testRequestUserCheckEmail() throws Exception
    {

    }


    public void testRequestUserChangePassword() throws Exception
    {

    }


    public void testRequestUserInformationEx() throws Exception
    {

    }


    public void testRequestUserInformationForPayment() throws Exception
    {

    }


    public void testRequestUserUpdateInformationForSocial() throws Exception
    {

    }


    public void testRequestUserBillingCardList() throws Exception
    {

    }


    public void testRequestUserDeleteBillingCard() throws Exception
    {

    }


    public void testRequestStayList() throws Exception
    {

    }


    public void testRequestStaySearchList() throws Exception
    {

    }


    public void testRequestHotelSearchAutoCompleteList() throws Exception
    {

    }


    public void testRequestHotelRegionList() throws Exception
    {

    }


    public void testRequestHotelPaymentInformation() throws Exception
    {

    }


    public void testRequestHotelDetailInformation() throws Exception
    {

    }


    public void testRequestHotelPayment() throws Exception
    {

    }


    public void testRequestBookingList() throws Exception
    {

    }


    public void testRequestGourmetBookingDetailInformation() throws Exception
    {

    }


    public void testRequestGourmetReceipt() throws Exception
    {

    }


    public void testRequestGourmetHiddenBooking() throws Exception
    {

    }


    public void testRequestGourmetAccountInformation() throws Exception
    {

    }


    public void testRequestGourmetRegionList() throws Exception
    {

    }


    public void testRequestGourmetList() throws Exception
    {

    }


    public void testRequestGourmetSearchAutoCompleteList() throws Exception
    {

    }


    public void testRequestGourmetSearchList() throws Exception
    {

    }


    public void testRequestGourmetDetailInformation() throws Exception
    {

    }


    public void testRequestGourmetPaymentInformation() throws Exception
    {

    }


    public void testRequestGourmetCheckTicket() throws Exception
    {

    }


    public void testRequestGourmetPayment() throws Exception
    {

    }


    public void testRequestDepositWaitDetailInformation() throws Exception
    {

    }


    public void testRequestHotelBookingDetailInformation() throws Exception
    {

    }


    public void testRequestHotelHiddenBooking() throws Exception
    {

    }


    public void testRequestHotelReceipt() throws Exception
    {

    }


    public void testRequestEventList() throws Exception
    {

    }


    public void testRequestEventNCouponNNoticeNewCount() throws Exception
    {

    }


    public void testRequestEventPageUrl() throws Exception
    {

    }


    public void testRequestCompanyInformation() throws Exception
    {

    }


    public void testRequestEventBannerList() throws Exception
    {

    }


    public void testRequestDailyUserVerfication() throws Exception
    {

    }


    public void testRequestDailyUserUpdatePhoneNumber() throws Exception
    {

    }


    public void testRequestSignupValidation() throws Exception
    {

    }


    public void testRequestDailyUserSignupVerfication() throws Exception
    {

    }


    public void testRequestDailyUserSignup() throws Exception
    {

    }


    public void testRequestFacebookUserSignup() throws Exception
    {

    }


    public void testRequestKakaoUserSignup() throws Exception
    {

    }


    public void testRequestDailyUserSignin() throws Exception
    {

    }


    public void testRequestFacebookUserSignin() throws Exception
    {

    }


    public void testRequestKakaoUserSignin() throws Exception
    {

    }


    public void testRequestCouponList() throws Exception
    {

    }


    public void testRequestCouponList1() throws Exception
    {

    }


    public void testRequestCouponList2() throws Exception
    {

    }


    public void testRequestCouponHistoryList() throws Exception
    {

    }


    public void testRequestNoticeAgreement() throws Exception
    {

    }


    public void testRequestNoticeAgreementResult() throws Exception
    {

    }


    public void testRequestBenefitMessage() throws Exception
    {

    }


    public void testRequestDownloadCoupon() throws Exception
    {

    }


    public void testRequestDownloadEventCoupon() throws Exception
    {

    }


    public void testRequestHasCoupon() throws Exception
    {

    }


    public void testRequestHasCoupon1() throws Exception
    {

    }


    public void testRequestCouponList3() throws Exception
    {

    }


    public void testRequestCouponList4() throws Exception
    {

    }


    public void testRequestRegistKeywordCoupon() throws Exception
    {

    }


    public void testRequestUpdateBenefitAgreement() throws Exception
    {

    }


    public void testRequestUserTracking() throws Exception
    {

    }


    public void testRequestNoticeList() throws Exception
    {

    }


    public void testRequestRecentStayList() throws Exception
    {

    }


    public void testRequestRecentGourmetList() throws Exception
    {

    }


    public void testRequestReceiptByEmail() throws Exception
    {

    }


    public void testRequestWishListCount() throws Exception
    {

    }


    public void testRequestWishList() throws Exception
    {

    }


    public void testRequestAddWishList() throws Exception
    {

    }


    public void testRequestRemoveWishList() throws Exception
    {

    }


    public void testRequestPolicyRefund() throws Exception
    {

    }


    public void testRequestPolicyRefund1() throws Exception
    {

    }


    public void testRequestRefund() throws Exception
    {

    }


    public void testRequestBankList() throws Exception
    {

    }


    public void testRequestStayReviewInformation() throws Exception
    {

    }


    public void testRequestGourmetReviewInformation() throws Exception
    {

    }


    public void testRequestStayReviewInformation1() throws Exception
    {

    }


    public void testRequestGourmetReviewInformation1() throws Exception
    {

    }


    public void testRequestAddReviewInformation() throws Exception
    {

    }


    public void testRequestAddReviewDetailInformation() throws Exception
    {

    }

}