package com.twoheart.dailyhotel.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.twoheart.dailyhotel.Const;
import com.twoheart.dailyhotel.DailyAssert;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

import static com.twoheart.dailyhotel.Const.TEST_MIN_RATING_PERSONS;

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
            params.put("user_name", Const.TEST_USER_NAME);
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

                            DailyAssert.assertNotNull(dataJSONObject.has("exceedLimitedBonus"));
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

        DailyMobileAPI.getInstance(mContext).requestUserCheckEmail(mNetworkTag, Const.TEST_CHECK_EMAIL_ADDRESS, networkCallback);
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

                            int hotelSaleCount = dataJSONObject.getInt("hotelSalesCount");
                            DailyAssert.assertNotNull(hotelSaleCount);

                            DailyAssert.assertEquals(Const.TEST_IS_SHOW_LIST_DETAIL, dataJSONObject.has("hotelSales"));

                            JSONArray hotelJSONArray = null;

                            if (dataJSONObject.has("hotelSales") == true)
                            {
                                hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
                            }

                            if (hotelJSONArray != null)
                            {
                                String imageUrl = dataJSONObject.getString("imgUrl");
                                DailyAssert.assertNotNull(imageUrl);

                                int nights = dataJSONObject.getInt("stays");
                                DailyAssert.assertEquals(Const.TEST_NIGHTS, nights);

                                int length = hotelJSONArray.length();
                                JSONObject jsonObject;
                                for (int i = 0; i < length; i++)
                                {
                                    jsonObject = hotelJSONArray.getJSONObject(i);
                                    DailyAssert.assertNotNull(jsonObject);

                                    try
                                    {
                                        String name = jsonObject.getString("name");
                                        int price = jsonObject.getInt("price");
                                        int discountPrice = jsonObject.getInt("discount"); // discountAvg ????
                                        String addressSummary = jsonObject.getString("addrSummary");

                                        DailyAssert.assertNotNull(name);
                                        DailyAssert.assertNotNull(price);
                                        DailyAssert.assertNotNull(discountPrice);
                                        DailyAssert.assertNotNull(addressSummary);

                                        try
                                        {
                                            Stay.Grade.valueOf(jsonObject.getString("grade"));
                                        } catch (Exception e)
                                        {
                                            DailyAssert.fail(e);
                                        }

                                        DailyAssert.assertNotNull(jsonObject.getInt("hotelIdx"));

                                        if (jsonObject.has("isSoldOut") == true)
                                        {
                                            boolean isSoldOut = jsonObject.getBoolean("isSoldOut");
                                        }

                                        DailyAssert.assertNotNull(jsonObject.getString("districtName"));
                                        DailyAssert.assertNotNull(jsonObject.getString("category"));
                                        DailyAssert.assertNotNull(jsonObject.getDouble("latitude"));
                                        DailyAssert.assertNotNull(jsonObject.getDouble("longitude"));
                                        DailyAssert.assertTrue(jsonObject.has("isDailyChoice"));
                                        DailyAssert.assertNotNull(jsonObject.getInt("rating")); // ratingValue ??
                                        DailyAssert.assertNotNull(jsonObject.getString("sday"));
                                        DailyAssert.assertNotNull(jsonObject.getDouble("distance"));

                                        JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
                                        DailyAssert.assertNotNull(imageJSONObject);

                                        String stayImageUrl = null;
                                        Iterator<String> iterator = imageJSONObject.keys();
                                        while (iterator.hasNext())
                                        {
                                            String key = iterator.next();

                                            try
                                            {
                                                JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
                                                stayImageUrl = imageUrl + key + pathJSONArray.getString(0);
                                                break;
                                            } catch (JSONException e)
                                            {
                                                DailyAssert.fail(e);
                                            }
                                        }

                                        DailyAssert.assertNotNull(stayImageUrl);

                                        if (jsonObject.has("benefit") == true) // hotelBenefit ?
                                        {
                                            DailyAssert.assertNotNull(jsonObject.getString("benefit"));
                                        }
                                    } catch (JSONException e)
                                    {
                                        DailyAssert.fail(e);
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

        HashMap<String, Object> paramMap = new HashMap<>();

        paramMap.put("dateCheckIn", mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
        paramMap.put("stays", Const.TEST_NIGHTS);
        paramMap.put("provinceIdx", Const.TEST_PROVINCE_INDEX);
        // area skip
        //        queryMap.put("areaIdx", areaIdx);

        paramMap.put("persons", Const.TEST_PERSONS);
        if (Category.ALL.code.equalsIgnoreCase(Const.TEST_CATEGORY_CODE) == false)
        {
            paramMap.put("category", Const.TEST_CATEGORY_CODE);
        }

        //        if(mBedTypeList != null && mBedTypeList.size() > 0)
        //        {
        //            hashMap.put("bedType", mBedTypeList);
        //        }
        //
        //        if(mLuxuryList != null && mLuxuryList.size() > 0)
        //        {
        //            hashMap.put("luxury", mLuxuryList);
        //        }

        if (Const.TEST_PAGE_INDEX > 0)
        {
            paramMap.put("page", Const.TEST_PAGE_INDEX);
            paramMap.put("limit", Const.TEST_LIMIT_LIST_COUNT);
        }

        // sort skip
        //        if (Constants.SortType.DEFAULT != mSort)
        //        {
        //            if (Util.isTextEmpty(sortProperty) == false)
        //            {
        //                queryMap.put("sortProperty", sortProperty);
        //            }
        //
        //            if (Util.isTextEmpty(sortDirection) == false)
        //            {
        //                queryMap.put("sortDirection", sortDirection);
        //            }
        //
        //            if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
        //            {
        //                queryMap.put("latitude", latitude);
        //                queryMap.put("longitude", longitude);
        //            }
        //        }

        paramMap.put("details", Const.TEST_IS_SHOW_LIST_DETAIL);

        DailyMobileAPI.getInstance(mContext).requestStayList(mNetworkTag, paramMap, null, null, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStaySearchAutoCompleteList() throws Exception
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
                    String keyword = call.request().url().queryParameter("term");

                    List<Keyword> keywordList = null;

                    try
                    {
                        JSONObject responseJSONObject = response.body();
                        DailyAssert.assertNotNull(responseJSONObject);

                        int msgCode = responseJSONObject.getInt("msgCode");
                        DailyAssert.assertEquals(100, msgCode);

                        if (msgCode == 100)
                        {
                            JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                            DailyAssert.assertNotNull(dataJSONArray);

                            ArrayList<String> etcList = new ArrayList<>();

                            int length = dataJSONArray.length();
                            for (int i = 0; i < length; i++)
                            {
                                JSONObject jsonObject = dataJSONArray.getJSONObject(i);
                                DailyAssert.assertNotNull(jsonObject);

                                String name = jsonObject.getString("displayText");
                                if (Util.isTextEmpty(name) == true || name.contains(keyword) == false)
                                {
                                    etcList.add(name);
                                }

                                if (etcList.size() > 0)
                                {
                                    DailyAssert.fail("keyword is not contain list, keyword : " + keyword + " , list : " + etcList.toString());
                                }

                                if (jsonObject.has("discount") == true)
                                {
                                    DailyAssert.assertNotNull(jsonObject.getInt("discount"));
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

        DailyMobileAPI.getInstance(mContext).requestStaySearchAutoCompleteList(mNetworkTag//
            , mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), Const.TEST_NIGHTS, Const.TEST_AUTO_SEARCH_TEXT, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayRegionList() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback mRegionListCallback = new retrofit2.Callback<JSONObject>()
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

                            JSONArray provinceArray = dataJSONObject.getJSONArray("regionProvince");
                            ArrayList<Province> provinceList = makeProvinceList(provinceArray);
                            DailyAssert.assertNotNull(provinceList);

                            JSONArray areaJSONArray = dataJSONObject.getJSONArray("regionArea");
                            ArrayList<Area> areaList = makeAreaList(areaJSONArray);
                            DailyAssert.assertNotNull(areaList);

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

            private ArrayList<Province> makeProvinceList(JSONArray jsonArray)
            {
                ArrayList<Province> provinceList = new ArrayList<>();

                try
                {
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        try
                        {
                            Province province = new Province(jsonObject, null);
                            provinceList.add(province);
                        } catch (JSONException e)
                        {
                            DailyAssert.fail(e);
                        }
                    }
                } catch (Exception e)
                {
                    DailyAssert.fail(e);
                }

                return provinceList;
            }

            private ArrayList<Area> makeAreaList(JSONArray jsonArray) throws JSONException
            {
                ArrayList<Area> areaList = new ArrayList<>();

                int length = jsonArray.length();
                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try
                    {
                        Area area = new Area(jsonObject);
                        areaList.add(area);
                    } catch (JSONException e)
                    {
                        DailyAssert.fail(e);
                    }
                }

                return areaList;
            }
        };

        DailyMobileAPI.getInstance(mContext).requestStayRegionList(mNetworkTag, mRegionListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayPaymentInformation() throws Exception
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
                        // 0	성공
                        // 4	데이터가 없을시
                        // 5	판매 마감시
                        // 6	현재 시간부터 날짜 바뀌기 전시간(새벽 3시
                        // 7    3시부터 9시까지
                        switch (msgCode)
                        {
                            case 6:
                            case 7:
                                if (responseJSONObject.has("msg") == true)
                                {
                                    DailyAssert.assertNotNull(responseJSONObject.getString("msg"));
                                }
                            case 0:
                            {
                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                                DailyAssert.assertNotNull(dataJSONObject);

                                long checkInDate = dataJSONObject.getLong("check_in_date");
                                long checkOutDate = dataJSONObject.getLong("check_out_date");
                                int discount = dataJSONObject.getInt("discount_total");
                                int availableRooms = dataJSONObject.getInt("available_rooms");

                                DailyAssert.assertNotNull(checkInDate);
                                DailyAssert.assertNotNull(checkOutDate);
                                DailyAssert.assertNotNull(discount);
                                DailyAssert.assertNotNull(availableRooms);

                                DailyAssert.assertTrue(dataJSONObject.has("on_sale"));
                                DailyAssert.assertTrue(dataJSONObject.has("refund_type"));
                                break;
                            }

                            case 5:
                            {
                                if (responseJSONObject.has("msg") == true)
                                {
                                    String msg = responseJSONObject.getString("msg");
                                    DailyAssert.fail(msg);
                                } else
                                {
                                    DailyAssert.fail();
                                }
                                break;
                            }

                            case 4:
                            default:
                                if (responseJSONObject.has("msg") == true)
                                {
                                    String msg = responseJSONObject.getString("msg");
                                    DailyAssert.fail(msg);
                                } else
                                {
                                    DailyAssert.fail();
                                }
                                break;
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

        DailyMobileAPI.getInstance(mContext).requestStayPaymentInformation(mNetworkTag//
            , Const.TEST_STAY_SALE_ROOM_INDEX//
            , mSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
            , Const.TEST_NIGHTS, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayDetailInformation() throws Exception
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

                        JSONObject dataJSONObject = null;

                        if (responseJSONObject.has("data") == true && responseJSONObject.isNull("data") == false)
                        {
                            dataJSONObject = responseJSONObject.getJSONObject("data");
                        }

                        if (msgCode == 100 && dataJSONObject == null)
                        {
                            msgCode = 4;
                        }

                        // 100	성공
                        // 4	데이터가 없을시
                        // 5	판매 마감시
                        switch (msgCode)
                        {
                            case 100:
                                checkStayDetail(dataJSONObject);
                                break;

                            case 5:
                            {
                                checkStayDetail(dataJSONObject);

                                if (responseJSONObject.has("msg") == true)
                                {
                                    String msg = responseJSONObject.getString("msg");
                                    DailyAssert.fail(msg);
                                } else
                                {
                                    DailyAssert.fail();
                                }
                                break;
                            }

                            case 4:
                            default:
                            {
                                if (responseJSONObject.has("msg") == true)
                                {
                                    String msg = responseJSONObject.getString("msg");
                                    DailyAssert.fail(msg);
                                } else
                                {
                                    DailyAssert.fail();
                                }
                                break;
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

            private void checkStayDetail(JSONObject jsonObject) throws Exception
            {
                Stay.Grade grade = Stay.Grade.valueOf(jsonObject.getString("grade"));
                DailyAssert.assertNotNull(grade);

                DailyAssert.assertNotNull(jsonObject.getString("name"));
                DailyAssert.assertNotNull(jsonObject.getString("address"));

                DailyAssert.assertNotNull(jsonObject.getDouble("longitude"));
                DailyAssert.assertNotNull(jsonObject.getDouble("latitude"));
                DailyAssert.assertNotNull(jsonObject.getBoolean("overseas"));

                boolean ratingShow = jsonObject.getBoolean("ratingShow");
                if (ratingShow == true)
                {
                    int ratingValue = jsonObject.getInt("ratingValue");
                    int ratingPersons = jsonObject.getInt("ratingPersons");

                    DailyAssert.assertTrue(ratingValue >= 0);
                    DailyAssert.assertTrue(ratingPersons >= TEST_MIN_RATING_PERSONS);
                }

                // Pictrogram
                // 주차
                DailyAssert.assertNotNull(jsonObject.has("parking"));
                // 주차금지
                DailyAssert.assertNotNull(jsonObject.has("noParking"));
                // 수영장
                DailyAssert.assertNotNull(jsonObject.has("pool"));
                // 피트니스
                DailyAssert.assertNotNull(jsonObject.has("fitness"));
                // 애완동물
                DailyAssert.assertNotNull(jsonObject.has("pet"));
                // 바베큐
                DailyAssert.assertNotNull(jsonObject.has("sharedBbq"));

                // Image Url
                String imageUrl = jsonObject.getString("imgUrl");
                DailyAssert.assertNotNull(imageUrl);

                JSONObject pathUrlJSONObject = jsonObject.getJSONObject("imgPath");
                DailyAssert.assertNotNull(pathUrlJSONObject);

                Iterator<String> iterator = pathUrlJSONObject.keys();
                while (iterator.hasNext())
                {
                    String key = iterator.next();

                    try
                    {
                        JSONArray pathJSONArray = pathUrlJSONObject.getJSONArray(key);
                        DailyAssert.assertNotNull(pathJSONArray);

                        int length = pathJSONArray.length();
                        for (int i = 0; i < length; i++)
                        {
                            JSONObject imageInformationJSONObject = pathJSONArray.getJSONObject(i);
                            DailyAssert.assertNotNull(imageInformationJSONObject);

                            String description = imageInformationJSONObject.getString("description");
                            DailyAssert.assertNotNull(description);

                            String imageFullUrl = imageUrl + key + imageInformationJSONObject.getString("name");
                            DailyAssert.assertNotNull(imageFullUrl);
                        }
                        break;
                    } catch (JSONException e)
                    {
                        DailyAssert.fail(e);
                    }
                }

                // benefit
                if (jsonObject.has("benefit") == true)
                {
                    String benefit = jsonObject.getString("benefit");

                    if (Util.isTextEmpty(benefit) == false && jsonObject.has("benefitContents") == true && jsonObject.isNull("benefitContents") == false)
                    {
                        JSONArray benefitJSONArray = jsonObject.getJSONArray("benefitContents");
                        DailyAssert.assertNotNull(benefitJSONArray);

                        int length = benefitJSONArray.length();
                        if (length > 0)
                        {
                            for (int i = 0; i < length; i++)
                            {
                                DailyAssert.assertNotNull(benefitJSONArray.getString(i));
                            }
                        }

                        if (jsonObject.has("benefitWarning") == true && jsonObject.isNull("benefitWarning") == false)
                        {
                            String benefitWarning = jsonObject.getString("benefitWarning");
                            DailyAssert.assertNotNull(benefitWarning);
                        }
                    } else
                    {
                        DailyAssert.fail();
                    }
                }

                // Detail
                JSONArray detailJSONArray = jsonObject.getJSONArray("details");
                DailyAssert.assertNotNull(detailJSONArray);
                int detailLength = detailJSONArray.length();
                for (int i = 0; i < detailLength; i++)
                {
                    JSONObject detailJsonObject = detailJSONArray.getJSONObject(i);

                    Iterator<String> detailIterator = detailJsonObject.keys();
                    if (detailIterator.hasNext() == true)
                    {
                        String detailTitle = detailIterator.next();
                        DailyAssert.assertNotNull(detailTitle);

                        JSONArray detailJsonArray = detailJsonObject.getJSONArray(detailTitle);
                        DailyAssert.assertNotNull(detailJsonArray);

                        int length = detailJsonArray.length();
                        for (int j = 0; j < length; j++)
                        {
                            DailyAssert.assertNotNull(detailJsonArray.getString(j));
                        }
                    }
                }

                // Room Sale Info
                if (jsonObject.has("rooms") == true && jsonObject.isNull("rooms") == false)
                {
                    JSONArray saleRoomJSONArray = jsonObject.getJSONArray("rooms");
                    DailyAssert.assertNotNull(saleRoomJSONArray);

                    int saleRoomLength = saleRoomJSONArray.length();
                    for (int i = 0; i < saleRoomLength; i++)
                    {
                        JSONObject saleRoomJsonObject = saleRoomJSONArray.getJSONObject(i);
                        DailyAssert.assertNotNull(saleRoomJsonObject);

                        DailyAssert.assertNotNull(saleRoomJsonObject.getInt("roomIdx"));
                        DailyAssert.assertNotNull(saleRoomJsonObject.getInt("discountAverage"));
                        DailyAssert.assertNotNull(saleRoomJsonObject.getInt("discountTotal"));
                        DailyAssert.assertNotNull(saleRoomJsonObject.getInt("price"));
                        DailyAssert.assertNotNull(saleRoomJsonObject.getString("roomName").trim());
                        DailyAssert.assertNotNull(saleRoomJsonObject.getString("description1").trim());
                        DailyAssert.assertNotNull(saleRoomJsonObject.getString("description2").trim());

                        if (saleRoomJsonObject.has("roomBenefit") == true)
                        {
                            DailyAssert.assertNotNull(saleRoomJsonObject.getString("roomBenefit").trim());
                        }

                        if (jsonObject.has("refundType") == true)
                        {
                            DailyAssert.assertNotNull(saleRoomJsonObject.getString("refundType"));
                        }
                    }
                }

                DailyAssert.assertTrue(jsonObject.has("myWish"));
                DailyAssert.assertTrue(jsonObject.has("wishCount"));
            }
        };

        DailyMobileAPI.getInstance(mContext).requestStayDetailInformation(mNetworkTag, Const.TEST_STAY_INDEX, //
            mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), Const.TEST_NIGHTS, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayPayment() throws Exception
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

                        // 해당 화면은 메시지를 넣지 않는다.
                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");
                        switch (msgCode)
                        {
                            case 1000:
                                DailyAssert.assertTrue("msgCode : " + msgCode + ", PayComplete", true);
                                break;

                            case 5:
                            case 300:
                            case 302:
                            case 303:
                            case 304:
                            case 1010:
                            case 1001:
                            case 1002:
                            case 1003:
                            case 1004:
                            case 1005:
                            case 1006:
                                DailyAssert.assertTrue("msgCode : " + msgCode, true);
                                break;

                            default:
                                DailyAssert.fail("msgCode : " + msgCode + ", message : " + message);
                                break;
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
        params.put("room_idx", String.valueOf(Const.TEST_STAY_SALE_ROOM_INDEX));
        params.put("checkin_date", mSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));
        params.put("nights", String.valueOf(Const.TEST_NIGHTS));
        params.put("billkey", Const.TEST_EASY_CARD_BILLINGKEY);

        // 쿠폰 및 적립금 패스
        //        switch (paymentInformation.discountType)
        //        {
        //            case BONUS:
        //                String bonus = String.valueOf(paymentInformation.bonus);
        //                params.put("bonus", bonus);
        //                break;
        //
        //            case COUPON:
        //                Coupon coupon = paymentInformation.getCoupon();
        //                params.put("user_coupon_code", coupon.userCouponCode);
        //                break;
        //        }

        params.put("guest_name", Const.TEST_USER_NAME);
        params.put("guest_phone", "01012345678");
        params.put("guest_email", Const.TEST_EMAIL);
        params.put("guest_msg", "");

        DailyMobileAPI.getInstance(mContext).requestStayPayment(mNetworkTag, params, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestBookingList() throws Exception
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
                        DailyAssert.assertEquals(0, msgCode);
                        if (msgCode == 0)
                        {
                            JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                            DailyAssert.assertNotNull(dataJSONArray);

                            int length = dataJSONArray.length();
                            if (length == 0)
                            {
                                DailyAssert.assertTrue("reservation list is empty", true);
                            } else
                            {
                                ArrayList<Booking> bookingList = makeBookingList(dataJSONArray);
                                DailyAssert.assertNotNull(bookingList);

                                if (bookingList != null && bookingList.isEmpty() == false)
                                {
                                    Booking booking = bookingList.get(0);
                                    requestGourmetAccountInformation(booking);
                                }
                            }
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

            private ArrayList<Booking> makeBookingList(JSONArray jsonArray) throws Exception
            {
                if (jsonArray == null || jsonArray.length() == 0)
                {
                    return null;
                }

                ArrayList<Booking> bookingList = new ArrayList<>();

                int length = jsonArray.length();
                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    DailyAssert.assertNotNull(jsonObject);

                    Booking booking = new Booking(jsonObject);
                    DailyAssert.assertNotNull(booking);

                    bookingList.add(booking);
                }

                return bookingList;
            }
        };

        DailyMobileAPI.getInstance(mContext).requestBookingList(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetBookingDetailInformation() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");

                        switch (msgCode)
                        {
                            case 100:
                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                                DailyAssert.assertNotNull(dataJSONObject);

                                GourmetBookingDetail gourmetBookingDetail = new GourmetBookingDetail();
                                gourmetBookingDetail.setData(dataJSONObject);
                                break;

                            // 예약 내역 진입시에 다른 사용자가 딥링크로 진입시 예외 처리 추가
                            case 501:
                                DailyAssert.fail(responseJSONObject.getString("msg"));
                                break;

                            default:
                                DailyAssert.fail(responseJSONObject.getString("msg"));
                                break;
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

        DailyMobileAPI.getInstance(mContext).requestGourmetBookingDetailInformation(//
            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetReceipt() throws Exception
    {
        mLock = new CountDownLatch(1);

        final retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
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

                        JSONObject dataJsonObject = responseJSONObject.getJSONObject("data");

                        if (msgCode == 100)
                        {
                            // 영수증
                            String reservationIdx = dataJsonObject.getString("gourmetReservationIdx");
                            DailyAssert.assertFalse(Util.isTextEmpty(reservationIdx));

                            String userName = dataJsonObject.getString("userName");
                            String userPhone = dataJsonObject.getString("userPhone");
                            int ticketCount = dataJsonObject.getInt("ticketCount");
                            String placeName = dataJsonObject.getString("restaurantName");
                            String placeAddress = dataJsonObject.getString("restaurantAddress");
                            String sday = dataJsonObject.getString("sday");
                            String valueDate = dataJsonObject.getString("paidAt");
                            //        String currency = receiptJSONObject.getString("currency");
                            int paymentAmount = dataJsonObject.getInt("paymentAmount");
                            int tax = dataJsonObject.getInt("tax");
                            int supplyPrice = dataJsonObject.getInt("supplyPrice");
                            int sellingPrice = dataJsonObject.getInt("sellingPrice");
                            String paymentType = dataJsonObject.getString("paymentType");
                            int counpon = dataJsonObject.getInt("couponAmount");

                            // **공급자**
                            String receiptNotice = dataJsonObject.getString("receiptNotice");

                            DailyAssert.assertNotNull(userName);
                            DailyAssert.assertNotNull(userPhone);
                            DailyAssert.assertTrue(ticketCount > 0);
                            DailyAssert.assertNotNull(placeName);
                            DailyAssert.assertNotNull(placeAddress);
                            DailyAssert.assertNotNull(sday);
                            DailyAssert.assertNotNull(valueDate);
                            DailyAssert.assertTrue(paymentAmount >= 0);
                            DailyAssert.assertTrue(tax >= 0);
                            DailyAssert.assertTrue(supplyPrice >= 0);
                            DailyAssert.assertTrue(sellingPrice >= 0);
                            DailyAssert.assertNotNull(paymentType);
                            DailyAssert.assertTrue(counpon >= 0);
                            DailyAssert.assertNotNull(receiptNotice);

                        } else
                        {
                            DailyAssert.fail(responseJSONObject.getString("msg"));
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

        DailyMobileAPI.getInstance(mContext).requestGourmetReceipt(mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 원래는 테스트 해야 하지만.. 예약 내역을 히든 할 경우 Test reservation index를 바꿔줘야 함으로 skip 함.
    @Ignore
    public void requestGourmetHiddenBooking() throws Exception
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

                        // 해당 화면은 메시지를 넣지 않는다.
                        int msgCode = responseJSONObject.getInt("msg_code");
                        DailyAssert.assertTrue(msgCode == 0 || msgCode == 100 || msgCode == 200);

                        DailyAssert.assertTrue(responseJSONObject.has("msg"));

                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        DailyAssert.assertNotNull(dataJSONObject);

                        boolean result = false;

                        if (dataJSONObject != null)
                        {
                            if (dataJSONObject.has("isSuccess") == true)
                            {
                                result = dataJSONObject.getInt("isSuccess") == 1;
                            } else if (dataJSONObject.has("is_success") == true)
                            {
                                result = dataJSONObject.getBoolean("is_success");
                            }
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

        DailyMobileAPI.getInstance(mContext).requestGourmetHiddenBooking(//
            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // booking tid 의 경우 항시 변경 됨으로 requestBookingList 이후에 진행하도록 함
    @Ignore
    public void requestGourmetAccountInformation(Booking booking) throws Exception
    {
        if (booking == null)
        {
            DailyAssert.assertNotNull(booking);
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
                        DailyAssert.assertNotNull(responseJSONObject);

                        int msgCode = responseJSONObject.getInt("msg_code");
                        DailyAssert.assertEquals(0, msgCode);

                        if (msgCode == 0)
                        {
                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");
                            DailyAssert.assertNotNull(jsonObject);

                            String accountNumber = jsonObject.getString("account_num");
                            String bankName = jsonObject.getString("bank_name");
                            String accountName = jsonObject.getString("name");
                            String date = jsonObject.getString("date");
                            String time = jsonObject.getString("time");
                            String msg1 = jsonObject.getString("msg1");

                            int couponAmount = jsonObject.getInt("coupon_amount");
                            int price = jsonObject.getInt("price");
                            int paymetPrice = jsonObject.getInt("amt");

                            DailyAssert.assertNotNull(accountNumber);
                            DailyAssert.assertNotNull(bankName);
                            DailyAssert.assertNotNull(accountName);
                            DailyAssert.assertNotNull(date);
                            DailyAssert.assertNotNull(time);
                            DailyAssert.assertNotNull(msg1);

                            DailyAssert.assertTrue(couponAmount >= 0);
                            DailyAssert.assertTrue(price >= 0);
                            DailyAssert.assertTrue(paymetPrice >= 0);

                        } else
                        {
                            DailyAssert.fail(responseJSONObject.getString("msg"));
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
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestGourmetAccountInformation(mNetworkTag, booking.tid, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
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