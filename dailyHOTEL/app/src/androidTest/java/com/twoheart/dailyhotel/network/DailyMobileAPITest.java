package com.twoheart.dailyhotel.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.twoheart.dailyhotel.Const;
import com.twoheart.dailyhotel.DailyAssert;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.User;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    private static final String mNetworkTag = DailyMobileAPITest.class.getSimpleName();
    private static final int COUNT_DOWN_DELEY_TIME = 15;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private CountDownLatch mLock = null;

    private Context mContext;

    private SaleTime mSaleTime;

    // user login
    private String mAuthorization;
    private User mUser;

    @Before
    public void setUp() throws Exception
    {
        ExLog.d("setUp");
        // Context Setting!
        //        mContext = InstrumentationRegistry.getContext();
        mContext = InstrumentationRegistry.getTargetContext(); // 앱 타켓 컨텍스트!

        requestDailyUserLoginBySetUp();
        requestCommonDateTimeBySetUp();
    }

    @After
    public void tearDown() throws Exception
    {
        //        DailyAssert.clearData();

        ExLog.d("tearDown");
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

        mLock = new CountDownLatch(1);

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

                                DailyHotel.AUTHORIZATION = mAuthorization;

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
                                //                                return;
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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestDailyUserLogin(mNetworkTag, params, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Ignore
    public void requestCommonDateTimeBySetUp() throws Exception
    {
        if (mSaleTime != null)
        {
            return;
        }

        mLock = new CountDownLatch(1);

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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
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
            mLock = new CountDownLatch(1);

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
                    mLock.countDown();
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t)
                {
                    DailyAssert.fail(call, t);
                    DailyAssert.clearData();
                    mLock.countDown();
                }
            };

            Map<String, String> params = new HashMap<>();
            params.put("birthday", Const.TEST_USER_BIRTHDAY);
            params.put("user_name", Crypto.getUrlDecoderEx(Const.TEST_USER_NAME));
            params.put("pw", Crypto.getUrlDecoderEx(Const.TEST_PASSWORD));

            DailyMobileAPI.getInstance(mContext).requestUserInformationUpdate(mNetworkTag, params, networkCallback);
            mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
        } else
        {
            DailyAssert.clearData();
        }
    }

    @Test
    public void requestStatusServer() throws Exception
    {
        mLock = new CountDownLatch(1);

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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestStatusServer(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCommonVersion() throws Exception
    {
        mLock = new CountDownLatch(1);

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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestCommonVersion(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCommonDateTime() throws Exception
    {
        mLock = new CountDownLatch(1);

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

                            DailyCalendar.convertDate(currentDate, DailyCalendar.ISO_8601_FORMAT);
                            DailyCalendar.convertDate(dailyDate, DailyCalendar.ISO_8601_FORMAT);
                            DailyCalendar.convertDate(openDate, DailyCalendar.ISO_8601_FORMAT);
                            DailyCalendar.convertDate(closeDate, DailyCalendar.ISO_8601_FORMAT);
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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserProfile() throws Exception
    {
        mLock = new CountDownLatch(1);

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
                            DailyCalendar.convertDate(phoneVerifiedAt, DailyCalendar.ISO_8601_FORMAT);

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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserProfile(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserBonus() throws Exception
    {
        mLock = new CountDownLatch(1);

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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserBonus(mNetworkTag, networkCallBack);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserInformationUpdate() throws Exception
    {
        mLock = new CountDownLatch(1);

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
                            mUser.setBirthDay(Const.TEST_MODIFY_USER_BIRTHDAY);
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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("birthday", Const.TEST_MODIFY_USER_BIRTHDAY);
        params.put("user_name", Const.TEST_MODIFY_USER_NAME);
        params.put("pw", Crypto.getUrlDecoderEx(Const.TEST_MODIFY_PASSWORD));

        DailyMobileAPI.getInstance(mContext).requestUserInformationUpdate(mNetworkTag, params, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserProfileBenefit() throws Exception
    {
        mLock = new CountDownLatch(1);

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
                            DailyAssert.assertNotNull(dataJSONObject);

                            DailyAssert.assertTrue(dataJSONObject.has("bonusAmount"));
                            DailyAssert.assertTrue(dataJSONObject.has("couponTotalCount"));
                            DailyAssert.assertTrue(dataJSONObject.has("exceedLimitedBonus"));

                            DailyAssert.assertTrue(dataJSONObject.getInt("bonusAmount") > 0);
                            DailyAssert.assertTrue(dataJSONObject.getInt("couponTotalCount") > 0);

                            boolean isExceedBonus = dataJSONObject.getBoolean("exceedLimitedBonus");
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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserCheckEmail() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback mUserCheckEmailCallback = new retrofit2.Callback<JSONObject>()
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

                        String result = responseJSONObject.getString("isSuccess");
                        DailyAssert.assertNotNull(result);

                        DailyAssert.assertEquals("true", result);
                        if ("true".equalsIgnoreCase(result) == true)
                        {
                            // do nothing
                        } else
                        {
                            String message = responseJSONObject.getString("msg");
                            DailyAssert.fail(message);
                        }
                    } catch (JSONException e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserCheckEmail(mNetworkTag, Const.TEST_CHECK_EMAIL_ADDRESS, mUserCheckEmailCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserChangePassword() throws Exception
    {
        mLock = new CountDownLatch(1);

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

                        String result = responseJSONObject.getString("isSuccess");
                        DailyAssert.assertEquals("true", result);

                        if ("true".equalsIgnoreCase(result) == true)
                        {
                            // do nothing!
                        } else
                        {
                            String message = responseJSONObject.getString("msg");
                            DailyAssert.fail(message);
                        }
                    } catch (JSONException e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserChangePassword(mNetworkTag, Crypto.getUrlDecoderEx(Const.TEST_EMAIL), networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserInformationForPayment() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback mUserInformationFinalCheckCallback = new retrofit2.Callback<JSONObject>()
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

                            String name = dataJSONObject.getString("user_name");
                            String phone = dataJSONObject.getString("user_phone");
                            String email = dataJSONObject.getString("user_email");
                            String userIndex = dataJSONObject.getString("user_idx");

                            DailyAssert.assertNotNull(name);
                            DailyAssert.assertNotNull(phone);
                            DailyAssert.assertNotNull(email);
                            DailyAssert.assertNotNull(userIndex);

                            if (mUser != null)
                            {
                                DailyAssert.assertEquals(mUser.getName(), name);
                                DailyAssert.assertEquals(mUser.getPhone(), phone);
                                DailyAssert.assertEquals(mUser.getEmail(), email);
                                DailyAssert.assertEquals(mUser.getUserIdx(), userIndex);
                            }

                            int bonus = dataJSONObject.getInt("user_bonus");
                            DailyAssert.assertTrue(bonus >= 0);
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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserInformationForPayment(mNetworkTag, mUserInformationFinalCheckCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserUpdateInformationForSocial() throws Exception
    {
        mLock = new CountDownLatch(1);

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
                        DailyAssert.assertEquals(100, msgCode);

                        if (msgCode == 100)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                            DailyAssert.assertNotNull(dataJSONObject);

                            boolean result = dataJSONObject.getBoolean("is_success");
                            DailyAssert.assertTrue(result);

                            String serverDate = dataJSONObject.getString("serverDate");
                            DailyAssert.assertNotNull(serverDate);

                            if (result == true)
                            {
                                // do nothing
                            } else
                            {
                                String message = responseJSONObject.getString("msg");
                                DailyAssert.fail(message);
                            }
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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("user_idx", Crypto.getUrlDecoderEx(Const.TEST_FACEBOOK_USER_INDEX));
        params.put("user_email", Crypto.getUrlDecoderEx(Const.TEST_FACEBOOK_USER_EMAIL));
        params.put("user_name", Crypto.getUrlDecoderEx(Const.TEST_FACEBOOK_USER_NAME));
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

        DailyMobileAPI.getInstance(mContext).requestUserUpdateInformationForSocial(mNetworkTag, params, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserBillingCardList() throws Exception
    {
        mLock = new CountDownLatch(1);

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
                        DailyAssert.assertEquals(100, msgCode);

                        JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                        DailyAssert.assertNotNull(dataJSONArray);

                        ArrayList<CreditCard> creditCardArrayList = new ArrayList<>();

                        int length = dataJSONArray.length();
                        if (length == 0)
                        {
                            // do nothing
                        } else
                        {
                            JSONObject jsonObject;
                            for (int i = 0; i < length; i++)
                            {
                                jsonObject = dataJSONArray.getJSONObject(i);

                                // 목록에서는 빌링키가 필요없다.
                                CreditCard creditCard = new CreditCard(//
                                    jsonObject.getString("card_name"), //
                                    jsonObject.getString("print_cardno"),//
                                    jsonObject.getString("billkey"), //
                                    jsonObject.getString("cardcd"));

                                creditCardArrayList.add(creditCard);

                                DailyAssert.assertFalse("카드 이름", Util.isTextEmpty(creditCard.name));
                                DailyAssert.assertFalse("카드 번호", Util.isTextEmpty(creditCard.number));
                                DailyAssert.assertFalse("카드 키", Util.isTextEmpty(creditCard.billingkey));
                                DailyAssert.assertFalse("카드 사명", Util.isTextEmpty(creditCard.cardcd));

                                String cardCds = CreditCard.getCardCDName(mContext, creditCard.cardcd);
                                DailyAssert.assertFalse("카드사명 찾기 실패", Util.isTextEmpty(cardCds));
                            }

                            if (creditCardArrayList.size() > 0)
                            {
                                String checkNum = Crypto.getUrlDecoderEx(Const.TEST_SKIP_DELETE_CREDITCARD_NUMBER);

                                boolean isNeedCheck = Util.isTextEmpty(checkNum) == false ? true : false;
                                if (isNeedCheck == false)
                                {
                                    CreditCard creditCard = creditCardArrayList.get(creditCardArrayList.size() - 1);
                                    requestUserDeleteBillingCard(creditCard.billingkey);
                                } else
                                {
                                    for (int i = creditCardArrayList.size() - 1; i >= 0; i--)
                                    {
                                        CreditCard creditCard = creditCardArrayList.get(i);
                                        if (checkNum.equalsIgnoreCase(creditCard.number) == false)
                                        {
                                            requestUserDeleteBillingCard(creditCard.billingkey);
                                            break;
                                        }
                                    }
                                }
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

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserBillingCardList(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Ignore
    public void requestUserDeleteBillingCard(String billingkey) throws Exception
    {
        mLock = new CountDownLatch(1);

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
                        DailyAssert.assertEquals(100, msgCode);

                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        DailyAssert.assertNotNull(dataJSONObject);

                        boolean result = false;

                        if (dataJSONObject != null)
                        {
                            result = dataJSONObject.getInt("isSuccess") == 1;
                        }

                        DailyAssert.assertTrue(result);
                    } catch (Exception e)
                    {
                        DailyAssert.fail(e);
                    }
                } else
                {
                    DailyAssert.fail();
                }

                mLock.countDown();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                DailyAssert.fail(call, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserDeleteBillingCard(mNetworkTag, billingkey, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //
        //        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
        //        {
        //            @Override
        //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        //            {
        //                DailyAssert.setData(call, response);
        //
        //                if (response != null && response.isSuccessful() && response.body() != null)
        //                {
        //                    int hotelSaleCount;
        //
        //                    try
        //                    {
        //                        JSONObject responseJSONObject = response.body();
        //                        DailyAssert.assertNotNull(responseJSONObject);
        //
        //                        int msgCode = responseJSONObject.getInt("msgCode");
        //                        DailyAssert.assertEquals(100, msgCode);
        //                        if (msgCode == 100)
        //                        {
        //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
        //                            DailyAssert.assertNotNull(dataJSONObject);
        //
        //                            hotelSaleCount = dataJSONObject.getInt("hotelSalesCount");
        //                            DailyAssert.assertNotNull(hotelSaleCount);
        //                        } else
        //                        {
        //                            hotelSaleCount = 0;
        //                        }
        //                    } catch (Exception e)
        //                    {
        //                        hotelSaleCount = 0;
        //                    }
        //
        //
        //                } else
        //                {
        //                    DailyAssert.fail();
        //                }
        //
        //        mLock.countDown();
        //            }
        //
        //            @Override
        //            public void onFailure(Call<JSONObject> call, Throwable t)
        //            {
        //                mOnNetworkControllerListener.onError(t);
        //                mLock.countDown();
        //            }
        //        };
        //
        //        HashMap<String, Object> queryhMap = new HashMap<>();
        //
        //        queryhMap.put("dateCheckIn", dateCheckIn);
        //        queryhMap.put("stays", stays);
        //        queryhMap.put("provinceIdx", provinceIdx);
        //
        //        if (areaIdx != 0)
        //        {
        //            queryhMap.put("areaIdx", areaIdx);
        //        }
        //
        //        if (persons != 0)
        //        {
        //            queryhMap.put("persons", persons);
        //        }
        //
        //        if (category != null && Category.ALL.code.equalsIgnoreCase(category.code) == false)
        //        {
        //            queryhMap.put("category", category.code);
        //        }
        //
        //        //        if(mBedTypeList != null && mBedTypeList.size() > 0)
        //        //        {
        //        //            hashMap.put("bedType", mBedTypeList);
        //        //        }
        //        //
        //        //        if(mLuxuryList != null && mLuxuryList.size() > 0)
        //        //        {
        //        //            hashMap.put("luxury", mLuxuryList);
        //        //        }
        //
        //        if (page > 0)
        //        {
        //            queryhMap.put("page", page);
        //            queryhMap.put("limit", limit);
        //        }
        //
        //        if (Constants.SortType.DEFAULT != mSort)
        //        {
        //            if (Util.isTextEmpty(sortProperty) == false)
        //            {
        //                queryhMap.put("sortProperty", sortProperty);
        //            }
        //
        //            if (Util.isTextEmpty(sortDirection) == false)
        //            {
        //                queryhMap.put("sortDirection", sortDirection);
        //            }
        //
        //            if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
        //            {
        //                queryhMap.put("latitude", latitude);
        //                queryhMap.put("longitude", longitude);
        //            }
        //        }
        //
        //        queryhMap.put("details", details);
        //
        //        DailyMobileAPI.getInstance(mContext).requestStayList(mNetworkTag, params.toParamsMap(), params.getBedTypeList(), params.getLuxuryList(), networkCallback);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStaySearchAutoCompleteList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayRegionList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayPaymentInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayDetailInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayPayment() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestBookingList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetBookingDetailInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetReceipt() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetHiddenBooking() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetAccountInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetRegionList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetSearchAutoCompleteList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetDetailInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetPaymentInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetCheckTicket() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetPayment() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestDepositWaitDetailInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayBookingDetailInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayHiddenBooking() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayReceipt() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestEventList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestEventNCouponNNoticeNewCount() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestEventPageUrl() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestEventBannerList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestDailyUserVerification() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestDailyUserUpdatePhoneNumber() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestSignupValidation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestDailyUserSignupVerfication() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestDailyUserSignup() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestFacebookUserSignup() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestKakaoUserSignup() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestDailyUserSignin() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestFacebookUserSignin() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestKakaoUserSignin() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList1() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList2() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponHistoryList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestNoticeAgreement() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestNoticeAgreementResult() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestBenefitMessage() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestDownloadCoupon() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestDownloadEventCoupon() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestHasCoupon() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestHasCoupon1() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList3() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList4() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestRegisterKeywordCoupon() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUpdateBenefitAgreement() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserTracking() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestNoticeList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestReceiptByEmail() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestWishListCount() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestWishList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestAddWishList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestRemoveWishList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestPolicyRefund() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestPolicyRefund1() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestRefund() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestBankList() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayReviewInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetReviewInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayReviewInformation1() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetReviewInformation1() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestAddReviewInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestAddReviewDetailInformation() throws Exception
    {
        //        mLock = new CountDownLatch(1);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

}