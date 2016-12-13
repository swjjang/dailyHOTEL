package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.util.Base64;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserProfile(String userIndex, String email, String name, String phoneNumber, String birthday//
            , String referralCode, boolean isVerified, boolean isPhoneVerified, String verifiedDate);

        void onUserProfileBenefit(boolean isExceedBonus);
    }

    public ProfileNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestUserProfile()
    {
        DailyMobileAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserInProfileCallback);
    }

    public void requestUserProfileBenfit()
    {
        DailyMobileAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserInProfileBenefitCallback);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mUserInProfileCallback = new retrofit2.Callback<JSONObject>()
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

                        String email = jsonObject.getString("email");
                        String name = jsonObject.getString("name");
                        String phone = jsonObject.getString("phone");
                        String userIndex = jsonObject.getString("userIdx");
                        boolean isVerified = jsonObject.getBoolean("verified");
                        boolean isPhoneVerified = jsonObject.getBoolean("phoneVerified");
                        String birthday = null;

                        if (jsonObject.has("birthday") == true)
                        {
                            birthday = jsonObject.getString("birthday");
                        }

                        String referralCode = jsonObject.getString("referralCode");

                        String verifiedDate = null;

                        if (isVerified == true && isPhoneVerified == true)
                        {
                            //                        verifiedDate = Util.simpleDateFormatISO8601toFormat( //
                            //                            jsonObject.getString("phoneVerifiedAt"), "yyyy.MM.dd");
                            try
                            {
                                verifiedDate = DailyCalendar.convertDateFormatString(jsonObject.getString("phoneVerifiedAt"), DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
                            } catch (NullPointerException e)
                            {
                                verifiedDate = null;
                            }
                        } else if (isVerified == false && isPhoneVerified == true)
                        {
                            verifiedDate = jsonObject.has("phoneVerifiedAt") == true ? jsonObject.getString("phoneVerifiedAt") : "no date";

                            if (Constants.DEBUG == false)
                            {
                                Crashlytics.logException(new RuntimeException("isVerified : " + isVerified //
                                    + " , isPhoneVerified : " + isPhoneVerified + " , verifiedDate : " + verifiedDate //
                                    + " , " + Base64.encodeToString(userIndex.getBytes(), Base64.NO_WRAP)));
                            }
                        }

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(userIndex//
                            , email, name, phone, birthday, referralCode, isVerified, isPhoneVerified, verifiedDate);
                    } else
                    {
                        String msg = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorToastMessage(msg);
                    }
                } catch (ParseException e)
                {
                    if (Constants.DEBUG == false)
                    {
                        Crashlytics.log("Url: " + call.request().url());
                    }

                    mOnNetworkControllerListener.onError(e);
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

    private retrofit2.Callback mUserInProfileBenefitCallback = new retrofit2.Callback<JSONObject>()
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
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        boolean isExceedBonus = dataJSONObject.getBoolean("exceedLimitedBonus");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfileBenefit(isExceedBonus);
                    } else
                    {
                        String msg = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorToastMessage(msg);
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
