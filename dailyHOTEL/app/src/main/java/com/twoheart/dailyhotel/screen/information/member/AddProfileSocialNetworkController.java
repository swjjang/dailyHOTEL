package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddProfileSocialNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUpdateSocialUserInformation(String message);
    }

    public AddProfileSocialNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestUpdateSocialUserInformation(String userIndex, String phoneNumber, String email, String name, String recommender, String birthday, boolean isBenefit)
    {
        Map<String, String> params = new HashMap<>();
        params.put("user_idx", userIndex);

        if (Util.isTextEmpty(email) == false)
        {
            params.put("user_email", email);
        }

        if (Util.isTextEmpty(name) == false)
        {
            params.put("user_name", name);
        }

        if (Util.isTextEmpty(phoneNumber) == false)
        {
            params.put("user_phone", phoneNumber.replaceAll("-", ""));
        }

        if (Util.isTextEmpty(birthday) == false)
        {
            params.put("birthday", birthday);
        }

        if (Util.isTextEmpty(recommender) == false)
        {
            params.put("recommendation_code", recommender);
        }

        DailyNetworkAPI.getInstance(mContext).requestUserUpdateInformationForSocial(mNetworkTag, params, mUserUpdateFacebookJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserUpdateFacebookJsonResponseListener = new DailyHotelJsonResponseListener()
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
                JSONObject jsonObject = response.getJSONObject("data");

                boolean result = jsonObject.getBoolean("is_success");

                // TODO :  추후에 msgCode결과를 가지고 구분하는 코드가 필요할듯.
                int msgCode = response.getInt("msg_code");

                if (result == true)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUpdateSocialUserInformation(null);
                } else
                {
                    String message = response.getString("msg");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUpdateSocialUserInformation(message);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };

    //    private DailyHotelJsonResponseListener mUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            unLockUI();
    //
    //            try
    //            {
    //                String result = response.getString("success");
    //                String msg = null;
    //
    //                if (response.length() > 1)
    //                {
    //                    msg = response.getString("msg");
    //                }
    //
    //                if (result.equals("true") == true)
    //                {
    //                    DailyToast.showToast(AddProfileSocialNetworkController.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);
    //
    //                    setResult(RESULT_OK);
    //                    finish();
    //                } else
    //                {
    //                    DailyToast.showToast(AddProfileSocialNetworkController.this, msg, Toast.LENGTH_LONG);
    //                }
    //            } catch (Exception e)
    //            {
    //                onError(e);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                String userIndex = String.valueOf(response.getInt("idx"));
    //
    //                AnalyticsManager.getInstance(AddProfileSocialNetworkController.this).setUserInformation(userIndex);
    //                AnalyticsManager.getInstance(AddProfileSocialNetworkController.this).signUpDailyUser(userIndex, mSignupParams.get("email")//
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
    //
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
    //                        DailyPreference.getInstance(AddProfileSocialNetworkController.this).setAuthorization(String.format("%s %s", tokenType, accessToken));
    //                        storeLoginInfo();
    //
    //                        lockUI();
    //                        DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, AddProfileSocialNetworkController.this);
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
    //                DailyToast.showToast(AddProfileSocialNetworkController.this, msg, Toast.LENGTH_LONG);
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
    //
    //    private DailyHotelJsonResponseListener mUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
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
    //                    boolean isSignup = jsonObject.getBoolean("is_signup");
    //
    //                    if (isSignup == true)
    //                    {
    //                        Map<String, String> params = new HashMap<>();
    //                        params.put("email", mSignupParams.get("email"));
    //                        params.put("pw", Crypto.encrypt(mSignupParams.get("pw")).replace("\n", ""));
    //                        params.put("social_id", "0");
    //                        params.put("user_type", Constants.DAILY_USER);
    //                        params.put("is_auto", "true");
    //
    //                        DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, AddProfileSocialNetworkController.this);
    //                        return;
    //                    }
    //                }
    //
    //                unLockUI();
    //
    //                String msg = response.getString("msg");
    //
    //                if (Util.isTextEmpty(msg) == true)
    //                {
    //                    msg = getString(R.string.toast_msg_failed_to_signup);
    //                }
    //
    //                DailyToast.showToast(AddProfileSocialNetworkController.this, msg, Toast.LENGTH_LONG);
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };
}
