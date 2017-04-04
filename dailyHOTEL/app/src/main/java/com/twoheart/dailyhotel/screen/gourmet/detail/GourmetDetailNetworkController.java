package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetDetailNetworkController extends PlaceDetailNetworkController
{
    public interface OnNetworkControllerListener extends PlaceDetailNetworkController.OnNetworkControllerListener
    {
        void onGourmetDetailInformation(GourmetDetailParams gourmetDetailParams);

        void onHasCoupon(boolean hasCoupon);
    }

    public GourmetDetailNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestGourmetDetailInformation(String day, int index)
    {
        DailyMobileAPI.getInstance(mContext).requestGourmetDetailInformation(mNetworkTag, //
            index, day, mGourmetDetailCallback);
    }

    public void requestHasCoupon(int placeIndex, String date)
    {
        DailyMobileAPI.getInstance(mContext).requestHasCoupon(mNetworkTag, placeIndex, date, mHasCouponCallback);
    }

    private retrofit2.Callback mGourmetDetailCallback = new retrofit2.Callback<BaseDto<GourmetDetailParams>>()
    {
        @Override
        public void onResponse(Call<BaseDto<GourmetDetailParams>> call, Response<BaseDto<GourmetDetailParams>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<GourmetDetailParams> baseDto = response.body();

                    if (baseDto.msgCode == 100 && baseDto.data == null)
                    {
                        baseDto.msgCode = 4;
                    }

                    // 100	성공
                    // 4	데이터가 없을시
                    // 5	판매 마감시
                    switch (baseDto.msgCode)
                    {
                        case 100:
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetDetailInformation(baseDto.data);
                            break;

                        case 5:
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetDetailInformation(baseDto.data);

                            mOnNetworkControllerListener.onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
                            break;
                        }

                        case 4:
                        default:
                        {
                            mOnNetworkControllerListener.onErrorToastMessage(baseDto.msg);
                            break;
                        }
                    }
                } catch (Exception e)
                {
                    if (Constants.DEBUG == false)
                    {
                        Crashlytics.log(call.request().url().toString());
                    }

                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<GourmetDetailParams>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };

    private retrofit2.Callback mHasCouponCallback = new retrofit2.Callback<JSONObject>()
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
                        boolean hasCoupon = dataJSONObject.getBoolean("existCoupons");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onHasCoupon(hasCoupon);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onHasCoupon(false);
                }
            } else
            {
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onHasCoupon(false);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, true);
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onHasCoupon(false);
        }
    };
}
