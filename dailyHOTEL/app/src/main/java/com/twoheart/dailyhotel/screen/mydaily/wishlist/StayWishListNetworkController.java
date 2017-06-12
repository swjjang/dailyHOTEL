package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.content.Context;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.PlaceWishItems;
import com.twoheart.dailyhotel.network.model.StayWishItem;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class StayWishListNetworkController extends BaseNetworkController
{
    public StayWishListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayWishList(ArrayList<Stay> list);

        void onRemoveStayWishListItem(boolean isSuccess, String message, int placeIndex);
    }

    public void requestStayWishList()
    {
        DailyMobileAPI.getInstance(mContext).requestStayWishList(mNetworkTag, mWishListCallback);
    }

    public void requestRemoveStayWishListItem(int placeIndex)
    {
        DailyMobileAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, //
            "hotel", placeIndex, mRemoveWishListCallback);
    }


    private retrofit2.Callback mWishListCallback = new retrofit2.Callback<BaseDto<PlaceWishItems<StayWishItem>>>()
    {
        @Override
        public void onResponse(Call<BaseDto<PlaceWishItems<StayWishItem>>> call, Response<BaseDto<PlaceWishItems<StayWishItem>>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<PlaceWishItems<StayWishItem>> baseDto = response.body();

                    int msgCode = baseDto.msgCode;
                    if (msgCode == 100)
                    {
                        PlaceWishItems<StayWishItem> placeWishItems = baseDto.data;
                        if (placeWishItems == null)
                        {
                            if (DailyTextUtils.isTextEmpty(baseDto.msg) == false)
                            {
                                mOnNetworkControllerListener.onErrorToastMessage(baseDto.msg);
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
                            return;
                        }

                        String imageUrl = placeWishItems.imgUrl;

                        List<StayWishItem> stayWishItemList = placeWishItems.items;

                        ArrayList<Stay> stayList;

                        if (stayWishItemList != null)
                        {
                            stayList = makeStayList(stayWishItemList, imageUrl);
                        } else
                        {
                            stayList = new ArrayList<>();
                        }

                        ((StayWishListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onStayWishList(stayList);
                    } else
                    {
                        String message = baseDto.msg;

                        if (Constants.DEBUG == false)
                        {
                            Crashlytics.log(call.request().url().toString());
                        }

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
        public void onFailure(Call<BaseDto<PlaceWishItems<StayWishItem>>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }

        private ArrayList<Stay> makeStayList(List<StayWishItem> stayWishItemList, String imageUrl) throws JSONException
        {
            if (stayWishItemList == null || stayWishItemList.size() == 0)
            {
                return new ArrayList<>();
            }

            int length = stayWishItemList.size();
            ArrayList<Stay> stayList = new ArrayList<>(length);
            StayWishItem stayWishItem;
            Stay stay;

            for (int i = 0; i < length; i++)
            {
                stayWishItem = stayWishItemList.get(i);

                stay = new Stay();

                if (stay.setStay(stayWishItem, imageUrl) == true)
                {
                    stayList.add(stay); // 추가.
                }
            }

            return stayList;
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

                    int placeIndex = -1;
                    Uri uri = Uri.parse(call.request().url().toString());
                    String indexString = uri.getLastPathSegment();

                    try
                    {
                        placeIndex = Integer.parseInt(indexString);
                    } catch (Exception e)
                    {
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onRemoveStayWishListItem(isSuccess, message, placeIndex);
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
