package com.twoheart.dailyhotel.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.RequiresDevice;
import android.support.test.runner.AndroidJUnit4;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Const;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.CouponHistory;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.model.User;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.CouponUtil;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

import static com.twoheart.dailyhotel.Const.TEST_EMAIL;
import static com.twoheart.dailyhotel.Const.TEST_NIGHTS;
import static com.twoheart.dailyhotel.Const.TEST_PAYMENT_TYPE;
import static com.twoheart.dailyhotel.DailyMatcher.isNotEmpty;
import static com.twoheart.dailyhotel.DailyMatcher.moreThan;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
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

    private TodayDateTime mTodayDateTime;

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
            return "request body is null";
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


            String errorBodyString;
            try
            {
                errorBodyString = response.errorBody().toString();
            } catch (Exception e2)
            {
                errorBodyString = e2.getMessage();
            }

            builder.append("\n").append("errorBody : ").append(errorBodyString);
            builder.append("\n" + "===================== body end =====================");
        }

        builder.append("\n");
        return builder.toString();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Ignore
    private void addException(Call call, Response response, Throwable t)
    {
        String customMessage = getNetworkMessage(call, response);
        mErrorConCollector.addError(new AssertionError(customMessage, t));
    }

    @Ignore
    private long getGourmetReservationTime() throws Exception
    {
        if (mTodayDateTime == null)
        {
            assertThat("mTodayDateTime is null", false);
            return -1;
        }

        String currentTimeString = mTodayDateTime.dailyDateTime;
        assertThat(currentTimeString, notNullValue());

        String findText = DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm");
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
        if (DailyTextUtils.isTextEmpty(mAuthorization) == false)
        {
            return;
        }

        mLock = new CountDownLatch(1);

        HashMap<String, String> params = new HashMap<>();
        params.put("email", Crypto.getUrlDecoderEx(TEST_EMAIL));
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

                    mAuthorization = String.format(Locale.KOREA, "%s %s", tokenType, accessToken);

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
        if (mTodayDateTime != null)
        {
            return;
        }

        mLock = new CountDownLatch(1);

        retrofit2.Callback networkCallback = new retrofit2.Callback<BaseDto<TodayDateTime>>()
        {
            @Override
            public void onResponse(Call<BaseDto<TodayDateTime>> call, Response<BaseDto<TodayDateTime>> response)
            {
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(BaseDto.class)));

                    BaseDto<TodayDateTime> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        mTodayDateTime = baseDto.data;
                    } else
                    {
                        assertThat(baseDto.msg, isNotEmpty());
                        assertThat(baseDto.msg, baseDto.msgCode, is(100));
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
            public void onFailure(Call<BaseDto<TodayDateTime>> call, Throwable t)
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

                        switch (Setting.getStore())
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

                        assertThat(Setting.getStore().getName(), minVersionName, notNullValue());
                        assertThat(Setting.getStore().getName(), maxVersionName, notNullValue());
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
        //        mLock = new CountDownLatch(1);
        //
        //        retrofit2.Callback networkCallback = new retrofit2.Callback<JSONObject>()
        //        {
        //            @Override
        //            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        //            {
        //                try
        //                {
        //                    assertThat(response, notNullValue());
        //                    assertThat(response.isSuccessful(), is(true));
        //                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));
        //
        //                    JSONObject responseJSONObject = response.body();
        //
        //                    String result = responseJSONObject.getString("isSuccess");
        //
        //                    assertThat(responseJSONObject.getString("msg"), allOf(notNullValue(), isA(String.class)));
        //
        //                    if ("false".equalsIgnoreCase(result) == true)
        //                    {
        //                        assertThat(true, anyOf(is(response.code() == 200), is(responseJSONObject.getString("msg").contains("이미 비밀번호 변경을 요청하였습니다."))));
        //                    } else
        //                    {
        //                        assertThat(result, is("true"));
        //                    }
        //
        //                } catch (Throwable t)
        //                {
        //                    addException(call, response, t);
        //                } finally
        //                {
        //                    mLock.countDown();
        //                }
        //            }
        //
        //            @Override
        //            public void onFailure(Call<JSONObject> call, Throwable t)
        //            {
        //                addException(call, null, t);
        //                mLock.countDown();
        //            }
        //        };
        //
        //        DailyMobileAPI.getInstance(mContext).requestUserCheckEmail(mNetworkTag, Crypto.getUrlDecoderEx(TEST_EMAIL), networkCallback);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserChangePassword() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback networkCallback = new retrofit2.Callback<BaseDto<Object>>()
        {
            @Override
            public void onResponse(Call<BaseDto<Object>> call, Response<BaseDto<Object>> response)
            {
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(BaseDto.class)));

                    BaseDto baseDto = response.body();

                    assertThat(baseDto.msg, allOf(notNullValue(), isA(String.class)));
                    assertThat(baseDto.msgCode, is(100));
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }
            }

            @Override
            public void onFailure(Call<BaseDto<Object>> call, Throwable t)
            {
                addException(call, null, t);
                mLock.countDown();
            }
        };

        DailyMobileAPI.getInstance(mContext).requestUserChangePassword(mNetworkTag, Crypto.getUrlDecoderEx(TEST_EMAIL), networkCallback);
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
                    assertThat(msgCode, is(0));

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
                    }

                    // requestUserDeleteBillingCard() 용 테스트 코드
                    if (creditCardArrayList.size() > 0)
                    {
                        String checkNum = Crypto.getUrlDecoderEx(Const.TEST_SKIP_DELETE_CREDITCARD_NUMBER);

                        boolean isNeedCheck = DailyTextUtils.isTextEmpty(checkNum) == false ? true : false;
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

    @Test
    public void requestStayList() throws Exception
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
                    assertThat(msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    assertThat(dataJSONObject.get("hotelSalesCount"), allOf(notNullValue(), instanceOf(Integer.class)));

                    if (Const.TEST_IS_SHOW_LIST_DETAIL == true)
                    {
                        assertThat(dataJSONObject.get("hotelSales"), allOf(notNullValue(), instanceOf(JSONArray.class)));
                    } else
                    {
                        assertThat(dataJSONObject.get("hotelSales"), notNullValue());
                    }

                    JSONArray hotelJSONArray = null;

                    if (dataJSONObject.has("hotelSales") == true)
                    {
                        hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
                    }

                    if (hotelJSONArray != null)
                    {
                        String imageUrl = dataJSONObject.getString("imgUrl");
                        assertThat(imageUrl, isNotEmpty());

                        int nights = dataJSONObject.getInt("stays");
                        assertThat(nights, is(TEST_NIGHTS));

                        JSONObject jsonObject;

                        int length = hotelJSONArray.length();
                        for (int i = 0; i < length; i++)
                        {
                            jsonObject = hotelJSONArray.getJSONObject(i);
                            assertThat(jsonObject, notNullValue());

                            String name = jsonObject.getString("name");
                            int price = jsonObject.getInt("price");
                            int discountPrice = jsonObject.getInt("discount"); // discountAvg ????
                            String addressSummary = jsonObject.getString("addrSummary");

                            assertThat(name, isNotEmpty());
                            assertThat(price, moreThan(0));
                            assertThat(discountPrice, moreThan(0));
                            assertThat(addressSummary, isNotEmpty());

                            assertThat(jsonObject.getString("grade"), isNotEmpty());

                            assertThat(jsonObject.getInt("hotelIdx"), allOf(notNullValue(), not(0)));

                            if (jsonObject.has("isSoldOut") == true)
                            {
                                assertThat(jsonObject.get("isSoldOut"), allOf(notNullValue(), instanceOf(Boolean.class)));
                            }

                            assertThat(jsonObject.getString("districtName"), isNotEmpty());
                            assertThat(jsonObject.getString("category"), isNotEmpty());
                            assertThat(jsonObject.getDouble("latitude"), notNullValue());
                            assertThat(jsonObject.getDouble("longitude"), notNullValue());
                            assertThat(jsonObject.get("isDailyChoice"), allOf(notNullValue(), instanceOf(Boolean.class)));
                            assertThat(jsonObject.getInt("rating"), moreThan(0)); // ratingValue ??
                            assertThat(jsonObject.getString("sday"), isNotEmpty());
                            assertThat(jsonObject.getDouble("distance"), moreThan(0d));

                            JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
                            assertThat(imageJSONObject, notNullValue());

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
                                    ExLog.d(e.getMessage());
                                    // 무시
                                }
                            }

                            assertThat(stayImageUrl, isNotEmpty());

                            if (jsonObject.has("benefit") == true) // hotelBenefit ?
                            {
                                assertThat(jsonObject.getString("benefit"), notNullValue());
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

        HashMap<String, Object> paramMap = new HashMap<>();

        paramMap.put("dateCheckIn", DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"));
        paramMap.put("stays", TEST_NIGHTS);
        paramMap.put("provinceIdx", Const.TEST_STAY_PROVINCE_INDEX);
        // area skip
        //  queryMap.put("areaIdx", areaIdx);

        paramMap.put("persons", Const.TEST_STAY_PERSONS);
        if (Category.ALL.code.equalsIgnoreCase(Const.TEST_STAY_CATEGORY_CODE) == false)
        {
            paramMap.put("category", Const.TEST_STAY_CATEGORY_CODE);
        }

        //  if(mBedTypeList != null && mBedTypeList.size() > 0)
        //  {
        //hashMap.put("bedType", mBedTypeList);
        //  }
        //
        //  if(mLuxuryList != null && mLuxuryList.size() > 0)
        //  {
        //hashMap.put("luxury", mLuxuryList);
        //  }

        if (Const.TEST_PAGE_INDEX > 0)
        {
            paramMap.put("page", Const.TEST_PAGE_INDEX);
            paramMap.put("limit", Const.TEST_LIMIT_LIST_COUNT);
        }

        // sort skip
        //  if (Constants.SortType.DEFAULT != mSort)
        //  {
        //if (com.daily.base.util.TextUtils.isTextEmpty(sortProperty) == false)
        //{
        // queryMap.put("sortProperty", sortProperty);
        //}
        //
        //if (com.daily.base.util.TextUtils.isTextEmpty(sortDirection) == false)
        //{
        // queryMap.put("sortDirection", sortDirection);
        //}
        //
        //if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
        //{
        // queryMap.put("latitude", latitude);
        // queryMap.put("longitude", longitude);
        //}
        //  }

        paramMap.put("details", Const.TEST_IS_SHOW_LIST_DETAIL);

        DailyMobileAPI.getInstance(mContext).requestStayList(mNetworkTag, paramMap, null, null, null, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

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
    //                try
    //                {
    //                    assertThat(response, notNullValue());
    //                    assertThat(response.isSuccessful(), is(true));
    //                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));
    //
    //                    JSONObject responseJSONObject = response.body();
    //
    //                    int msgCode = responseJSONObject.getInt("msgCode");
    //                    assertThat(msgCode, is(100));
    //
    //                    JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //
    //                    int length = dataJSONArray.length();
    //                    for (int i = 0; i < length; i++)
    //                    {
    //                        Keyword keyword = new Keyword(dataJSONArray.getJSONObject(i), PlaceSearchLayout.HOTEL_ICON);
    //                        assertThat(keyword, notNullValue());
    //                        assertThat(keyword.name, isNotEmpty());
    //                    }
    //                } catch (Throwable t)
    //                {
    //                    addException(call, response, t);
    //                } finally
    //                {
    //                    mLock.countDown();
    //                }
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                addException(call, null, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestStaySearchAutoCompleteList(mNetworkTag//
    //            , DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd")//
    //            , TEST_NIGHTS, Const.TEST_STAY_AUTO_SEARCH_TEXT, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }

    @Test
    public void requestStayRegionList() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback mRegionListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    JSONArray provinceArray = dataJSONObject.getJSONArray("regionProvince");
                    assertThat(provinceArray, notNullValue());

                    ArrayList<Province> provinceList = makeProvinceList(provinceArray);
                    assertThat(provinceList, notNullValue());
                    assertThat(provinceList.size(), moreThan(1));

                    JSONArray areaJSONArray = dataJSONObject.getJSONArray("regionArea");
                    assertThat(areaJSONArray, notNullValue());

                    ArrayList<Area> areaList = makeAreaList(areaJSONArray);
                    assertThat(areaList, notNullValue());
                    assertThat(areaList.size(), moreThan(1));
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

            private ArrayList<Province> makeProvinceList(JSONArray jsonArray) throws Throwable
            {
                ArrayList<Province> provinceList = new ArrayList<>();

                int length = jsonArray.length();
                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Province province = new Province(jsonObject, null);
                    provinceList.add(province);
                }

                return provinceList;
            }

            private ArrayList<Area> makeAreaList(JSONArray jsonArray) throws Throwable
            {
                ArrayList<Area> areaList = new ArrayList<>();

                int length = jsonArray.length();
                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Area area = new Area(jsonObject);
                    areaList.add(area);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());

                    // 0	성공
                    // 4	데이터가 없을시
                    // 5	판매 마감시
                    // 6	현재 시간부터 날짜 바뀌기 전시간(새벽 3시
                    // 7 3시부터 9시까지
                    assertThat(message, msgCode, anyOf(is(0), is(4), is(5), is(6), is(7)));
                    assertThat(message, msgCode, is(0));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    long checkInDate = dataJSONObject.getLong("check_in_date");
                    long checkOutDate = dataJSONObject.getLong("check_out_date");
                    int discount = dataJSONObject.getInt("discount_total");
                    int availableRooms = dataJSONObject.getInt("available_rooms");

                    assertThat(checkInDate, moreThan(1l));
                    assertThat(checkOutDate, moreThan(1l));
                    assertThat(discount, moreThan(0));
                    assertThat(availableRooms, moreThan(0));

                    assertThat(dataJSONObject.get("on_sale"), allOf(notNullValue(), instanceOf(Boolean.class)));
                    assertThat(dataJSONObject.get("refund_type"), allOf(notNullValue(), instanceOf(String.class)));
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

        DailyMobileAPI.getInstance(mContext).requestStayPaymentInformation(mNetworkTag//
            , Const.TEST_STAY_SALE_ROOM_INDEX//
            , DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd")//
            , TEST_NIGHTS, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayDetailInformation() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback networkCallback = new retrofit2.Callback<BaseDto<StayDetailParams>>()
        {
            @Override
            public void onResponse(Call<BaseDto<StayDetailParams>> call, Response<BaseDto<StayDetailParams>> response)
            {
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(BaseDto.class)));

                    BaseDto<StayDetailParams> baseDto = response.body();

                    int msgCode = baseDto.msgCode;
                    String message = baseDto.msg;
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, anyOf(is(100), is(5)));

                    assertThat(baseDto.data, allOf(notNullValue(), instanceOf(StayDetailParams.class)));

                    // 100	성공
                    // 4	데이터가 없을시
                    // 5	판매 마감시
                    StayDetailParams stayDetailParams = baseDto.data;
                    checkStayDetail(stayDetailParams);
                } catch (Throwable t)
                {
                    addException(call, response, t);
                } finally
                {
                    mLock.countDown();
                }

            }

            @Override
            public void onFailure(Call<BaseDto<StayDetailParams>> call, Throwable t)
            {
                addException(call, null, t);
                mLock.countDown();
            }

            private void checkStayDetail(StayDetailParams stayDetailParams) throws Throwable
            {
                Stay.Grade grade = stayDetailParams.getGrade();
                assertThat(grade, notNullValue());

                assertThat(stayDetailParams.name, isNotEmpty());
                assertThat(stayDetailParams.address, isNotEmpty());

                assertThat(stayDetailParams.longitude, allOf(notNullValue(), not(0d)));
                assertThat(stayDetailParams.isOverseas, allOf(notNullValue(), instanceOf(Boolean.class)));

                boolean ratingShow = stayDetailParams.ratingShow;
                if (ratingShow == true)
                {
                    assertThat(stayDetailParams.ratingValue, allOf(notNullValue(), moreThan(0)));
                    assertThat(stayDetailParams.ratingPersons, allOf(notNullValue(), moreThan(Const.TEST_MIN_RATING_PERSONS)));
                }

                // Pictrogram
                // 주차
                assertThat(stayDetailParams.parking, allOf(notNullValue(), instanceOf(Boolean.class)));
                // 주차금지
                assertThat(stayDetailParams.noParking, allOf(notNullValue(), instanceOf(Boolean.class)));
                // 수영장
                assertThat(stayDetailParams.pool, allOf(notNullValue(), instanceOf(Boolean.class)));
                // 피트니스
                assertThat(stayDetailParams.fitness, allOf(notNullValue(), instanceOf(Boolean.class)));
                // 애완동물
                assertThat(stayDetailParams.pet, allOf(notNullValue(), instanceOf(Boolean.class)));
                // 바베큐
                assertThat(stayDetailParams.sharedBBQ, allOf(notNullValue(), instanceOf(Boolean.class)));

                // Image Url
                String imageUrl = stayDetailParams.imgUrl;
                assertThat(imageUrl, isNotEmpty());

                List<ImageInformation> imageInformationList = stayDetailParams.getImageList();
                assertThat(imageInformationList, notNullValue());

                for (ImageInformation imageInformation : imageInformationList)
                {
                    assertThat(imageInformation.description, notNullValue());
                    assertThat(imageInformation.getImageUrl(), isNotEmpty());
                }

                // benefit
                if (DailyTextUtils.isTextEmpty(stayDetailParams.benefit) == false)
                {
                    assertThat(stayDetailParams.benefit, isNotEmpty());
                    assertThat(stayDetailParams.getBenefitList(), allOf(notNullValue(), instanceOf(List.class)));

                    for (String text : stayDetailParams.getBenefitList())
                    {
                        assertThat(text, isNotEmpty());
                    }

                    if (DailyTextUtils.isTextEmpty(stayDetailParams.benefitWarning) == false)
                    {
                        assertThat(stayDetailParams.benefitWarning, isNotEmpty());
                    }
                }

                // Detail
                List<DetailInformation> detailInformationList = stayDetailParams.getDetailList();
                assertThat(detailInformationList, notNullValue());

                for (DetailInformation detailInformation : detailInformationList)
                {
                    assertThat(detailInformation, notNullValue());

                    assertThat(detailInformation.title, isNotEmpty());

                    List<String> contentList = detailInformation.getContentsList();
                    assertThat(contentList, notNullValue());

                    for (String content : contentList)
                    {
                        assertThat(content, isNotEmpty());
                    }
                }

                // Room Sale Info
                List<StayProduct> stayProductList = stayDetailParams.getProductList();
                if (stayProductList != null)
                {
                    for (StayProduct stayProduct : stayProductList)
                    {
                        assertThat(stayProduct, notNullValue());

                        assertThat(stayProduct.roomIndex, moreThan(0));
                        assertThat(stayProduct.averageDiscount, moreThan(0));
                        assertThat(stayProduct.totalDiscount, moreThan(0));
                        assertThat(stayProduct.price, moreThan(0));
                        assertThat(stayProduct.roomName, isNotEmpty());
                        assertThat(stayProduct.option, isNotEmpty());
                        assertThat(stayProduct.amenities, isNotEmpty());

                        if (stayProduct.roomBenefit != null)
                        {
                            assertThat(stayProduct.roomBenefit, isNotEmpty());
                        }

                        if (stayProduct.refundType != null)
                        {
                            assertThat(stayProduct.refundType, isNotEmpty());
                        }
                    }
                }

                assertThat(stayDetailParams.myWish, allOf(notNullValue(), instanceOf(Boolean.class)));
                assertThat(stayDetailParams.wishCount, allOf(notNullValue(), instanceOf(Boolean.class)));
            }
        };

        DailyMobileAPI.getInstance(mContext).requestStayDetailInformation(mNetworkTag, Const.TEST_STAY_INDEX, //
            DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"), TEST_NIGHTS, networkCallback);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    // 해당 화면은 메시지를 넣지 않는다.
                    int msgCode = responseJSONObject.getInt("msgCode");
                    String message = responseJSONObject.getString("msg");
                    switch (msgCode)
                    {
                        case 1000:
                            assertThat("msgCode : " + msgCode + ", PayComplete", true);
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
                            assertThat("msgCode : " + msgCode, true);
                            break;

                        default:
                            assertThat("msgCode : " + msgCode + ", message : " + message, false);
                            break;
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
        params.put("room_idx", String.valueOf(Const.TEST_STAY_SALE_ROOM_INDEX));
        params.put("checkin_date", DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));
        params.put("nights", String.valueOf(TEST_NIGHTS));
        params.put("billkey", Const.TEST_EASY_CARD_BILLINGKEY);

        // 쿠폰 및 적립금 패스
        //  switch (paymentInformation.discountType)
        //  {
        //case BONUS:
        // String bonus = String.valueOf(paymentInformation.bonus);
        // params.put("bonus", bonus);
        // break;
        //
        //case COUPON:
        // Coupon coupon = paymentInformation.getCoupon();
        // params.put("user_coupon_code", coupon.userCouponCode);
        // break;
        //  }

        params.put("guest_name", Const.TEST_USER_NAME);
        params.put("guest_phone", Const.TEST_USER_PHONE);
        params.put("guest_email", TEST_EMAIL);
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");
                    String message = responseJSONObject.getString("message");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(0));

                    JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                    assertThat(dataJSONArray, notNullValue());

                    int length = dataJSONArray.length();
                    if (length == 0)
                    {
                        assertThat("reservation list is empty", true);
                    } else
                    {
                        ArrayList<Booking> bookingList = makeBookingList(dataJSONArray);
                        assertThat(bookingList, notNullValue());

                        if (bookingList != null && bookingList.isEmpty() == false)
                        {
                            Booking booking = bookingList.get(0);
                            if (Constants.PlaceType.FNB.equals(booking.placeType) == true)
                            {
                                requestGourmetAccountInformation(booking);
                            } else if (Constants.PlaceType.HOTEL.equals(booking.placeType) == true)
                            {
                                requestDepositWaitDetailInformation(booking);
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
                    assertThat(jsonObject, notNullValue());

                    Booking booking = new Booking(jsonObject);
                    assertThat(booking, notNullValue());

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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    GourmetBookingDetail gourmetBookingDetail = new GourmetBookingDetail();
                    gourmetBookingDetail.setData(dataJSONObject);
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

        DailyMobileAPI.getInstance(mContext).requestGourmetReservationDetail(//
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJsonObject = responseJSONObject.getJSONObject("data");

                    // 영수증
                    String reservationIdx = dataJsonObject.getString("gourmetReservationIdx");
                    assertThat(reservationIdx, isNotEmpty());

                    String userName = dataJsonObject.getString("userName");
                    String userPhone = dataJsonObject.getString("userPhone");
                    int ticketCount = dataJsonObject.getInt("ticketCount");
                    String placeName = dataJsonObject.getString("restaurantName");
                    String placeAddress = dataJsonObject.getString("restaurantAddress");
                    String sday = dataJsonObject.getString("sday");
                    String valueDate = dataJsonObject.getString("paidAt");
                    //  String currency = receiptJSONObject.getString("currency");
                    int paymentAmount = dataJsonObject.getInt("paymentAmount");
                    int tax = dataJsonObject.getInt("tax");
                    int supplyPrice = dataJsonObject.getInt("supplyPrice");
                    int sellingPrice = dataJsonObject.getInt("sellingPrice");
                    String paymentType = dataJsonObject.getString("paymentType");
                    int counpon = dataJsonObject.getInt("couponAmount");

                    // **공급자**
                    String receiptNotice = dataJsonObject.getString("receiptNotice");

                    assertThat(userName, isNotEmpty());
                    assertThat(userPhone, isNotEmpty());
                    assertThat(ticketCount, moreThan(1));
                    assertThat(placeName, isNotEmpty());
                    assertThat(placeAddress, isNotEmpty());
                    assertThat(sday, isNotEmpty());
                    assertThat(valueDate, isNotEmpty());
                    assertThat(paymentAmount, moreThan(0));
                    assertThat(tax, moreThan(0));
                    assertThat(supplyPrice, moreThan(0));
                    assertThat(sellingPrice, moreThan(0));
                    assertThat(paymentType, isNotEmpty());
                    assertThat(counpon, moreThan(0));
                    assertThat(receiptNotice, isNotEmpty());
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
                try
                {
                    assertThat(response, notNullValue());
                    assertThat(response.isSuccessful(), is(true));
                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    JSONObject responseJSONObject = response.body();

                    // 해당 화면은 메시지를 넣지 않는다.
                    int msgCode = responseJSONObject.getInt("msg_code");
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, anyOf(is(0), is(100), is(200)));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

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

                    assertThat("result", result);
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
            try
            {
                assertThat("booking is null", false);
            } catch (Throwable t)
            {
                addException(null, null, t);
            }
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

                    int msgCode = responseJSONObject.getInt("msg_code");
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(0));

                    JSONObject jsonObject = responseJSONObject.getJSONObject("data");
                    assertThat(jsonObject, notNullValue());

                    String accountNumber = jsonObject.getString("account_num");
                    String bankName = jsonObject.getString("bank_name");
                    String accountName = jsonObject.getString("name");
                    String date = jsonObject.getString("date");
                    String time = jsonObject.getString("time");
                    String msg1 = jsonObject.getString("msg1");

                    int couponAmount = jsonObject.getInt("coupon_amount");
                    int price = jsonObject.getInt("price");
                    int paymetPrice = jsonObject.getInt("amt");

                    assertThat(accountNumber, isNotEmpty());
                    assertThat(bankName, isNotEmpty());
                    assertThat(accountName, isNotEmpty());
                    assertThat(date, isNotEmpty());
                    assertThat(time, isNotEmpty());
                    assertThat(msg1, isNotEmpty());

                    assertThat(couponAmount, moreThan(0));
                    assertThat(price, moreThan(0));
                    assertThat(paymetPrice, moreThan(0));
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

        DailyMobileAPI.getInstance(mContext).requestGourmetAccountInformation(mNetworkTag, booking.tid, networkCallback);

        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetRegionList() throws Exception
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    JSONArray provinceArray = dataJSONObject.getJSONArray("regionProvince");
                    assertThat(provinceArray, notNullValue());

                    ArrayList<Province> provinceList = makeProvinceList(provinceArray);
                    assertThat(provinceList, notNullValue());
                    assertThat(provinceList.size(), moreThan(1));

                    JSONArray areaJSONArray = dataJSONObject.getJSONArray("regionArea");
                    ArrayList<Area> areaList = makeAreaList(areaJSONArray);
                    assertThat(areaList, notNullValue());
                    assertThat(areaList.size(), moreThan(1));

                    String imageUrl = dataJSONObject.getString("imgUrl");
                    assertThat(imageUrl, isNotEmpty());
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

            private ArrayList<Area> makeAreaList(JSONArray jsonArray) throws Throwable
            {
                ArrayList<Area> areaList = new ArrayList<>();

                int length = jsonArray.length();
                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    assertThat(jsonObject, notNullValue());

                    try
                    {
                        Area area = new Area(jsonObject);
                        areaList.add(area);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.toString());
                    }
                }

                return areaList;
            }

            private ArrayList<Province> makeProvinceList(JSONArray jsonArray) throws Throwable
            {
                ArrayList<Province> provinceList = new ArrayList<>();

                int length = jsonArray.length();
                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    assertThat(jsonObject, notNullValue());

                    try
                    {
                        Province province = new Province(jsonObject, null);
                        provinceList.add(province);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.getMessage());
                    }
                }

                return provinceList;
            }
        };

        DailyMobileAPI.getInstance(mContext).requestGourmetRegionList(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetList() throws Exception
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    JSONArray gourmetJSONArray = null;

                    if (dataJSONObject.has("gourmetSales") == true)
                    {
                        gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSales");
                        assertThat(gourmetJSONArray, notNullValue());
                    }

                    int totalCount = dataJSONObject.getInt("gourmetSalesCount");
                    int maxCount = dataJSONObject.getInt("searchMaxCount");

                    assertThat(totalCount, moreThan(0));
                    assertThat(maxCount, moreThan(0));

                    String imageUrl;
                    if (gourmetJSONArray != null)
                    {
                        imageUrl = dataJSONObject.getString("imgUrl");
                        assertThat(imageUrl, isNotEmpty());

                        checkGourmetList(gourmetJSONArray);
                    }

                    JSONObject filterJSONObject = dataJSONObject.getJSONObject("filter");
                    assertThat(filterJSONObject, notNullValue());

                    JSONArray categoryJSONArray = filterJSONObject.getJSONArray("categories");
                    assertThat(categoryJSONArray, notNullValue());

                    // 필터 정보 넣기
                    int categoryCount = categoryJSONArray.length();
                    for (int i = 0; i < categoryCount; i++)
                    {
                        JSONObject categoryJSONObject = categoryJSONArray.getJSONObject(i);
                        assertThat(categoryJSONObject, notNullValue());

                        int categoryCode = categoryJSONObject.getInt("code");
                        int categorySeq = categoryJSONObject.getInt("sequence");
                        String categoryName = categoryJSONObject.getString("name");

                        assertThat(categoryCode, moreThan(0));
                        assertThat(categorySeq, moreThan(0));
                        assertThat(categoryName, isNotEmpty());
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

            private void checkGourmetList(JSONArray jsonArray) throws Throwable
            {
                if (jsonArray == null)
                {
                    return;
                }

                int length = jsonArray.length();
                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    assertThat(jsonObject, notNullValue());

                    int index = jsonObject.getInt("restaurantIdx");

                    String name = null;
                    if (jsonObject.has("restaurantName") == true)
                    {
                        name = jsonObject.getString("restaurantName");
                    } else if (jsonObject.has("name") == true)
                    {
                        name = jsonObject.getString("name");
                    }

                    assertThat(index, moreThan(1));
                    assertThat(name, isNotEmpty());

                    assertThat(jsonObject.getInt("price"), moreThan(0));
                    assertThat(jsonObject.getInt("discount"), moreThan(0));

                    assertThat(jsonObject.getString("addrSummary"), isNotEmpty());
                    assertThat(jsonObject.getString("districtName"), isNotEmpty());

                    assertThat(jsonObject.getDouble("latitude"), not(0d));
                    assertThat(jsonObject.getDouble("longitude"), not(0d));

                    assertThat(jsonObject.get("isDailychoice"), allOf(notNullValue(), instanceOf(Boolean.class)));
                    assertThat(jsonObject.get("isSoldOut"), allOf(notNullValue(), instanceOf(Boolean.class)));

                    assertThat(jsonObject.getInt("persons"), moreThan(1));

                    assertThat(jsonObject.getString("category"), isNotEmpty());

                    assertThat(jsonObject.getInt("categoryCode"), moreThan(0));
                    assertThat(jsonObject.getInt("categorySeq"), moreThan(0));

                    if (jsonObject.has("categorySub") == true)
                    {
                        assertThat(jsonObject.getString("categorySub"), isNotEmpty());
                    }

                    if (jsonObject.has("rating") == true)
                    {
                        assertThat(jsonObject.getInt("rating"), moreThan(0));
                    }

                    if (jsonObject.has("distance") == true)
                    {
                        assertThat(jsonObject.getDouble("distance"), moreThan(0d));
                    }

                    JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
                    assertThat(imageJSONObject, notNullValue());

                    Iterator<String> iterator = imageJSONObject.keys();
                    while (iterator.hasNext())
                    {
                        String key = iterator.next();
                        JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
                        assertThat(pathJSONArray, notNullValue());
                        assertThat(pathJSONArray.getString(0), isNotEmpty());
                        break;
                    }

                    if (jsonObject.has("benefit") == true)
                    {
                        assertThat(jsonObject.getString("benefit"), notNullValue());
                    }
                }
            }
        };

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("reserveDate", DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"));
        hashMap.put("provinceIdx", Const.TEST_GOURMET_PROVINCE_INDEX);
        // skip Area
        //  hashMap.put("areaIdx", areaIdx);
        hashMap.put("persons", Const.TEST_STAY_PERSONS);

        if (Const.TEST_PAGE_INDEX > 0)
        {
            hashMap.put("page", Const.TEST_PAGE_INDEX);
            hashMap.put("limit", Const.TEST_LIMIT_LIST_COUNT);
        }

        //skip sort
        //  if (Constants.SortType.DEFAULT != mSort)
        //  {
        //if (com.daily.base.util.TextUtils.isTextEmpty(sortProperty) == false)
        //{
        // hashMap.put("sortProperty", sortProperty);
        //}
        //
        //if (com.daily.base.util.TextUtils.isTextEmpty(sortDirection) == false)
        //{
        // hashMap.put("sortDirection", sortDirection);
        //}
        //
        //if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
        //{
        // hashMap.put("latitude", latitude);
        // hashMap.put("longitude", longitude);
        //}
        // }

        hashMap.put("details", Const.TEST_IS_SHOW_LIST_DETAIL);

        DailyMobileAPI.getInstance(mContext).requestGourmetList(mNetworkTag, hashMap, null, null, null, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

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
    //                try
    //                {
    //                    assertThat(response, notNullValue());
    //                    assertThat(response.isSuccessful(), is(true));
    //                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));
    //
    //                    JSONObject responseJSONObject = response.body();
    //
    //                    int msgCode = responseJSONObject.getInt("msgCode");
    //                    String message = responseJSONObject.getString("msg");
    //                    assertThat(message, isNotEmpty());
    //                    assertThat(message, msgCode, is(100));
    //
    //                    JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //                    assertThat(dataJSONArray, notNullValue());
    //
    //                    int length = dataJSONArray.length();
    //                    for (int i = 0; i < length; i++)
    //                    {
    //                        JSONObject keywordJSONObject = dataJSONArray.getJSONObject(i);
    //                        assertThat(keywordJSONObject, notNullValue());
    //                        assertThat(keywordJSONObject.getString("displayText"), isNotEmpty());
    //
    //                        if (keywordJSONObject.has("discount") == true)
    //                        {
    //                            assertThat(keywordJSONObject.getInt("discount"), moreThan(0));
    //                        }
    //                    }
    //                } catch (Throwable t)
    //                {
    //                    addException(call, response, t);
    //                } finally
    //                {
    //                    mLock.countDown();
    //                }
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                addException(call, null, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        DailyMobileAPI.getInstance(mContext).requestGourmetSearchAutoCompleteList(mNetworkTag//
    //            , DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"), Const.TEST_GOURMET_AUTO_SEARCH_TEXT, networkCallback);
    //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    //    }

    @Test
    public void requestGourmetDetailInformation() throws Exception
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    // 100	성공
                    // 4	데이터가 없을시
                    // 5	판매 마감시
                    assertThat(message, msgCode, anyOf(is(100), is(5)));

                    assertThat(responseJSONObject.get("data"), allOf(notNullValue(), instanceOf(JSONObject.class)));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    checkGourmetDetail(dataJSONObject);

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

            private void checkGourmetDetail(JSONObject jsonObject) throws Exception
            {
                assertThat(jsonObject.getString("category"), isNotEmpty());
                assertThat(jsonObject.getString("categorySub"), isNotEmpty());
                assertThat(jsonObject.getString("name"), isNotEmpty());
                assertThat(jsonObject.getString("address"), isNotEmpty());

                assertThat(jsonObject.getDouble("longitude"), allOf(notNullValue(), not(0d)));
                assertThat(jsonObject.getDouble("latitude"), allOf(notNullValue(), not(0d)));

                boolean ratingShow = jsonObject.getBoolean("ratingShow");
                if (ratingShow == true)
                {
                    assertThat(jsonObject.getInt("ratingValue"), moreThan(0));
                    assertThat(jsonObject.getInt("ratingPersons"), moreThan(0));
                }

                // 주차가능
                assertThat(jsonObject.get("parking"), allOf(notNullValue(), instanceOf(Boolean.class)));
                // 발렛가능
                assertThat(jsonObject.get("valet"), allOf(notNullValue(), instanceOf(Boolean.class)));
                // 프라이빗룸
                assertThat(jsonObject.get("privateRoom"), allOf(notNullValue(), instanceOf(Boolean.class)));
                // 단체예약
                assertThat(jsonObject.get("groupBooking"), allOf(notNullValue(), instanceOf(Boolean.class)));
                // 베이비시트
                assertThat(jsonObject.get("babySeat"), allOf(notNullValue(), instanceOf(Boolean.class)));
                // 코르키지
                assertThat(jsonObject.get("corkage"), allOf(notNullValue(), instanceOf(Boolean.class)));

                // Image Url
                String imageUrl = jsonObject.getString("imgUrl");
                assertThat(imageUrl, isNotEmpty());

                JSONObject pathUrlJSONObject = jsonObject.getJSONObject("imgPathMain");
                assertThat(pathUrlJSONObject, notNullValue());

                Iterator<String> iterator = pathUrlJSONObject.keys();
                while (iterator.hasNext())
                {
                    String key = iterator.next();

                    JSONArray pathJSONArray = pathUrlJSONObject.getJSONArray(key);
                    assertThat(pathUrlJSONObject, notNullValue());

                    int length = pathJSONArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        JSONObject imageInformationJSONObject = pathJSONArray.getJSONObject(i);
                        assertThat(imageInformationJSONObject, notNullValue());

                        assertThat(imageInformationJSONObject.getString("description"), isNotEmpty());
                        assertThat(imageInformationJSONObject.getString("name"), isNotEmpty());
                    }
                    break;
                }

                //benefit
                if (jsonObject.has("benefit") == true)
                {
                    String benefit = jsonObject.getString("benefit");
                    assertThat(benefit, isNotEmpty());
                    assertThat(jsonObject.get("benefitContents"), allOf(notNullValue(), instanceOf(JSONArray.class)));

                    JSONArray benefitJSONArray = jsonObject.getJSONArray("benefitContents");

                    int length = benefitJSONArray.length();
                    if (length > 0)
                    {
                        for (int i = 0; i < length; i++)
                        {
                            assertThat(benefitJSONArray.getString(i), isNotEmpty());
                        }
                    }
                }

                // Detail
                JSONArray detailJSONArray = jsonObject.getJSONArray("details");

                int detailLength = detailJSONArray.length();
                for (int i = 0; i < detailLength; i++)
                {
                    JSONObject detailJSONObject = detailJSONArray.getJSONObject(i);
                    assertThat(detailJSONObject, notNullValue());

                    Iterator<String> detailIterator = jsonObject.keys();
                    if (detailIterator.hasNext() == true)
                    {
                        String title = detailIterator.next();
                        JSONArray jsonArray = jsonObject.getJSONArray(title);
                        assertThat(jsonArray, notNullValue());

                        int length = jsonArray.length();
                        for (int j = 0; j < length; j++)
                        {
                            assertThat(jsonArray.getString(i), isNotEmpty());
                        }
                    }
                }

                // Ticket Information
                if (jsonObject.has("tickets") == true && jsonObject.isNull("tickets") == false)
                {
                    JSONArray ticketInformationJSONArray = jsonObject.getJSONArray("tickets");
                    assertThat(ticketInformationJSONArray, notNullValue());

                    int ticketInformationLength = ticketInformationJSONArray.length();
                    for (int i = 0; i < ticketInformationLength; i++)
                    {
                        JSONObject ticketInfromateionJSONObject = ticketInformationJSONArray.getJSONObject(i);
                        assertThat(ticketInfromateionJSONObject.getInt("saleIdx"), moreThan(1));
                        assertThat(ticketInfromateionJSONObject.getString("ticketName"), isNotEmpty());
                        assertThat(ticketInfromateionJSONObject.getString("option"), isNotEmpty());
                        assertThat(ticketInfromateionJSONObject.getString("benefit"), isNotEmpty());
                        assertThat(ticketInfromateionJSONObject.getInt("price"), moreThan(0));
                        assertThat(ticketInfromateionJSONObject.getInt("discount"), moreThan(0));
                    }
                }

                assertThat(jsonObject.get("myWish"), allOf(notNullValue(), instanceOf(Boolean.class)));
                assertThat(jsonObject.get("wishCount"), allOf(notNullValue(), instanceOf(Boolean.class)));
            }
        };

        DailyMobileAPI.getInstance(mContext).requestGourmetDetailInformation(//
            mNetworkTag, Const.TEST_GOURMET_INDEX, DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"), networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetPaymentInformation() throws Exception
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
                    assertThat(message, msgCode, is(0));

                    JSONObject jsonObject = responseJSONObject.getJSONObject("data");
                    assertThat(jsonObject, notNullValue());

                    int discountPrice = jsonObject.getInt("discount");
                    long sday = jsonObject.getLong("sday");
                    int maxCount = jsonObject.getInt("max_sale_count");
                    assertThat(discountPrice, moreThan(0));
                    assertThat(sday, moreThan(0L));
                    assertThat(maxCount, moreThan(0));

                    JSONArray timeJSONArray = jsonObject.getJSONArray("eating_time_list");
                    assertThat(timeJSONArray, notNullValue());

                    int length = timeJSONArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        assertThat(timeJSONArray.getLong(i), moreThan(1L));
                    }

                    if (length > 0)
                    {
                        requestGourmetCheckTicket(Const.TEST_GOURMET_TIKET_INDEX, timeJSONArray.getLong(0));
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

        DailyMobileAPI.getInstance(mContext).requestGourmetPaymentInformation(//
            mNetworkTag, Const.TEST_GOURMET_TIKET_INDEX, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 해당 티켓은 requestGourmetPaymentInformation 에서 처리 해야 함. GourmetPaymentInformation의 정보가 필요함.
    @Ignore
    public void requestGourmetCheckTicket(int ticketIndex, long ticketTime) throws Exception
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
                    assertThat(msgCode, is(0));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    assertThat(dataJSONObject.get("on_sale"), allOf(notNullValue(), instanceOf(Boolean.class)));
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

        DailyMobileAPI.getInstance(mContext).requestGourmetCheckTicket(mNetworkTag//
            , ticketIndex//
            , DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyMMdd")//
            , Const.TEST_GOURMET_TICKET_COUNT//
            , Long.toString(ticketTime), networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetPayment() throws Exception
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

                    if (msgCode == 0)
                    {
                        // do nothing!
                    } else
                    {
                        if (responseJSONObject.has("msg") == false)
                        {
                            assertThat("message is Null", msgCode, is(0));
                        } else
                        {
                            String msg = responseJSONObject.getString("msg");
                            assertThat(msg, isNotEmpty());

                            String[] result = msg.split("\\^");
                            if ("SUCCESS".equalsIgnoreCase(result[0]) == true)
                            {
                                // do nothing!
                            } else if ("FAIL".equalsIgnoreCase(result[0]) == true)
                            {
                                // do nothing!
                            } else
                            {
                                assertThat(result.toString(), false);
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

        Map<String, String> params = new HashMap<>();
        params.put("sale_reco_idx", Integer.toString(Const.TEST_GOURMET_TIKET_INDEX));
        params.put("billkey", Const.TEST_EASY_CARD_BILLINGKEY);
        params.put("ticket_count", Integer.toString(Const.TEST_GOURMET_TICKET_COUNT));

        // coupon skip
        //        params.put("user_coupon_code", coupon.userCouponCode);

        params.put("customer_name", Const.TEST_USER_NAME);
        params.put("customer_phone", Const.TEST_USER_PHONE);
        params.put("customer_email", TEST_EMAIL);
        params.put("arrival_time", String.valueOf(getGourmetReservationTime()));
        params.put("customer_msg", "");

        DailyMobileAPI.getInstance(mContext).requestGourmetPayment(mNetworkTag, params, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 호텔 예약 대기 - Booking tid 는 항시 변하는 항목을 requestBookingList에서 테스트
    @Ignore
    public void requestDepositWaitDetailInformation(Booking booking) throws Exception
    {
        if (booking == null)
        {
            try
            {
                assertThat("booking is null", false);
            } catch (Throwable t)
            {
                addException(null, null, t);
            }
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    JSONObject reservationJSONObject = dataJSONObject.getJSONObject("reservation");
                    assertThat(reservationJSONObject, notNullValue());

                    String accountNumber = reservationJSONObject.getString("vactNum");
                    assertThat(accountNumber, isNotEmpty());

                    assertThat(reservationJSONObject.getString("bankName"), isNotEmpty());
                    assertThat(reservationJSONObject.getString("vactName"), isNotEmpty());

                    // 입금기한
                    String validToDate = DailyCalendar.convertDateFormatString(//
                        reservationJSONObject.getString("validTo"),//
                        DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일 HH시 mm분 까지");
                    assertThat(validToDate, isNotEmpty());

                    // 결재 금액 정보
                    assertThat(reservationJSONObject.getInt("price"), moreThan(0));
                    assertThat(reservationJSONObject.getInt("bonus"), moreThan(0));
                    assertThat(reservationJSONObject.getInt("couponAmount"), moreThan(0));
                    assertThat(reservationJSONObject.getInt("amt"), moreThan(0));

                    // 확인 사항
                    assertThat(dataJSONObject.getString("msg1"), isNotEmpty());
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

        DailyMobileAPI.getInstance(mContext).requestDepositWaitDetailInformation(//
            mNetworkTag, booking.tid, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Ignore
    public void requestStayBookingDetailInformation() throws Exception
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                    StayBookingDetail stayBookingDetail = new StayBookingDetail();
                    stayBookingDetail.setData(jsonObject);
                    assertThat(stayBookingDetail, notNullValue());
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

        DailyMobileAPI.getInstance(mContext).requestStayReservationDetail( //
            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 원래는 테스트 해야 하지만.. 예약 내역을 히든 할 경우 Test reservation index를 바꿔줘야 함으로 skip 함.
    @Ignore
    public void requestStayHiddenBooking() throws Exception
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
                    assertThat(message, msgCode, anyOf(is(100), is(0), is(200)));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

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

                    assertThat(result, is(true));
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

        DailyMobileAPI.getInstance(mContext).requestStayHiddenBooking(//
            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayReceipt() throws Exception
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
                    assertThat(message, msgCode, is(0));

                    //			msg_code : 0
                    //			data :
                    //			- [String] user_name /* 유저 이름 */
                    //			- [String] user_phone /* 유저 번호 */
                    //			- [String] checkin /* 체크인 날짜(yyyy/mm/dd) */
                    //			- [String] checkout /* 체크아웃 날짜(yyyy/mm/dd) */
                    //			- [int] nights /* 연박 일수 */
                    //			- [int] rooms /* 객실수 */
                    //			- [String] hotel_name /* 호텔 명 */
                    //			- [String] hotel_address /* 호텔 주소 */
                    //			- [String] value_date(yyyy/mm/dd) /* 결제일 */
                    //			- [String] currency /* 화폐 단위 */
                    //			- [int] discount /* 결제 금액 */
                    //			- [int] vat /* 부가세 */
                    //			- [int] supply_value /* 공급가액 */
                    //			- [String] payment_name /* 결제수단 */
                    //			---------------------------------
                    JSONObject dataJsonObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJsonObject, notNullValue());

                    JSONObject receiptJSONObject = dataJsonObject.getJSONObject("receipt");
                    assertThat(receiptJSONObject, notNullValue());

                    assertThat(dataJsonObject.getString("reservation_idx"), isNotEmpty());
                    assertThat(receiptJSONObject.getString("user_name"), isNotEmpty());
                    assertThat(receiptJSONObject.getString("user_phone"), isNotEmpty());
                    assertThat(receiptJSONObject.getString("checkin"), isNotEmpty());
                    assertThat(receiptJSONObject.getString("checkout"), isNotEmpty());
                    assertThat(receiptJSONObject.getInt("nights"), moreThan(1));
                    assertThat(receiptJSONObject.getInt("rooms"), moreThan(1));
                    assertThat(receiptJSONObject.getString("hotel_name"), isNotEmpty());
                    assertThat(receiptJSONObject.getString("hotel_address"), isNotEmpty());
                    assertThat(receiptJSONObject.getString("value_date"), isNotEmpty());
                    assertThat(receiptJSONObject.getInt("discount"), moreThan(0));
                    assertThat(receiptJSONObject.getInt("vat"), moreThan(0));
                    assertThat(receiptJSONObject.getInt("supply_value"), moreThan(0));
                    assertThat(receiptJSONObject.getString("payment_name"), isNotEmpty());

                    assertThat(receiptJSONObject.getInt("bonus"), moreThan(0));
                    assertThat(receiptJSONObject.getInt("coupon_amount"), moreThan(0));
                    assertThat(receiptJSONObject.getInt("price"), moreThan(0));

                    // **공급자**
                    JSONObject provider = dataJsonObject.getJSONObject("provider");
                    assertThat(provider, notNullValue());

                    String memo = provider.getString("memo");
                    assertThat(memo, notNullValue());
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

        DailyMobileAPI.getInstance(mContext).requestStayReceipt(//
            mNetworkTag, Integer.toString(Const.TEST_STAY_RESERVATION_INDEX), networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestEventList() throws Exception
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
                    assertThat(message, msgCode, is(0));

                    JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                    assertThat(dataJSONArray, notNullValue());

                    int eventIndex = -1;

                    int length = dataJSONArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject = dataJSONArray.getJSONObject(i);

                        eventIndex = jsonObject.getInt("idx");
                        assertThat(eventIndex, moreThan(0));
                        assertThat(jsonObject.getString("img_url"), isNotEmpty());
                        assertThat(jsonObject.getInt("is_event_join"), not(0));
                        assertThat(jsonObject.getString("name"), isNotEmpty());
                    }

                    if (eventIndex != -1)
                    {
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

        DailyMobileAPI.getInstance(mContext).requestEventList(mNetworkTag, "google", networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestEventNCouponNNoticeNewCount() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback mDailyEventCountCallback = new retrofit2.Callback<JSONObject>()
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
                    assertThat(msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    assertThat(dataJSONObject.get("isExistNewEvent"), allOf(notNullValue(), instanceOf(Boolean.class)));
                    assertThat(dataJSONObject.get("isExistNewCoupon"), allOf(notNullValue(), instanceOf(Boolean.class)));
                    assertThat(dataJSONObject.get("isExistNewNotices"), allOf(notNullValue(), instanceOf(Boolean.class)));
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

        String viewedEventTime = DailyPreference.getInstance(mContext).getViewedEventTime();
        String viewedCouponTime = DailyPreference.getInstance(mContext).getViewedCouponTime();
        String viewedNoticeTime = DailyPreference.getInstance(mContext).getViewedNoticeTime();

        if (DailyTextUtils.isTextEmpty(viewedEventTime) == true)
        {
            viewedEventTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
        }

        if (DailyTextUtils.isTextEmpty(viewedCouponTime) == true)
        {
            viewedCouponTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
        }

        if (DailyTextUtils.isTextEmpty(viewedNoticeTime) == true)
        {
            viewedNoticeTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
        }

        DailyMobileAPI.getInstance(mContext).requestEventNCouponNNoticeNewCount(//
            mNetworkTag, viewedEventTime, viewedCouponTime, viewedNoticeTime, mDailyEventCountCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // eventIndex 의 경우 고정이 아니기 때문에 requestEventList 이후에 진행하도록 한다.
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
    //                try
    //                {
    //                    assertThat(response, notNullValue());
    //                    assertThat(response.isSuccessful(), is(true));
    //                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));
    //
    //                    JSONObject responseJSONObject = response.body();
    //
    //                    int msgCode = responseJSONObject.getInt("msg_code");
    //                    String message = responseJSONObject.getString("msg");
    //                    assertThat(message, isNotEmpty());
    //                    assertThat(message, msgCode, is(0));
    //
    //                    JSONObject eventJsonObject = responseJSONObject.getJSONObject("data");
    //                    assertThat(eventJsonObject, notNullValue());
    //
    //                    String eventUrl = eventJsonObject.getString("url");
    //                    assertThat(eventUrl, isNotEmpty());
    //                } catch (Throwable t)
    //                {
    //                    addException(call, response, t);
    //                } finally
    //                {
    //                    mLock.countDown();
    //                }
    //            }
    //
    //            @Override
    //            public void onFailure(Call<JSONObject> call, Throwable t)
    //            {
    //                addException(call, null, t);
    //                mLock.countDown();
    //            }
    //        };
    //
    //        String store;
    //        if (Setting.getStore() == Setting.Stores.PLAY_STORE)
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

    @Test
    public void requestDailyUserVerification() throws Exception
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

                    if (response.isSuccessful() == true && response.body() != null)
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");

                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(100));
                    } else if (response.isSuccessful() == false && response.errorBody() != null)
                    {
                        JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
                        assertThat(responseJSONObject, notNullValue());

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");

                        assertThat(message, isNotEmpty());
                        assertThat(message, response.code(), is(422));
                        assertThat(message, msgCode, is(2001));

                        JSONObject dataJONObject = responseJSONObject.getJSONObject("data");
                        assertThat(dataJONObject, notNullValue());

                        assertThat(dataJONObject.getString("phone"), isNotEmpty());

                        assertThat(response.isSuccessful(), is(true));
                    } else
                    {
                        assertThat(response.isSuccessful(), is(true));
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

        DailyMobileAPI.getInstance(mContext).requestDailyUserVerification( //
            mNetworkTag, Const.TEST_USER_PHONE, Const.TEST_IS_FORCE_PHONE_VALIDATION, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 폰 넘버와 문자를 받아서 할 것이냐.... 자동으로 받아서 할것인지가.... 음.. 모르겠음
    @RequiresDevice
    @Ignore
    public void requestDailyUserUpdatePhoneNumber(String phoneNumber, String code) throws Exception
    {
        if (DailyTextUtils.isTextEmpty(phoneNumber, code) == false)
        {
            try
            {
                assertThat("required data is empty! phone : " + phoneNumber + " , code : " + code, false);
            } catch (Throwable t)
            {
                addException(null, null, t);
            }
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
                    //                    assertThat(response.isSuccessful(), is(true));
                    //                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    if (response.isSuccessful() == true && response.body() != null)
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");
                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(100));
                    } else if (response.isSuccessful() == false && response.errorBody() != null)
                    {
                        JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
                        assertThat(responseJSONObject, notNullValue());

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");

                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(422));
                        assertThat(response.isSuccessful(), is(true));
                    } else
                    {
                        assertThat(response.isSuccessful(), is(true));
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

        DailyMobileAPI.getInstance(mContext).requestDailyUserUpdatePhoneNumber(mNetworkTag, phoneNumber.replaceAll("-", ""), code, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestSignupValidation() throws Exception
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    String signupKey = dataJSONObject.getString("signup_key");
                    String serverDate = dataJSONObject.getString("serverDate");

                    assertThat(signupKey, isNotEmpty());
                    assertThat(serverDate, isNotEmpty());

                    requestDailyUserSignupVerfication(signupKey, Const.TEST_USER_PHONE, false);
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

        String email = "dh_" + DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy_MM_dd") + "@dailyhotel.com";

        HashMap<String, String> signUpParams = new HashMap<>();

        signUpParams.put("email", email);
        signUpParams.put("pw", Const.TEST_PASSWORD);
        signUpParams.put("name", Const.TEST_USER_NAME);

        // recommender skip!
        //        if (com.daily.base.util.TextUtils.isTextEmpty(recommender) == false)
        //        {
        //            signUpParams.put("recommender", recommender);
        //        }

        signUpParams.put("birthday", Const.TEST_USER_BIRTHDAY);
        signUpParams.put("market_type", Setting.getStore().getName());
        signUpParams.put("isAgreedBenefit", Boolean.toString(Const.TEST_IS_AGREED_BENEFIT));

        DailyMobileAPI.getInstance(mContext).requestSignupValidation(mNetworkTag, signUpParams, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // requestSignupValidation 이후에 진행 되어야 함
    @Ignore
    public void requestDailyUserSignupVerfication(String signupKey, String phoneNumber, boolean force) throws Exception
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
                    //                    assertThat(response.isSuccessful(), is(true));
                    //                    assertThat(response.body(), allOf(notNullValue(), isA(JSONObject.class)));

                    if (response.isSuccessful() == true && response.body() != null)
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");
                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(100));

                    } else if (response.isSuccessful() == false && response.errorBody() != null)
                    {
                        JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
                        assertThat(responseJSONObject, notNullValue());

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");
                        assertThat(message, isNotEmpty());
                        assertThat(message, response.code(), is(422));
                        // 동일한 전화번호로 인증 받은 사용자가
                        assertThat(message, msgCode, is(2001));
                        JSONObject dataJONObject = responseJSONObject.getJSONObject("data");
                        assertThat(dataJONObject, notNullValue());

                        String phoneNumber = dataJONObject.getString("phone");
                        assertThat(phoneNumber, isNotEmpty());

                        // 강제 실패
                        assertThat(response.isSuccessful(), is(true));
                    } else
                    {
                        // 강제 실패
                        assertThat(response.isSuccessful(), is(true));
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

        DailyMobileAPI.getInstance(mContext).requestDailyUserSignupVerfication(mNetworkTag, signupKey, phoneNumber, force, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // requestSignupValidation 이후에 진행 되어야 함 - 인증번호 필수
    @RequiresDevice
    @Ignore
    public void requestDailyUserSignup(String signupKey, String vaildateCode, String phoneNumber) throws Exception
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

                    if (response.isSuccessful() == true && response.body() != null)
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");
                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(0));

                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        assertThat(dataJSONObject, notNullValue());

                        boolean isSignup = dataJSONObject.getBoolean("is_signup");
                        assertThat(isSignup, is(true));

                    } else if (response.isSuccessful() == false && response.errorBody() != null)
                    {
                        JSONObject responseJSONObject = new JSONObject(response.errorBody().string());
                        assertThat(responseJSONObject, notNullValue());

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");
                        assertThat(message, isNotEmpty());
                        assertThat(message, msgCode, is(422));

                        assertThat(response.isSuccessful(), is(true));
                    } else
                    {
                        assertThat(response.isSuccessful(), is(true));
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

        DailyMobileAPI.getInstance(mContext).requestDailyUserSignup(//
            mNetworkTag, signupKey, vaildateCode, phoneNumber, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 해당 기능은... 일단 패스 고려하지 않음
    @Ignore
    public void requestFacebookUserSignup() throws Exception
    {
        //  mLock = new CountDownLatch(1);
        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 해당 기능은... 일단 패스 고려하지 않음
    @Ignore
    public void requestKakaoUserSignup() throws Exception
    {
        //  mLock = new CountDownLatch(1);
        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 해당 기능은 사전 테스트로 인하여 그냥 성공!
    @Test
    public void requestDailyUserSignin() throws Exception
    {
        //  mLock = new CountDownLatch(1);
        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 해당 기능은... 일단 패스 고려하지 않음
    @RequiresDevice
    @Ignore
    public void requestFacebookUserSignin() throws Exception
    {
        //  mLock = new CountDownLatch(1);
        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 해당 기능은... 일단 패스 고려하지 않음
    @RequiresDevice
    @Ignore
    public void requestKakaoUserSignin() throws Exception
    {
        //  mLock = new CountDownLatch(1);
        //  mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList() throws Exception
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
                    assertThat(list, notNullValue());

                    for (Coupon coupon : list)
                    {
                        if (coupon.isDownloaded == false)
                        {
                            requestDownloadCoupon(coupon.couponCode);
                            break;
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

        DailyMobileAPI.getInstance(mContext).requestCouponList(mNetworkTag, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList1() throws Exception
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
                    assertThat(list, notNullValue());
                    assertThat(list.size(), moreThan(0));
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

        Calendar calendar = DailyCalendar.getInstance();
        DailyCalendar.setCalendarDateString(calendar, mTodayDateTime.dailyDateTime, 1);

        DailyMobileAPI.getInstance(mContext).requestCouponList(//
            mNetworkTag, Const.TEST_STAY_INDEX, Const.TEST_STAY_SALE_ROOM_INDEX,//
            mTodayDateTime.dailyDateTime,//
            DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT), networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList2() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback couponListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
                    assertThat(list, notNullValue());
                    assertThat(list.size(), moreThan(0));
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

        DailyMobileAPI.getInstance(mContext).requestCouponList(//
            mNetworkTag, Const.TEST_GOURMET_INDEX, Const.TEST_GOURMET_TICKET_COUNT, couponListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponHistoryList() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback couponHistoryCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    ArrayList<CouponHistory> list = CouponUtil.getCouponHistoryList(responseJSONObject);
                    assertThat(list, notNullValue());
                    assertThat(list.size(), moreThan(0));
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

        DailyMobileAPI.getInstance(mContext).requestCouponHistoryList(mNetworkTag, couponHistoryCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestNoticeAgreement() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback noticeAgreementCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    assertThat(dataJSONObject.getString("description1"), isNotEmpty());
                    assertThat(dataJSONObject.getString("description2"), isNotEmpty());
                    assertThat(dataJSONObject.get("isFirstTimeBuyer"), allOf(notNullValue(), instanceOf(Boolean.class)));
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

        DailyMobileAPI.getInstance(mContext).requestNoticeAgreement(mNetworkTag, noticeAgreementCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestNoticeAgreementResult() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback noticeAgreementResultCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    String agreeAt = dataJSONObject.getString("agreedAt");
                    assertThat(agreeAt, isNotEmpty());
                    assertThat(dataJSONObject.getString("description1InAgree"), isNotEmpty());
                    assertThat(dataJSONObject.getString("description2InAgree"), isNotEmpty());
                    assertThat(dataJSONObject.getString("description1InReject"), isNotEmpty());
                    assertThat(dataJSONObject.getString("description2InReject"), isNotEmpty());

                    agreeAt = DailyCalendar.convertDateFormatString(agreeAt, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
                    assertThat(agreeAt, isNotEmpty());
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

        DailyMobileAPI.getInstance(mContext).requestNoticeAgreementResult(mNetworkTag, Const.TEST_IS_NOTICE_AGREE, noticeAgreementResultCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestBenefitMessage() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback benefitMessageCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    String bodyMessage = dataJSONObject.getString("body");
                    assertThat(bodyMessage, isNotEmpty());
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

        DailyMobileAPI.getInstance(mContext).requestBenefitMessage(mNetworkTag, benefitMessageCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 다운로드 가능상태의 쿠폰이 없을 수도 있음으로 requestCouponList 이후에 진행하도록 함!
    @Ignore
    public void requestDownloadCoupon(String couponCode) throws Exception
    {
        if (DailyTextUtils.isTextEmpty(couponCode) == true)
        {
            assertThat("couponCode is null", false);
            return;
        }

        mLock = new CountDownLatch(1);

        retrofit2.Callback downloadCouponCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));
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

        DailyMobileAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, couponCode, downloadCouponCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 웹페이지 연동이 필요하여 일단 스킵!
    @Ignore
    public void requestDownloadEventCoupon(String couponCode) throws Exception
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    String validFrom = dataJSONObject.getString("validFrom");
                    String validTo = dataJSONObject.getString("validTo");
                    assertThat(DailyCalendar.convertDateFormatString(validFrom, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"), isNotEmpty());
                    assertThat(DailyCalendar.convertDateFormatString(validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"), isNotEmpty());
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

        DailyMobileAPI.getInstance(mContext).requestDownloadEventCoupon(mNetworkTag, couponCode, networkCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestHasCoupon() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback hasCouponCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    assertThat(dataJSONObject.get("existCoupons"), allOf(notNullValue(), instanceOf(Boolean.class)));
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

        DailyMobileAPI.getInstance(mContext).requestHasCoupon( //
            mNetworkTag, Const.TEST_STAY_INDEX, DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"), Const.TEST_NIGHTS, hasCouponCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestHasCoupon1() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback hasCouponCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());
                    assertThat(dataJSONObject.get("existCoupons"), allOf(notNullValue(), instanceOf(Boolean.class)));
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

        DailyMobileAPI.getInstance(mContext).requestHasCoupon(//
            mNetworkTag, Const.TEST_GOURMET_INDEX, DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"), hasCouponCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList3() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback couponListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
                    assertThat(list, notNullValue());
                    assertThat(list.isEmpty(), is(false));

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

        DailyMobileAPI.getInstance(mContext).requestCouponList(//
            mNetworkTag, Const.TEST_STAY_INDEX,//
            mTodayDateTime.dailyDateTime,//
            Const.TEST_NIGHTS, couponListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestCouponList4() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback couponListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
                    assertThat(list, notNullValue());
                    assertThat(list.isEmpty(), is(false));

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

        DailyMobileAPI.getInstance(mContext).requestCouponList(//
            mNetworkTag, Const.TEST_GOURMET_INDEX,//
            mTodayDateTime.dailyDateTime, couponListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestRegisterKeywordCoupon() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback registerKeywordCouponCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

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

        DailyMobileAPI.getInstance(mContext).requestRegisterKeywordCoupon(//
            mNetworkTag, Const.TEST_KEYWORD_COUPON_CODE, registerKeywordCouponCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUpdateBenefitAgreement() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback updateBenefitCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    String serverDate = dataJSONObject.getString("serverDate");
                    assertThat(serverDate, isNotEmpty());

                    serverDate = DailyCalendar.convertDateFormatString(serverDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
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

        DailyMobileAPI.getInstance(mContext).requestUpdateBenefitAgreement(//
            mNetworkTag, Const.TEST_IS_UPDATE_BENEFIT_AGREEMENT, updateBenefitCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestUserTracking() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback userTrackingCallback = new retrofit2.Callback<JSONObject>()
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

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    JSONObject tracking = dataJSONObject.getJSONObject("tracking");
                    assertThat(tracking, notNullValue());

                    int gourmetPaymentCompletedCount = tracking.getInt("countOfGourmetPaymentCompleted");
                    int hotelPaymentCompletedCount = tracking.getInt("countOfHotelPaymentCompleted");

                    assertThat(gourmetPaymentCompletedCount, moreThan(0));
                    assertThat(hotelPaymentCompletedCount, moreThan(0));
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

        DailyMobileAPI.getInstance(mContext).requestUserTracking(mNetworkTag, userTrackingCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestNoticeList() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback noticeListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    JSONArray jsonArray = dataJSONObject.getJSONArray("notices");
                    assertThat(jsonArray, notNullValue());

                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        assertThat(jsonObject.getInt("idx"), moreThan(1));
                        assertThat(jsonObject.getString("title"), isNotEmpty());
                        assertThat(jsonObject.getString("linkUrl"), isNotEmpty());
                        assertThat(jsonObject.getString("createdAt"), isNotEmpty());
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

        DailyMobileAPI.getInstance(mContext).requestNoticeList(mNetworkTag, noticeListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestReceiptByEmail() throws Exception
    {
        retrofit2.Callback receiptByEmailCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));
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

        mLock = new CountDownLatch(1);

        DailyMobileAPI.getInstance(mContext).requestReceiptByEmail( //
            mNetworkTag, "gourmet", Integer.toString(Const.TEST_GOURMET_RESERVATION_INDEX), Const.TEST_EMAIL, receiptByEmailCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);

        mLock = new CountDownLatch(1);

        DailyMobileAPI.getInstance(mContext).requestReceiptByEmail( //
            mNetworkTag, "stay", Integer.toString(Const.TEST_STAY_RESERVATION_INDEX), Const.TEST_EMAIL, receiptByEmailCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestWishListCount() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback wishListCountCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    int userIndex = dataJSONObject.getInt("userIdx");
                    int stayWishCount = dataJSONObject.getInt("wishHotelCount");
                    int gourmetWishCount = dataJSONObject.getInt("wishGourmetCount");

                    assertThat(userIndex, moreThan(1));
                    assertThat(stayWishCount, moreThan(0));
                    assertThat(gourmetWishCount, moreThan(0));

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

        DailyMobileAPI.getInstance(mContext).requestWishListCount(mNetworkTag, wishListCountCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestWishList() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback gourmetWishListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    JSONArray gourmetJSONArray = null;

                    if (dataJSONObject.has("gourmetSales") == true)
                    {
                        gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSales");
                        assertThat(gourmetJSONArray, notNullValue());
                    }

                    if (gourmetJSONArray != null)
                    {
                        String imageUrl = dataJSONObject.getString("imgUrl");
                        assertThat(imageUrl, isNotEmpty());

                        JSONObject jsonObject;
                        int testPlaceIndex = -1;

                        int length = gourmetJSONArray.length();
                        for (int i = 0; i < length; i++)
                        {
                            jsonObject = gourmetJSONArray.getJSONObject(i);
                            assertThat(jsonObject, notNullValue());

                            int index = jsonObject.getInt("restaurantIdx");
                            assertThat(index, moreThan(1));

                            testPlaceIndex = index;

                            String name = null;
                            if (jsonObject.has("restaurantName") == true)
                            {
                                name = jsonObject.getString("restaurantName");
                            } else if (jsonObject.has("name") == true)
                            {
                                name = jsonObject.getString("name");
                            }

                            assertThat(name, isNotEmpty());

                            assertThat(jsonObject.getInt("price"), moreThan(0));
                            assertThat(jsonObject.getInt("discount"), moreThan(0));
                            assertThat(jsonObject.getString("addrSummary"), isNotEmpty());
                            assertThat(jsonObject.getString("districtName"), isNotEmpty());
                            assertThat(jsonObject.getDouble("latitude"), not(0d));
                            assertThat(jsonObject.getDouble("longitude"), not(0d));
                            assertThat(jsonObject.get("isDailychoice"), allOf(notNullValue(), instanceOf(Boolean.class)));
                            assertThat(jsonObject.get("isSoldOut"), allOf(notNullValue(), instanceOf(Boolean.class)));
                            assertThat(jsonObject.getInt("persons"), moreThan(1));
                            assertThat(jsonObject.getString("category"), isNotEmpty());
                            assertThat(jsonObject.getInt("categoryCode"), moreThan(0));
                            assertThat(jsonObject.getInt("categorySeq"), moreThan(0));

                            if (jsonObject.has("categorySub") == true)
                            {
                                assertThat(jsonObject.getString("categorySub"), isNotEmpty());
                            }

                            if (jsonObject.has("rating") == true)
                            {
                                assertThat(jsonObject.getInt("rating"), moreThan(0));
                            }

                            if (jsonObject.has("distance") == true)
                            {
                                assertThat(jsonObject.getDouble("distance"), moreThan(0d));
                            }

                            JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
                            assertThat(imageJSONObject, notNullValue());

                            Iterator<String> iterator = imageJSONObject.keys();
                            while (iterator.hasNext())
                            {
                                String key = iterator.next();
                                assertThat(key, isNotEmpty());

                                JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
                                assertThat(pathJSONArray, notNullValue());
                                assertThat(pathJSONArray.length(), moreThan(1));

                                assertThat(pathJSONArray.getString(0), isNotEmpty());
                                break;
                            }

                            if (jsonObject.has("benefit") == true)
                            {
                                assertThat(jsonObject.getString("benefit"), notNullValue());
                            }
                        }

                        if (testPlaceIndex != -1)
                        {
                            requestRemoveWishList(Constants.PlaceType.FNB, testPlaceIndex);
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

        //        DailyMobileAPI.getInstance(mContext).
        //            requestWishList(mNetworkTag, "gourmet", gourmetWishListCallback);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);

        mLock = new CountDownLatch(1);

        retrofit2.Callback stayWishListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    JSONArray hotelJSONArray = null;

                    if (dataJSONObject.has("hotelSales") == true)
                    {
                        hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
                        assertThat(hotelJSONArray, notNullValue());
                    }

                    if (hotelJSONArray != null)
                    {
                        String imageUrl = dataJSONObject.getString("imgUrl");
                        assertThat(imageUrl, isNotEmpty());

                        int nights = dataJSONObject.getInt("stays");
                        assertThat(nights, moreThan(1));

                        JSONObject jsonObject;
                        int testPlaceIndex = -1;

                        int length = hotelJSONArray.length();
                        for (int i = 0; i < length; i++)
                        {
                            jsonObject = hotelJSONArray.getJSONObject(i);
                            assertThat(jsonObject, notNullValue());

                            assertThat(jsonObject.getString("name"), isNotEmpty());
                            assertThat(jsonObject.getInt("price"), moreThan(0));
                            assertThat(jsonObject.getInt("discount"), moreThan(0)); // discountAvg ????
                            assertThat(jsonObject.getString("addrSummary"), isNotEmpty());

                            String gradeString = jsonObject.getString("grade");
                            assertThat(gradeString, isNotEmpty());
                            assertThat(Stay.Grade.valueOf(gradeString), notNullValue());

                            int index = jsonObject.getInt("hotelIdx");
                            assertThat(index, moreThan(1));
                            testPlaceIndex = index;

                            assertThat(jsonObject.has("isSoldOut"), is(true));

                            assertThat(jsonObject.getString("districtName"), isNotEmpty());
                            assertThat(jsonObject.getString("category"), isNotEmpty());
                            assertThat(jsonObject.getDouble("latitude"), not(0d));
                            assertThat(jsonObject.getDouble("longitude"), not(0d));
                            assertThat(jsonObject.get("isDailyChoice"), allOf(notNullValue(), instanceOf(Boolean.class)));
                            assertThat(jsonObject.getInt("rating"), moreThan(0)); // ratingValue ??
                            assertThat(jsonObject.getString("sday"), isNotEmpty());
                            assertThat(jsonObject.getDouble("distance"), moreThan(0d));

                            JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");
                            assertThat(imageJSONObject, notNullValue());

                            Iterator<String> iterator = imageJSONObject.keys();
                            while (iterator.hasNext())
                            {
                                String key = iterator.next();
                                assertThat(key, isNotEmpty());

                                JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
                                assertThat(pathJSONArray, notNullValue());

                                assertThat(pathJSONArray.getString(0), isNotEmpty());
                                break;
                            }

                            if (jsonObject.has("benefit") == true) // hotelBenefit ?
                            {
                                assertThat(jsonObject.getString("benefit"), notNullValue());
                            }
                        }

                        if (testPlaceIndex != -1)
                        {
                            requestRemoveWishList(Constants.PlaceType.HOTEL, testPlaceIndex);
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

        //        DailyMobileAPI.getInstance(mContext).requestWishList(mNetworkTag, "hotel", stayWishListCallback);
        //        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestAddWishList() throws Exception
    {
        retrofit2.Callback addWishListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

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

        mLock = new CountDownLatch(1);

        DailyMobileAPI.getInstance(mContext).requestAddWishList(mNetworkTag, "hotel", Const.TEST_STAY_INDEX, addWishListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);

        mLock = new CountDownLatch(1);

        DailyMobileAPI.getInstance(mContext).requestAddWishList(mNetworkTag, "gourmet", Const.TEST_GOURMET_INDEX, addWishListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    // 제거할 위시리스트의 인덱스는 계속 변경 됨으로 위시리스트 목록을 가져오는 테스트 이후에 진행하도록 함
    @Ignore
    public void requestRemoveWishList(Constants.PlaceType placeType, int placeIndex) throws Exception
    {
        mLock = new CountDownLatch(1);

        if (placeIndex <= 0)
        {
            assertThat("placeIndex less than or equal to zero", false);
            return;
        }

        String type = Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel";

        retrofit2.Callback removeWishListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

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

        DailyMobileAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, type, placeIndex, removeWishListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestPolicyRefund() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback policyRefundCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    String comment = dataJSONObject.getString("comment");
                    String refundPolicy = dataJSONObject.getString("refundPolicy");
                    assertThat(comment, isNotEmpty());
                    assertThat(refundPolicy, isNotEmpty());

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

        Calendar calendar = DailyCalendar.getInstance();
        DailyCalendar.setCalendarDateString(calendar, mTodayDateTime.dailyDateTime, 1);

        DailyMobileAPI.getInstance(mContext).requestPolicyRefund(mNetworkTag//
            , Const.TEST_STAY_INDEX, Const.TEST_STAY_SALE_ROOM_INDEX//
            , DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd")//
            , DailyCalendar.format(calendar.getTime(), "yyyy-MM-dd") //
            , policyRefundCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestPolicyRefund1() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback policyRefundCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, allOf(is(100), is(1015)));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    String comment = dataJSONObject.getString("comment");
                    String refundPolicy = dataJSONObject.getString("refundPolicy");
                    boolean refundManual = dataJSONObject.getBoolean("refundManual");

                    assertThat(comment, isNotEmpty());
                    assertThat(refundPolicy, isNotEmpty());
                    assertThat(dataJSONObject.get("refundManual"), allOf(notNullValue(), instanceOf(Boolean.class)));

                    // 환불 킬스위치 ON
                    if (refundManual == true)
                    {
                        if (StayBookingDetail.STATUS_NRD.equalsIgnoreCase(refundPolicy) == true)
                        {
                            // do nothing!
                        } else
                        {
                            assertThat(responseJSONObject.getString("msg"), false);
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

        DailyMobileAPI.getInstance(mContext).requestPolicyRefund(mNetworkTag//
            , Const.TEST_STAY_RESERVATION_INDEX, TEST_PAYMENT_TYPE, policyRefundCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestRefund() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback refundCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = null;

                    // msgCode 1013: 환불 요청 중 실패한 것으로 messageFromPg를 사용자에게 노출함.
                    // msgCode 1014: 무료 취소 횟수를 초과한 것으로 msg 내용을 사용자에게 노출함.
                    // msgCode 1015: 환불 수동 스위치 ON일 경우
                    switch (msgCode)
                    {
                        case 1014:
                            message = responseJSONObject.getString("msg");
                            assertThat(message, false);
                            break;

                        case 1013:
                        case 1015:
                        default:
                            if (responseJSONObject.has("data") == true && responseJSONObject.isNull("data") == false)
                            {
                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                                message = dataJSONObject.getString("messageFromPg");
                                assertThat(message, isNotEmpty());

                                assertThat(dataJSONObject.getBoolean("readyForRefund"), is(true));
                            }

                            if (DailyTextUtils.isTextEmpty(message) == true)
                            {
                                message = responseJSONObject.getString("msg");
                                assertThat(message, isNotEmpty());
                            }
                            break;
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

        DailyMobileAPI.getInstance(mContext).requestRefund(mNetworkTag, Const.TEST_STAY_INDEX, //
            DailyCalendar.convertDateFormatString(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"), Const.TEST_PAYMENT_TYPE//
            , Const.TEST_STAY_RESERVATION_INDEX, Const.TEST_AUTO_REFUND_CANCEL_MESSAGE, null, null, null, refundCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestBankList() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback bankListCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                    assertThat(dataJSONArray, notNullValue());

                    int length = dataJSONArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject = dataJSONArray.getJSONObject(i);
                        assertThat(jsonObject, notNullValue());

                        assertThat(jsonObject.getString("code"), isNotEmpty());
                        assertThat(jsonObject.getString("name"), isNotEmpty());
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

        DailyMobileAPI.getInstance(mContext).requestBankList(mNetworkTag, bankListCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayReviewInformation() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback reviewStayCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));
                    // 리뷰가 존재하지 않는 경우 msgCode : 701

                    if (msgCode == 100 && responseJSONObject.has("data") == true)
                    {
                        JSONObject jsonObject = responseJSONObject.getJSONObject("data");
                        assertThat(jsonObject, notNullValue());

                        Review review = new Review(jsonObject);
                        assertThat(review, notNullValue());
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

        DailyMobileAPI.getInstance(mContext).requestStayReviewInformation(mNetworkTag, reviewStayCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetReviewInformation() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback reviewGourmetCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));
                    // 리뷰가 존재하지 않는 경우 msgCode : 701

                    if (msgCode == 100 && responseJSONObject.has("data") == true)
                    {
                        JSONObject jsonObject = responseJSONObject.getJSONObject("data");
                        assertThat(jsonObject, notNullValue());

                        Review review = new Review(jsonObject);
                        assertThat(review, notNullValue());
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

        DailyMobileAPI.getInstance(mContext).requestGourmetReviewInformation(mNetworkTag, reviewGourmetCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestStayReviewInformation1() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback stayReviewInformationCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    Review review = new Review(dataJSONObject);
                    assertThat(review, notNullValue());
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

        DailyMobileAPI.getInstance(mContext).

            requestStayReviewInformation(//
                mNetworkTag, Const.TEST_STAY_RESERVATION_INDEX, stayReviewInformationCallback);

        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestGourmetReviewInformation1() throws Exception
    {
        mLock = new CountDownLatch(1);

        retrofit2.Callback gourmetReviewInformationCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    assertThat(dataJSONObject, notNullValue());

                    Review review = new Review(dataJSONObject);
                    assertThat(review, notNullValue());
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

        DailyMobileAPI.getInstance(mContext).requestGourmetReviewInformation( //
            mNetworkTag, Const.TEST_GOURMET_RESERVATION_INDEX, gourmetReviewInformationCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestAddReviewInformation() throws Exception
    {
        retrofit2.Callback addReviewCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));
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

        mLock = new CountDownLatch(1);

        JSONObject stayJsonObject = new JSONObject();
        stayJsonObject.put("grade", Const.TEST_STAY_ADD_REVIEW_GRADE);
        stayJsonObject.put("itemIdx", Const.TEST_STAY_ADD_REVIEW_ITEM_INDEX);
        stayJsonObject.put("reserveIdx", Const.TEST_STAY_RESERVATION_INDEX);
        stayJsonObject.put("serviceType", "HOTEL");

        DailyMobileAPI.getInstance(mContext).requestAddReviewInformation(mNetworkTag, stayJsonObject, addReviewCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);

        mLock = new CountDownLatch(1);

        JSONObject gourmetJsonObject = new JSONObject();
        gourmetJsonObject.put("grade", Const.TEST_GOURMET_ADD_REVIEW_GRADE);
        gourmetJsonObject.put("itemIdx", Const.TEST_GOURMET_ADD_REVIEW_ITEM_INDEX);
        gourmetJsonObject.put("reserveIdx", Const.TEST_GOURMET_RESERVATION_INDEX);
        gourmetJsonObject.put("serviceType", "GOURMET");

        DailyMobileAPI.getInstance(mContext).requestAddReviewInformation(mNetworkTag, gourmetJsonObject, addReviewCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    @Test
    public void requestAddReviewDetailInformation() throws Exception
    {
        retrofit2.Callback addReviewDetailCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");
                    assertThat(message, isNotEmpty());
                    assertThat(message, msgCode, is(100));
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

        mLock = new CountDownLatch(1);

        JSONObject stayJsonObject = new JSONObject();
        stayJsonObject.put("comment", DailyTextUtils.isTextEmpty(Const.TEST_ADD_REVIEW_DETAIL_COMMENT) == true ? "" : Const.TEST_ADD_REVIEW_DETAIL_COMMENT);

        stayJsonObject.put("itemIdx", Const.TEST_STAY_ADD_REVIEW_ITEM_INDEX);
        stayJsonObject.put("reserveIdx", Const.TEST_STAY_RESERVATION_INDEX);

        // 생략!
        //            if (scoreJSONArray != null)
        //            {
        //                jsonObject.put("reviewScores", scoreJSONArray);
        //            }
        //
        //            if (pickJSONArray != null)
        //            {
        //                jsonObject.put("reviewPicks", pickJSONArray);
        //            }

        stayJsonObject.put("serviceType", "HOTEL");
        DailyMobileAPI.getInstance(mContext).requestAddReviewDetailInformation(mNetworkTag, stayJsonObject, addReviewDetailCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);

        mLock = new CountDownLatch(1);

        JSONObject gourmetJsonObject = new JSONObject();
        gourmetJsonObject.put("comment", DailyTextUtils.isTextEmpty(Const.TEST_ADD_REVIEW_DETAIL_COMMENT) == true ? "" : Const.TEST_ADD_REVIEW_DETAIL_COMMENT);

        gourmetJsonObject.put("itemIdx", Const.TEST_GOURMET_ADD_REVIEW_ITEM_INDEX);
        gourmetJsonObject.put("reserveIdx", Const.TEST_GOURMET_RESERVATION_INDEX);

        // 생략!
        //            if (scoreJSONArray != null)
        //            {
        //                jsonObject.put("reviewScores", scoreJSONArray);
        //            }
        //
        //            if (pickJSONArray != null)
        //            {
        //                jsonObject.put("reviewPicks", pickJSONArray);
        //            }

        gourmetJsonObject.put("serviceType", "GOURMET");
        DailyMobileAPI.getInstance(mContext).requestAddReviewDetailInformation(mNetworkTag, gourmetJsonObject, addReviewDetailCallback);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }
}