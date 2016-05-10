package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupStep2NetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onVerification(String time);

        void onSignUp(int notificationUid, String gcmRegisterId);

        void onLogin(String authorization);
    }

    public SignupStep2NetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    public void requestVerfication(String signupKey, String phoneNumber)
    {
        DailyNetworkAPI.getInstance(mContext).requestDailyUserSignupVerfication(mNetworkTag, signupKey, phoneNumber, mVerificationJsonResponseListener);
    }

    public void requestSingUp(String signupKey, String code)
    {
        DailyNetworkAPI.getInstance(mContext).requestDailyUserSignup(mNetworkTag, signupKey, code, mDailyUserSignupJsonResponseListener);
    }

    public void requestLogin(String email, String password)
    {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("pw", password);
        params.put("social_id", "0");
        params.put("user_type", Constants.DAILY_USER);
        params.put("is_auto", "true");

        DailyNetworkAPI.getInstance(mContext).requestUserSignin(mNetworkTag, params, mDailyUserLoginJsonResponseListener, this);
    }

    public void requestGoogleCloudMessagingId()
    {
        Util.requestGoogleCloudMessaging(mContext, new Util.OnGoogleCloudMessagingListener()
        {
            @Override
            public void onResult(final String registrationId)
            {
                if (Util.isTextEmpty(registrationId) == false)
                {
                    DailyNetworkAPI.getInstance(mContext).requestUserRegisterNotification(mNetworkTag, registrationId, new DailyHotelJsonResponseListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError volleyError)
                        {

                        }

                        @Override
                        public void onResponse(String url, JSONObject response)
                        {
                            int uid = -1;

                            try
                            {
                                int msg_code = response.getInt("msgCode");

                                if (msg_code == 0 && response.has("data") == true)
                                {
                                    JSONObject jsonObject = response.getJSONObject("data");

                                    uid = jsonObject.getInt("uid");
                                }
                            } catch (Exception e)
                            {
                                ExLog.d(e.toString());
                            }

                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onSignUp(uid, registrationId);
                        }
                    }, new ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError arg0)
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onSignUp(-1, null);
                        }
                    });
                } else
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onSignUp(-1, null);
                }
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mVerificationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJONObject = response.getJSONObject("data");
                    String message = dataJONObject.getString("msg");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onVerification(message);
                } else
                {
                    // 다른 폰에서 인증된 경우
                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(new String(volleyError.networkResponse.data));
                mOnNetworkControllerListener.onErrorPopupMessage(jsonObject.getInt("msgCode"), jsonObject.getString("msg"));
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onErrorResponse(volleyError);
            }
        }
    };

    private DailyHotelJsonResponseListener mDailyUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignup = jsonObject.getBoolean("is_signup");

                    if (isSignup == true)
                    {
                        requestGoogleCloudMessagingId();
                        return;
                    }
                }

                String msg = response.getString("msg");

                if (Util.isTextEmpty(msg) == true)
                {
                    msg = mContext.getString(R.string.toast_msg_failed_to_signup);
                }

                mOnNetworkControllerListener.onErrorPopupMessage(msgCode, msg);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(new String(volleyError.networkResponse.data));
                mOnNetworkControllerListener.onErrorPopupMessage(jsonObject.getInt("msgCode"), jsonObject.getString("msg"));
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onErrorResponse(volleyError);
            }
        }
    };

    private DailyHotelJsonResponseListener mDailyUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        JSONObject tokenJSONObject = response.getJSONObject("token");
                        String accessToken = tokenJSONObject.getString("access_token");
                        String tokenType = tokenJSONObject.getString("token_type");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onLogin(String.format("%s %s", tokenType, accessToken));
                        return;
                    }
                }

                // 로그인이 실패한 경우
                String msg = response.getString("msg");

                if (Util.isTextEmpty(msg) == true)
                {
                    msg = mContext.getString(R.string.toast_msg_failed_to_login);
                }

                mOnNetworkControllerListener.onErrorPopupMessage(msgCode, msg);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}
