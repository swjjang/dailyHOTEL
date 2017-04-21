package com.twoheart.dailyhotel.screen.hotel.preview;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class StayPreviewNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayDetailInformation(StayDetailParams stayDetailParams);

        void onAddWishList(boolean isSuccess, String message);

        void onRemoveWishList(boolean isSuccess, String message);

        void onPlaceReviewScores(PlaceReviewScores placeReviewScores);
    }

    public StayPreviewNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStayDetailInformation(int placeIndex, String day, int nights)
    {
        DailyMobileAPI.getInstance(mContext).requestStayDetailInformation(mNetworkTag, placeIndex, //
            day, nights, mStayDetailInformationCallback);
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
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(null);
                    }
                } catch (Exception e)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(null);
                }
            } else
            {
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(null);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<PlaceReviewScores>> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onPlaceReviewScores(null);
        }
    };
}
