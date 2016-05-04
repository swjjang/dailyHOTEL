package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.Map;

public class SignupStep2NetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onSignUp(int notificationUid, String gcmRegisterId);

        void onResponseVerification(String time);
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

    public void requestUserSingUp(Map<String, String> signupParams)
    {
        DailyNetworkAPI.getInstance().requestUserSignup(mNetworkTag, signupParams, mUserSignupJsonResponseListener, this);
    }

    public void requestVerfication(String signupKey, String phone)
    {
        DailyNetworkAPI.getInstance().requestVerfication(mNetworkTag, signupKey, phone, mVerificationJsonResponseListener, this);
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
                    DailyNetworkAPI.getInstance().requestUserRegisterNotification(mNetworkTag, registrationId, new DailyHotelJsonResponseListener()
                    {
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
                                    //                                    DailyPreference.getInstance(mContext).setNotificationUid(uid);
                                    //                                    DailyPreference.getInstance(mContext).setGCMRegistrationId(registrationId);
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

    //    private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                String userIndex = String.valueOf(response.getInt("idx"));
    //
    //                AnalyticsManager.getInstance(mContext).setUserIndex(userIndex);
    //                AnalyticsManager.getInstance(mContext).signUpDailyUser(userIndex, mSignupParams.get("email")//
    //                    , mSignupParams.get("name"), mSignupParams.get("phone"), AnalyticsManager.UserType.EMAIL);
    //
    //                requestGoogleCloudMessagingId();
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };

    //    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                int msg_code = response.getInt("msg_code");
    //
    //                if (msg_code == 0)
    //                {
    //                    JSONObject jsonObject = response.getJSONObject("data");
    //
    //                    boolean isSignin = jsonObject.getBoolean("is_signin");
    //
    //                    if (isSignin == true)
    //                    {
    //                        JSONObject tokenJSONObject = response.getJSONObject("token");
    //                        String accessToken = tokenJSONObject.getString("access_token");
    //                        String tokenType = tokenJSONObject.getString("token_type");
    //
    //                        storeUserInformation(String.format("%s %s", tokenType, accessToken));
    //
    //                        lockUI();
    //                        DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, SignupStep2NetworkController.this);
    //                        return;
    //                    }
    //                }
    //
    //                // 로그인이 실패한 경우
    //                String msg = response.getString("msg");
    //
    //                if (Util.isTextEmpty(msg) == true)
    //                {
    //                    msg = getString(R.string.toast_msg_failed_to_login);
    //                }
    //
    //                DailyToast.showToast(SignupStep2NetworkController.this, msg, Toast.LENGTH_LONG);
    //
    //                unLockUI();
    //                finish();
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };

    private DailyHotelJsonResponseListener mUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            //            try
            //            {
            //                int msg_code = response.getInt("msg_code");
            //
            //                if (msg_code == 0)
            //                {
            //                    JSONObject jsonObject = response.getJSONObject("data");
            //
            //                    boolean isSignup = jsonObject.getBoolean("is_signup");
            //
            //                    if (isSignup == true)
            //                    {
            //                        requestGoogleCloudMessagingId();
            //                        return;
            //                    }
            //                }
            //
            //                String msg = response.getString("msg");
            //
            //                if (Util.isTextEmpty(msg) == true)
            //                {
            //                    msg = getString(R.string.toast_msg_failed_to_signup);
            //                }
            //            } catch (Exception e)
            //            {
            //                mOnNetworkControllerListener.onError(e);
            //            }
        }
    };

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
                    String message = "010-NNNN-NNNN로 N자리 인증번호를 보내드렸습니다.\nN분내 인증번호를 입력해주세요!";

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseVerification(message);
                } else
                {
                    // 다른 폰에서 인증된 경우
                    mOnNetworkControllerListener.onErrorMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}
