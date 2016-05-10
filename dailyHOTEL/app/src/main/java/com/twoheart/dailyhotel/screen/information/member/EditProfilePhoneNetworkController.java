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

        void onConfirm();
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

    public void requestDailyUserVerification(String phoneNumber)
    {
        DailyNetworkAPI.getInstance(mContext).requestDailyUserVerfication(mNetworkTag, phoneNumber, mDailUserVerificationJsonResponseListener);
    }

    public void requestUpdateDailyUserInformation(String phoneNumber, String code)
    {
        DailyNetworkAPI.getInstance(mContext).requestDailyUserUpdatePhoneNumber(mNetworkTag, phoneNumber, code, mDailyserUpdatePhoneNumberJsonResponseListener);
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

    private DailyHotelJsonResponseListener mDailyserUpdatePhoneNumberJsonResponseListener = new DailyHotelJsonResponseListener()
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
                mOnNetworkControllerListener.onErrorPopupMessage(jsonObject.getInt("msgCode"), jsonObject.getString("msg"));
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
                    mOnNetworkControllerListener.onErrorToastMessage(response.getString("msg"));
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}
