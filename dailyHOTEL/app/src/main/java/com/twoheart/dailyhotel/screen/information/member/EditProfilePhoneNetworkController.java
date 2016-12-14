package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class EditProfilePhoneNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onVerification(String number);

        void onAlreadyVerification(String phoneNumber);

        void onConfirm();

        void onInvalidPhoneNumber(String message);

        // SMS에서 받은 코드
        void onInvalidVerificationNumber(String message);
    }

    public EditProfilePhoneNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestDailyUserVerification(String phoneNumber, boolean force)
    {
        DailyNetworkAPI.getInstance(mContext).requestDailyUserVerfication(mNetworkTag, phoneNumber.replaceAll("-", ""), force, mDailUserVerificationJsonResponseListener);
    }

    public void requestUpdateDailyUserInformation(String phoneNumber, String code)
    {
        DailyNetworkAPI.getInstance(mContext).requestDailyUserUpdatePhoneNumber(mNetworkTag, phoneNumber.replaceAll("-", ""), code, mDailyserUpdateVerificationPhoneNumberJsonResponseListener);
    }

    public void requestUpdateSocialUserInformation(String userIndex, String phoneNumber)
    {
        Map<String, String> params = new HashMap<>();
        params.put("user_idx", userIndex);
        params.put("user_phone", phoneNumber.replaceAll("-", ""));

        DailyMobileAPI.getInstance(mContext).requestUserUpdateInformationForSocial(mNetworkTag, params, mUserUpdateSocialJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDailUserVerificationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                String message = response.getString("msg");

                switch (msgCode)
                {
                    case 100:
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onVerification(message);
                        break;
                    }

                    default:
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                        break;
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
                int msgCode = jsonObject.getInt("msgCode");
                String message = jsonObject.getString("msg");

                if (volleyError.networkResponse.statusCode == 422)
                {
                    switch (msgCode)
                    {
                        // 동일한 전화번호로 인증 받은 사용자가 있는 경우
                        case 2001:
                        {
                            JSONObject dataJONObject = jsonObject.getJSONObject("data");
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
                }

                mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onErrorResponse(volleyError);
            }
        }
    };

    private DailyHotelJsonResponseListener mDailyserUpdateVerificationPhoneNumberJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onConfirm();
                } else
                {
                    mOnNetworkControllerListener.onErrorPopupMessage(response.getInt("msgCode"), response.getString("msg"));
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
                int msgCode = jsonObject.getInt("msgCode");
                String message = jsonObject.getString("msg");

                if (volleyError.networkResponse.statusCode == 422)
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
                mOnNetworkControllerListener.onErrorResponse(volleyError);
            }
        }
    };

    private retrofit2.Callback mUserUpdateSocialJsonResponseListener = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                    boolean result = dataJSONObject.getBoolean("is_success");
                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (result == true)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onConfirm();
                    } else
                    {
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, responseJSONObject.getString("msg"));
                    }
                } catch (Exception e)
                {
                    mOnNetworkControllerListener.onError(e);
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
