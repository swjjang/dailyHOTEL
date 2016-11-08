package com.twoheart.dailyhotel.place.networkcontroller;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

import java.util.Map;

public abstract class PlaceDetailNetworkController extends BaseNetworkController
{
    public PlaceDetailNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCommonDateTime(long currentDateTime, long dailyDateTime);

        //        void onUserInformation(Customer user, String birthday, boolean isDailyUser);

        void onUserProfile(Customer user, String birthday, boolean isDailyUser, boolean isVerified, boolean isPhoneVerified);

        void onAddWishList(boolean isSuccess, String message);

        void onRemoveWishList(boolean isSuccess, String message);

    }

    public void requestCommonDatetime()
    {
        DailyNetworkAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, mDateTimeJsonResponseListener);
    }

    public void requestProfile()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserProfileJsonResponseListener);
    }

    public void requestAddWishList(Constants.PlaceType placeType, int placeIndex)
    {
        DailyNetworkAPI.getInstance(mContext).requestAddWishList(mNetworkTag, placeType, placeIndex, mAddWishListJsonResponseListener);
    }

    public void requestRemoveWishList(Constants.PlaceType placeType, int placeIndex)
    {
        DailyNetworkAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, placeType, placeIndex, mRemoveWishListJsonResponseListener);
    }

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                    long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onCommonDateTime(currentDateTime, dailyDateTime);
                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
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

    private DailyHotelJsonResponseListener mUserProfileJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

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

                    String userType = jsonObject.getString("userType");
                    boolean isDailyUser = Constants.DAILY_USER.equalsIgnoreCase(userType);

                    boolean isVerified = jsonObject.getBoolean("verified");
                    boolean isPhoneVerified = jsonObject.getBoolean("phoneVerified");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(user, birthday, isDailyUser, isVerified, isPhoneVerified);
                } else
                {
                    String msg = response.getString("msg");
                    mOnNetworkControllerListener.onErrorToastMessage(msg);
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

    private DailyHotelJsonResponseListener mAddWishListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                boolean isSuccess = msgCode == 100 ? true : false;

                String message = null;
                if (response.has("msg") == true)
                {
                    message = response.getString("msg");
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onAddWishList(isSuccess, message);
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

    private DailyHotelJsonResponseListener mRemoveWishListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                boolean isSuccess = msgCode == 100 ? true : false;

                String message = null;
                if (response.has("msg") == true)
                {
                    message = response.getString("msg");
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onRemoveWishList(isSuccess, message);
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
