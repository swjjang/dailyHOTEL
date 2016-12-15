package com.twoheart.dailyhotel.network;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.ApplicationTest;
import com.twoheart.dailyhotel.Const;
import com.twoheart.dailyhotel.NetworkApiTest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

        DailyNetworkAPI.getInstance(application).requestDailyUserSignin(mNetworkTag, params, new DailyHotelJsonResponseListener()
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
                            mAuthorization = String.format("%s %s", tokenType, accessToken);

                            DailyAssert.assertNotNull(accessToken);
                            DailyAssert.assertNotNull(tokenType);

                            JSONObject userJSONObject = dataJSONObject.getJSONObject("user");
                            DailyAssert.assertNotNull(userJSONObject);

                            mUser = new User();
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
        };

        DailyNetworkAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, responseListener);
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
        DailyHotelJsonResponseListener stayResponseListener = new DailyHotelJsonResponseListener()
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

        DailyNetworkAPI.getInstance(mContext).requestCommonReview(mNetworkTag, "hotel", stayResponseListener);

        DailyHotelJsonResponseListener gourmetResponseListener = new DailyHotelJsonResponseListener()
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
        DailyNetworkAPI.getInstance(mContext).requestCommonReview(mNetworkTag, "gourmet", gourmetResponseListener);
    }

    public void testRequestCommonDateTime() throws Exception
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
                        JSONObject dataJSONObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(dataJSONObject);

                        long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long openDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("openDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long closeDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("closeDateTime"), DailyCalendar.ISO_8601_FORMAT);

                        DailyAssert.assertNotNull(currentDateTime);
                        DailyAssert.assertNotNull(dailyDateTime);
                        DailyAssert.assertNotNull(openDateTime);
                        DailyAssert.assertNotNull(closeDateTime);

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
        };

        DailyNetworkAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, responseListener);
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

                        String userIndex = jsonObject.getString("userIdx");
                        String userType = jsonObject.has("userType") == true ? jsonObject.getString("userType") : AnalyticsManager.ValueType.EMPTY;

                        String userEmail = jsonObject.getString("email");
                        String userName = jsonObject.getString("name");
                        String userPhone = jsonObject.getString("phone");

                        String referralCode = jsonObject.getString("referralCode"); // 자신의 추천 번호

                        DailyAssert.assertNotNull(userIndex);
                        DailyAssert.assertNotSame(AnalyticsManager.ValueType.EMPTY, userType);
                        DailyAssert.assertTrue(Constants.DAILY_USER.equalsIgnoreCase(userType) //
                            || Constants.KAKAO_USER.equalsIgnoreCase(userType) //
                            || Constants.FACEBOOK_USER.equalsIgnoreCase(userType));

                        DailyAssert.assertTrue(Pattern.matches(Const.REGEX_EMAIL_FORMAT, userEmail));
                        DailyAssert.assertNotNull(userName);
                        DailyAssert.assertNotNull(userPhone);
                        DailyAssert.assertNotNull(referralCode);

                        DailyAssert.assertTrue(jsonObject.has("agreedBenefit")); // 값 존재 유무만 필요!

                        boolean isVerified = jsonObject.getBoolean("verified");
                        boolean isPhoneVerified = jsonObject.getBoolean("phoneVerified");

                        // 인증 후 인증이 해지된 경우
                        if (isVerified == true && isPhoneVerified == false && DailyPreference.getInstance(mContext).isVerification() == true)
                        {
                            DailyAssert.fail("인증 만료");
                        }
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
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                //적립금 내역리스트
                try
                {
                    int msgCode = response.getInt("msgCode");
                    DailyAssert.assertEquals(100, msgCode);

                    if (msgCode == 100)
                    {
                        JSONArray jsonArray = response.getJSONArray("history");

                        // DailyAssert.assertNotNull(jsonArray); // 적립금이 있을수도 없을수도 있음

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
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                DailyAssert.fail(volleyError);
            }
        };

        DailyNetworkAPI.getInstance(mContext).requestUserBonus(mNetworkTag, responseListener);
    }


    public void testRequestUserInformationUpdate() throws Exception
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
        Map<String, String> params = new HashMap<>();
        params.put("birthday", Const.TEST_USER_BIRTHDAY);
        params.put("user_name", Const.TEST_USER_NAME);
        params.put("pw", Const.TEST_MODIFY_PASSWORD);
        DailyNetworkAPI.getInstance(mContext).requestUserInformationUpdate(mNetworkTag, params, responseListener);

        // password 원복
        Map<String, String> params2 = Collections.singletonMap("pw", Const.TEST_PASSWORD);
        DailyNetworkAPI.getInstance(mContext).requestUserInformationUpdate(mNetworkTag, params2, new DailyHotelJsonResponseListener()
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
        });
    }


    public void testRequestUserProfileBenefit() throws Exception
    {
        DailyHotelJsonResponseListener mUserProfileBenefitJsonResponseListener = new DailyHotelJsonResponseListener()
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


                        DailyAssert.assertTrue(jsonObject.has("bonusAmount"));
                        DailyAssert.assertTrue(jsonObject.has("couponTotalCount"));
                        DailyAssert.assertTrue(jsonObject.has("exceedLimitedBonus"));

                        int bonus = jsonObject.getInt("bonusAmount");
                        int couponTotalCount = jsonObject.getInt("couponTotalCount");
                        boolean isExceedBonus = jsonObject.getBoolean("exceedLimitedBonus");
                        DailyAssert.assertTrue("bonus : " + bonus + " , couponTotalCount : "//
                            + couponTotalCount + ", isExceedBonus : " + isExceedBonus, true);
                    } else
                    {
                        String msg = response.getString("msg");
                        DailyAssert.fail(msg);
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

        DailyNetworkAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserProfileBenefitJsonResponseListener);
    }


    public void testRequestUserCheckEmail() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    String result = response.getString("isSuccess");
                    DailyAssert.assertEquals("isSuccess", "true", result);

                    if ("true".equalsIgnoreCase(result) == true)
                    {
                        // do nothing!
                    } else
                    {
                        String message = response.getString("msg");
                        DailyAssert.fail(message);
                    }
                } catch (JSONException e)
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

        DailyNetworkAPI.getInstance(mContext).requestUserCheckEmail(mNetworkTag, Const.TEST_CHECK_EMAIL_ADDRESS, responseListener);
    }


    public void testRequestUserChangePassword() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    String result = response.getString("isSuccess");
                    DailyAssert.assertEquals("isSuccess", "true", result);

                    if ("true".equalsIgnoreCase(result) == true)
                    {
                        // do nothing!
                    } else
                    {
                        String message = response.getString("msg");
                        DailyAssert.fail(message);
                    }
                } catch (JSONException e)
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

        DailyNetworkAPI.getInstance(mContext).requestUserChangePassword(mNetworkTag, //
            DailyHotelJsonRequest.getUrlDecoderEx(Const.TEST_EMAIL), responseListener);
    }

    @Deprecated
    public void testRequestUserInformationEx() throws Exception
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
                        // do nothing!
                    } else
                    {
                        String msg = response.getString("msg");
                        DailyAssert.fail(msg);
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

        DailyNetworkAPI.getInstance(mContext).requestUserInformationEx(mNetworkTag, responseListener);
    }


    public void testRequestUserInformationForPayment() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msg_code");
                    DailyAssert.assertEquals(0, msgCode);

                    if (msgCode == 0)
                    {
                        JSONObject jsonObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(jsonObject);

                        int bonus = jsonObject.getInt("user_bonus");
                        DailyAssert.assertTrue("bonus is minus", bonus > 0);

                        String name = jsonObject.getString("user_name");
                        String phone = jsonObject.getString("user_phone");
                        String email = jsonObject.getString("user_email");
                        String userIndex = jsonObject.getString("user_idx");

                        DailyAssert.assertEquals(mUser.getName(), name);
                        DailyAssert.assertEquals(mUser.getPhone(), phone);
                        DailyAssert.assertEquals(mUser.getEmail(), email);
                        DailyAssert.assertEquals(mUser.getUserIdx(), userIndex);
                    } else
                    {
                        DailyAssert.fail(msgCode + " : " + response.getString("msg"));
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

        DailyNetworkAPI.getInstance(mContext).requestUserInformationForPayment(mNetworkTag, responseListener);
    }


    public void testRequestUserUpdateInformationForSocial() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msg_code");
                    DailyAssert.assertEquals(100, msgCode);

                    if (msgCode == 100)
                    {
                        JSONObject jsonObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(jsonObject);

                        boolean result = jsonObject.getBoolean("is_success");
                        DailyAssert.assertTrue("is_success", result);

                        String serverDate = jsonObject.getString("serverDate");
                        DailyAssert.assertNotNull(serverDate);
                        DailyAssert.assertTrue(Pattern.matches(DailyCalendar.ISO_8601_FORMAT, serverDate));

                        if (result == true)
                        {
                            // do nothing!
                        } else
                        {
                            String message = response.getString("msg");
                            DailyAssert.fail(message);
                        }
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

        Map<String, String> params = new HashMap<>();
        params.put("user_idx", DailyHotelJsonRequest.getUrlDecoderEx(Const.TEST_FACEBOOK_USER_INDEX));
        params.put("user_email", DailyHotelJsonRequest.getUrlDecoderEx(Const.TEST_FACEBOOK_USER_EMAIL));
        params.put("user_name", DailyHotelJsonRequest.getUrlDecoderEx(Const.TEST_FACEBOOK_USER_NAME));
        params.put("birthday", Const.TEST_USER_BIRTHDAY);

        // 전화번호 일단 패스
        String phoneNumber = "";
        if (Util.isTextEmpty(phoneNumber) == false)
        {
            params.put("user_phone", phoneNumber.replaceAll("-", ""));
        }

        // 추천도 일단 패스
        String recommender = "";
        if (Util.isTextEmpty(recommender) == false)
        {
            params.put("recommendation_code", recommender);
        }

        // 베내핏도 패스
        boolean isBenefit = true;
        params.put("isAgreedBenefit", isBenefit == true ? "true" : "false");

        DailyNetworkAPI.getInstance(mContext).requestUserUpdateInformationForSocial(mNetworkTag, params, responseListener);
    }


    public void testRequestUserBillingCardList() throws Exception
    {
        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msg_code");
                    DailyAssert.assertEquals(100, msgCode);

                    JSONArray jsonArray = response.getJSONArray("data");
                    DailyAssert.assertNotNull(jsonArray);

                    ArrayList<CreditCard> creditCardArrayList = new ArrayList<>();

                    int length = jsonArray.length();
                    if (length == 0)
                    {
                        // do nothing!
                    } else
                    {
                        JSONObject jsonObject;
                        for (int i = 0; i < length; i++)
                        {
                            jsonObject = jsonArray.getJSONObject(i);

                            // 목록에서는 빌링키가 필요없다.
                            CreditCard creditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"),//
                                jsonObject.getString("billkey"), //
                                jsonObject.getString("cardcd"));

                            creditCardArrayList.add(creditCard);

                            DailyAssert.assertFalse("카드 이름", Util.isTextEmpty(creditCard.name));
                            DailyAssert.assertFalse("카드 번호", Util.isTextEmpty(creditCard.number));
                            DailyAssert.assertFalse("카드 키", Util.isTextEmpty(creditCard.billingkey));
                            DailyAssert.assertFalse("카드 사명", Util.isTextEmpty(creditCard.cardcd));

                            String cardcds = CreditCard.getCardCDName(mContext, creditCard.cardcd);
                            DailyAssert.assertFalse("카드사명 찾기 실패", Util.isTextEmpty(cardcds));
                        }
                    }

                    if (creditCardArrayList.size() > 0)
                    {
                        String checkNum = DailyHotelJsonRequest.getUrlDecoderEx(Const.TEST_SKIP_DELETE_CREDITCARD_NUMBER);
                        if (Util.isTextEmpty(checkNum) == false)
                        {
                            for (int i = creditCardArrayList.size() - 1; i >= 0; i--)
                            {
                                CreditCard creditCard = creditCardArrayList.get(i);
                                if (checkNum.equalsIgnoreCase(creditCard.number) == false)
                                {
                                    ignore_testRequestUserDeleteBillingCard(creditCard.billingkey);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e)
                {
                    // 해당 화면 에러시에는 일반 결제가 가능해야 한다.
                    DailyAssert.fail(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                DailyAssert.fail(volleyError);
            }
        };

        DailyNetworkAPI.getInstance(mContext).requestUserBillingCardList(mNetworkTag, responseListener);
    }

    // 위 리스트 테스트 와 병행하여 처리 함
    public void ignore_testRequestUserDeleteBillingCard(String billingKey) throws Exception
    {
        if (Util.isTextEmpty(billingKey) == true)
        {
            DailyAssert.fail("billingKey is null");
            return;
        }

        DailyHotelJsonResponseListener responseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msg_code");
                    DailyAssert.assertEquals(100, msgCode);

                    JSONObject jsonObject = response.getJSONObject("data");
                    DailyAssert.assertNotNull(jsonObject);

                    boolean result = false;
                    if (jsonObject != null)
                    {
                        result = jsonObject.getInt("isSuccess") == 1;
                    }

                    DailyAssert.assertTrue("result", result);
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

        DailyNetworkAPI.getInstance(mContext).requestUserDeleteBillingCard(mNetworkTag, billingKey, responseListener);
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