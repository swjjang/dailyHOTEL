package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class AddProfileSocialNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUpdateSocialUserInformation(String message, String agreedDate);
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

        params.put("isAgreedBenefit", isBenefit == true ? "true" : "false");

        if (Constants.DEBUG == false)
        {
            if (Util.isTextEmpty(name) == true)
            {
                Crashlytics.log("AddProfileSocialNetworkController::requestUpdateSocialUserInformation :: name="//
                    + name + " , userIndex=" + userIndex + " , user_email=" + email);
            }
        }

        DailyMobileAPI.getInstance(mContext).requestUserUpdateInformationForSocial(mNetworkTag, params, mUserUpdateFacebookCallback);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mUserUpdateFacebookCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        boolean result = dataJSONObject.getBoolean("is_success");
                        String serverDate = dataJSONObject.getString("serverDate");

                        if (result == true)
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onUpdateSocialUserInformation(null, serverDate);
                        } else
                        {
                            String message = responseJSONObject.getString("msg");

                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onUpdateSocialUserInformation(message, null);
                        }
                    } else
                    {
                        String message = responseJSONObject.getString("msg");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUpdateSocialUserInformation(message, null);
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
