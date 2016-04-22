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

        void onFailed(String message);
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

    public void requestUpdateDailyUserInformation(String name, String phoneNumber)
    {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("phone", phoneNumber.replaceAll("-", ""));

        DailyNetworkAPI.getInstance().requestUserInformationUpdate(mNetworkTag, params, mUserUpdateJsonResponseListener, this);
    }

    public void requestUpdateSocialUserInformation(String userIndex, String phoneNumber)
    {
        Map<String, String> params = new HashMap<>();
        params.put("user_idx", userIndex);
        params.put("user_phone", phoneNumber.replaceAll("-", ""));

        DailyNetworkAPI.getInstance().requestUserUpdateInformationForSocial(mNetworkTag, params, mUserUpdateSocialJsonResponseListener, this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                String result = response.getString("success");

                if (result.equals("true") == true)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onConfirm();
                } else
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onFailed(response.getString("msg"));
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserUpdateSocialJsonResponseListener = new DailyHotelJsonResponseListener()
    {
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
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onFailed(response.getString("msg"));
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}
