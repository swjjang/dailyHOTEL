package com.twoheart.dailyhotel.screen.information;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Sam Lee on 2016. 5. 19..
 */
public class InformationNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserProfile(String type, String email, String name, String birthday, String recommender, boolean isAgreedBenefit);

        void onUserProfileBenefit(int bonus, int couponTotalCount, boolean isExceedBonus);

        void onPushBenefitMessage(String message);

        void onBenefitAgreement(boolean isAgree, String updateDate);

        void onReviewGourmet(Review review);

        void onReviewStay(Review review);
    }

    public InformationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
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

    protected void requestReviewGourmet()
    {
        DailyMobileAPI.getInstance(mContext).requestGourmetReviewInformation(mNetworkTag, mReviewGourmetCallback);
    }

    protected void requestReviewStay()
    {
        DailyMobileAPI.getInstance(mContext).requestStayReviewInformation(mNetworkTag, mReviewStayCallback);
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
                    if (Constants.DEBUG == false)
                    {
                        Crashlytics.log("Url: " + call.request().url().toString());
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

    private retrofit2.Callback mReviewGourmetCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            Review review = null;

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    // 리뷰가 존재하지 않는 경우 msgCode : 701
                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100 && responseJSONObject.has("data") == true)
                    {
                        review = new Review(responseJSONObject.getJSONObject("data"));


                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewGourmet(review);
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewGourmet(null);
        }
    };

    private retrofit2.Callback mReviewStayCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            Review review = null;

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    // 리뷰가 존재하지 않는 경우 msgCode : 701
                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100 && responseJSONObject.has("data") == true)
                    {
                        review = new Review(responseJSONObject.getJSONObject("data"));
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewStay(review);
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewStay(null);
        }
    };
}
