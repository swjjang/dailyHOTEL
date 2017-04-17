package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class StayDetailNetworkController extends PlaceDetailNetworkController
{
    public interface OnNetworkControllerListener extends PlaceDetailNetworkController.OnNetworkControllerListener
    {
        void onStayDetailInformation(StayDetailParams stayDetailParams);

        void onHasCoupon(boolean hasCoupon);
    }

    public StayDetailNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStayDetailInformation(int placeIndex, String day, int nights)
    {
        DailyMobileAPI.getInstance(mContext).requestStayDetailInformation(mNetworkTag, placeIndex, //
            day, nights, mStayDetailInformationCallback);
    }

    public void requestHasCoupon(int placeIndex, String date, int nights)
    {
        DailyMobileAPI.getInstance(mContext).requestHasCoupon(mNetworkTag, placeIndex, date, nights, mHasCouponCallback);
    }

    private retrofit2.Callback mStayDetailInformationCallback = new retrofit2.Callback<BaseDto<StayDetailParams>>()
    {
        @Override
        public void onResponse(Call<BaseDto<StayDetailParams>> call, Response<BaseDto<StayDetailParams>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<StayDetailParams> baseDto = response.body();

                    int msgCode = baseDto.msgCode;

                    StayDetailParams stayDetailParams = baseDto.data;

                    if (msgCode == 100 && stayDetailParams == null)
                    {
                        msgCode = 4;
                    }

                    // 100	성공
                    // 4	데이터가 없을시
                    // 5	판매 마감시
                    switch (msgCode)
                    {
                        case 100:
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayDetailInformation(stayDetailParams);
                            break;

                        case 5:
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayDetailInformation(stayDetailParams);

                            if (DailyTextUtils.isTextEmpty(baseDto.msg) == false)
                            {
                                mOnNetworkControllerListener.onErrorPopupMessage(msgCode, baseDto.msg);
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
                            break;
                        }

                        case 4:
                        default:
                        {
                            if (DailyTextUtils.isTextEmpty(baseDto.msg) == false)
                            {
                                mOnNetworkControllerListener.onErrorToastMessage(baseDto.msg);
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
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
        public void onFailure(Call<BaseDto<StayDetailParams>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(t);
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
