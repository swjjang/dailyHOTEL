package com.twoheart.dailyhotel.screen.gourmet.preview;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetPreviewNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onGourmetDetailInformation(GourmetDetailParams gourmetDetailParams);

        void onAddWishList(boolean isSuccess, String message);

        void onRemoveWishList(boolean isSuccess, String message);

        void onPlaceReviewScores(PlaceReviewScores placeReviewScores);
    }

    public GourmetPreviewNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestGourmetDetailInformation(int index, String day)
    {
        DailyMobileAPI.getInstance(mContext).requestGourmetDetailInformation(mNetworkTag, //
            index, day, mGourmetDetailCallback);
    }

    public void requestAddWishList(Constants.PlaceType placeType, int placeIndex)
    {
        String type = Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel";

        DailyMobileAPI.getInstance(mContext).requestAddWishList(mNetworkTag, type, placeIndex, mAddWishListCallback);
    }

    public void requestRemoveWishList(Constants.PlaceType placeType, int placeIndex)
    {
        String type = Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel";

        DailyMobileAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, type, placeIndex, mRemoveWishListCallback);
    }

    public void requestPlaceReviewScores(Constants.PlaceType placeType, int placeIndex)
    {
        String type = Constants.PlaceType.FNB.equals(placeType) ? "gourmet" : "hotel";

        DailyMobileAPI.getInstance(mContext).requestPlaceReviewScores(mNetworkTag, type, placeIndex, mPlaceReviewScoresCallback);
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

    private retrofit2.Callback mAddWishListCallback = new retrofit2.Callback<JSONObject>()
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
                    boolean isSuccess = msgCode == 100;

                    String message = null;
                    if (responseJSONObject.has("msg") == true)
                    {
                        message = responseJSONObject.getString("msg");
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onAddWishList(isSuccess, message);
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

    private retrofit2.Callback mRemoveWishListCallback = new retrofit2.Callback<JSONObject>()
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
                    boolean isSuccess = msgCode == 100;

                    String message = null;
                    if (responseJSONObject.has("msg") == true)
                    {
                        message = responseJSONObject.getString("msg");
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onRemoveWishList(isSuccess, message);
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

    private retrofit2.Callback mPlaceReviewScoresCallback = new retrofit2.Callback<BaseDto<PlaceReviewScores>>()
    {
        @Override
        public void onResponse(Call<BaseDto<PlaceReviewScores>> call, Response<BaseDto<PlaceReviewScores>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<PlaceReviewScores> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(baseDto.data);
                    } else
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(new PlaceReviewScores());
                    }
                } catch (Exception e)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(new PlaceReviewScores());
                }
            } else
            {
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(new PlaceReviewScores());
            }
        }

        @Override
        public void onFailure(Call<BaseDto<PlaceReviewScores>> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(new PlaceReviewScores());
        }
    };
}
