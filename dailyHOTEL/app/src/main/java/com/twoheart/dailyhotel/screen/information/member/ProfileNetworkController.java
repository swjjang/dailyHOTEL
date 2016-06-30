package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.util.Base64;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

public class ProfileNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserInformation(String userIndex, String email, String name, String phoneNumber//
            , boolean isVerified, boolean isPhoneVerified, String verifiedDate, boolean isExceedBonus);
    }

    public ProfileNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestUserInformation()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserInformation(mNetworkTag, mUserInformationJsonResponseListener, mUserInformationJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
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
                String email = response.getString("email");
                String name = response.getString("name");
                String phone = response.getString("phone");
                String userIndex = response.getString("idx");
                boolean isVerified = response.getBoolean("is_verified");
                boolean isPhoneVerified = response.getBoolean("is_phone_verified");
                boolean isExceedBonus = response.getBoolean("is_exceed_bonus");

                DailyPreference.getInstance(mContext).setUserExceedBonus(isExceedBonus);

                String verifiedDate = null;

                if (isVerified == true && isPhoneVerified == true)
                {
                    verifiedDate = Util.simpleDateFormatISO8601toFormat( //
                        response.getString("phone_verified_at"), "yyyy.MM.dd");
                } else if (isVerified == false && isPhoneVerified == true)
                {
                    verifiedDate = response.has("phone_verified_at") == true ? response.getString("phone_verified_at") : "no date";
                    Crashlytics.logException(new RuntimeException("isVerified : " + isVerified //
                        + " , isPhoneVerified : " + isPhoneVerified + " , verifiedDate : " + verifiedDate //
                        + " , " + Base64.encodeToString(userIndex.getBytes(), Base64.NO_WRAP)));
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserInformation(userIndex//
                    , email, name, phone, isVerified, isPhoneVerified, verifiedDate, isExceedBonus);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}
