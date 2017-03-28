package com.twoheart.dailyhotel.place.networkcontroller;

import android.content.Context;

import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.PlaceReviews;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

import retrofit2.Call;
import retrofit2.Response;

public class PlaceReviewNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onReviews(PlaceReviews placeReviews);
    }

    public PlaceReviewNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestPlaceReviews(Constants.PlaceType placeType, int placeIndex, int page, int limit)
    {
        String type = Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel";

        placeIndex = 1087;

        DailyMobileAPI.getInstance(mContext).requestPlaceReviews(mNetworkTag, type, placeIndex, page, limit, mPlaceReviewsCallback);
    }


    private retrofit2.Callback mPlaceReviewsCallback = new retrofit2.Callback<BaseDto<PlaceReviews>>()
    {
        @Override
        public void onResponse(Call<BaseDto<PlaceReviews>> call, Response<BaseDto<PlaceReviews>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<PlaceReviews> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviews(baseDto.data);
                    } else
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviews(null);
                    }
                } catch (Exception e)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviews(null);
                }
            } else
            {
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviews(null);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<PlaceReviews>> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviews(null);
        }
    };
}
