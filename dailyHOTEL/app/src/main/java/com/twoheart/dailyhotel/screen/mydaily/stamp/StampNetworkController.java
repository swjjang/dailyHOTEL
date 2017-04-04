package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.content.Context;
import android.net.ParseException;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.Stamp;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class StampNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onBenefitAgreement(boolean isAgree, String updateDate);

        void onUserStamps(Stamp stamp);
    }

    public StampNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestUserStamps(boolean details)
    {
        DailyMobileAPI.getInstance(mContext).requestUserStamps(mNetworkTag, details, mStampCallback);
    }

    public void requestPushBenefit(boolean isAgree)
    {
        DailyMobileAPI.getInstance(mContext).requestUpdateBenefitAgreement(mNetworkTag, isAgree, mUpdateBenefitCallback);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mStampCallback = new retrofit2.Callback<BaseDto<Stamp>>()
    {
        @Override
        public void onResponse(Call<BaseDto<Stamp>> call, Response<BaseDto<Stamp>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                BaseDto<Stamp> baseDto = response.body();

                if (baseDto.msgCode == 100)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserStamps(baseDto.data);
                } else
                {
                    mOnNetworkControllerListener.onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<Stamp>> call, Throwable t)
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
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };
}