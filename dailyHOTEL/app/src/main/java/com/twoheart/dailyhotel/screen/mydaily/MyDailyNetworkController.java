package com.twoheart.dailyhotel.screen.mydaily;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Sam Lee on 2016. 5. 19..
 */
public class MyDailyNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserProfile(String type, String email, String name, String birthday, String recommender, boolean isAgreedBenefit);

        void onUserProfileBenefit(int bonus, int couponTotalCount, boolean isExceedBonus);

        void onPushBenefitMessage(String message);

        void onBenefitAgreement(boolean isAgree, String updateDate);
    }

    public MyDailyNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestUserProfile()
    {
        DailyMobileAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserProfileCallback);
    }

    public void requestUserProfileBenefit()
    {
        DailyMobileAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserProfileBenefitCallback);
    }

    public void requestPushBenefitText()
    {
        DailyMobileAPI.getInstance(mContext).requestBenefitMessage(mNetworkTag, mBenefitMessageCallback);
    }

    public void requestPushBenefit(boolean isAgree)
    {
        DailyMobileAPI.getInstance(mContext).requestUpdateBenefitAgreement(mNetworkTag, isAgree, mUpdateBenefitCallback);
    }

    /**
     * 쿠폰 갯수 적립금 등
     */
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

                        String email = jsonObject.getString("email");
                        String name = jsonObject.getString("name");
                        String birthday = jsonObject.getString("birthday");
                        String referralCode = jsonObject.getString("referralCode"); // 자신의 추천 번호
                        String userType = jsonObject.getString("userType");
                        boolean isAgreedBenefit = jsonObject.getBoolean("agreedBenefit");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(userType, email, name, birthday, referralCode, isAgreedBenefit);
                    } else
                    {
                        String msg = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorToastMessage(msg);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                ExLog.e(response.toString());

                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };

    private retrofit2.Callback mUserProfileBenefitCallback = new retrofit2.Callback<JSONObject>()
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

                        int bonus = dataJSONObject.getInt("bonusAmount");
                        int couponTotalCount = dataJSONObject.getInt("couponTotalCount");
                        boolean isExceedBonus = dataJSONObject.getBoolean("exceedLimitedBonus");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfileBenefit(bonus, couponTotalCount, isExceedBonus);
                    } else
                    {
                        String msg = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorToastMessage(msg);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                ExLog.e(response.toString());

                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };

    /**
     * 혜택 알림 - 문구
     */
    private retrofit2.Callback mBenefitMessageCallback = new retrofit2.Callback<JSONObject>()
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

                        String message = dataJSONObject.getString("body");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onPushBenefitMessage(message);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                ExLog.e(response.toString());

                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };

    /**
     * 혜택 알림 - 설정 상태 업데이트!
     */
    private retrofit2.Callback mUpdateBenefitCallback = new retrofit2.Callback<JSONObject>()
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
                        String serverDate = dataJSONObject.getString("serverDate");

                        boolean isAgreed = Boolean.parseBoolean(call.request().url().queryParameter("isAgreed"));

                        //                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onBenefitAgreement(isAgreed, Util.simpleDateFormatISO8601toFormat(serverDate, "yyyy년 MM월 dd일"));
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onBenefitAgreement(isAgreed, DailyCalendar.convertDateFormatString(serverDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일"));
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
                } catch (ParseException e)
                {
                    Crashlytics.log("Call url: " + call.request().url().toString());
                    Crashlytics.logException(e);
                    mOnNetworkControllerListener.onError(e);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                ExLog.e(response.toString());

                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };
}
