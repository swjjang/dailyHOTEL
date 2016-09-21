package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Bonus;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.DailyPreference;

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

    public void requestProfileBenefit()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserProfileBenefitJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserBonusListResponseListener = new DailyHotelJsonResponseListener()
    {
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

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
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
                    DailyNetworkAPI.getInstance(mContext).requestUserBonus(mNetworkTag, mUserBonusListResponseListener);
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

                    int bonus = jsonObject.getInt("bonusAmount");
                    boolean isExceedBonus = jsonObject.getBoolean("exceedLimitedBonus");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onBonus(bonus);

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
}