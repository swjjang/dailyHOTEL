package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserInformation(String userIndex, String email, String name, String phoneNumber, boolean isVerified, boolean isPhoneVerified, String verifiedDate);
    }

    public ProfileNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    public void requestUserInformation()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserInformation(mNetworkTag, mUserInformationJsonResponseListener, this);
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

                String verifiedDate = null;

                if (isVerified == true && isPhoneVerified == true)
                {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
                    Date date = simpleDateFormat.parse(response.getString("phone_verified_at"));

                    SimpleDateFormat verifiedSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    verifiedDate = verifiedSimpleDateFormat.format(date);
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserInformation(userIndex, email, name, phone, isVerified, isPhoneVerified, verifiedDate);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}
