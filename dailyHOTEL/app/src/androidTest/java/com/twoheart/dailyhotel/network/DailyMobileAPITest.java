package com.twoheart.dailyhotel.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.twoheart.dailyhotel.Const;
import com.twoheart.dailyhotel.DailyAssert;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.User;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 12. 15..
 */
@RunWith(AndroidJUnit4.class)
public class DailyMobileAPITest
{
    private static final String mNetworkTag = DailyNetworkAPITest.class.getSimpleName();

    private Context mContext;

    private SaleTime mSaleTime;

    // user login
    private String mAuthorization;
    private User mUser;

    @Before
    public void setUp() throws Exception
    {
        // Context Setting!
        //        mContext = InstrumentationRegistry.getContext();
        mContext = InstrumentationRegistry.getTargetContext(); // 앱 타켓 컨텍스트!

        requestDailyUserLoginBySetUp();
        requestCommonDateTimeBySetUp();
    }

    @After
    public void tearDown() throws Exception
    {
        DailyAssert.clearData();
        requestUserInformationUpdateByTearDown();
    }

    @Ignore
    private String bodyToString(final RequestBody request)
    {
        try
        {
            Buffer buffer = new Buffer();

            request.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    @Ignore
    public void requestDailyUserLoginBySetUp() throws Exception
    {
        if (Util.isTextEmpty(mAuthorization) == false)
        {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("email", Crypto.getUrlDecoderEx(Const.TEST_EMAIL));
        params.put("pw", Crypto.getUrlDecoderEx(Const.TEST_PASSWORD));
        params.put("social_id", "0");
        params.put("user_type", Constants.DAILY_USER);

        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                DailyAssert.setData(call, response);

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();
                        DailyAssert.assertNotNull(responseJSONObject);

                        int msgCode = responseJSONObject.getInt("msg_code");
                        DailyAssert.assertEquals(0, msgCode);

                        if (msgCode == 0)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                            DailyAssert.assertNotNull(dataJSONObject);

                            JSONObject tokenJSONObject = responseJSONObject.getJSONObject("token");
                            DailyAssert.assertNotNull(tokenJSONObject);

                            boolean isLogin = dataJSONObject.getBoolean("is_signin");
                            DailyAssert.assertTrue(isLogin);

                            if (isLogin == true)
                            {
                                String accessToken = tokenJSONObject.getString("access_token");
                                String tokenType = tokenJSONObject.getString("token_type");
                                mAuthorization = String.format("%s %s", tokenType, accessToken);

                                DailyAssert.assertNotNull(accessToken);
                                DailyAssert.assertNotNull(tokenType);

                                JSONObject userJSONObject = dataJSONObject.getJSONObject("user");
                                DailyAssert.assertNotNull(userJSONObject);

                                mUser = new User();
                                mUser.setPassword(Const.TEST_PASSWORD);
                                mUser.setUserIdx(userJSONObject.getString("idx"));
                                mUser.setEmail(userJSONObject.getString("email"));
                                mUser.setName(userJSONObject.getString("name"));
                                mUser.setRecommender(userJSONObject.getString("rndnum"));
                                mUser.setType(userJSONObject.getString("userType"));
                                mUser.setPhone(userJSONObject.getString("phone"));

                                String birthday = null;
                                if (userJSONObject.has("birthday") == true && userJSONObject.isNull("birthday") == false)
                                {
                                    birthday = userJSONObject.getString("birthday");
                                }

                                mUser.setBirthDay(birthday);

                                DailyAssert.assertNotNull(mUser.getUserIdx());
                                DailyAssert.assertNotNull(mUser.getName());
                                DailyAssert.assertEquals(Constants.DAILY_USER, mUser.getType());
                                return;
                            }
                        }
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
            }
        };

        DailyMobileAPI.getInstance(mContext).requestDailyUserSignin(mNetworkTag, params, networkCallback);
    }

