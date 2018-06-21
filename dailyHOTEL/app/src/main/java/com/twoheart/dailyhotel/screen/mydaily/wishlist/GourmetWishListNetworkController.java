package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.content.Context;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.GourmetWishItem;
import com.twoheart.dailyhotel.network.model.PlaceWishItems;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class GourmetWishListNetworkController extends BaseNetworkController
{
    public GourmetWishListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onGourmetWishList(ArrayList<Gourmet> list);

        void onRemoveGourmetWishListItem(boolean isSuccess, String message, int placeIndex);
    }

    public void requestGourmetWishList()
    {
        DailyMobileAPI.getInstance(mContext).requestGourmetWishList(mNetworkTag, mWishListCallback);
    }

    public void requestRemoveGourmetWishListItem(int placeIndex)
    {
        DailyMobileAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, //
            "gourmet", placeIndex, mRemoveWishListCallback);
    }

    private retrofit2.Callback mWishListCallback = new retrofit2.Callback<BaseDto<PlaceWishItems<GourmetWishItem>>>()
    {
        @Override
        public void onResponse(Call<BaseDto<PlaceWishItems<GourmetWishItem>>> call, Response<BaseDto<PlaceWishItems<GourmetWishItem>>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<PlaceWishItems<GourmetWishItem>> baseDto = response.body();

                    int msgCode = baseDto.msgCode;
                    if (msgCode == 100)
                    {
                        PlaceWishItems<GourmetWishItem> placeWishItems = baseDto.data;
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

                        List<GourmetWishItem> gourmetWishItemList = placeWishItems.items;

                        ArrayList<Gourmet> gourmetList;

                        if (gourmetWishItemList != null)
                        {
                            gourmetList = makeGourmetList(gourmetWishItemList, imageUrl);
                        } else
                        {
                            gourmetList = new ArrayList<>();
                        }

                        ((GourmetWishListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetWishList(gourmetList);
                    } else
                    {
                        String message = baseDto.msg;

                        Crashlytics.logException(new BaseException(msgCode, "Call url : " + call.request().url().toString()));
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
                    Crashlytics.log("Call url : " + call.request().url().toString());
                    Crashlytics.logException(e);
                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<PlaceWishItems<GourmetWishItem>>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }

        private ArrayList<Gourmet> makeGourmetList(List<GourmetWishItem> gourmetWishItemList, String imageUrl)
        {
            if (gourmetWishItemList == null || gourmetWishItemList.size() == 0)
            {
                return new ArrayList<>();
            }

            int length = gourmetWishItemList.size();
            ArrayList<Gourmet> gourmetList = new ArrayList<>(length);
            GourmetWishItem gourmetWishItem;
            Gourmet gourmet;

            boolean isLowResource = false;

            if (ScreenUtils.getScreenWidth(mContext) < Sticker.DEFAULT_SCREEN_WIDTH)
            {
                isLowResource = true;
            }

            for (int i = 0; i < length; i++)
            {
                gourmetWishItem = gourmetWishItemList.get(i);

                gourmet = new Gourmet();

                if (gourmet.setData(gourmetWishItem, imageUrl, isLowResource) == true)
                {
                    gourmetList.add(gourmet);
                }
            }

            return gourmetList;
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

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onRemoveGourmetWishListItem(isSuccess, message, placeIndex);
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
