package com.twoheart.dailyhotel.screen.review;

import android.content.Context;

import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

public class ReviewNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onAddReviewInformation(String grade);

        void onAddReviewDetailInformation();
    }

    public ReviewNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAddReviewInformation(JSONObject jsonObject)
    {
        DailyMobileAPI.getInstance(mContext).requestAddReviewInformation(mNetworkTag, jsonObject, mAddReviewCallback);
    }

    public void requestAddReviewDetailInformation(JSONObject jsonObject)
    {
        DailyMobileAPI.getInstance(mContext).requestAddReviewDetailInformation(mNetworkTag, jsonObject, mAddReviewDetailCallback);
    }

    private retrofit2.Callback mAddReviewCallback = new retrofit2.Callback<JSONObject>()
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
                        JSONObject requestJSONObject = new JSONObject(bodyToString(call.request().body()));

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onAddReviewInformation(requestJSONObject.getString("grade"));
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
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

        private String bodyToString(final RequestBody request)
        {
            try
            {
                Buffer buffer = new Buffer();

                request.writeTo(buffer);
                return buffer.readUtf8();
            } catch (IOException e)
            {
                ExLog.d(e.toString());
            }

            return null;
        }
    };

    private retrofit2.Callback mAddReviewDetailCallback = new retrofit2.Callback<JSONObject>()
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
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onAddReviewDetailInformation();
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
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
}