    @Ignore
    public void requestCommonDateTimeBySetUp() throws Exception
    {
        if (mSaleTime != null)
        {
            return;
        }

        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                DailyAssert.setData(call, response);

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();
                        DailyAssert.assertNotNull(response);

                        int msgCode = responseJSONObject.getInt("msgCode");
                        DailyAssert.assertEquals(100, msgCode);

                        if (msgCode == 100)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
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
                            String message = responseJSONObject.getString("msg");
                            DailyAssert.fail(message);
                        }
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
            }
        };

        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, networkCallback);
    }

    @Ignore
    public void requestUserInformationUpdateByTearDown() throws Exception
    {
        if (mUser == null)
        {
            DailyAssert.clearData();
            return;
        }

        if (Const.TEST_USER_NAME.equalsIgnoreCase(mUser.getName()) == false //
            || Const.TEST_PASSWORD.equalsIgnoreCase(mUser.getPassword()) == false//
            || Const.TEST_USER_BIRTHDAY.equalsIgnoreCase(mUser.getBirthDay()) == false)
        {

            retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
            {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
                {
                    DailyAssert.setData(call, response);

                    if (response != null && response.isSuccessful() && response.body() != null)
                    {
                        try
                        {
                            JSONObject responseJSONObject = response.body();

                            int msgCode = responseJSONObject.getInt("msgCode");
                            DailyAssert.assertEquals(100, msgCode);
                        } catch (Exception e)
                        {
                            DailyAssert.fail(e);
                        }
                    } else
                    {
                        DailyAssert.fail();
                    }

                    DailyAssert.clearData();
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t)
                {
                    DailyAssert.fail(call, t);
                    DailyAssert.clearData();
                }
            };

            Map<String, String> params = new HashMap<>();
            params.put("birthday", Const.TEST_USER_BIRTHDAY);
            params.put("user_name", Const.TEST_USER_NAME);
            params.put("pw", Const.TEST_PASSWORD);

            DailyMobileAPI.getInstance(mContext).requestUserInformationUpdate(mNetworkTag, params, networkCallback);
        } else
        {
            DailyAssert.clearData();
        }
    }

    @Test
    public void requestStatusServer() throws Exception
    {
        retrofit2.Callback<JSONObject> networkCallback = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                DailyAssert.setData(call, response);

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();
                        DailyAssert.assertNotNull(responseJSONObject);

                        int msgCode = responseJSONObject.getInt("msg_code");
                        DailyAssert.assertEquals(200, msgCode);

                        if (msgCode == 200)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                            DailyAssert.assertNotNull(dataJSONObject);

                            DailyAssert.assertNotNull(dataJSONObject.has("isSuspend"));
                            boolean isSuspend = dataJSONObject.getBoolean("isSuspend");
                            if (isSuspend == true)
                            {
                                String title = dataJSONObject.getString("messageTitle");
                                String message = dataJSONObject.getString("messageBody");

                                DailyAssert.assertNotNull(title);
                                DailyAssert.assertNotNull(message);
                            } else
                            {
                                // do nothing!
                            }
                        } else
                        {
                            DailyAssert.fail();
                        }
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
            }
        };

        DailyMobileAPI.getInstance(mContext).requestStatusServer(mNetworkTag, networkCallback);
    }

    @Test
    public void requestCommonVersion() throws Exception
    {
        retrofit2.Callback<JSONObject> networkCallback = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                DailyAssert.setData(call, response);

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");

                        if (msgCode == 100)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
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

                            DailyAssert.assertNotNull(Constants.RELEASE_STORE.getName(), minVersionName);
                            DailyAssert.assertNotNull(Constants.RELEASE_STORE.getName(), maxVersionName);
                        } else
                        {
                            String message = responseJSONObject.getString("msg");
                            DailyAssert.fail(message);
                        }
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
            }
        };

        DailyMobileAPI.getInstance(mContext).requestCommonVersion(mNetworkTag, networkCallback);
    }

    @Test
    public void requestCommonDateTime() throws Exception
    {
        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                DailyAssert.setData(call, response);

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();
                        DailyAssert.assertNotNull(responseJSONObject);

                        int msgCode = responseJSONObject.getInt("msgCode");
                        DailyAssert.assertEquals(100, msgCode);

                        if (msgCode == 100)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            String currentDate = dataJSONObject.getString("currentDateTime");
                            String dailyDate = dataJSONObject.getString("dailyDateTime");
                            String openDate = dataJSONObject.getString("openDateTime");
                            String closeDate = dataJSONObject.getString("closeDateTime");

                            DailyAssert.assertNotNull(currentDate);
                            DailyAssert.assertNotNull(dailyDate);
                            DailyAssert.assertNotNull(openDate);
                            DailyAssert.assertNotNull(closeDate);
                            DailyAssert.assertTrue(Pattern.matches(DailyCalendar.ISO_8601_FORMAT, currentDate));
                            DailyAssert.assertTrue(Pattern.matches(DailyCalendar.ISO_8601_FORMAT, dailyDate));
                            DailyAssert.assertTrue(Pattern.matches(DailyCalendar.ISO_8601_FORMAT, openDate));
                            DailyAssert.assertTrue(Pattern.matches(DailyCalendar.ISO_8601_FORMAT, closeDate));
                        } else
                        {
                            String message = responseJSONObject.getString("msg");
                            DailyAssert.fail(message);
                        }
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
            }
        };

        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, networkCallback);
    }

    @Test
    public void requestUserProfile() throws Exception
    {
        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                DailyAssert.setData(call, response);

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();
                        DailyAssert.assertNotNull(responseJSONObject);

                        int msgCode = responseJSONObject.getInt("msgCode");
                        DailyAssert.assertEquals(100, msgCode);

                        if (msgCode == 100)
                        {
                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");
                            DailyAssert.assertNotNull(jsonObject);

                            String userIndex = jsonObject.getString("userIdx");
                            String userType = jsonObject.getString("userType");
                            String userEmail = jsonObject.getString("email");
                            String userName = jsonObject.getString("name");
                            String userPhone = jsonObject.getString("phone");
                            String referralCode = jsonObject.getString("referralCode"); // 자신의 추천 번호
                            String birthday = jsonObject.getString("birthday");
                            String phoneVerifiedAt = jsonObject.getString("phoneVerifiedAt");

                            DailyAssert.assertEquals(mUser.getUserIdx(), userIndex);
                            DailyAssert.assertEquals(mUser.getType(), userType);
                            DailyAssert.assertEquals(mUser.getEmail(), userEmail);
                            DailyAssert.assertEquals(mUser.getName(), userName);
                            DailyAssert.assertEquals(mUser.getPhone(), userPhone);
                            DailyAssert.assertNotNull(referralCode);
                            DailyAssert.assertEquals(mUser.getBirthDay(), birthday);
                            DailyAssert.assertTrue(Pattern.matches(DailyCalendar.ISO_8601_FORMAT, phoneVerifiedAt));

                            DailyAssert.assertTrue(jsonObject.has("verified"));
                            DailyAssert.assertTrue(jsonObject.has("phoneVerified"));
                            DailyAssert.assertTrue(jsonObject.has("agreedBenefit"));
                        } else
                        {
                            String msg = responseJSONObject.getString("msg");
                            DailyAssert.fail(msg);
                        }
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserProfile(mNetworkTag, networkCallback);
    }

    @Test
    public void requestUserBonus() throws Exception
    {
        retrofit2.Callback networkCallBack = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                DailyAssert.setData(call, response);

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    //적립금 내역리스트
                    try
                    {
                        JSONObject responseJSONObject = response.body();
                        DailyAssert.assertNotNull(responseJSONObject);

                        if (responseJSONObject.has("history") == true && responseJSONObject.isNull("history") == false)
                        {
                            JSONArray jsonArray = responseJSONObject.getJSONArray("history");
                            int length = jsonArray.length();

                            for (int i = 0; i < length; i++)
                            {
                                JSONObject historyObj = jsonArray.getJSONObject(i);
                                DailyAssert.assertNotNull(historyObj);

                                String content = historyObj.getString("content");
                                String expires = historyObj.getString("expires");
                                int bonus = historyObj.getInt("bonus");

                                DailyAssert.assertNotNull(content);
                                DailyAssert.assertNotNull(expires);
                                DailyAssert.assertNotNull(bonus);
                            }
                        }
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(t);
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserBonus(mNetworkTag, networkCallBack);
    }

    @Test
    public void requestUserInformationUpdate() throws Exception
    {
        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                DailyAssert.setData(call, response);

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");
                        DailyAssert.assertEquals(100, msgCode);

                        if (msgCode == 100)
                        {
                            mUser.setBirthDay(Const.TEST_USER_BIRTHDAY);
                            mUser.setPassword(Const.TEST_MODIFY_PASSWORD);
                            mUser.setName(Const.TEST_MODIFY_USER_NAME);
                        }
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("birthday", Const.TEST_MODIFY_USER_BIRTHDAY);
        params.put("user_name", Const.TEST_MODIFY_USER_NAME);
        params.put("pw", Const.TEST_MODIFY_PASSWORD);

        DailyMobileAPI.getInstance(mContext).requestUserInformationUpdate(mNetworkTag, params, networkCallback);
    }

    @Test
    public void requestUserProfileBenefit() throws Exception
    {

    }

    @Test
    public void requestUserCheckEmail() throws Exception
    {

    }

    @Test
    public void requestUserChangePassword() throws Exception
    {

    }

    @Test
    public void requestUserInformationForPayment() throws Exception
    {

    }

    @Test
    public void requestUserUpdateInformationForSocial() throws Exception
    {

    }

    @Test
    public void requestUserBillingCardList() throws Exception
    {

    }

    @Test
    public void requestUserDeleteBillingCard() throws Exception
    {

    }

    @Test
    public void requestStayList() throws Exception
    {

    }

    @Test
    public void requestStaySearchAutoCompleteList() throws Exception
    {

    }

    @Test
    public void requestStayRegionList() throws Exception
    {

    }

    @Test
    public void requestStayPaymentInformation() throws Exception
    {

    }

    @Test
    public void requestStayDetailInformation() throws Exception
    {

    }

    @Test
    public void requestStayPayment() throws Exception
    {

    }

    @Test
    public void requestBookingList() throws Exception
    {

    }

    @Test
    public void requestGourmetBookingDetailInformation() throws Exception
    {

    }

    @Test
    public void requestGourmetReceipt() throws Exception
    {

    }

    @Test
    public void requestGourmetHiddenBooking() throws Exception
    {

    }

    @Test
    public void requestGourmetAccountInformation() throws Exception
    {

    }

    @Test
    public void requestGourmetRegionList() throws Exception
    {

    }

    @Test
    public void requestGourmetList() throws Exception
    {

    }

    @Test
    public void requestGourmetSearchAutoCompleteList() throws Exception
    {

    }

    @Test
    public void requestGourmetDetailInformation() throws Exception
    {

    }

    @Test
    public void requestGourmetPaymentInformation() throws Exception
    {

    }

    @Test
    public void requestGourmetCheckTicket() throws Exception
    {

    }

    @Test
    public void requestGourmetPayment() throws Exception
    {

    }

    @Test
    public void requestDepositWaitDetailInformation() throws Exception
    {

    }

    @Test
    public void requestStayBookingDetailInformation() throws Exception
    {

    }

    @Test
    public void requestStayHiddenBooking() throws Exception
    {

    }

    @Test
    public void requestStayReceipt() throws Exception
    {

    }

    @Test
    public void requestEventList() throws Exception
    {

    }

    @Test
    public void requestEventNCouponNNoticeNewCount() throws Exception
    {

    }

    @Test
    public void requestEventPageUrl() throws Exception
    {

    }

    @Test
    public void requestEventBannerList() throws Exception
    {

    }

    @Test
    public void requestDailyUserVerification() throws Exception
    {

    }

    @Test
    public void requestDailyUserUpdatePhoneNumber() throws Exception
    {

    }

    @Test
    public void requestSignupValidation() throws Exception
    {

    }

    @Test
    public void requestDailyUserSignupVerfication() throws Exception
    {

    }

    @Test
    public void requestDailyUserSignup() throws Exception
    {

    }

    @Test
    public void requestFacebookUserSignup() throws Exception
    {

    }

    @Test
    public void requestKakaoUserSignup() throws Exception
    {

    }

    @Test
    public void requestDailyUserSignin() throws Exception
    {

    }

    @Test
    public void requestFacebookUserSignin() throws Exception
    {

    }

    @Test
    public void requestKakaoUserSignin() throws Exception
    {

    }

    @Test
    public void requestCouponList() throws Exception
    {

    }

    @Test
    public void requestCouponList1() throws Exception
    {

    }

    @Test
    public void requestCouponList2() throws Exception
    {

    }

    @Test
    public void requestCouponHistoryList() throws Exception
    {

    }

    @Test
    public void requestNoticeAgreement() throws Exception
    {

    }

    @Test
    public void requestNoticeAgreementResult() throws Exception
    {

    }

    @Test
    public void requestBenefitMessage() throws Exception
    {

    }

    @Test
    public void requestDownloadCoupon() throws Exception
    {

    }

    @Test
    public void requestDownloadEventCoupon() throws Exception
    {

    }

    @Test
    public void requestHasCoupon() throws Exception
    {

    }

    @Test
    public void requestHasCoupon1() throws Exception
    {

    }

    @Test
    public void requestCouponList3() throws Exception
    {

    }

    @Test
    public void requestCouponList4() throws Exception
    {

    }

    @Test
    public void requestRegisterKeywordCoupon() throws Exception
    {

    }

    @Test
    public void requestUpdateBenefitAgreement() throws Exception
    {

    }

    @Test
    public void requestUserTracking() throws Exception
    {

    }

    @Test
    public void requestNoticeList() throws Exception
    {

    }

    @Test
    public void requestReceiptByEmail() throws Exception
    {

    }

    @Test
    public void requestWishListCount() throws Exception
    {

    }

    @Test
    public void requestWishList() throws Exception
    {

    }

    @Test
    public void requestAddWishList() throws Exception
    {

    }

    @Test
    public void requestRemoveWishList() throws Exception
    {

    }

    @Test
    public void requestPolicyRefund() throws Exception
    {

    }

    @Test
    public void requestPolicyRefund1() throws Exception
    {

    }

    @Test
    public void requestRefund() throws Exception
    {

    }

    @Test
    public void requestBankList() throws Exception
    {

    }

    @Test
    public void requestStayReviewInformation() throws Exception
    {

    }

    @Test
    public void requestGourmetReviewInformation() throws Exception
    {

    }

    @Test
    public void requestStayReviewInformation1() throws Exception
    {

    }

    @Test
    public void requestGourmetReviewInformation1() throws Exception
    {

    }

    @Test
    public void requestAddReviewInformation() throws Exception
    {

    }

    @Test
    public void requestAddReviewDetailInformation() throws Exception
    {

    }

}