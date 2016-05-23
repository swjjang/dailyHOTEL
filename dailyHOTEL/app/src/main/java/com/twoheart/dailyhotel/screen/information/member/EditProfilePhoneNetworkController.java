package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
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

        DailyNetworkAPI.getInstance(mContext).requestUserUpdateInformationForSocial(mNetworkTag, params, mUserUpdateSocialJsonResponseListener, this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDailUserVerificationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
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
        public void onResponse(String url, JSONObject response)
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

    private DailyHotelJsonResponseListener mUserUpdateSocialJsonResponseListener = new DailyHotelJsonResponseListener()
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
                int msgCode = response.getInt("msg_code");

                if (result == true)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onConfirm();
                } else
                {
                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}
