package com.twoheart.dailyhotel.screen.information;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

/**
 * Created by Sam Lee on 2016. 5. 19..
 */
public class InformationNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserInformation(String type, String email, String name, String recommender, int bonus, int couponTotalCount, boolean isAgreedBenefit, boolean isExceedBonus);

        void onPushBenefitMessage(String message);

        void onBenefitAgreement(boolean isAgree, String updateDate);
    }

    public InformationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }


    public void requestUserInformation()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserInformation(mNetworkTag, mUserInformationJsonResponseListener, this);
    }

    public void requestPushBenefitText()
    {
        DailyNetworkAPI.getInstance(mContext).requestBenefitMessage(mNetworkTag, mBenefitMessageJsonResponseListener);
    }

    public void requestPushBenefit(boolean isAuthorization, boolean isAgree)
    {
        DailyNetworkAPI.getInstance(mContext).requestUpdateBenefitAgreement(mNetworkTag, isAuthorization, isAgree, mUpdateBenefitJsonResponseListener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    /**
     * 쿠폰 갯수 적립금 등
     */
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
                String ownRecommender = response.getString("rndnum"); // 자신의 추천 번호
                String userType = response.getString("user_type");
                int bonus = response.getInt("bonus");
                int couponTotalCount = response.getInt("coupon_total_count");
                boolean isAgreedBenefit = response.getBoolean("is_agreed_benefit");
                boolean isExceedBonus = response.getBoolean("is_exceed_bonus");

                DailyPreference.getInstance(mContext).setUserExceedBonus(isExceedBonus);

                ((OnNetworkControllerListener) mOnNetworkControllerListener) //
                    .onUserInformation(userType, email, name, ownRecommender, bonus, //
                        couponTotalCount, isAgreedBenefit, isExceedBonus);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
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
            InformationNetworkController.this.onErrorResponse(volleyError);
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
            InformationNetworkController.this.onErrorResponse(volleyError);
        }
    };
}
