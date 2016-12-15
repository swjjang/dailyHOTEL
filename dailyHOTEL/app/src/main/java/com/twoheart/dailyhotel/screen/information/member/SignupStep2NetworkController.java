package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class SignupStep2NetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onVerification(String message);

        void onSignUp();

        void onLogin(String authorization, String userIndex, String email, String name, String birthday,//
                     String recommender, String userType, String phoneNumber, boolean isBenefit);

        void onAlreadyVerification(String phoneNumber);

        void onInvalidPhoneNumber(String phoneNumber);

        // SMS에서 받은 코드
        void onInvalidVerificationNumber(String message);

        void onRetryDailyUserSignIn();

    }

    public SignupStep2NetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestVerfication(String signupKey, String phoneNumber, boolean force)
    {
        DailyMobileAPI.getInstance(mContext).requestDailyUserSignupVerfication(mNetworkTag, signupKey, phoneNumber, force, mVerificationCallback);
    }

    public void requestSingUp(String signupKey, String code, String phoneNumber)
    {
        DailyMobileAPI.getInstance(mContext).requestDailyUserSignup(mNetworkTag, signupKey, code, phoneNumber, mDailyUserSignupCallback);
    }

    public void requestLogin(String email, String password)
    {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("pw", password);
        params.put("social_id", "0");
        params.put("user_type", Constants.DAILY_USER);

        DailyMobileAPI.getInstance(mContext).requestDailyUserSignin(mNetworkTag, params, mDailyUserLoginCallback);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mVerificationCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null)
            {
                if (response.isSuccessful() == true && response.body() != null)
                {
                    JSONObject responseJSONObject = response.body();

                    try
                    {
                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");

                        switch (msgCode)
                        {
                            case 100:
                            {
                                //                        JSONObject dataJONObject = response.getJSONObject("data");
                                //                        String phone = dataJONObject.getString("phone");
                                ((OnNetworkControllerListener) mOnNetworkControllerListener).onVerification(message);
                                break;
                            }

                            // 회원 가입 중 세션이 만료되었습니다
                            case 2000:
                            {
                                //                        JSONObject dataJONObject = response.getJSONObject("data");
                                mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                                break;
                            }

                            default:
                                mOnNetworkControllerListener.onErrorPopupMessage(msgCode, responseJSONObject.getString("msg"));
                                break;
                        }
                    } catch (Exception e)
                    {
                        mOnNetworkControllerListener.onError(e);
                    }
                } else if (response.isSuccessful() == false && response.errorBody() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = new JSONObject(response.errorBody().string());

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");

                        switch (response.code())
                        {
                            case 422:
                            {
                                switch (msgCode)
                                {
                                    // 동일한 전화번호로 인증 받은 사용자가
                                    case 2001:
                                    {
                                        JSONObject dataJONObject = responseJSONObject.getJSONObject("data");
                                        String phoneNumber = dataJONObject.getString("phone");

                                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onAlreadyVerification(phoneNumber);
                                        return;
                                    }

                                    // 전화번호가 유효하지 않을 때
                                    case 2003:
                                    {
                                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onInvalidPhoneNumber(message);
                                        return;
                                    }
                                }
                                break;
                            }

                            case 400:
                            {
                                switch (msgCode)
                                {
                                    case 2004:
                                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onInvalidPhoneNumber(message);
                                        break;
                                }
                                break;
                            }
                        }

                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    } catch (Exception e)
                    {
                        mOnNetworkControllerListener.onError(e);
                    }
                } else
                {
                    mOnNetworkControllerListener.onErrorResponse(call, response);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(t);
        }
    };

    private retrofit2.Callback mDailyUserSignupCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null)
            {
                if (response.isSuccessful() == true && response.body() != null)
                {
                    JSONObject responseJSONObject = response.body();

                    try
                    {
                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");

                        if (msgCode == 0)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            boolean isSignup = dataJSONObject.getBoolean("is_signup");

                            if (isSignup == true)
                            {
                                ((OnNetworkControllerListener) mOnNetworkControllerListener).onSignUp();
                                return;
                            }
                        }

                        if (Util.isTextEmpty(message) == true)
                        {
                            message = mContext.getString(R.string.toast_msg_failed_to_signup);
                        }

                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    } catch (Exception e)
                    {
                        mOnNetworkControllerListener.onError(e);
                    }
                } else if (response.isSuccessful() == false && response.errorBody() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = new JSONObject(response.errorBody().string());

                        int msgCode = responseJSONObject.getInt("msgCode");
                        String message = responseJSONObject.getString("msg");

                        if (response.code() == 422)
                        {
                            switch (msgCode)
                            {
                                // SMS인증키가 잘못된 경우
                                case 2002:
                                {
                                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onInvalidVerificationNumber(message);
                                    return;
                                }

                                // 전화번호가 유효하지 않을 때
                                case 2003:
                                {
                                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onInvalidPhoneNumber(message);
                                    return;
                                }
                            }
                        }

                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    } catch (Exception e)
                    {
                        mOnNetworkControllerListener.onError(e);
                    }
                } else
                {
                    mOnNetworkControllerListener.onErrorResponse(call, response);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(t);
        }
    };

    private retrofit2.Callback mDailyUserLoginCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null)
            {
                if (response.isSuccessful() == true && response.body() != null)
                {
                    JSONObject responseJSONObject = response.body();

                    try
                    {
                        int msgCode = responseJSONObject.getInt("msg_code");

                        if (msgCode == 0)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            boolean isSignin = dataJSONObject.getBoolean("is_signin");

                            if (isSignin == true)
                            {
                                JSONObject tokenJSONObject = responseJSONObject.getJSONObject("token");
                                String accessToken = tokenJSONObject.getString("access_token");
                                String tokenType = tokenJSONObject.getString("token_type");

                                JSONObject userJSONObject = dataJSONObject.getJSONObject("user");
                                String userIndex = userJSONObject.getString("idx");
                                String email = userJSONObject.getString("email");
                                String name = userJSONObject.getString("name");
                                String rndnum = userJSONObject.getString("rndnum");
                                String userType = userJSONObject.getString("userType");
                                String phoneNumber = userJSONObject.getString("phone");
                                String birthday = null;

                                if (userJSONObject.has("birthday") == true && userJSONObject.isNull("birthday") == false)
                                {
                                    birthday = userJSONObject.getString("birthday");
                                }

                                boolean isAgreedBenefit = userJSONObject.getBoolean("isAgreedBenefit");

                                if (Util.isTextEmpty(userIndex) == true || Util.isTextEmpty(name) == true)
                                {
                                    if (Constants.DEBUG == true)
                                    {
                                        ExLog.w(dataJSONObject.toString());
                                    } else
                                    {
                                        Crashlytics.logException(new RuntimeException("JSON USER Check : " + dataJSONObject.toString(1)));
                                    }
                                }

                                ((OnNetworkControllerListener) mOnNetworkControllerListener).onLogin(String.format("%s %s", tokenType, accessToken),//
                                    userIndex, email, name, birthday, rndnum, userType, phoneNumber, isAgreedBenefit);
                                return;
                            }
                        }

                        // 로그인이 실패한 경우
                        String message = responseJSONObject.getString("msg");

                        if (Util.isTextEmpty(message) == true)
                        {
                            message = mContext.getString(R.string.toast_msg_failed_to_login);
                        }

                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    } catch (Exception e)
                    {
                        mOnNetworkControllerListener.onError(e);
                    }
                } else if (response.isSuccessful() == false && response.errorBody() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = new JSONObject(response.errorBody().string());

                        int msgCode = responseJSONObject.getInt("msg_code");

                        if (response.code() == 422)
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onRetryDailyUserSignIn();
                        } else
                        {
                            mOnNetworkControllerListener.onErrorResponse(call, response);
                        }
                    } catch (Exception e)
                    {
                        mOnNetworkControllerListener.onError(e);
                    }
                } else
                {
                    mOnNetworkControllerListener.onErrorResponse(call, response);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(t);
        }
    };
}
