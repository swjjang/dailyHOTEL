package com.twoheart.dailyhotel.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.twoheart.dailyhotel.Const;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.User;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Rule
    public ErrorCollector mErrorConCollector = new ErrorCollector();

    @Before
    public void setUp() throws Exception
    {
        ExLog.d("setUp");
        // Context Setting!
        //          mContext = InstrumentationRegistry.getContext();
        mContext = InstrumentationRegistry.getTargetContext(); // 앱 타켓 컨텍스트!

        requestDailyUserLoginBySetUp();
        requestCommonDateTimeBySetUp();
    }

    @After
    public void tearDown() throws Exception
    {
        ExLog.d("tearDown");
        requestUserInformationUpdateByTearDown();
    }

    @Ignore
    private static String bodyToString(final RequestBody request)
    {
        if (request == null)
        {
            return "request is null";
        }

        try
        {
            Buffer buffer = new Buffer();

            request.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e)
        {
            return e.getMessage();
        }
    }

    @Ignore
    private static String getNetworkMessage(@NonNull Call<JSONObject> call, Response<JSONObject> response)
    {
        if (call == null)
        {
            return "call is null";
        }

        if (call.request() == null)
        {
            return "call.request is null";
        }

        String url = call.request().url().toString();
        String body = bodyToString(call.request().body());

        StringBuilder builder = new StringBuilder();

        //        if (throwable != null && TextUtils.isEmpty(throwable.getMessage()) == false)
        //        {
        //            builder.append(throwable.getMessage());
        //        }

        builder.append("\n===================== requset start =====================");
        builder.append("\nurl : ").append(url).append("\nbody : ").append(body);
        builder.append("\n" + "===================== request end =====================");

        if (response != null)
        {
            builder.append("\n===================== body start =====================");
            builder.append("\n").append("isSuccessful : ").append(response.isSuccessful());
            builder.append("\n").append("code : ").append(response.code());

            String bodyString;
            try
            {
                bodyString = response.body().toString(1);
            } catch (Exception e1)
            {
                bodyString = e1.getMessage();
            }

            builder.append("\n").append("body : ").append(bodyString);
            builder.append("\n").append("message : ").append(response.message());
            builder.append("\n").append("errorBody : ").append(response.errorBody());
            builder.append("\n" + "===================== body end =====================");
        }

        builder.append("\n");
        return builder.toString();
    }

    @Ignore
    private void addException(Call call, Response response, Throwable t)
    {
        String customMessage = getNetworkMessage(call, response);
        mErrorConCollector.addError(new AssertionError(customMessage, t));
    }

    @Ignore
    private long getGourmetReservationTime() throws Exception
    {
        if (mSaleTime == null)
        {
            assertThat("mSaleTime is null", false);
            return -1;
        }

        String currentTimeString = mSaleTime.getDayOfDaysDateFormat(DailyCalendar.ISO_8601_FORMAT);
        assertThat(currentTimeString, notNullValue());

        String findText = mSaleTime.getDayOfDaysDateFormat("HH:mm");
        assertThat(findText, allOf(notNullValue(), containsString(":")));

        currentTimeString = currentTimeString.replace(findText, Const.TEST_GOURMET_RESERVATION_TIME);
        assertThat(currentTimeString, allOf(notNullValue(), containsString(":")));

        Date date = DailyCalendar.convertDate(currentTimeString, DailyCalendar.ISO_8601_FORMAT, TimeZone.getTimeZone("GMT"));
        assertThat(date, allOf(notNullValue(), isA(Date.class)));

        return date.getTime();
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");
                    assertThat(msgCode, is(0));

                    assertThat(responseJSONObject.getJSONObject("data"), allOf(notNullValue(), isA(JSONObject.class)));
                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                    assertThat(responseJSONObject.getJSONObject("token"), allOf(notNullValue(), isA(JSONObject.class)));
                    JSONObject tokenJSONObject = responseJSONObject.getJSONObject("token");

                    assertThat(dataJSONObject.getBoolean("is_signin"), allOf(instanceOf(Boolean.class), is(true)));

                    String accessToken = tokenJSONObject.getString("access_token");
                    String tokenType = tokenJSONObject.getString("token_type");
                    assertThat(accessToken, isNotEmpty());
                    assertThat(tokenType, isNotEmpty());

                    mAuthorization = String.format("%s %s", tokenType, accessToken);

                    JSONObject userJSONObject = dataJSONObject.getJSONObject("user");
                    assertThat(userJSONObject, notNullValue());

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

                    assertThat(mUser.getUserIdx(), isNotEmpty());
                    assertThat(mUser.getName(), isNotEmpty());
                    assertThat(mUser.getType(), equalTo(Constants.DAILY_USER));
                    //  return;

                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        assertThat(dataJSONObject, allOf(notNullValue(), isA(JSONObject.class)));

                        long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

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
                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(100));
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                    try
                    {
                        assertThat(response, notNullValue());
                        assertThat(response.isSuccessful(), is(true));
                        assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                        JSONObject responseJSONObject = response.body();
                        assertThat(responseJSONObject.getInt("msgCode"), is(100));
                    } catch (Throwable t)
                    {
                        addException(call, response, t);
                    } finally
                    {
                        mLock.countDown();
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t)
                {
                    addException(call, null, t);
                    mLock.countDown();
                }
            };

            Map<String, String> params = new HashMap<>();
            params.put("birthday", Const.TEST_USER_BIRTHDAY);
            params.put("user_name", Const.TEST_USER_NAME);
            params.put("pw", Crypto.getUrlDecoderEx(Const.TEST_PASSWORD));

            DailyMobileAPI.getInstance(mContext).requestUserInformationUpdate(mNetworkTag, params, networkCallback);
            mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();
                    assertThat(responseJSONObject, allOf(notNullValue(), isA(JSONObject.class)));

                    int msgCode = responseJSONObject.getInt("msg_code");
                    assertThat(msgCode, is(200));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, allOf(notNullValue(), isA(JSONObject.class)));

                    boolean isSuspend = dataJSONObject.getBoolean("isSuspend");
                    assertThat(isSuspend, isA(Boolean.class));

                    if (isSuspend == true)
                    {
                        String title = dataJSONObject.getString("messageTitle");
                        String message = dataJSONObject.getString("messageBody");

                        assertThat(title, isNotEmpty());
                        assertThat(message, isNotEmpty());
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        assertThat(dataJSONObject, notNullValue());

                        String maxVersionName;
                        String minVersionName;

                        switch (Const.RELEASE_STORE)
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

                        assertThat(Const.RELEASE_STORE.getName(), minVersionName, notNullValue());
                        assertThat(Const.RELEASE_STORE.getName(), maxVersionName, notNullValue());
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(100));
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();
                    assertThat(responseJSONObject, notNullValue());

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        String currentDate = dataJSONObject.getString("currentDateTime");
                        String dailyDate = dataJSONObject.getString("dailyDateTime");
                        String openDate = dataJSONObject.getString("openDateTime");
                        String closeDate = dataJSONObject.getString("closeDateTime");

                        assertThat(currentDate, isNotEmpty());
                        assertThat(dailyDate, isNotEmpty());
                        assertThat(openDate, isNotEmpty());
                        assertThat(closeDate, isNotEmpty());

                        DailyCalendar.convertDate(currentDate, DailyCalendar.ISO_8601_FORMAT);
                        DailyCalendar.convertDate(dailyDate, DailyCalendar.ISO_8601_FORMAT);
                        DailyCalendar.convertDate(openDate, DailyCalendar.ISO_8601_FORMAT);
                        DailyCalendar.convertDate(closeDate, DailyCalendar.ISO_8601_FORMAT);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(100));
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();
                    assertThat(responseJSONObject, allOf(notNullValue(), isA(JSONObject.class)));

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject jsonObject = responseJSONObject.getJSONObject("data");
                        assertThat(jsonObject, allOf(notNullValue(), isA(JSONObject.class)));

                        String userIndex = jsonObject.getString("userIdx");
                        String userType = jsonObject.getString("userType");
                        String userEmail = jsonObject.getString("email");
                        String userName = jsonObject.getString("name");
                        String userPhone = jsonObject.getString("phone");
                        String referralCode = jsonObject.getString("referralCode"); // 자신의 추천 번호
                        String birthday = jsonObject.getString("birthday");
                        String phoneVerifiedAt = jsonObject.getString("phoneVerifiedAt");

                        assertThat(userIndex, equalTo(mUser.getUserIdx()));
                        assertThat(userType, equalTo(mUser.getType()));
                        assertThat(userEmail, equalTo(mUser.getEmail()));
                        assertThat(userName, equalTo(mUser.getName()));
                        assertThat(userPhone, equalTo(mUser.getPhone()));
                        assertThat(referralCode, isNotEmpty());
                        assertThat(birthday, equalTo(mUser.getBirthDay()));

                        DailyCalendar.convertDate(phoneVerifiedAt, DailyCalendar.ISO_8601_FORMAT);

                        assertThat(jsonObject.getBoolean("verified"), allOf(notNullValue(), isA(Boolean.class)));
                        assertThat(jsonObject.getBoolean("phoneVerified"), allOf(notNullValue(), isA(Boolean.class)));
                        assertThat(jsonObject.getBoolean("agreedBenefit"), allOf(notNullValue(), isA(Boolean.class)));
                    } else
                    {
                        String msg = responseJSONObject.getString("msg");
                        assertThat(msg, isNotEmpty());
                        assertThat(msg, msgCode, is(100));
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    //적립금 내역리스트
                    JSONObject responseJSONObject = response.body();

                    if (responseJSONObject.has("history") == true && responseJSONObject.isNull("history") == false)
                    {
                        JSONArray jsonArray = responseJSONObject.getJSONArray("history");
                        assertThat(jsonArray, allOf(notNullValue(), isA(JSONArray.class)));

                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++)
                        {
                            JSONObject historyObj = jsonArray.getJSONObject(i);

                            assertThat(historyObj, notNullValue());

                            String content = historyObj.getString("content");
                            String expires = historyObj.getString("expires");
                            int bonus = historyObj.getInt("bonus");

                            assertThat(content, isNotEmpty());
                            assertThat(expires, isNotEmpty());
                            assertThat(bonus, moreThan(0));
                        }
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }

            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        mUser.setBirthDay(Const.TEST_MODIFY_USER_BIRTHDAY);
                        mUser.setPassword(Const.TEST_MODIFY_PASSWORD);
                        mUser.setName(Const.TEST_MODIFY_USER_NAME);
                    } else
                    {
                        assertThat(msgCode, is(100));
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        assertThat(dataJSONObject, allOf(notNullValue(), isA(JSONObject.class)));

                        assertThat(dataJSONObject.getBoolean("exceedLimitedBonus"), allOf(notNullValue(), isA(Boolean.class)));
                        assertThat(dataJSONObject.getInt("bonusAmount"), moreThan(0));
                        assertThat(dataJSONObject.getInt("couponTotalCount"), moreThan(0));
                    } else
                    {
                        String msg = responseJSONObject.getString("msg");
                        assertThat(msg, isNotEmpty());
                        assertThat(msg, msgCode, is(100));
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    String result = responseJSONObject.getString("isSuccess");

                    assertThat(responseJSONObject.getString("msg"), allOf(notNullValue(), isA(String.class)));
                    assertThat(result, is("true"));
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserCheckEmail(mNetworkTag, Const.TEST_EMAIL, networkCallback);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    String result = responseJSONObject.getString("isSuccess");

                    assertThat(responseJSONObject.getString("msg"), allOf(notNullValue(), isA(String.class)));
                    assertThat(result, is("true"));
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");
                    if (msgCode == 0)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        assertThat(dataJSONObject, notNullValue());

                        String name = dataJSONObject.getString("user_name");
                        String phone = dataJSONObject.getString("user_phone");
                        String email = dataJSONObject.getString("user_email");
                        String userIndex = dataJSONObject.getString("user_idx");

                        assertThat(name, isNotEmpty());
                        assertThat(phone, isNotEmpty());
                        assertThat(email, isNotEmpty());
                        assertThat(userIndex, isNotEmpty());

                        if (mUser != null)
                        {
                            assertThat(name, equalTo(mUser.getName()));
                            assertThat(phone, equalTo(mUser.getPhone()));
                            assertThat(email, equalTo(mUser.getEmail()));
                            assertThat(userIndex, equalTo(mUser.getUserIdx()));
                        }

                        int bonus = dataJSONObject.getInt("user_bonus");
                        assertThat(bonus, moreThan(0));
                    } else
                    {
                        assertThat(responseJSONObject.getString("msg"), isNotEmpty());
                        assertThat(msgCode, is(0));
                    }
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    assertThat(dataJSONObject.getBoolean("is_success"), allOf(is(true), isA(Boolean.class)));

                    String serverDate = dataJSONObject.getString("serverDate");
                    assertThat(serverDate, isNotEmpty());
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");
                    assertThat(msgCode, is(100));

                    JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                    assertThat(dataJSONArray, notNullValue());

                    ArrayList<CreditCard> creditCardArrayList = new ArrayList<>();

                    int length = dataJSONArray.length();
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

                        assertThat("카드 이름", creditCard.name, isNotEmpty());
                        assertThat("카드 번호", creditCard.number, isNotEmpty());
                        assertThat("카드 키", creditCard.billingkey, isNotEmpty());
                        assertThat("카드 사명", creditCard.cardcd, isNotEmpty());

                        String cardCds = CreditCard.getCardCDName(mContext, creditCard.cardcd);
                        assertThat("카드사명 찾기 실패", cardCds, isNotEmpty());
                    }

                    // requestUserDeleteBillingCard() 용 테스트 코드
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
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");
                    assertThat(msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    assertThat(dataJSONObject.getInt("isSuccess"), is(1));
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                addException(call, null, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserDeleteBillingCard(mNetworkTag, billingkey, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }
    //
    //    @Test
    //    public void requestStayList() throws Exception
    //    {
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
    //                            int hotelSaleCount = dataJSONObject.getInt("hotelSalesCount");
    //                            DailyAssert.assertNotNull(hotelSaleCount);
    //
    //                            DailyAssert.assertEquals(Const.TEST_IS_SHOW_LIST_DETAIL, dataJSONObject.has("hotelSales"));
    //
    //                            JSONArray hotelJSONArray = null;
    //
    //                            if (dataJSONObject.has("hotelSales") == true)
    //                            {
    //                                hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
    //                            }
    //
    //                            if (hotelJSONArray != null)
    //                            {
    //                                String imageUrl = dataJSONObject.getString("imgUrl");
    //                                DailyAssert.assertNotNull(imageUrl);
    //
    //                                int nights = dataJSONObject.getInt("stays");
    //                                DailyAssert.assertEquals(Const.TEST_NIGHTS, nights);
    //
    //                                int length = hotelJSONArray.length();
    //                                JSONObject jsonObject;
    //                                for (int i = 0; i < length; i++)
    //                                {
    //                                    jsonObject = hotelJSONArray.getJSONObject(i);
    //                                    DailyAssert.assertNotNull(jsonObject);
    //
    //                                    try
    //                                    {
    //                                        String name = jsonObject.getString("name");
    //                                        int price = jsonObject.getInt("price");
    //                                        int discountPrice = jsonObject.getInt("discount"); // discountAvg ????
    //                                        String addressSummary = jsonObject.getString("addrSummary");
    //
    //                                        DailyAssert.assertNotNull(name);
    //                                        DailyAssert.assertNotNull(price);
    //                                        DailyAssert.assertNotNull(discountPrice);
    //                                        DailyAssert.assertNotNull(addressSummary);
    //
    //                                        try
    //                                        {
    //                                            Stay.Grade.valueOf(jsonObject.getString("grade"));
    //                                        } catch (Exception e)
    //                                        {
    //                                            DailyAssert.fail(e);
    //                                        }
    //
    //                                        DailyAssert.assertNotNull(jsonObject.getInt("hotelIdx"));
    //
    //                                        if (jsonObject.has("isSoldOut") == true)
    //                                        {
    //                                            boolean isSoldOut = jsonObject.getBoolean("isSoldOut");
    //                                        }
    //
    //                                        DailyAssert.assertNotNull(jsonObject.getString("districtName"));
    //                                        DailyAssert.assertNotNull(jsonObject.getString("category"));
    //                                        DailyAssert.assertNotNull(jsonObject.getDouble("latitude"));
    //                                        DailyAssert.assertNotNull(jsonObject.getDouble("longitude"));
    //                                        DailyAssert.assertTrue(jsonObject.has("isDailyChoice"));
    //                                        DailyAssert.assertNotNull(jsonObject.getInt("rating")); // ratingValue ??
    //                                        DailyAssert.assertNotNull(jsonObject.getString("sday"));
    //                                        DailyAssert.assertNotNull(jsonObject.getDouble("distance"));
    //
    //                                        JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
    //                                        DailyAssert.assertNotNull(imageJSONObject);
    //
    //                                        String stayImageUrl = null;
    //                                        Iterator<String> iterator = imageJSONObject.keys();
    //                                        while (iterator.hasNext())
    //                                        {
    //                                            String key = iterator.next();
    //
    //                                            try
    //                                            {
    //                                                JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
    //                                                stayImageUrl = imageUrl + key + pathJSONArray.getString(0);
    //                                                break;
    //                                            } catch (JSONException e)
    //                                            {
    //                                                DailyAssert.fail(e);
    //                                            }
    //                                        }
    //
    //                                        DailyAssert.assertNotNull(stayImageUrl);
    //
    //                                        if (jsonObject.has("benefit") == true) // hotelBenefit ?
    //                                        {
    //                                            DailyAssert.assertNotNull(jsonObject.getString("benefit"));
    //                                        }
    //                                    } catch (JSONException e)
    //                                    {
    //                                        DailyAssert.fail(e);
    //                                    }
    //                                }
    //                            }
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        HashMap<String, Object> paramMap = new HashMap<>();
    //
    //        paramMap.put("dateCheckIn", mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
    //        paramMap.put("stays", Const.TEST_NIGHTS);
    //        paramMap.put("provinceIdx", Const.TEST_STAY_PROVINCE_INDEX);
    //        // area skip
    //        //  queryMap.put("areaIdx", areaIdx);
    //
    //        paramMap.put("persons", Const.TEST_STAY_PERSONS);
    //        if (Category.ALL.code.equalsIgnoreCase(Const.TEST_STAY_CATEGORY_CODE) == false)
    //        {
    //            paramMap.put("category", Const.TEST_STAY_CATEGORY_CODE);
    //        }
    //
    //        //  if(mBedTypeList != null && mBedTypeList.size() > 0)
    //        //  {
    //        //hashMap.put("bedType", mBedTypeList);
    //        //  }
    //        //
    //        //  if(mLuxuryList != null && mLuxuryList.size() > 0)
    //        //  {
    //        //hashMap.put("luxury", mLuxuryList);
    //        //  }
    //
    //        if (Const.TEST_PAGE_INDEX > 0)
    //        {
    //            paramMap.put("page", Const.TEST_PAGE_INDEX);
    //            paramMap.put("limit", Const.TEST_LIMIT_LIST_COUNT);
    //        }
    //
    //        // sort skip
    //        //  if (Constants.SortType.DEFAULT != mSort)
    //        //  {
    //        //if (Util.isTextEmpty(sortProperty) == false)
    //        //{
    //        // queryMap.put("sortProperty", sortProperty);
    //        //}
    //        //
    //        //if (Util.isTextEmpty(sortDirection) == false)
    //        //{
    //        // queryMap.put("sortDirection", sortDirection);
    //        //}
    //        //
    //        //if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
    //        //{
    //        // queryMap.put("latitude", latitude);
    //        // queryMap.put("longitude", longitude);
    //        //}
    //        //  }
    //
    //        paramMap.put("details", Const.TEST_IS_SHOW_LIST_DETAIL);
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayList(mNetworkTag, paramMap, null, null, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestStaySearchAutoCompleteList() throws Exception
    //    {
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
    //                    String keyword = call.request().url().queryParameter("term");
    //
    //                    List<Keyword> keywordList = null;
    //
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //                            DailyAssert.assertNotNull(dataJSONArray);
    //
    //                            ArrayList<String> etcList = new ArrayList<>();
    //
    //                            int length = dataJSONArray.length();
    //                            for (int i = 0; i < length; i++)
    //                            {
    //                                JSONObject jsonObject = dataJSONArray.getJSONObject(i);
    //                                DailyAssert.assertNotNull(jsonObject);
    //
    //                                String name = jsonObject.getString("displayText");
    //                                if (Util.isTextEmpty(name) == true || name.contains(keyword) == false)
    //                                {
    //                                    etcList.add(name);
    //                                }
    //
    //                                if (etcList.size() > 0)
    //                                {
    //                                    DailyAssert.fail("keyword is not contain list, keyword : " + keyword + " , list : " + etcList.toString());
    //                                }
    //
    //                                if (jsonObject.has("discount") == true)
    //                                {
    //                                    DailyAssert.assertNotNull(jsonObject.getInt("discount"));
    //                                }
    //
    //                            }
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStaySearchAutoCompleteList(mNetworkTag//
    //            , mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), Const.TEST_NIGHTS, Const.TEST_STAY_AUTO_SEARCH_TEXT, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestStayRegionList() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback mRegionListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
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
    //                            JSONArray provinceArray = dataJSONObject.getJSONArray("regionProvince");
    //                            ArrayList<Province> provinceList = makeProvinceList(provinceArray);
    //                            DailyAssert.assertNotNull(provinceList);
    //
    //                            JSONArray areaJSONArray = dataJSONObject.getJSONArray("regionArea");
    //                            ArrayList<Area> areaList = makeAreaList(areaJSONArray);
    //                            DailyAssert.assertNotNull(areaList);
    //
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //
    //            private ArrayList<Province> makeProvinceList(JSONArray jsonArray)
    //            {
    //                ArrayList<Province> provinceList = new ArrayList<>();
    //
    //                try
    //                {
    //                    int length = jsonArray.length();
    //                    for (int i = 0; i < length; i++)
    //                    {
    //                        JSONObject jsonObject = jsonArray.getJSONObject(i);
    //
    //                        try
    //                        {
    //                            Province province = new Province(jsonObject, null);
    //                            provinceList.add(province);
    //                        } catch (JSONException e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    }
    //                } catch (Exception e)
    //                {
    //                    DailyAssert.fail(e);
    //                }
    //
    //                return provinceList;
    //            }
    //
    //            private ArrayList<Area> makeAreaList(JSONArray jsonArray) throws JSONException
    //            {
    //                ArrayList<Area> areaList = new ArrayList<>();
    //
    //                int length = jsonArray.length();
    //                for (int i = 0; i < length; i++)
    //                {
    //                    JSONObject jsonObject = jsonArray.getJSONObject(i);
    //
    //                    try
    //                    {
    //                        Area area = new Area(jsonObject);
    //                        areaList.add(area);
    //                    } catch (JSONException e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                }
    //
    //                return areaList;
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayRegionList(mNetworkTag, mRegionListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestStayPaymentInformation() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        // 0	성공
    //                        // 4	데이터가 없을시
    //                        // 5	판매 마감시
    //                        // 6	현재 시간부터 날짜 바뀌기 전시간(새벽 3시
    //                        // 7 3시부터 9시까지
    //                        switch (msgCode)
    //                        {
    //                            case 6:
    //                            case 7:
    //                                if (responseJSONObject.has("msg") == true)
    //                                {
    //                                    DailyAssert.assertNotNull(responseJSONObject.getString("msg"));
    //                                }
    //                            case 0:
    //                            {
    //                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                                DailyAssert.assertNotNull(dataJSONObject);
    //
    //                                long checkInDate = dataJSONObject.getLong("check_in_date");
    //                                long checkOutDate = dataJSONObject.getLong("check_out_date");
    //                                int discount = dataJSONObject.getInt("discount_total");
    //                                int availableRooms = dataJSONObject.getInt("available_rooms");
    //
    //                                DailyAssert.assertNotNull(checkInDate);
    //                                DailyAssert.assertNotNull(checkOutDate);
    //                                DailyAssert.assertNotNull(discount);
    //                                DailyAssert.assertNotNull(availableRooms);
    //
    //                                DailyAssert.assertTrue(dataJSONObject.has("on_sale"));
    //                                DailyAssert.assertTrue(dataJSONObject.has("refund_type"));
    //                                break;
    //                            }
    //
    //                            case 5:
    //                            {
    //                                if (responseJSONObject.has("msg") == true)
    //                                {
    //                                    String msg = responseJSONObject.getString("msg");
    //                                    DailyAssert.fail(msg);
    //                                } else
    //                                {
    //                                    DailyAssert.fail();
    //                                }
    //                                break;
    //                            }
    //
    //                            case 4:
    //                            default:
    //                                if (responseJSONObject.has("msg") == true)
    //                                {
    //                                    String msg = responseJSONObject.getString("msg");
    //                                    DailyAssert.fail(msg);
    //                                } else
    //                                {
    //                                    DailyAssert.fail();
    //                                }
    //                                break;
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayPaymentInformation(mNetworkTag//
    //            , Const.TEST_STAY_SALE_ROOM_INDEX//
    //            , mSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
    //            , Const.TEST_NIGHTS, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestStayDetailInformation() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //
    //                        JSONObject dataJSONObject = null;
    //
    //                        if (responseJSONObject.has("data") == true && responseJSONObject.isNull("data") == false)
    //                        {
    //                            dataJSONObject = responseJSONObject.getJSONObject("data");
    //                        }
    //
    //                        if (msgCode == 100 && dataJSONObject == null)
    //                        {
    //                            msgCode = 4;
    //                        }
    //
    //                        // 100	성공
    //                        // 4	데이터가 없을시
    //                        // 5	판매 마감시
    //                        switch (msgCode)
    //                        {
    //                            case 100:
    //                                checkStayDetail(dataJSONObject);
    //                                break;
    //
    //                            case 5:
    //                            {
    //                                checkStayDetail(dataJSONObject);
    //
    //                                if (responseJSONObject.has("msg") == true)
    //                                {
    //                                    String msg = responseJSONObject.getString("msg");
    //                                    DailyAssert.fail(msg);
    //                                } else
    //                                {
    //                                    DailyAssert.fail();
    //                                }
    //                                break;
    //                            }
    //
    //                            case 4:
    //                            default:
    //                            {
    //                                if (responseJSONObject.has("msg") == true)
    //                                {
    //                                    String msg = responseJSONObject.getString("msg");
    //                                    DailyAssert.fail(msg);
    //                                } else
    //                                {
    //                                    DailyAssert.fail();
    //                                }
    //                                break;
    //                            }
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //
    //            private void checkStayDetail(JSONObject jsonObject) throws Exception
    //            {
    //                Stay.Grade grade = Stay.Grade.valueOf(jsonObject.getString("grade"));
    //                DailyAssert.assertNotNull(grade);
    //
    //                DailyAssert.assertNotNull(jsonObject.getString("name"));
    //                DailyAssert.assertNotNull(jsonObject.getString("address"));
    //
    //                DailyAssert.assertNotNull(jsonObject.getDouble("longitude"));
    //                DailyAssert.assertNotNull(jsonObject.getDouble("latitude"));
    //                DailyAssert.assertNotNull(jsonObject.getBoolean("overseas"));
    //
    //                boolean ratingShow = jsonObject.getBoolean("ratingShow");
    //                if (ratingShow == true)
    //                {
    //                    int ratingValue = jsonObject.getInt("ratingValue");
    //                    int ratingPersons = jsonObject.getInt("ratingPersons");
    //
    //                    DailyAssert.assertTrue(ratingValue >= 0);
    //                    DailyAssert.assertTrue(ratingPersons >= Const.TEST_MIN_RATING_PERSONS);
    //                }
    //
    //                // Pictrogram
    //                // 주차
    //                DailyAssert.assertNotNull(jsonObject.has("parking"));
    //                // 주차금지
    //                DailyAssert.assertNotNull(jsonObject.has("noParking"));
    //                // 수영장
    //                DailyAssert.assertNotNull(jsonObject.has("pool"));
    //                // 피트니스
    //                DailyAssert.assertNotNull(jsonObject.has("fitness"));
    //                // 애완동물
    //                DailyAssert.assertNotNull(jsonObject.has("pet"));
    //                // 바베큐
    //                DailyAssert.assertNotNull(jsonObject.has("sharedBbq"));
    //
    //                // Image Url
    //                String imageUrl = jsonObject.getString("imgUrl");
    //                DailyAssert.assertNotNull(imageUrl);
    //
    //                JSONObject pathUrlJSONObject = jsonObject.getJSONObject("imgPath");
    //                DailyAssert.assertNotNull(pathUrlJSONObject);
    //
    //                Iterator<String> iterator = pathUrlJSONObject.keys();
    //                while (iterator.hasNext())
    //                {
    //                    String key = iterator.next();
    //
    //                    try
    //                    {
    //                        JSONArray pathJSONArray = pathUrlJSONObject.getJSONArray(key);
    //                        DailyAssert.assertNotNull(pathJSONArray);
    //
    //                        int length = pathJSONArray.length();
    //                        for (int i = 0; i < length; i++)
    //                        {
    //                            JSONObject imageInformationJSONObject = pathJSONArray.getJSONObject(i);
    //                            DailyAssert.assertNotNull(imageInformationJSONObject);
    //
    //                            String description = imageInformationJSONObject.getString("description");
    //                            DailyAssert.assertNotNull(description);
    //
    //                            String imageFullUrl = imageUrl + key + imageInformationJSONObject.getString("name");
    //                            DailyAssert.assertNotNull(imageFullUrl);
    //                        }
    //                        break;
    //                    } catch (JSONException e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                }
    //
    //                // benefit
    //                if (jsonObject.has("benefit") == true)
    //                {
    //                    String benefit = jsonObject.getString("benefit");
    //
    //                    if (Util.isTextEmpty(benefit) == false && jsonObject.has("benefitContents") == true && jsonObject.isNull("benefitContents") == false)
    //                    {
    //                        JSONArray benefitJSONArray = jsonObject.getJSONArray("benefitContents");
    //                        DailyAssert.assertNotNull(benefitJSONArray);
    //
    //                        int length = benefitJSONArray.length();
    //                        if (length > 0)
    //                        {
    //                            for (int i = 0; i < length; i++)
    //                            {
    //                                DailyAssert.assertNotNull(benefitJSONArray.getString(i));
    //                            }
    //                        }
    //
    //                        if (jsonObject.has("benefitWarning") == true && jsonObject.isNull("benefitWarning") == false)
    //                        {
    //                            String benefitWarning = jsonObject.getString("benefitWarning");
    //                            DailyAssert.assertNotNull(benefitWarning);
    //                        }
    //                    } else
    //                    {
    //                        DailyAssert.fail();
    //                    }
    //                }
    //
    //                // Detail
    //                JSONArray detailJSONArray = jsonObject.getJSONArray("details");
    //                DailyAssert.assertNotNull(detailJSONArray);
    //                int detailLength = detailJSONArray.length();
    //                for (int i = 0; i < detailLength; i++)
    //                {
    //                    JSONObject detailJsonObject = detailJSONArray.getJSONObject(i);
    //
    //                    Iterator<String> detailIterator = detailJsonObject.keys();
    //                    if (detailIterator.hasNext() == true)
    //                    {
    //                        String detailTitle = detailIterator.next();
    //                        DailyAssert.assertNotNull(detailTitle);
    //
    //                        JSONArray detailJsonArray = detailJsonObject.getJSONArray(detailTitle);
    //                        DailyAssert.assertNotNull(detailJsonArray);
    //
    //                        int length = detailJsonArray.length();
    //                        for (int j = 0; j < length; j++)
    //                        {
    //                            DailyAssert.assertNotNull(detailJsonArray.getString(j));
    //                        }
    //                    }
    //                }
    //
    //                // Room Sale Info
    //                if (jsonObject.has("rooms") == true && jsonObject.isNull("rooms") == false)
    //                {
    //                    JSONArray saleRoomJSONArray = jsonObject.getJSONArray("rooms");
    //                    DailyAssert.assertNotNull(saleRoomJSONArray);
    //
    //                    int saleRoomLength = saleRoomJSONArray.length();
    //                    for (int i = 0; i < saleRoomLength; i++)
    //                    {
    //                        JSONObject saleRoomJsonObject = saleRoomJSONArray.getJSONObject(i);
    //                        DailyAssert.assertNotNull(saleRoomJsonObject);
    //
    //                        DailyAssert.assertNotNull(saleRoomJsonObject.getInt("roomIdx"));
    //                        DailyAssert.assertNotNull(saleRoomJsonObject.getInt("discountAverage"));
    //                        DailyAssert.assertNotNull(saleRoomJsonObject.getInt("discountTotal"));
    //                        DailyAssert.assertNotNull(saleRoomJsonObject.getInt("price"));
    //                        DailyAssert.assertNotNull(saleRoomJsonObject.getString("roomName").trim());
    //                        DailyAssert.assertNotNull(saleRoomJsonObject.getString("description1").trim());
    //                        DailyAssert.assertNotNull(saleRoomJsonObject.getString("description2").trim());
    //
    //                        if (saleRoomJsonObject.has("roomBenefit") == true)
    //                        {
    //                            DailyAssert.assertNotNull(saleRoomJsonObject.getString("roomBenefit").trim());
    //                        }
    //
    //                        if (jsonObject.has("refundType") == true)
    //                        {
    //                            DailyAssert.assertNotNull(saleRoomJsonObject.getString("refundType"));
    //                        }
    //                    }
    //                }
    //
    //                DailyAssert.assertTrue(jsonObject.has("myWish"));
    //                DailyAssert.assertTrue(jsonObject.has("wishCount"));
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayDetailInformation(mNetworkTag, Const.TEST_STAY_INDEX, //
    //            mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), Const.TEST_NIGHTS, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestStayPayment() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        // 해당 화면은 메시지를 넣지 않는다.
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        String message = responseJSONObject.getString("msg");
    //                        switch (msgCode)
    //                        {
    //                            case 1000:
    //                                DailyAssert.assertTrue("msgCode : " + msgCode + ", PayComplete", true);
    //                                break;
    //
    //                            case 5:
    //                            case 300:
    //                            case 302:
    //                            case 303:
    //                            case 304:
    //                            case 1010:
    //                            case 1001:
    //                            case 1002:
    //                            case 1003:
    //                            case 1004:
    //                            case 1005:
    //                            case 1006:
    //                                DailyAssert.assertTrue("msgCode : " + msgCode, true);
    //                                break;
    //
    //                            default:
    //                                DailyAssert.fail("msgCode : " + msgCode + ", message : " + message);
    //                                break;
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        Map<String, String> params = new HashMap<>();
    //        params.put("room_idx", String.valueOf(Const.TEST_STAY_SALE_ROOM_INDEX));
    //        params.put("checkin_date", mSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));
    //        params.put("nights", String.valueOf(Const.TEST_NIGHTS));
    //        params.put("billkey", Const.TEST_EASY_CARD_BILLINGKEY);
    //
    //        // 쿠폰 및 적립금 패스
    //        //  switch (paymentInformation.discountType)
    //        //  {
    //        //case BONUS:
    //        // String bonus = String.valueOf(paymentInformation.bonus);
    //        // params.put("bonus", bonus);
    //        // break;
    //        //
    //        //case COUPON:
    //        // Coupon coupon = paymentInformation.getCoupon();
    //        // params.put("user_coupon_code", coupon.userCouponCode);
    //        // break;
    //        //  }
    //
    //        params.put("guest_name", Const.TEST_USER_NAME);
    //        params.put("guest_phone", Const.TEST_USER_PHONE);
    //        params.put("guest_email", Const.TEST_EMAIL);
    //        params.put("guest_msg", "");
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayPayment(mNetworkTag, params, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestBookingList() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertEquals(0, msgCode);
    //                        if (msgCode == 0)
    //                        {
    //                            JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //                            DailyAssert.assertNotNull(dataJSONArray);
    //
    //                            int length = dataJSONArray.length();
    //                            if (length == 0)
    //                            {
    //                                DailyAssert.assertTrue("reservation list is empty", true);
    //                            } else
    //                            {
    //                                ArrayList<Booking> bookingList = makeBookingList(dataJSONArray);
    //                                DailyAssert.assertNotNull(bookingList);
    //
    //                                if (bookingList != null && bookingList.isEmpty() == false)
    //                                {
    //                                    Booking booking = bookingList.get(0);
    //                                    if (Constants.PlaceType.FNB.equals(booking.placeType) == true)
    //                                    {
    //                                        requestGourmetAccountInformation(booking);
    //                                    } else if (Constants.PlaceType.HOTEL.equals(booking.placeType) == true)
    //                                    {
    //                                        requestDepositWaitDetailInformation(booking);
    //                                    }
    //                                }
    //                            }
    //                        } else
    //                        {
    //                            String msg = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(msg);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //
    //            private ArrayList<Booking> makeBookingList(JSONArray jsonArray) throws Exception
    //            {
    //                if (jsonArray == null || jsonArray.length() == 0)
    //                {
    //                    return null;
    //                }
    //
    //                ArrayList<Booking> bookingList = new ArrayList<>();
    //
    //                int length = jsonArray.length();
    //                for (int i = 0; i < length; i++)
    //                {
    //                    JSONObject jsonObject = jsonArray.getJSONObject(i);
    //                    DailyAssert.assertNotNull(jsonObject);
    //
    //                    Booking booking = new Booking(jsonObject);
    //                    DailyAssert.assertNotNull(booking);
    //
    //                    bookingList.add(booking);
    //                }
    //
    //                return bookingList;
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestBookingList(mNetworkTag, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetBookingDetailInformation() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //
    //                        switch (msgCode)
    //                        {
    //                            case 100:
    //                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                                DailyAssert.assertNotNull(dataJSONObject);
    //
    //                                GourmetBookingDetail gourmetBookingDetail = new GourmetBookingDetail();
    //                                gourmetBookingDetail.setData(dataJSONObject);
    //                                break;
    //
    //                            // 예약 내역 진입시에 다른 사용자가 딥링크로 진입시 예외 처리 추가
    //                            case 501:
    //                                DailyAssert.fail(responseJSONObject.getString("msg"));
    //                                break;
    //
    //                            default:
    //                                DailyAssert.fail(responseJSONObject.getString("msg"));
    //                                break;
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetBookingDetailInformation(//
    //            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetReceipt() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        final retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        JSONObject dataJsonObject = responseJSONObject.getJSONObject("data");
    //
    //                        if (msgCode == 100)
    //                        {
    //                            // 영수증
    //                            String reservationIdx = dataJsonObject.getString("gourmetReservationIdx");
    //                            DailyAssert.assertFalse(Util.isTextEmpty(reservationIdx));
    //
    //                            String userName = dataJsonObject.getString("userName");
    //                            String userPhone = dataJsonObject.getString("userPhone");
    //                            int ticketCount = dataJsonObject.getInt("ticketCount");
    //                            String placeName = dataJsonObject.getString("restaurantName");
    //                            String placeAddress = dataJsonObject.getString("restaurantAddress");
    //                            String sday = dataJsonObject.getString("sday");
    //                            String valueDate = dataJsonObject.getString("paidAt");
    //                            //  String currency = receiptJSONObject.getString("currency");
    //                            int paymentAmount = dataJsonObject.getInt("paymentAmount");
    //                            int tax = dataJsonObject.getInt("tax");
    //                            int supplyPrice = dataJsonObject.getInt("supplyPrice");
    //                            int sellingPrice = dataJsonObject.getInt("sellingPrice");
    //                            String paymentType = dataJsonObject.getString("paymentType");
    //                            int counpon = dataJsonObject.getInt("couponAmount");
    //
    //                            // **공급자**
    //                            String receiptNotice = dataJsonObject.getString("receiptNotice");
    //
    //                            DailyAssert.assertNotNull(userName);
    //                            DailyAssert.assertNotNull(userPhone);
    //                            DailyAssert.assertTrue(ticketCount > 0);
    //                            DailyAssert.assertNotNull(placeName);
    //                            DailyAssert.assertNotNull(placeAddress);
    //                            DailyAssert.assertNotNull(sday);
    //                            DailyAssert.assertNotNull(valueDate);
    //                            DailyAssert.assertTrue(paymentAmount >= 0);
    //                            DailyAssert.assertTrue(tax >= 0);
    //                            DailyAssert.assertTrue(supplyPrice >= 0);
    //                            DailyAssert.assertTrue(sellingPrice >= 0);
    //                            DailyAssert.assertNotNull(paymentType);
    //                            DailyAssert.assertTrue(counpon >= 0);
    //                            DailyAssert.assertNotNull(receiptNotice);
    //
    //                        } else
    //                        {
    //                            DailyAssert.fail(responseJSONObject.getString("msg"));
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetReceipt(mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 원래는 테스트 해야 하지만.. 예약 내역을 히든 할 경우 Test reservation index를 바꿔줘야 함으로 skip 함.
    //    @Ignore
    //    public void requestGourmetHiddenBooking() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        // 해당 화면은 메시지를 넣지 않는다.
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertTrue(msgCode == 0 || msgCode == 100 || msgCode == 200);
    //
    //                        DailyAssert.assertTrue(responseJSONObject.has("msg"));
    //
    //                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                        DailyAssert.assertNotNull(dataJSONObject);
    //
    //                        boolean result = false;
    //
    //                        if (dataJSONObject != null)
    //                        {
    //                            if (dataJSONObject.has("isSuccess") == true)
    //                            {
    //                                result = dataJSONObject.getInt("isSuccess") == 1;
    //                            } else if (dataJSONObject.has("is_success") == true)
    //                            {
    //                                result = dataJSONObject.getBoolean("is_success");
    //                            }
    //                        }
    //
    //                        DailyAssert.assertTrue(result);
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetHiddenBooking(//
    //            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // booking tid 의 경우 항시 변경 됨으로 requestBookingList 이후에 진행하도록 함
    //    @Ignore
    //    public void requestGourmetAccountInformation(Booking booking) throws Exception
    //    {
    //        if (booking == null)
    //        {
    //            DailyAssert.assertNotNull(booking);
    //            return;
    //        }
    //
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertEquals(0, msgCode);
    //
    //                        if (msgCode == 0)
    //                        {
    //                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(jsonObject);
    //
    //                            String accountNumber = jsonObject.getString("account_num");
    //                            String bankName = jsonObject.getString("bank_name");
    //                            String accountName = jsonObject.getString("name");
    //                            String date = jsonObject.getString("date");
    //                            String time = jsonObject.getString("time");
    //                            String msg1 = jsonObject.getString("msg1");
    //
    //                            int couponAmount = jsonObject.getInt("coupon_amount");
    //                            int price = jsonObject.getInt("price");
    //                            int paymetPrice = jsonObject.getInt("amt");
    //
    //                            DailyAssert.assertNotNull(accountNumber);
    //                            DailyAssert.assertNotNull(bankName);
    //                            DailyAssert.assertNotNull(accountName);
    //                            DailyAssert.assertNotNull(date);
    //                            DailyAssert.assertNotNull(time);
    //                            DailyAssert.assertNotNull(msg1);
    //
    //                            DailyAssert.assertTrue(couponAmount >= 0);
    //                            DailyAssert.assertTrue(price >= 0);
    //                            DailyAssert.assertTrue(paymetPrice >= 0);
    //
    //                        } else
    //                        {
    //                            DailyAssert.fail(responseJSONObject.getString("msg"));
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetAccountInformation(mNetworkTag, booking.tid, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetRegionList() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            JSONArray provinceArray = dataJSONObject.getJSONArray("province");
    //                            DailyAssert.assertNotNull(provinceArray);
    //                            ArrayList<Province> provinceList = makeProvinceList(provinceArray);
    //                            DailyAssert.assertNotNull(provinceList);
    //
    //                            JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
    //                            ArrayList<Area> areaList = makeAreaList(areaJSONArray);
    //                            DailyAssert.assertNotNull(areaList);
    //
    //                            String imageUrl = dataJSONObject.getString("imgUrl");
    //                            DailyAssert.assertNotNull(imageUrl);
    //
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //
    //            private ArrayList<Area> makeAreaList(JSONArray jsonArray) throws JSONException
    //            {
    //                ArrayList<Area> areaList = new ArrayList<>();
    //
    //                int length = jsonArray.length();
    //                for (int i = 0; i < length; i++)
    //                {
    //                    JSONObject jsonObject = jsonArray.getJSONObject(i);
    //                    DailyAssert.assertNotNull(jsonObject);
    //
    //                    try
    //                    {
    //                        Area area = new Area(jsonObject);
    //                        areaList.add(area);
    //                    } catch (JSONException e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                }
    //
    //                return areaList;
    //            }
    //
    //            private ArrayList<Province> makeProvinceList(JSONArray jsonArray)
    //            {
    //                ArrayList<Province> provinceList = new ArrayList<>();
    //
    //                try
    //                {
    //                    int length = jsonArray.length();
    //                    for (int i = 0; i < length; i++)
    //                    {
    //                        JSONObject jsonObject = jsonArray.getJSONObject(i);
    //                        DailyAssert.assertNotNull(jsonObject);
    //
    //                        try
    //                        {
    //                            Province province = new Province(jsonObject, null);
    //                            provinceList.add(province);
    //                        } catch (JSONException e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    }
    //                } catch (Exception e)
    //                {
    //                    DailyAssert.fail(e);
    //                }
    //
    //                return provinceList;
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetRegionList(mNetworkTag, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetList() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            JSONArray gourmetJSONArray = null;
    //
    //                            if (dataJSONObject.has("gourmetSales") == true)
    //                            {
    //                                gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSales");
    //                                DailyAssert.assertNotNull(gourmetJSONArray);
    //                            }
    //
    //                            int totalCount = dataJSONObject.getInt("searchCount");
    //                            int maxCount = dataJSONObject.getInt("searchMaxCount");
    //
    //                            DailyAssert.assertTrue(totalCount >= 0);
    //                            DailyAssert.assertTrue(maxCount >= 0);
    //
    //                            int page;
    //                            String imageUrl;
    //
    //                            ArrayList<Gourmet> gourmetList = new ArrayList<>();
    //
    //                            if (gourmetJSONArray != null)
    //                            {
    //                                imageUrl = dataJSONObject.getString("imgUrl");
    //                                DailyAssert.assertNotNull(imageUrl);
    //
    //                                checkGourmetList(gourmetJSONArray);
    //                            }
    //
    //                            JSONObject filterJSONObject = dataJSONObject.getJSONObject("filter");
    //                            DailyAssert.assertNotNull(filterJSONObject);
    //
    //                            JSONArray categoryJSONArray = filterJSONObject.getJSONArray("categories");
    //                            DailyAssert.assertNotNull(categoryJSONArray);
    //
    //                            int categoryCount = categoryJSONArray.length();
    //                            // 필터 정보 넣기
    //                            for (int i = 0; i < categoryCount; i++)
    //                            {
    //                                JSONObject categoryJSONObject = categoryJSONArray.getJSONObject(i);
    //                                DailyAssert.assertNotNull(categoryJSONObject);
    //
    //                                int categoryCode = categoryJSONObject.getInt("code");
    //                                int categorySeq = categoryJSONObject.getInt("sequence");
    //                                String categoryName = categoryJSONObject.getString("name");
    //
    //                                DailyAssert.assertTrue(categoryCode >= 0);
    //                                DailyAssert.assertTrue(categorySeq >= 0);
    //                                DailyAssert.assertNotNull(categoryName);
    //                            }
    //
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //
    //            private void checkGourmetList(JSONArray jsonArray) throws JSONException
    //            {
    //                int length = jsonArray.length();
    //                for (int i = 0; i < length; i++)
    //                {
    //                    JSONObject jsonObject = jsonArray.getJSONObject(i);
    //                    DailyAssert.assertNotNull(jsonObject);
    //
    //                    int index = jsonObject.getInt("restaurantIdx");
    //
    //                    String name = null;
    //                    if (jsonObject.has("restaurantName") == true)
    //                    {
    //                        name = jsonObject.getString("restaurantName");
    //                    } else if (jsonObject.has("name") == true)
    //                    {
    //                        name = jsonObject.getString("name");
    //                    }
    //
    //                    DailyAssert.assertTrue(index > 0);
    //                    DailyAssert.assertNotNull(name);
    //
    //                    DailyAssert.assertTrue(jsonObject.getInt("price") >= 0);
    //                    DailyAssert.assertTrue(jsonObject.getInt("discount") >= 0);
    //
    //                    DailyAssert.assertNotNull(jsonObject.getString("addrSummary"));
    //                    DailyAssert.assertNotNull(jsonObject.getString("districtName"));
    //
    //                    DailyAssert.assertNotNull(jsonObject.getDouble("latitude"));
    //                    DailyAssert.assertNotNull(jsonObject.getDouble("longitude"));
    //
    //                    DailyAssert.assertTrue(jsonObject.has("isDailychoice"));
    //                    DailyAssert.assertTrue(jsonObject.has("isSoldOut"));
    //
    //                    DailyAssert.assertTrue(jsonObject.getInt("persons") > 0);
    //
    //                    DailyAssert.assertNotNull(jsonObject.getString("category"));
    //
    //                    DailyAssert.assertTrue(jsonObject.getInt("categoryCode") >= 0);
    //                    DailyAssert.assertTrue(jsonObject.getInt("categorySeq") >= 0);
    //
    //                    if (jsonObject.has("categorySub") == true)
    //                    {
    //                        DailyAssert.assertNotNull(jsonObject.getString("categorySub"));
    //                    }
    //
    //                    if (jsonObject.has("rating") == true)
    //                    {
    //                        DailyAssert.assertTrue(jsonObject.getInt("rating") >= 0);
    //                    }
    //
    //                    if (jsonObject.has("distance") == true)
    //                    {
    //                        DailyAssert.assertTrue(jsonObject.getDouble("distance") >= 0);
    //                    }
    //
    //                    JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
    //                    DailyAssert.assertNotNull(imageJSONObject);
    //
    //                    Iterator<String> iterator = imageJSONObject.keys();
    //                    while (iterator.hasNext())
    //                    {
    //                        String key = iterator.next();
    //                        JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
    //                        DailyAssert.assertNotNull(pathJSONArray);
    //                        DailyAssert.assertNotNull(pathJSONArray.getString(0));
    //                        break;
    //                    }
    //
    //                    if (jsonObject.has("benefit") == true)
    //                    {
    //                        DailyAssert.assertNotNull(jsonObject.getString("benefit"));
    //                    }
    //                }
    //            }
    //        };
    //
    //        HashMap<String, Object> hashMap = new HashMap<>();
    //        hashMap.put("reserveDate", mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
    //        hashMap.put("provinceIdx", Const.TEST_GOURMET_PROVINCE_INDEX);
    //        // skip Area
    //        //  hashMap.put("areaIdx", areaIdx);
    //        hashMap.put("persons", Const.TEST_STAY_PERSONS);
    //
    //        if (Const.TEST_PAGE_INDEX > 0)
    //        {
    //            hashMap.put("page", Const.TEST_PAGE_INDEX);
    //            hashMap.put("limit", Const.TEST_LIMIT_LIST_COUNT);
    //        }
    //
    //        //skip sort
    //        //  if (Constants.SortType.DEFAULT != mSort)
    //        //  {
    //        //if (Util.isTextEmpty(sortProperty) == false)
    //        //{
    //        // hashMap.put("sortProperty", sortProperty);
    //        //}
    //        //
    //        //if (Util.isTextEmpty(sortDirection) == false)
    //        //{
    //        // hashMap.put("sortDirection", sortDirection);
    //        //}
    //        //
    //        //if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
    //        //{
    //        // hashMap.put("latitude", latitude);
    //        // hashMap.put("longitude", longitude);
    //        //}
    //        // }
    //
    //        hashMap.put("details", Const.TEST_IS_SHOW_LIST_DETAIL);
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetList(mNetworkTag, hashMap, null, null, null, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetSearchAutoCompleteList() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //                            DailyAssert.assertNotNull(dataJSONArray);
    //
    //                            int length = dataJSONArray.length();
    //                            for (int i = 0; i < length; i++)
    //                            {
    //                                JSONObject keywordJSONObject = dataJSONArray.getJSONObject(i);
    //                                DailyAssert.assertNotNull(keywordJSONObject);
    //                                DailyAssert.assertNotNull(keywordJSONObject.getString("displayText"));
    //
    //                                if (keywordJSONObject.has("discount") == true)
    //                                {
    //                                    DailyAssert.assertNotNull(keywordJSONObject.getInt("discount"));
    //                                }
    //                            }
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetSearchAutoCompleteList(mNetworkTag//
    //            , mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), Const.TEST_GOURMET_AUTO_SEARCH_TEXT, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetDetailInformation() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //
    //                        JSONObject dataJSONObject = null;
    //
    //                        if (responseJSONObject.has("data") == true && responseJSONObject.isNull("data") == false)
    //                        {
    //                            dataJSONObject = responseJSONObject.getJSONObject("data");
    //                        }
    //
    //                        if (msgCode == 100 && dataJSONObject == null)
    //                        {
    //                            msgCode = 4;
    //                        }
    //
    //                        // 100	성공
    //                        // 4	데이터가 없을시
    //                        // 5	판매 마감시
    //                        switch (msgCode)
    //                        {
    //                            case 100:
    //                                checkGourmetDetail(dataJSONObject);
    //                                break;
    //
    //                            case 5:
    //                            {
    //                                checkGourmetDetail(dataJSONObject);
    //
    //                                if (responseJSONObject.has("msg") == true)
    //                                {
    //                                    String msg = responseJSONObject.getString("msg");
    //                                    DailyAssert.fail(msg);
    //                                } else
    //                                {
    //                                    DailyAssert.fail();
    //                                }
    //                                break;
    //                            }
    //
    //                            case 4:
    //                            default:
    //                            {
    //                                if (responseJSONObject.has("msg") == true)
    //                                {
    //                                    String msg = responseJSONObject.getString("msg");
    //                                    DailyAssert.fail(msg);
    //                                } else
    //                                {
    //                                    DailyAssert.fail();
    //                                }
    //                                break;
    //                            }
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //
    //            private void checkGourmetDetail(JSONObject jsonObject) throws Exception
    //            {
    //                DailyAssert.assertNotNull(jsonObject.getString("category"));
    //                DailyAssert.assertNotNull(jsonObject.getString("categorySub"));
    //                DailyAssert.assertNotNull(jsonObject.getString("name"));
    //                DailyAssert.assertNotNull(jsonObject.getString("address"));
    //
    //                DailyAssert.assertNotNull(jsonObject.getDouble("longitude"));
    //                DailyAssert.assertNotNull(jsonObject.getDouble("latitude"));
    //
    //                boolean ratingShow = jsonObject.getBoolean("ratingShow");
    //                if (ratingShow == true)
    //                {
    //                    DailyAssert.assertTrue(jsonObject.getInt("ratingValue") >= 0);
    //                    DailyAssert.assertTrue(jsonObject.getInt("ratingPersons") >= 0);
    //                }
    //
    //                // 주차가능
    //                DailyAssert.assertTrue(jsonObject.has("parking"));
    //                // 발렛가능
    //                DailyAssert.assertTrue(jsonObject.has("valet"));
    //                // 프라이빗룸
    //                DailyAssert.assertTrue(jsonObject.has("privateRoom"));
    //                // 단체예약
    //                DailyAssert.assertTrue(jsonObject.has("groupBooking"));
    //                // 베이비시트
    //                DailyAssert.assertTrue(jsonObject.has("babySeat"));
    //                // 코르키지
    //                DailyAssert.assertTrue(jsonObject.has("corkage"));
    //
    //                // Image Url
    //                String imageUrl = jsonObject.getString("imgUrl");
    //                DailyAssert.assertNotNull(imageUrl);
    //
    //                JSONObject pathUrlJSONObject = jsonObject.getJSONObject("imgPath");
    //                DailyAssert.assertNotNull(pathUrlJSONObject);
    //
    //                Iterator<String> iterator = pathUrlJSONObject.keys();
    //                while (iterator.hasNext())
    //                {
    //                    String key = iterator.next();
    //
    //                    try
    //                    {
    //                        JSONArray pathJSONArray = pathUrlJSONObject.getJSONArray(key);
    //                        DailyAssert.assertNotNull(pathUrlJSONObject);
    //
    //                        int length = pathJSONArray.length();
    //                        for (int i = 0; i < length; i++)
    //                        {
    //                            JSONObject imageInformationJSONObject = pathJSONArray.getJSONObject(i);
    //                            DailyAssert.assertNotNull(imageInformationJSONObject);
    //
    //                            DailyAssert.assertNotNull(imageInformationJSONObject.getString("description"));
    //                            DailyAssert.assertNotNull(imageInformationJSONObject.getString("name"));
    //                        }
    //                        break;
    //                    } catch (JSONException e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                }
    //
    //                //benefit
    //                if (jsonObject.has("benefit") == true)
    //                {
    //                    String benefit = jsonObject.getString("benefit");
    //
    //                    if (Util.isTextEmpty(benefit) == false && jsonObject.has("benefitContents") == true && jsonObject.isNull("benefitContents") == false)
    //                    {
    //                        JSONArray benefitJSONArray = jsonObject.getJSONArray("benefitContents");
    //                        DailyAssert.assertNotNull(benefitJSONArray);
    //
    //                        int length = benefitJSONArray.length();
    //                        if (length > 0)
    //                        {
    //                            for (int i = 0; i < length; i++)
    //                            {
    //                                DailyAssert.assertNotNull(benefitJSONArray.getString(i));
    //                            }
    //                        }
    //                    }
    //                }
    //
    //                // Detail
    //                JSONArray detailJSONArray = jsonObject.getJSONArray("details");
    //
    //                int detailLength = detailJSONArray.length();
    //                for (int i = 0; i < detailLength; i++)
    //                {
    //                    JSONObject detailJSONObject = detailJSONArray.getJSONObject(i);
    //                    DailyAssert.assertNotNull(detailJSONObject);
    //
    //                    Iterator<String> detailIterator = jsonObject.keys();
    //                    if (detailIterator.hasNext() == true)
    //                    {
    //                        String title = detailIterator.next();
    //                        JSONArray jsonArray = jsonObject.getJSONArray(title);
    //                        DailyAssert.assertNotNull(jsonArray);
    //
    //                        int length = jsonArray.length();
    //                        for (int j = 0; j < length; j++)
    //                        {
    //                            DailyAssert.assertNotNull(jsonArray.getString(i));
    //                        }
    //                    }
    //                }
    //
    //                // Ticket Information
    //                if (jsonObject.has("tickets") == true && jsonObject.isNull("tickets") == false)
    //                {
    //                    JSONArray ticketInformationJSONArray = jsonObject.getJSONArray("tickets");
    //                    DailyAssert.assertNotNull(ticketInformationJSONArray);
    //
    //                    int ticketInformationLength = ticketInformationJSONArray.length();
    //                    for (int i = 0; i < ticketInformationLength; i++)
    //                    {
    //                        JSONObject ticketInfromateionJSONObject = ticketInformationJSONArray.getJSONObject(i);
    //                        DailyAssert.assertTrue(ticketInfromateionJSONObject.getInt("saleIdx") > 0);
    //                        DailyAssert.assertNotNull(ticketInfromateionJSONObject.getString("ticketName").trim());
    //                        DailyAssert.assertNotNull(ticketInfromateionJSONObject.getString("option").trim());
    //                        DailyAssert.assertNotNull(ticketInfromateionJSONObject.getString("benefit").trim());
    //                        DailyAssert.assertTrue(ticketInfromateionJSONObject.getInt("price") >= 0);
    //                        DailyAssert.assertTrue(ticketInfromateionJSONObject.getInt("discount") >= 0);
    //                    }
    //                }
    //
    //                DailyAssert.assertTrue(jsonObject.has("myWish"));
    //                DailyAssert.assertTrue(jsonObject.has("wishCount"));
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetDetailInformation(//
    //            mNetworkTag, Const.TEST_GOURMET_INDEX, mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetPaymentInformation() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertEquals(0, msgCode);
    //
    //                        if (msgCode == 0)
    //                        {
    //                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(jsonObject);
    //
    //                            int discountPrice = jsonObject.getInt("discount");
    //                            long sday = jsonObject.getLong("sday");
    //                            int maxCount = jsonObject.getInt("max_sale_count");
    //                            DailyAssert.assertTrue(discountPrice >= 0);
    //                            DailyAssert.assertTrue(sday > 0);
    //                            DailyAssert.assertTrue(maxCount >= 0);
    //
    //                            JSONArray timeJSONArray = jsonObject.getJSONArray("eating_time_list");
    //                            DailyAssert.assertNotNull(timeJSONArray);
    //
    //                            int length = timeJSONArray.length();
    //                            for (int i = 0; i < length; i++)
    //                            {
    //                                DailyAssert.assertNotNull(timeJSONArray.getLong(i));
    //                            }
    //
    //                            if (length > 0)
    //                            {
    //                                requestGourmetCheckTicket(Const.TEST_GOURMET_TIKET_INDEX, timeJSONArray.getLong(0));
    //                            }
    //
    //                        } else
    //                        {
    //                            DailyAssert.fail(responseJSONObject.getString("msg"));
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetPaymentInformation(//
    //            mNetworkTag, Const.TEST_GOURMET_TIKET_INDEX, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 해당 티켓은 requestGourmetPaymentInformation 에서 처리 해야 함. GourmetPaymentInformation의 정보가 필요함.
    //    @Ignore
    //    public void requestGourmetCheckTicket(int ticketIndex, long ticketTime) throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                        DailyAssert.assertNotNull(dataJSONObject);
    //
    //                        DailyAssert.assertTrue(dataJSONObject.has("on_sale"));
    //
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertEquals(0, msgCode);
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetCheckTicket(mNetworkTag//
    //            , ticketIndex//
    //            , mSaleTime.getDayOfDaysDateFormat("yyMMdd")//
    //            , Const.TEST_GOURMET_TICKET_COUNT//
    //            , Long.toString(ticketTime), networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetPayment() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertEquals(0, msgCode);
    //
    //                        if (msgCode == 0)
    //                        {
    //                            // do nothing!
    //                        } else
    //                        {
    //                            if (responseJSONObject.has("msg") == false)
    //                            {
    //                                DailyAssert.fail();
    //                            } else
    //                            {
    //                                String msg = responseJSONObject.getString("msg");
    //                                DailyAssert.assertNotNull(msg);
    //
    //                                String[] result = msg.split("\\^");
    //                                DailyAssert.assertTrue(result != null && result.length > 0);
    //
    //                                if ("SUCCESS".equalsIgnoreCase(result[0]) == true)
    //                                {
    //                                    // do nothing!
    //                                } else if ("FAIL".equalsIgnoreCase(result[0]) == true)
    //                                {
    //                                    // do nothing!
    //                                } else
    //                                {
    //                                    DailyAssert.fail(result.toString());
    //                                }
    //                            }
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        Map<String, String> params = new HashMap<>();
    //        params.put("sale_reco_idx", Integer.toString(Const.TEST_GOURMET_TIKET_INDEX));
    //        params.put("billkey", Const.TEST_EASY_CARD_BILLINGKEY);
    //        params.put("ticket_count", Integer.toString(Const.TEST_GOURMET_TICKET_COUNT));
    //
    //        // coupon skip
    //        //        params.put("user_coupon_code", coupon.userCouponCode);
    //
    //        params.put("customer_name", Const.TEST_USER_NAME);
    //        params.put("customer_phone", Const.TEST_USER_PHONE);
    //        params.put("customer_email", Const.TEST_EMAIL);
    //        params.put("arrival_time", String.valueOf(getGourmetReservationTime()));
    //        params.put("customer_msg", "");
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetPayment(mNetworkTag, params, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 호텔 예약 대기 - Booking tid 는 항시 변하는 항목을 requestBookingList에서 테스트
    //    @Ignore
    //    public void requestDepositWaitDetailInformation(Booking booking) throws Exception
    //    {
    //        if (booking == null)
    //        {
    //            DailyAssert.assertNotNull(booking);
    //            return;
    //        }
    //
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            JSONObject reservationJSONObject = dataJSONObject.getJSONObject("reservation");
    //                            DailyAssert.assertNotNull(reservationJSONObject);
    //
    //                            String accountNumber = reservationJSONObject.getString("vactNum");
    //                            DailyAssert.assertNotNull(accountNumber);
    //
    //                            DailyAssert.assertNotNull(reservationJSONObject.getString("bankName"));
    //                            DailyAssert.assertNotNull(reservationJSONObject.getString("vactName"));
    //
    //                            // 입금기한
    //                            String validToDate = DailyCalendar.convertDateFormatString(reservationJSONObject.getString("validTo"), DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일 HH시 mm분 까지");
    //                            DailyAssert.assertNotNull(validToDate);
    //
    //                            // 결재 금액 정보
    //                            DailyAssert.assertTrue(reservationJSONObject.getInt("price") >= 0);
    //                            DailyAssert.assertTrue(reservationJSONObject.getInt("bonus") >= 0);
    //                            DailyAssert.assertTrue(reservationJSONObject.getInt("couponAmount") >= 0);
    //                            DailyAssert.assertTrue(reservationJSONObject.getInt("amt") >= 0);
    //
    //                            // 확인 사항
    //                            DailyAssert.assertNotNull(dataJSONObject.getString("msg1"));
    //
    //                        } else
    //                        {
    //                            DailyAssert.assertNotNull(responseJSONObject.getString("msg"));
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestDepositWaitDetailInformation(//
    //            mNetworkTag, booking.tid, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Ignore
    //    public void requestStayBookingDetailInformation() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //                        switch (msgCode)
    //                        {
    //                            case 100:
    //                                JSONObject jsonObject = responseJSONObject.getJSONObject("data");
    //
    //                                StayBookingDetail stayBookingDetail = new StayBookingDetail();
    //                                stayBookingDetail.setData(jsonObject);
    //                                break;
    //
    //                            // 예약 내역 진입시에 다른 사용자가 딥링크로 진입시 예외 처리 추가
    //                            case 501:
    //                                DailyAssert.assertNotNull(responseJSONObject.getString("msg"));
    //                                break;
    //
    //                            default:
    //                                DailyAssert.assertNotNull(responseJSONObject.getString("msg"));
    //                                break;
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayBookingDetailInformation( //
    //            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 원래는 테스트 해야 하지만.. 예약 내역을 히든 할 경우 Test reservation index를 바꿔줘야 함으로 skip 함.
    //    @Ignore
    //    public void requestStayHiddenBooking() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        // 해당 화면은 메시지를 넣지 않는다.
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertTrue(msgCode == 0 || msgCode == 100 || msgCode == 200);
    //
    //                        DailyAssert.assertTrue(responseJSONObject.has("msg"));
    //
    //                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                        DailyAssert.assertNotNull(dataJSONObject);
    //
    //                        boolean result = false;
    //
    //                        if (dataJSONObject != null)
    //                        {
    //                            if (dataJSONObject.has("isSuccess") == true)
    //                            {
    //                                result = dataJSONObject.getInt("isSuccess") == 1;
    //                            } else if (dataJSONObject.has("is_success") == true)
    //                            {
    //                                result = dataJSONObject.getBoolean("is_success");
    //                            }
    //                        }
    //
    //                        DailyAssert.assertTrue(result);
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).
    //            requestStayHiddenBooking(//
    //                mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestStayReceipt() throws Exception
    //    {
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
    //                    //			msg_code : 0
    //                    //			data :
    //                    //			- [String] user_name /* 유저 이름 */
    //                    //			- [String] user_phone /* 유저 번호 */
    //                    //			- [String] checkin /* 체크인 날짜(yyyy/mm/dd) */
    //                    //			- [String] checkout /* 체크아웃 날짜(yyyy/mm/dd) */
    //                    //			- [int] nights /* 연박 일수 */
    //                    //			- [int] rooms /* 객실수 */
    //                    //			- [String] hotel_name /* 호텔 명 */
    //                    //			- [String] hotel_address /* 호텔 주소 */
    //                    //			- [String] value_date(yyyy/mm/dd) /* 결제일 */
    //                    //			- [String] currency /* 화폐 단위 */
    //                    //			- [int] discount /* 결제 금액 */
    //                    //			- [int] vat /* 부가세 */
    //                    //			- [int] supply_value /* 공급가액 */
    //                    //			- [String] payment_name /* 결제수단 */
    //                    //			---------------------------------
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertEquals(0, msgCode);
    //
    //                        if (msgCode == 0)
    //                        {
    //                            JSONObject dataJsonObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJsonObject);
    //
    //                            JSONObject receiptJSONObject = dataJsonObject.getJSONObject("receipt");
    //                            DailyAssert.assertNotNull(receiptJSONObject);
    //
    //                            DailyAssert.assertNotNull(dataJsonObject.getString("reservation_idx"));
    //                            DailyAssert.assertNotNull(receiptJSONObject.getString("user_name"));
    //                            DailyAssert.assertNotNull(receiptJSONObject.getString("user_phone"));
    //                            DailyAssert.assertNotNull(receiptJSONObject.getString("checkin"));
    //                            DailyAssert.assertNotNull(receiptJSONObject.getString("checkout"));
    //                            DailyAssert.assertTrue(receiptJSONObject.getInt("nights") > 0);
    //                            DailyAssert.assertTrue(receiptJSONObject.getInt("rooms") > 0);
    //                            DailyAssert.assertNotNull(receiptJSONObject.getString("hotel_name"));
    //                            DailyAssert.assertNotNull(receiptJSONObject.getString("hotel_address"));
    //                            DailyAssert.assertNotNull(receiptJSONObject.getString("value_date"));
    //                            DailyAssert.assertTrue(receiptJSONObject.getInt("discount") >= 0);
    //                            DailyAssert.assertTrue(receiptJSONObject.getInt("vat") >= 0);
    //                            DailyAssert.assertTrue(receiptJSONObject.getInt("supply_value") >= 0);
    //                            DailyAssert.assertNotNull(receiptJSONObject.getString("payment_name"));
    //
    //                            DailyAssert.assertTrue(receiptJSONObject.getInt("bonus") >= 0);
    //                            DailyAssert.assertTrue(receiptJSONObject.getInt("coupon_amount") >= 0);
    //                            DailyAssert.assertTrue(receiptJSONObject.getInt("price") >= 0);
    //
    //                            // **공급자**
    //                            JSONObject provider = dataJsonObject.getJSONObject("provider");
    //                            DailyAssert.assertNotNull(provider);
    //
    //                            String memo = provider.getString("memo");
    //                            DailyAssert.assertNotNull(memo);
    //
    //                        } else
    //                        {
    //                            String msg = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(msg);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayReceipt(//
    //            mNetworkTag, Integer.toString(Const.TEST_STAY_RESERVATION_INDEX), networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestEventList() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertEquals(0, msgCode);
    //
    //                        if (msgCode != 0)
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        } else
    //                        {
    //                            JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //                            DailyAssert.assertNotNull(dataJSONArray);
    //
    //                            if (dataJSONArray != null)
    //                            {
    //                                int length = dataJSONArray.length();
    //                                if (length == 0)
    //                                {
    //                                    DailyAssert.fail();
    //                                } else
    //                                {
    //                                    int eventIndex = -1;
    //
    //                                    for (int i = 0; i < length; i++)
    //                                    {
    //                                        JSONObject jsonObject = dataJSONArray.getJSONObject(i);
    //
    //                                        eventIndex = jsonObject.getInt("idx");
    //                                        DailyAssert.assertTrue(eventIndex >= 0);
    //                                        DailyAssert.assertNotNull(jsonObject.getString("img_url"));
    //                                        DailyAssert.assertTrue(jsonObject.getInt("is_event_join") != 0);
    //                                        DailyAssert.assertNotNull(jsonObject.getString("name"));
    //                                    }
    //
    //                                    if (eventIndex != -1)
    //                                    {
    //                                        requestEventPageUrl(eventIndex);
    //                                    }
    //                                }
    //                            }
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestEventList(mNetworkTag, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestEventNCouponNNoticeNewCount() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback mDailyEventCountCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        boolean isExistNewEvent = false;
    //                        boolean isExistNewCoupon = false;
    //                        boolean isExistNewNotices = false;
    //
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            DailyAssert.assertTrue(dataJSONObject.has("isExistNewEvent"));
    //                            DailyAssert.assertTrue(dataJSONObject.has("isExistNewCoupon"));
    //                            DailyAssert.assertTrue(dataJSONObject.has("isExistNewNotices"));
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        String viewedEventTime = DailyPreference.getInstance(mContext).getViewedEventTime();
    //        String viewedCouponTime = DailyPreference.getInstance(mContext).getViewedCouponTime();
    //        String viewedNoticeTime = DailyPreference.getInstance(mContext).getViewedNoticeTime();
    //
    //        if (Util.isTextEmpty(viewedEventTime) == true)
    //        {
    //            viewedEventTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
    //        }
    //
    //        if (Util.isTextEmpty(viewedCouponTime) == true)
    //        {
    //            viewedCouponTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
    //        }
    //
    //        if (Util.isTextEmpty(viewedNoticeTime) == true)
    //        {
    //            viewedNoticeTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
    //        }
    //
    //        DailyMobileAPI.getInstance(mContext).requestEventNCouponNNoticeNewCount(//
    //            mNetworkTag, viewedEventTime, viewedCouponTime, viewedNoticeTime, mDailyEventCountCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // eventIndex 의 경우 고정이 아니기 때문에 requestEventList 이후에 진행하도록 한다.
    //    @Ignore
    //    public void requestEventPageUrl(int eventIndex) throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        final retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msg_code");
    //                        DailyAssert.assertEquals(0, msgCode);
    //                        if (msgCode != 0)
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        } else
    //                        {
    //                            JSONObject eventJsonObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(eventJsonObject);
    //
    //                            String eventUrl = eventJsonObject.getString("url");
    //                            DailyAssert.assertNotNull(eventUrl);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        String store;
    //        if (Const.RELEASE_STORE == Constants.Stores.PLAY_STORE)
    //        {
    //            store = "google";
    //        } else
    //        {
    //            store = "skt";
    //        }
    //
    //        DailyMobileAPI.getInstance(mContext).requestEventPageUrl(mNetworkTag, eventIndex, store, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Ignore
    //    public void requestEventBannerList() throws Exception
    //    {
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
    //                    JSONObject responseJSONObject = response.body();
    //                    DailyAssert.assertNotNull(responseJSONObject);
    //
    //                    try
    //                    {
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            String baseUrl = dataJSONObject.getString("imgUrl");
    //                            DailyAssert.assertNotNull(baseUrl);
    //
    //                            JSONArray jsonArray = dataJSONObject.getJSONArray("eventBanner");
    //                            DailyAssert.assertNotNull(jsonArray);
    //
    //                            int length = jsonArray.length();
    //                            if (length > 0)
    //                            {
    //                                for (int i = 0; i < length; i++)
    //                                {
    //                                    EventBanner eventBanner = new EventBanner(jsonArray.getJSONObject(i), baseUrl);
    //                                    DailyAssert.assertNotNull(eventBanner);
    //                                }
    //                            } else
    //                            {
    //                                DailyAssert.fail();
    //                            }
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        ExLog.d(e.toString());
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        mLock = new CountDownLatch(1);
    //        DailyMobileAPI.getInstance(mContext).requestEventBannerList(mNetworkTag, "gourmet", networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //
    //        mLock = new CountDownLatch(1);
    //        DailyMobileAPI.getInstance(mContext).requestEventBannerList(mNetworkTag, "hotel", networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestDailyUserVerification() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null)
    //                {
    //                    if (response.isSuccessful() == true && response.body() != null)
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        try
    //                        {
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            String message = responseJSONObject.getString("msg");
    //
    //                            DailyAssert.assertEquals(100, msgCode);
    //                            DailyAssert.assertNotNull(message);
    //                        } catch (Exception e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    } else if (response.isSuccessful() == false && response.errorBody() != null)
    //                    {
    //                        try
    //                        {
    //                            JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
    //                            DailyAssert.assertNotNull(responseJSONObject);
    //
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            String message = responseJSONObject.getString("msg");
    //
    //                            DailyAssert.assertEquals(422, response.code());
    //                            if (response.code() == 422)
    //                            {
    //                                switch (msgCode)
    //                                {
    //                                    // 동일한 전화번호로 인증 받은 사용자가 있는 경우
    //                                    case 2001:
    //                                    {
    //                                        JSONObject dataJONObject = responseJSONObject.getJSONObject("data");
    //                                        DailyAssert.assertNotNull(dataJONObject);
    //
    //                                        String phoneNumber = dataJONObject.getString("phone");
    //                                        DailyAssert.assertNotNull(phoneNumber);
    //
    //                                        mLock.countDown();
    //                                        return;
    //                                    }
    //
    //                                    // 전화번호가 유효하지 않을 때
    //                                    case 2003:
    //                                    {
    //                                        DailyAssert.fail("invaild phone number");
    //                                        mLock.countDown();
    //                                        return;
    //                                    }
    //                                }
    //
    //                                DailyAssert.fail(message);
    //                            } else
    //                            {
    //                                DailyAssert.fail();
    //                            }
    //                        } catch (Exception e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    } else
    //                    {
    //                        DailyAssert.fail();
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestDailyUserVerification( //
    //            mNetworkTag, Const.TEST_USER_PHONE, Const.TEST_IS_FORCE_PHONE_VALIDATION, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 폰 넘버와 문자를 받아서 할 것이냐.... 자동으로 받아서 할것인지가.... 음.. 모르겠음
    //    @RequiresDevice
    //    @Ignore
    //    public void requestDailyUserUpdatePhoneNumber(String phoneNumber, String code) throws Exception
    //    {
    //        if (Util.isTextEmpty(phoneNumber, code) == false)
    //        {
    //            DailyAssert.assertTrue("required data is empty! phone : " + phoneNumber + " , code : " + code, false);
    //            return;
    //        }
    //
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null)
    //                {
    //                    if (response.isSuccessful() == true && response.body() != null)
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        try
    //                        {
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            String message = responseJSONObject.getString("msg");
    //
    //                            DailyAssert.assertEquals(100, msgCode);
    //                            DailyAssert.assertNotNull(message);
    //                        } catch (Exception e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    } else if (response.isSuccessful() == false && response.errorBody() != null)
    //                    {
    //                        try
    //                        {
    //                            JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
    //                            DailyAssert.assertNotNull(responseJSONObject);
    //
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            String message = responseJSONObject.getString("msg");
    //
    //                            DailyAssert.assertEquals(422, msgCode);
    //                            DailyAssert.assertNotNull(message);
    //                            //                            if (response.code() == 422)
    //                            //                            {
    //                            //                                switch (msgCode)
    //                            //                                {
    //                            //                                  // SMS인증키가 잘못된 경우
    //                            //                                    case 2002:
    //                            //                                    {
    //                            //                                        return;
    //                            //                                    }
    //                            //
    //                            //                                    // 전화번호가 유효하지 않을 때
    //                            //                                    case 2003:
    //                            //                                    {
    //                            //                                        return;
    //                            //                                    }
    //                            //                                }
    //                            //                            }
    //
    //                            DailyAssert.fail();
    //                        } catch (Exception e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    } else
    //                    {
    //                        DailyAssert.fail();
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestDailyUserUpdatePhoneNumber(mNetworkTag, phoneNumber.replaceAll("-", ""), code, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestSignupValidation() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.body() != null)
    //                {
    //                    JSONObject responseJSONObject = response.body();
    //                    DailyAssert.assertNotNull(responseJSONObject);
    //
    //                    try
    //                    {
    //                        if (response.isSuccessful() == true)
    //                        {
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            DailyAssert.assertEquals(100, msgCode);
    //
    //                            if (msgCode == 100)
    //                            {
    //                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                                DailyAssert.assertNotNull(dataJSONObject);
    //
    //                                String signupKey = dataJSONObject.getString("signup_key");
    //                                String serverDate = dataJSONObject.getString("serverDate");
    //
    //                                DailyAssert.assertNotNull(signupKey);
    //                                DailyAssert.assertNotNull(serverDate);
    //
    //                                requestDailyUserSignupVerfication(signupKey, Const.TEST_USER_PHONE, false);
    //                            } else
    //                            {
    //                                DailyAssert.fail(responseJSONObject.getString("msg"));
    //                            }
    //                        } else
    //                        {
    //                            DailyAssert.fail(responseJSONObject.getString("msg"));
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        String email = "dh_" + mSaleTime.getDayOfDaysDateFormat("yyyy_MM_dd") + "@dailyhotel.com";
    //
    //        HashMap<String, String> signUpParams = new HashMap<>();
    //
    //        signUpParams.put("email", email);
    //        signUpParams.put("pw", Const.TEST_PASSWORD);
    //        signUpParams.put("name", Const.TEST_USER_NAME);
    //
    //        // recommender skip!
    //        //        if (Util.isTextEmpty(recommender) == false)
    //        //        {
    //        //            signUpParams.put("recommender", recommender);
    //        //        }
    //
    //        signUpParams.put("birthday", Const.TEST_USER_BIRTHDAY);
    //        signUpParams.put("market_type", Const.RELEASE_STORE.getName());
    //        signUpParams.put("isAgreedBenefit", Boolean.toString(Const.TEST_IS_AGREED_BENEFIT));
    //
    //        DailyMobileAPI.getInstance(mContext).requestSignupValidation(mNetworkTag, signUpParams, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // requestSignupValidation 이후에 진행 되어야 함
    //    @Ignore
    //    public void requestDailyUserSignupVerfication(String signupKey, String phoneNumber, boolean force) throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null)
    //                {
    //                    if (response.isSuccessful() == true && response.body() != null)
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //
    //                        try
    //                        {
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            String message = responseJSONObject.getString("msg");
    //
    //                            DailyAssert.assertEquals(100, msgCode);
    //                            DailyAssert.assertNotNull(message);
    //                        } catch (Exception e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    } else if (response.isSuccessful() == false && response.errorBody() != null)
    //                    {
    //                        try
    //                        {
    //                            JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
    //                            DailyAssert.assertNotNull(responseJSONObject);
    //
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            String message = responseJSONObject.getString("msg");
    //
    //                            DailyAssert.assertEquals(402, response.code());
    //                            DailyAssert.assertNotNull(message);
    //
    //                            switch (response.code())
    //                            {
    //                                case 422:
    //                                {
    //                                    switch (msgCode)
    //                                    {
    //                                        // 동일한 전화번호로 인증 받은 사용자가
    //                                        case 2001:
    //                                        {
    //                                            JSONObject dataJONObject = responseJSONObject.getJSONObject("data");
    //                                            DailyAssert.assertNotNull(dataJONObject);
    //
    //                                            String phoneNumber = dataJONObject.getString("phone");
    //                                            DailyAssert.assertNotNull(phoneNumber);
    //                                            return;
    //                                        }
    //
    //                                        //                                        // 전화번호가 유효하지 않을 때
    //                                        //                                        case 2003:
    //                                        //                                        {
    //                                        //                                            return;
    //                                        //                                        }
    //                                    }
    //                                    break;
    //                                }
    //                                //
    //                                //                                case 400:
    //                                //                                {
    //                                //                                    switch (msgCode)
    //                                //                                    {
    //                                //                                        case 2004:
    //                                //                                            break;
    //                                //                                    }
    //                                //                                    break;
    //                                //                                }
    //                            }
    //
    //                            DailyAssert.fail();
    //                        } catch (Exception e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    } else
    //                    {
    //                        DailyAssert.fail();
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestDailyUserSignupVerfication(mNetworkTag, signupKey, phoneNumber, force, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // requestSignupValidation 이후에 진행 되어야 함 - 인증번호 필수
    //    @RequiresDevice
    //    @Ignore
    //    public void requestDailyUserSignup(String signupKey, String vaildateCode, String phoneNumber) throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null)
    //                {
    //                    if (response.isSuccessful() == true && response.body() != null)
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        try
    //                        {
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            String message = responseJSONObject.getString("msg");
    //
    //                            DailyAssert.assertEquals(0, msgCode);
    //                            DailyAssert.assertNotNull(message);
    //
    //                            if (msgCode == 0)
    //                            {
    //                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                                DailyAssert.assertNotNull(dataJSONObject);
    //
    //                                boolean isSignup = dataJSONObject.getBoolean("is_signup");
    //                                DailyAssert.assertTrue(isSignup);
    //
    //                                if (isSignup == true)
    //                                {
    //                                    mLock.countDown();
    //                                    return;
    //                                }
    //                            }
    //
    //                            DailyAssert.fail();
    //
    //                        } catch (Exception e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    } else if (response.isSuccessful() == false && response.errorBody() != null)
    //                    {
    //                        try
    //                        {
    //                            JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
    //                            DailyAssert.assertNotNull(responseJSONObject);
    //
    //                            int msgCode = responseJSONObject.getInt("msgCode");
    //                            String message = responseJSONObject.getString("msg");
    //
    //                            DailyAssert.assertEquals(422, msgCode);
    //                            DailyAssert.assertNotNull(message);
    //
    //                            DailyAssert.fail();
    //                        } catch (Exception e)
    //                        {
    //                            DailyAssert.fail(e);
    //                        }
    //                    } else
    //                    {
    //                        DailyAssert.fail();
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestDailyUserSignup(//
    //            mNetworkTag, signupKey, vaildateCode, phoneNumber, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 해당 기능은... 일단 패스 고려하지 않음
    //    @Ignore
    //    public void requestFacebookUserSignup() throws Exception
    //    {
    //        //  mLock = new CountDownLatch(1);
    //        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 해당 기능은... 일단 패스 고려하지 않음
    //    @Ignore
    //    public void requestKakaoUserSignup() throws Exception
    //    {
    //        //  mLock = new CountDownLatch(1);
    //        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 해당 기능은 사전 테스트로 인하여 그냥 성공!
    //    @Test
    //    public void requestDailyUserSignin() throws Exception
    //    {
    //        //  mLock = new CountDownLatch(1);
    //        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 해당 기능은... 일단 패스 고려하지 않음
    //    @RequiresDevice
    //    @Ignore
    //    public void requestFacebookUserSignin() throws Exception
    //    {
    //        //  mLock = new CountDownLatch(1);
    //        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 해당 기능은... 일단 패스 고려하지 않음
    //    @RequiresDevice
    //    @Ignore
    //    public void requestKakaoUserSignin() throws Exception
    //    {
    //        //  mLock = new CountDownLatch(1);
    //        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestCouponList() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
    //                            DailyAssert.assertNotNull(list);
    //
    //                            for (Coupon coupon : list)
    //                            {
    //                                if (coupon.isDownloaded == false)
    //                                {
    //                                    requestDownloadCoupon(coupon.userCouponCode);
    //                                    break;
    //                                }
    //                            }
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestCouponList(mNetworkTag, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestCouponList1() throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
    //                            DailyAssert.assertNotNull(list);
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestCouponList(//
    //            mNetworkTag, Const.TEST_STAY_INDEX, Const.TEST_STAY_SALE_ROOM_INDEX,//
    //            mSaleTime.getDayOfDaysDateFormat(DailyCalendar.ISO_8601_FORMAT),//
    //            mSaleTime.getClone(Const.TEST_NIGHTS).getDayOfDaysDateFormat(DailyCalendar.ISO_8601_FORMAT), networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestCouponList2() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback couponListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
    //                            DailyAssert.assertNotNull(list);
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestCouponList(//
    //            mNetworkTag, Const.TEST_GOURMET_INDEX, Const.TEST_GOURMET_TICKET_COUNT, couponListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestCouponHistoryList() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback couponHistoryCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            ArrayList<CouponHistory> list = CouponUtil.getCouponHistoryList(responseJSONObject);
    //                            DailyAssert.assertNotNull(list);
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestCouponHistoryList(mNetworkTag, couponHistoryCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestNoticeAgreement() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback noticeAgreementCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            DailyAssert.assertNotNull(dataJSONObject.getString("description1"));
    //                            DailyAssert.assertNotNull(dataJSONObject.getString("description2"));
    //                            DailyAssert.assertTrue(dataJSONObject.has("isFirstTimeBuyer"));
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestNoticeAgreement(mNetworkTag, noticeAgreementCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestNoticeAgreementResult() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback noticeAgreementResultCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            String agreeAt = dataJSONObject.getString("agreedAt");
    //                            DailyAssert.assertNotNull(agreeAt);
    //                            DailyAssert.assertNotNull(dataJSONObject.getString("description1InAgree"));
    //                            DailyAssert.assertNotNull(dataJSONObject.getString("description2InAgree"));
    //                            DailyAssert.assertNotNull(dataJSONObject.getString("description1InReject"));
    //                            DailyAssert.assertNotNull(dataJSONObject.getString("description2InReject"));
    //
    //                            agreeAt = DailyCalendar.convertDateFormatString(agreeAt, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
    //                            DailyAssert.assertNotNull(agreeAt);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestNoticeAgreementResult(mNetworkTag, Const.TEST_IS_NOTICE_AGREE, noticeAgreementResultCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestBenefitMessage() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback benefitMessageCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            String message = dataJSONObject.getString("body");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestBenefitMessage(mNetworkTag, benefitMessageCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 다운로드 가능상태의 쿠폰이 없을 수도 있음으로 requestCouponList 이후에 진행하도록 함!
    //    @Ignore
    //    public void requestDownloadCoupon(String userCouponCode) throws Exception
    //    {
    //        if (Util.isTextEmpty(userCouponCode) == true)
    //        {
    //            DailyAssert.fail("userCouponCode is null");
    //            return;
    //        }
    //
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback downloadCouponCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            // do nothing!
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.assertNotNull(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, userCouponCode, downloadCouponCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 웹페이지 연동이 필요하여 일단 스킵!
    //    @Ignore
    //    public void requestDownloadEventCoupon(String couponCode) throws Exception
    //    {
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
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            String validFrom = dataJSONObject.getString("validFrom");
    //                            String validTo = dataJSONObject.getString("validTo");
    //                            DailyAssert.assertNotNull(DailyCalendar.convertDateFormatString(validFrom, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
    //                            DailyAssert.assertNotNull(DailyCalendar.convertDateFormatString(validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //
    //                    } catch (ParseException e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //
    //                    mLock.countDown();
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestDownloadEventCoupon(mNetworkTag, couponCode, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestHasCoupon() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback hasCouponCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            DailyAssert.assertTrue(dataJSONObject.has("existCoupons"));
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestHasCoupon( //
    //            mNetworkTag, Const.TEST_STAY_INDEX, mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), Const.TEST_NIGHTS, hasCouponCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestHasCoupon1() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback hasCouponCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            DailyAssert.assertTrue(dataJSONObject.has("existCoupons"));
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestHasCoupon(//
    //            mNetworkTag, Const.TEST_GOURMET_INDEX, mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), hasCouponCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestCouponList3() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback couponListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
    //                            DailyAssert.assertNotNull(list);
    //                            DailyAssert.assertFalse(list.isEmpty());
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestCouponList(//
    //            mNetworkTag, Const.TEST_STAY_INDEX,//
    //            mSaleTime.getDayOfDaysDateFormat(DailyCalendar.ISO_8601_FORMAT),//
    //            Const.TEST_NIGHTS, couponListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestCouponList4() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback couponListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
    //                            DailyAssert.assertNotNull(list);
    //                            DailyAssert.assertFalse(list.isEmpty());
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestCouponList(//
    //            mNetworkTag, Const.TEST_GOURMET_INDEX,//
    //            mSaleTime.getDayOfDaysDateFormat(DailyCalendar.ISO_8601_FORMAT), couponListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestRegisterKeywordCoupon() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback registerKeywordCouponCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        String message = responseJSONObject.getString("msg");
    //                        DailyAssert.assertNotNull(message);
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestRegisterKeywordCoupon(mNetworkTag, Const.TEST_KEYWORD_COUPON_CODE, registerKeywordCouponCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestUpdateBenefitAgreement() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback updateBenefitCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            String serverDate = dataJSONObject.getString("serverDate");
    //                            DailyAssert.assertNotNull(serverDate);
    //
    //                            serverDate = DailyCalendar.convertDateFormatString(serverDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
    //                            DailyAssert.assertNotNull(serverDate);
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (ParseException e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestUpdateBenefitAgreement(//
    //            mNetworkTag, Const.TEST_IS_UPDATE_BENEFIT_AGREEMENT, updateBenefitCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestUserTracking() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback userTrackingCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                        DailyAssert.assertNotNull(dataJSONObject);
    //
    //                        JSONObject tracking = dataJSONObject.getJSONObject("tracking");
    //                        DailyAssert.assertNotNull(tracking);
    //
    //                        int gourmetPaymentCompletedCount = tracking.getInt("countOfGourmetPaymentCompleted");
    //                        int hotelPaymentCompletedCount = tracking.getInt("countOfHotelPaymentCompleted");
    //
    //                        DailyAssert.assertTrue(gourmetPaymentCompletedCount >= 0);
    //                        DailyAssert.assertTrue(hotelPaymentCompletedCount >= 0);
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestUserTracking(mNetworkTag, userTrackingCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestNoticeList() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback noticeListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            JSONArray jsonArray = dataJSONObject.getJSONArray("notices");
    //                            DailyAssert.assertNotNull(jsonArray);
    //
    //                            int length = jsonArray.length();
    //                            if (length > 0)
    //                            {
    //                                for (int i = 0; i < length; i++)
    //                                {
    //                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
    //                                    DailyAssert.assertNotNull(jsonObject.getInt("idx"));
    //                                    DailyAssert.assertNotNull(jsonObject.getString("title"));
    //                                    DailyAssert.assertNotNull(jsonObject.getString("linkUrl"));
    //                                    DailyAssert.assertNotNull(jsonObject.getString("createdAt"));
    //                                }
    //                            }
    //                        } else
    //                        {
    //                            String msg = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(msg);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestNoticeList(mNetworkTag, noticeListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestReceiptByEmail() throws Exception
    //    {
    //        retrofit2.Callback receiptByEmailCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        String message = responseJSONObject.getString("msg");
    //                        DailyAssert.assertNotNull(message);
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        mLock = new CountDownLatch(1);
    //
    //        DailyMobileAPI.getInstance(mContext).requestReceiptByEmail( //
    //            mNetworkTag, "gourmet", Integer.toString(Const.TEST_GOURMET_RESERVATION_INDEX), Const.TEST_EMAIL, receiptByEmailCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //
    //        mLock = new CountDownLatch(1);
    //
    //        DailyMobileAPI.getInstance(mContext).requestReceiptByEmail( //
    //            mNetworkTag, "stay", Integer.toString(Const.TEST_STAY_RESERVATION_INDEX), Const.TEST_EMAIL, receiptByEmailCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestWishListCount() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback wishListCountCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            int userIndex = dataJSONObject.getInt("userIdx");
    //                            int stayWishCount = dataJSONObject.getInt("wishHotelCount");
    //                            int gourmetWishCount = dataJSONObject.getInt("wishGourmetCount");
    //
    //                            DailyAssert.assertTrue(userIndex > 0);
    //                            DailyAssert.assertTrue(stayWishCount >= 0);
    //                            DailyAssert.assertTrue(gourmetWishCount >= 0);
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestWishListCount(mNetworkTag, wishListCountCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestWishList() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback gourmetWishListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            JSONArray gourmetJSONArray = null;
    //
    //                            if (dataJSONObject.has("gourmetSales") == true)
    //                            {
    //                                gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSales");
    //                                DailyAssert.assertNotNull(gourmetJSONArray);
    //                            }
    //
    //                            if (gourmetJSONArray != null)
    //                            {
    //                                String imageUrl = dataJSONObject.getString("imgUrl");
    //                                DailyAssert.assertNotNull(imageUrl);
    //
    //                                JSONObject jsonObject;
    //                                int testPlaceIndex = -1;
    //
    //                                int length = gourmetJSONArray.length();
    //                                for (int i = 0; i < length; i++)
    //                                {
    //                                    jsonObject = gourmetJSONArray.getJSONObject(i);
    //                                    DailyAssert.assertNotNull(jsonObject);
    //
    //                                    int index = jsonObject.getInt("restaurantIdx");
    //                                    DailyAssert.assertTrue(index > 0);
    //
    //                                    testPlaceIndex = index;
    //
    //                                    String name = null;
    //                                    if (jsonObject.has("restaurantName") == true)
    //                                    {
    //                                        name = jsonObject.getString("restaurantName");
    //                                    } else if (jsonObject.has("name") == true)
    //                                    {
    //                                        name = jsonObject.getString("name");
    //                                    }
    //
    //                                    DailyAssert.assertNotNull(name);
    //
    //                                    DailyAssert.assertTrue(jsonObject.getInt("price") >= 0);
    //                                    DailyAssert.assertTrue(jsonObject.getInt("discount") >= 0);
    //                                    DailyAssert.assertNotNull(jsonObject.getString("addrSummary"));
    //                                    DailyAssert.assertNotNull(jsonObject.getString("districtName"));
    //                                    DailyAssert.assertNotNull(jsonObject.getDouble("latitude"));
    //                                    DailyAssert.assertNotNull(jsonObject.getDouble("longitude"));
    //                                    DailyAssert.assertTrue(jsonObject.has("isDailychoice"));
    //                                    DailyAssert.assertTrue(jsonObject.has("isSoldOut"));
    //                                    DailyAssert.assertTrue(jsonObject.getInt("persons") > 0);
    //                                    DailyAssert.assertNotNull(jsonObject.getString("category"));
    //                                    DailyAssert.assertNotNull(jsonObject.getInt("categoryCode"));
    //                                    DailyAssert.assertNotNull(jsonObject.getInt("categorySeq"));
    //
    //                                    if (jsonObject.has("categorySub") == true)
    //                                    {
    //                                        DailyAssert.assertNotNull(jsonObject.getString("categorySub"));
    //                                    }
    //
    //                                    if (jsonObject.has("rating") == true)
    //                                    {
    //                                        DailyAssert.assertTrue(jsonObject.getInt("rating") >= 0);
    //                                    }
    //
    //                                    if (jsonObject.has("distance") == true)
    //                                    {
    //                                        DailyAssert.assertTrue(jsonObject.getDouble("distance") >= 0.0d);
    //                                    }
    //
    //                                    JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
    //                                    DailyAssert.assertNotNull(imageJSONObject);
    //
    //                                    Iterator<String> iterator = imageJSONObject.keys();
    //                                    while (iterator.hasNext())
    //                                    {
    //                                        String key = iterator.next();
    //                                        DailyAssert.assertNotNull(key);
    //
    //                                        try
    //                                        {
    //                                            JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
    //                                            DailyAssert.assertTrue(pathJSONArray != null && pathJSONArray.length() > 0);
    //
    //                                            DailyAssert.assertNotNull(pathJSONArray.getString(0));
    //                                            break;
    //                                        } catch (JSONException e)
    //                                        {
    //                                            DailyAssert.fail(e);
    //                                        }
    //                                    }
    //
    //                                    if (jsonObject.has("benefit") == true)
    //                                    {
    //                                        DailyAssert.assertNotNull(jsonObject.getString("benefit"));
    //                                    }
    //                                }
    //
    //                                if (testPlaceIndex != -1)
    //                                {
    //                                    requestRemoveWishList(Constants.PlaceType.FNB, testPlaceIndex);
    //                                }
    //                            }
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestWishList(mNetworkTag, "gourmet", gourmetWishListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback stayWishListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            JSONArray hotelJSONArray = null;
    //
    //                            if (dataJSONObject.has("hotelSales") == true)
    //                            {
    //                                hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
    //                                DailyAssert.assertNotNull(hotelJSONArray);
    //                            }
    //
    //                            if (hotelJSONArray != null)
    //                            {
    //                                String imageUrl = dataJSONObject.getString("imgUrl");
    //                                DailyAssert.assertNotNull(imageUrl);
    //
    //                                int nights = dataJSONObject.getInt("stays");
    //                                DailyAssert.assertTrue(nights > 0);
    //
    //                                JSONObject jsonObject;
    //                                int testPlaceIndex = -1;
    //
    //                                int length = hotelJSONArray.length();
    //                                for (int i = 0; i < length; i++)
    //                                {
    //                                    jsonObject = hotelJSONArray.getJSONObject(i);
    //                                    DailyAssert.assertNotNull(jsonObject);
    //
    //                                    DailyAssert.assertNotNull(jsonObject.getString("name"));
    //                                    DailyAssert.assertTrue(jsonObject.getInt("price") >= 0);
    //                                    DailyAssert.assertTrue(jsonObject.getInt("discount") >= 0); // discountAvg ????
    //                                    DailyAssert.assertNotNull(jsonObject.getString("addrSummary"));
    //
    //                                    String gradeString = jsonObject.getString("grade");
    //                                    DailyAssert.assertNotNull(gradeString);
    //                                    DailyAssert.assertNotNull(Stay.Grade.valueOf(gradeString));
    //
    //                                    int index = jsonObject.getInt("hotelIdx");
    //                                    DailyAssert.assertTrue(index > 0);
    //                                    testPlaceIndex = index;
    //
    //                                    DailyAssert.assertTrue(jsonObject.has("isSoldOut"));
    //
    //                                    DailyAssert.assertNotNull(jsonObject.getString("districtName"));
    //                                    DailyAssert.assertNotNull(jsonObject.getString("category"));
    //                                    DailyAssert.assertNotNull(jsonObject.getDouble("latitude"));
    //                                    DailyAssert.assertNotNull(jsonObject.getDouble("longitude"));
    //                                    DailyAssert.assertTrue(jsonObject.has("isDailyChoice"));
    //                                    DailyAssert.assertTrue(jsonObject.getInt("rating") >= 0); // ratingValue ??
    //                                    DailyAssert.assertNotNull(jsonObject.getString("sday"));
    //                                    DailyAssert.assertTrue(jsonObject.getDouble("distance") >= 0.0d);
    //
    //                                    JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
    //                                    DailyAssert.assertNotNull(imageJSONObject);
    //
    //                                    Iterator<String> iterator = imageJSONObject.keys();
    //                                    while (iterator.hasNext())
    //                                    {
    //                                        String key = iterator.next();
    //                                        DailyAssert.assertNotNull(key);
    //
    //                                        try
    //                                        {
    //                                            JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
    //                                            DailyAssert.assertNotNull(pathJSONArray);
    //
    //                                            DailyAssert.assertNotNull(pathJSONArray.getString(0));
    //                                            break;
    //                                        } catch (JSONException e)
    //                                        {
    //                                            DailyAssert.fail(e);
    //                                        }
    //                                    }
    //
    //                                    if (jsonObject.has("benefit") == true) // hotelBenefit ?
    //                                    {
    //                                        DailyAssert.assertNotNull(jsonObject.getString("benefit"));
    //                                    }
    //                                }
    //
    //                                if (testPlaceIndex != -1)
    //                                {
    //                                    requestRemoveWishList(Constants.PlaceType.HOTEL, testPlaceIndex);
    //                                }
    //                            }
    //
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestWishList(mNetworkTag, "hotel", stayWishListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestAddWishList() throws Exception
    //    {
    //        retrofit2.Callback addWishListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (responseJSONObject.has("msg") == true)
    //                        {
    //                            DailyAssert.assertNotNull(responseJSONObject.getString("msg"));
    //
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        mLock = new CountDownLatch(1);
    //
    //        DailyMobileAPI.getInstance(mContext).requestAddWishList(mNetworkTag, "hotel", Const.TEST_STAY_INDEX, addWishListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //
    //        mLock = new CountDownLatch(1);
    //
    //        DailyMobileAPI.getInstance(mContext).requestAddWishList(mNetworkTag, "gourmet", Const.TEST_GOURMET_INDEX, addWishListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    // 제거할 위시리스트의 인덱스는 계속 변경 됨으로 위시리스트 목록을 가져오는 테스트 이후에 진행하도록 함
    //    @Ignore
    //    public void requestRemoveWishList(Constants.PlaceType placeType, int placeIndex) throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        if (placeIndex <= 0)
    //        {
    //            DailyAssert.fail("placeIndex less than or equal to zero");
    //            return;
    //        }
    //
    //        String type = Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel";
    //
    //        retrofit2.Callback removeWishListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (responseJSONObject.has("msg") == true)
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, type, placeIndex, removeWishListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestPolicyRefund() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback policyRefundCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //                        switch (msgCode)
    //                        {
    //                            case 100:
    //                            {
    //                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                                DailyAssert.assertNotNull(dataJSONObject);
    //
    //                                String comment = dataJSONObject.getString("comment");
    //                                String refundPolicy = dataJSONObject.getString("refundPolicy");
    //                                DailyAssert.assertNotNull(comment);
    //                                DailyAssert.assertNotNull(refundPolicy);
    //
    //                                //                                if (Util.isTextEmpty(refundPolicy) == false)
    //                                //                                {
    //                                //                                    switch (refundPolicy)
    //                                //                                    {
    //                                //                                        case StayBookingDetail.STATUS_NO_CHARGE_REFUND:
    //                                //                                            break;
    //                                //
    //                                //                                        case StayBookingDetail.STATUS_SURCHARGE_REFUND:
    //                                //                                            break;
    //                                //
    //                                //                                        default:
    //                                //                                            break;
    //                                //                                    }
    //                                //                                } else
    //                                //                                {
    //                                //                                    // empty refundPolicy
    //                                //                                    DailyAssert.fail();
    //                                //                                }
    //                                break;
    //                            }
    //
    //                            default:
    //                                // 에러가 발생하더라도 결제는 가능하도록 수정
    //                                DailyAssert.fail();
    //                                break;
    //                        }
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestPolicyRefund(mNetworkTag//
    //            , Const.TEST_STAY_INDEX, Const.TEST_STAY_SALE_ROOM_INDEX//
    //            , mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), mSaleTime.getClone(1).getDayOfDaysDateFormat("yyyy-MM-dd") //
    //            , policyRefundCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestPolicyRefund1() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback policyRefundCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertTrue(100 == msgCode || 1015 == msgCode);
    //
    //                        switch (msgCode)
    //                        {
    //                            case 100:
    //                            case 1015:
    //                            {
    //                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                                DailyAssert.assertNotNull(dataJSONObject);
    //
    //                                String comment = dataJSONObject.getString("comment");
    //                                String refundPolicy = dataJSONObject.getString("refundPolicy");
    //                                boolean refundManual = dataJSONObject.getBoolean("refundManual");
    //
    //                                DailyAssert.assertNotNull(comment);
    //                                DailyAssert.assertNotNull(refundPolicy);
    //                                DailyAssert.assertTrue(dataJSONObject.has("refundManual"));
    //
    //                                // 환불 킬스위치 ON
    //                                if (refundManual == true)
    //                                {
    //                                    if (StayBookingDetail.STATUS_NRD.equalsIgnoreCase(refundPolicy) == true)
    //                                    {
    //                                        // do nothing!
    //                                    } else
    //                                    {
    //                                        DailyAssert.assertNotNull(responseJSONObject.getString("msg"));
    //                                    }
    //                                }
    //                                break;
    //                            }
    //
    //                            default:
    //                                break;
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestPolicyRefund(mNetworkTag//
    //            , Const.TEST_STAY_RESERVATION_INDEX, Const.TEST_PAYMENT_TYPE, policyRefundCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestRefund() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback refundCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        String message = null;
    //                        boolean readyForRefund = false;
    //
    //                        // msgCode 1013: 환불 요청 중 실패한 것으로 messageFromPg를 사용자에게 노출함.
    //                        // msgCode 1014: 무료 취소 횟수를 초과한 것으로 msg 내용을 사용자에게 노출함.
    //                        // msgCode 1015: 환불 수동 스위치 ON일 경우
    //                        switch (msgCode)
    //                        {
    //                            case 1014:
    //                                message = responseJSONObject.getString("msg");
    //                                DailyAssert.fail(message);
    //                                break;
    //
    //                            case 1013:
    //                            case 1015:
    //                            default:
    //                                if (responseJSONObject.has("data") == true && responseJSONObject.isNull("data") == false)
    //                                {
    //                                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                                    message = dataJSONObject.getString("messageFromPg");
    //                                    DailyAssert.assertNotNull(message);
    //
    //                                    readyForRefund = dataJSONObject.getBoolean("readyForRefund");
    //                                    DailyAssert.assertTrue(readyForRefund);
    //                                }
    //
    //                                if (Util.isTextEmpty(message) == true)
    //                                {
    //                                    message = responseJSONObject.getString("msg");
    //                                    DailyAssert.assertNotNull(message);
    //                                }
    //                                break;
    //                        }
    //
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestRefund(mNetworkTag, Const.TEST_STAY_INDEX, //
    //            mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), Const.TEST_PAYMENT_TYPE//
    //            , Const.TEST_STAY_RESERVATION_INDEX, Const.TEST_AUTO_REFUND_CANCEL_MESSAGE, null, null, null, refundCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestBankList() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback bankListCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //                            DailyAssert.assertNotNull(dataJSONArray);
    //
    //                            int length = dataJSONArray.length();
    //                            for (int i = 0; i < length; i++)
    //                            {
    //                                JSONObject jsonObject = dataJSONArray.getJSONObject(i);
    //                                DailyAssert.assertNotNull(jsonObject);
    //
    //                                DailyAssert.assertNotNull(jsonObject.getString("code"));
    //                                DailyAssert.assertNotNull(jsonObject.getString("name"));
    //                            }
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestBankList(mNetworkTag, bankListCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestStayReviewInformation() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback reviewStayCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        // 리뷰가 존재하지 않는 경우 msgCode : 701
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100 && responseJSONObject.has("data") == true)
    //                        {
    //                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(jsonObject);
    //
    //                            Review review = new Review(jsonObject);
    //                            DailyAssert.assertNotNull(review);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayReviewInformation(mNetworkTag, reviewStayCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetReviewInformation() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback reviewGourmetCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        // 리뷰가 존재하지 않는 경우 msgCode : 701
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100 && responseJSONObject.has("data") == true)
    //                        {
    //                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(jsonObject);
    //
    //                            Review review = new Review(jsonObject);
    //                            DailyAssert.assertNotNull(review);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetReviewInformation(mNetworkTag, reviewGourmetCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestStayReviewInformation1() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback stayReviewInformationCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            Review review = new Review(dataJSONObject);
    //                            DailyAssert.assertNotNull(review);
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStayReviewInformation(//
    //            mNetworkTag, Const.TEST_STAY_RESERVATION_INDEX, stayReviewInformationCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestGourmetReviewInformation1() throws Exception
    //    {
    //        mLock = new CountDownLatch(1);
    //
    //        retrofit2.Callback gourmetReviewInformationCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
    //                            DailyAssert.assertNotNull(dataJSONObject);
    //
    //                            Review review = new Review(dataJSONObject);
    //                            DailyAssert.assertNotNull(review);
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetReviewInformation( //
    //            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, gourmetReviewInformationCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestAddReviewInformation() throws Exception
    //    {
    //        retrofit2.Callback addReviewCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            // do nothing!
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.fail(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        mLock = new CountDownLatch(1);
    //
    //        JSONObject stayJsonObject = new JSONObject();
    //        stayJsonObject.put("grade", Const.TEST_STAY_ADD_REVIEW_GRADE);
    //        stayJsonObject.put("itemIdx", Const.TEST_STAY_ADD_REVIEW_ITEM_INDEX);
    //        stayJsonObject.put("reserveIdx", Const.TEST_STAY_RESERVATION_INDEX);
    //        stayJsonObject.put("serviceType", "HOTEL");
    //
    //        DailyMobileAPI.getInstance(mContext).requestAddReviewInformation(mNetworkTag, stayJsonObject, addReviewCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //
    //        mLock = new CountDownLatch(1);
    //
    //        JSONObject gourmetJsonObject = new JSONObject();
    //        gourmetJsonObject.put("grade", Const.TEST_GOURMET_ADD_REVIEW_GRADE);
    //        gourmetJsonObject.put("itemIdx", Const.TEST_GOURMET_ADD_REVIEW_ITEM_INDEX);
    //        gourmetJsonObject.put("reserveIdx", Const.TEST_GOURMET_RESERVATION_INDEX);
    //        gourmetJsonObject.put("serviceType", "GOURMET");
    //
    //        DailyMobileAPI.getInstance(mContext).requestAddReviewInformation(mNetworkTag, gourmetJsonObject, addReviewCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }
    //
    //    @Test
    //    public void requestAddReviewDetailInformation() throws Exception
    //    {
    //        retrofit2.Callback addReviewDetailCallback = new retrofit2.Callback<JSONObject>()
    //        {
    //            @Override
    //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //            {
    //                DailyAssert.setData(call, response);
    //
    //                if (response != null && response.isSuccessful() && response.body() != null)
    //                {
    //                    try
    //                    {
    //                        JSONObject responseJSONObject = response.body();
    //                        DailyAssert.assertNotNull(responseJSONObject);
    //
    //                        int msgCode = responseJSONObject.getInt("msgCode");
    //                        DailyAssert.assertEquals(100, msgCode);
    //
    //                        if (msgCode == 100)
    //                        {
    //                            // do nothing!
    //                        } else
    //                        {
    //                            String message = responseJSONObject.getString("msg");
    //                            DailyAssert.assertNotNull(message);
    //                        }
    //                    } catch (Exception e)
    //                    {
    //                        DailyAssert.fail(e);
    //                    }
    //                } else
    //                {
    //                    DailyAssert.fail();
    //                }
    //
    //                mLock.countDown();
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                DailyAssert.fail(call, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        mLock = new CountDownLatch(1);
    //
    //        JSONObject stayJsonObject = new JSONObject();
    //        stayJsonObject.put("comment", Util.isTextEmpty(Const.TEST_ADD_REVIEW_DETAIL_COMMENT) == true ? "" : Const.TEST_ADD_REVIEW_DETAIL_COMMENT);
    //
    //        stayJsonObject.put("itemIdx", Const.TEST_STAY_ADD_REVIEW_ITEM_INDEX);
    //        stayJsonObject.put("reserveIdx", Const.TEST_STAY_RESERVATION_INDEX);
    //
    //        // 생략!
    //        //            if (scoreJSONArray != null)
    //        //            {
    //        //                jsonObject.put("reviewScores", scoreJSONArray);
    //        //            }
    //        //
    //        //            if (pickJSONArray != null)
    //        //            {
    //        //                jsonObject.put("reviewPicks", pickJSONArray);
    //        //            }
    //
    //        stayJsonObject.put("serviceType", "HOTEL");
    //        DailyMobileAPI.getInstance(mContext).requestAddReviewDetailInformation(mNetworkTag, stayJsonObject, addReviewDetailCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //
    //        mLock = new CountDownLatch(1);
    //
    //        JSONObject gourmetJsonObject = new JSONObject();
    //        gourmetJsonObject.put("comment", Util.isTextEmpty(Const.TEST_ADD_REVIEW_DETAIL_COMMENT) == true ? "" : Const.TEST_ADD_REVIEW_DETAIL_COMMENT);
    //
    //        gourmetJsonObject.put("itemIdx", Const.TEST_GOURMET_ADD_REVIEW_ITEM_INDEX);
    //        gourmetJsonObject.put("reserveIdx", Const.TEST_GOURMET_RESERVATION_INDEX);
    //
    //        // 생략!
    //        //            if (scoreJSONArray != null)
    //        //            {
    //        //                jsonObject.put("reviewScores", scoreJSONArray);
    //        //            }
    //        //
    //        //            if (pickJSONArray != null)
    //        //            {
    //        //                jsonObject.put("reviewPicks", pickJSONArray);
    //        //            }
    //
    //        gourmetJsonObject.put("serviceType", "GOURMET");
    //        DailyMobileAPI.getInstance(mContext).requestAddReviewDetailInformation(mNetworkTag, gourmetJsonObject, addReviewDetailCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }

    public static Matcher<String> isEmpty()
    {
        return new TypeSafeMatcher<String>()
        {
            String stringValue;

            @Override
            protected boolean matchesSafely(String item)
            {
                stringValue = item;
                return Util.isTextEmpty(item);
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(stringValue + " is empty");
            }
        };
    }

    public static Matcher<String> isNotEmpty()
    {
        return new TypeSafeMatcher<String>()
        {
            String stringValue;

            @Override
            protected boolean matchesSafely(String item)
            {
                stringValue = item;
                return Util.isTextEmpty(item) == false;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(stringValue + " is empty");
            }
        };
    }

    public static Matcher<Integer> moreThan(final int defaultValue)
    {
        return new TypeSafeMatcher<Integer>()
        {
            int intValue;

            @Override
            protected boolean matchesSafely(Integer item)
            {
                intValue = item;
                return item >= defaultValue;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(intValue + " is not over " + defaultValue);
            }
        };
    }
}