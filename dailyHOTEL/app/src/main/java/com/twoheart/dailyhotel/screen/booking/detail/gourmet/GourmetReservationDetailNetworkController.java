package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.content.Context;

import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class GourmetReservationDetailNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onReviewInformation(Review review);

        void onReservationDetail(JSONObject jsonObject);

        void onEnterOtherUserReservationDetailError(int msgCode, String message);

        void onExpiredSessionError();

        void onReservationDetailError(Throwable throwable);
    }

    public GourmetReservationDetailNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestReviewInformation(int reserveIdx)
    {
        DailyMobileAPI.getInstance(mContext).requestGourmetReviewInformation(mNetworkTag, reserveIdx, mGourmetReviewInformationCallback);
    }

    public void requestGourmetReservationDetail(int reservationIndex)
    {
        DailyMobileAPI.getInstance(mContext).requestGourmetReservationDetail(mNetworkTag, reservationIndex, mReservationBookingDetailCallback);
    }

    private retrofit2.Callback mGourmetReviewInformationCallback = new retrofit2.Callback<JSONObject>()
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
                        Review review = new Review(dataJSONObject);

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation(review);
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
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };

    private retrofit2.Callback mReservationBookingDetailCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = responseJSONObject.getString("msg");

                    switch (msgCode)
                    {
                        case 100:
                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onReservationDetail(jsonObject);
                            break;

                        // 예약 내역 진입시에 다른 사용자가 딥링크로 진입시 예외 처리 추가
                        case 501:
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onEnterOtherUserReservationDetailError(msgCode, message);
                            break;

                        default:
                            mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                            break;
                    }
                } catch (Exception e)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onReservationDetailError(e);
                }
            } else
            {
                if (response != null && response.code() == 401)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onExpiredSessionError();
                } else
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onReservationDetailError(null);
                }
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, true);
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onReservationDetailError(t);
        }
    };
}
