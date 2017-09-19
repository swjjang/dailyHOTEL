package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

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

        void onUserProfile(Customer customer, String birthday);
    }

    public AddProfileSocialNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestProfile()
    {
        DailyMobileAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserProfileCallback);
    }

    public void requestUpdateSocialUserInformation(String phoneNumber, String email, String name, String recommender, String birthday, boolean isBenefit)
    {
        Map<String, String> params = new HashMap<>();

        if (DailyTextUtils.isTextEmpty(email) == false)
        {
            params.put("user_email", email);
        }

        if (DailyTextUtils.isTextEmpty(name) == false)
        {
            params.put("user_name", name);
        }

        if (DailyTextUtils.isTextEmpty(phoneNumber) == false)
        {
            params.put("user_phone", phoneNumber.replaceAll("-", ""));
        }

        if (DailyTextUtils.isTextEmpty(birthday) == false)
        {
            params.put("birthday", birthday);
        }

        if (DailyTextUtils.isTextEmpty(recommender) == false)
        {
            params.put("recommendation_code", recommender);
        }

        params.put("isAgreedBenefit", isBenefit == true ? "true" : "false");

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
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };

    private retrofit2.Callback mUserProfileCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                        Customer user = new Customer();
                        user.setEmail(jsonObject.getString("email"));
                        user.setName(jsonObject.getString("name"));
                        user.setPhone(jsonObject.getString("phone"));
                        user.setUserIdx(jsonObject.getString("userIdx"));

                        String birthday = null;

                        if (jsonObject.has("birthday") == true && jsonObject.isNull("birthday") == false)
                        {
                            birthday = jsonObject.getString("birthday");
                        }

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(user, birthday);
                    } else
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(null, null);
                    }
                } catch (Exception e)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(null, null);
                }
            } else
            {
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(null, null);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(null, null);
        }
    };
}
