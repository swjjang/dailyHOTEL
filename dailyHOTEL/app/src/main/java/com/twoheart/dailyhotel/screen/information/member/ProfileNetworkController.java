package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.util.Base64;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

public class ProfileNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserProfile(String userIndex, String email, String name, String phoneNumber//
            , boolean isVerified, boolean isPhoneVerified, String verifiedDate);

        void onUserProfileBenefit(boolean isExceedBonus);
    }

    public ProfileNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestUserProfile()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserInProfileJsonResponseListener);
    }

    public void requestUserProfileBenfit()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserInProfileBenefitJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserInProfileJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    String email = jsonObject.getString("email");
                    String name = jsonObject.getString("name");
                    String phone = jsonObject.getString("phone");
                    String userIndex = jsonObject.getString("userIdx");
                    boolean isVerified = jsonObject.getBoolean("verified");
                    boolean isPhoneVerified = jsonObject.getBoolean("phoneVerified");

                    String verifiedDate = null;

                    if (isVerified == true && isPhoneVerified == true)
                    {
                        //                        verifiedDate = Util.simpleDateFormatISO8601toFormat( //
                        //                            jsonObject.getString("phoneVerifiedAt"), "yyyy.MM.dd");

                        verifiedDate = DailyCalendar.convertDateFormatString(jsonObject.getString("phoneVerifiedAt"), DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
                    } else if (isVerified == false && isPhoneVerified == true)
                    {
                        verifiedDate = jsonObject.has("phoneVerifiedAt") == true ? jsonObject.getString("phoneVerifiedAt") : "no date";
                        Crashlytics.logException(new RuntimeException("isVerified : " + isVerified //
                            + " , isPhoneVerified : " + isPhoneVerified + " , verifiedDate : " + verifiedDate //
                            + " , " + Base64.encodeToString(userIndex.getBytes(), Base64.NO_WRAP)));
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(userIndex//
                        , email, name, phone, isVerified, isPhoneVerified, verifiedDate);
                } else
                {
                    mOnNetworkControllerListener.onError(null);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mUserInProfileBenefitJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isExceedBonus = jsonObject.getBoolean("exceedLimitedBonus");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfileBenefit(isExceedBonus);
                } else
                {
                    mOnNetworkControllerListener.onError(null);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };
}
