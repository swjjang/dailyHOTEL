package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Bonus;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BonusNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserInformation(String name, String recommendCode, boolean isExceedBonus);

        void onBonusHistoryList(List<Bonus> list);

        void onBonus(int bonus);
    }

    public BonusNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestBonus()
    {
        DailyNetworkAPI.getInstance(mContext).requestBonus(mNetworkTag, mReserveSavedMoneyStringResponseListener, mErrorListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserBonusListResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            //적립금 내역리스트
            try
            {
                JSONArray jsonArray = response.getJSONArray("history");
                int length = jsonArray.length();

                List<Bonus> list = new ArrayList<>();

                for (int i = 0; i < length; i++)
                {
                    JSONObject historyObj = jsonArray.getJSONObject(i);

                    String content = historyObj.getString("content");
                    String expires = historyObj.getString("expires");
                    int bonus = historyObj.getInt("bonus");

                    list.add(new Bonus(content, bonus, expires));
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onBonusHistoryList(list);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserProfileJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    String recommendCode = jsonObject.getString("referralCode");
                    String name = jsonObject.getString("name");
                    boolean isExceedBonus = DailyPreference.getInstance(mContext).isUserExceedBonus();

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserInformation(recommendCode, name, isExceedBonus);

                    // 적립금 목록 요청.
                    DailyNetworkAPI.getInstance(mContext).requestUserBonus(mNetworkTag, mUserBonusListResponseListener, mErrorListener);
                } else
                {

                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserProfileBenefitJsonResponseListener = new DailyHotelJsonResponseListener()
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

                    DailyPreference.getInstance(mContext).setUserExceedBonus(isExceedBonus);

                    DailyNetworkAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserProfileJsonResponseListener);
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

    private DailyHotelStringResponseListener mReserveSavedMoneyStringResponseListener = new DailyHotelStringResponseListener()
    {
        @Override
        public void onResponse(String url, String response)
        {
            try
            {
                String result = null;

                if (false == Util.isTextEmpty(response))
                {
                    result = response.trim();
                }

                int bonus = 0;

                try
                {
                    bonus = Integer.parseInt(result);
                } catch (NumberFormatException e)
                {
                    ExLog.d(e.toString());
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onBonus(bonus);

                // 사용자 정보 요청.
                DailyNetworkAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserProfileBenefitJsonResponseListener);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };

    private Response.ErrorListener mErrorListener = new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };
}