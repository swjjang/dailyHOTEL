package com.twoheart.dailyhotel.screen.common;

import android.content.Context;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HappyTalkCategoryDialogNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onHappyTalkCategory(String happyTalkCategory);

        void onUserProfile(String userIndex, String name, String phone, String email);

        void onCommonDateTime(TodayDateTime todayDateTime);
    }

    public HappyTalkCategoryDialogNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestHappyTalkCategory()
    {
        DailyMobileAPI.getInstance(mContext).requestHappyTalkCategory(mNetworkTag, mHappyTalkCategoryCallback);
    }

    public void requestUserProfile()
    {
        DailyMobileAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserProfileCallback);
    }

    public void requestCommonDateTime()
    {
        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, mDateTimeCallback);
    }

    private retrofit2.Callback mHappyTalkCategoryCallback = new Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject jsonObjectData = response.body();

                    if ("success".equalsIgnoreCase(jsonObjectData.getString("code")) == true)
                    {
                        JSONObject jsonObjectResults = jsonObjectData.getJSONObject("results");
                        JSONArray jsonArray = jsonObjectResults.getJSONArray("assign");

                        String happyTalkCategory = jsonArray.toString();
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onHappyTalkCategory(happyTalkCategory);
                    } else
                    {
                        mOnNetworkControllerListener.onError(null);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());

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

    private retrofit2.Callback mUserProfileCallback = new Callback<JSONObject>()
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

                        String userIndex = jsonObject.getString("userIdx");
                        String name = jsonObject.getString("name");
                        String phone = jsonObject.getString("phone");
                        String email = jsonObject.getString("email");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfile(userIndex, name, phone, email);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());

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

    private retrofit2.Callback mDateTimeCallback = new retrofit2.Callback<BaseDto<TodayDateTime>>()
    {
        @Override
        public void onResponse(Call<BaseDto<TodayDateTime>> call, Response<BaseDto<TodayDateTime>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<TodayDateTime> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onCommonDateTime(baseDto.data);
                    } else
                    {
                        mOnNetworkControllerListener.onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
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
        public void onFailure(Call<BaseDto<TodayDateTime>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(t);
        }
    };
}