package com.twoheart.dailyhotel.screen.information;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

/**
 * Created by Sam Lee on 2016. 5. 19..
 */
public class InformationNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserProfile(String type, String email, String name, String recommender, boolean isAgreedBenefit);

        void onUserProfileBenefit(int bonus, int couponTotalCount, boolean isExceedBonus);

        void onPushBenefitMessage(String message);

        void onBenefitAgreement(boolean isAgree, String updateDate);
    }

    public InformationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestUserProfile()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserProfileJsonResponseListener);
    }

    public void requestUserProfileBenefit()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserProfileBenefitJsonResponseListener);
    }

    public void requestPushBenefitText()
    {
        DailyNetworkAPI.getInstance(mContext).requestBenefitMessage(mNetworkTag, mBenefitMessageJsonResponseListener);
    }

    public void requestPushBenefit(boolean isAgree)
    {
        DailyNetworkAPI.getInstance(mContext).requestUpdateBenefitAgreement(mNetworkTag, isAgree, mUpdateBenefitJsonResponseListener);
    }

    /**
     * 쿠폰 갯수 적립금 등
     */
    private DailyHotelJsonResponseListener mUserProfileJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    String referralCode = jsonObject.getString("referralCode"); // 자신의 추천 번호
                    String userType = jsonObject.getString("userType");
                    boolean isAgreedBenefit = jsonObject.getBoolean("agreedBenefit");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(userType, email, name, referralCode, isAgreedBenefit);
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
                    int couponTotalCount = jsonObject.getInt("couponTotalCount");
                    boolean isExceedBonus = jsonObject.getBoolean("exceedLimitedBonus");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfileBenefit(bonus, couponTotalCount, isExceedBonus);
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

    /**
     * 혜택 알림 - 문구
     */
    private DailyHotelJsonResponseListener mBenefitMessageJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject data = response.getJSONObject("data");

                    String message = data.getString("body");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onPushBenefitMessage(message);
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

    /**
     * 혜택 알림 - 설정 상태 업데이트!
     */
    private DailyHotelJsonResponseListener mUpdateBenefitJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {

                    JSONObject dataJSONObject = response.getJSONObject("data");
                    String serverDate = dataJSONObject.getString("serverDate");

                    boolean isAgreed = Uri.parse(url).getBooleanQueryParameter("isAgreed", false);

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onBenefitAgreement(isAgreed, Util.simpleDateFormatISO8601toFormat(serverDate, "yyyy년 MM월 dd일"));
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
}
