package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        DailyMobileAPI.getInstance(mContext).requestWishList(mNetworkTag, "gourmet", mWishListCallback);
    }

    public void requestRemoveGourmetWishListItem(int placeIndex)
    {
        DailyMobileAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, //
            "gourmet", placeIndex, mRemoveWishListCallback);
    }

    private retrofit2.Callback mWishListCallback = new retrofit2.Callback<JSONObject>()
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
                        JSONArray gourmetJSONArray = null;

                        if (dataJSONObject.has("gourmetSales") == true)
                        {
                            gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSales");
                        }

                        String imageUrl;

                        ArrayList<Gourmet> gourmetList;

                        if (gourmetJSONArray != null)
                        {
                            imageUrl = dataJSONObject.getString("imgUrl");
                            gourmetList = makeGourmetList(gourmetJSONArray, imageUrl, dataJSONObject.getJSONArray("stickers"));
                        } else
                        {
                            gourmetList = new ArrayList<>();
                        }

                        ((GourmetWishListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetWishList(gourmetList);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");

                        if (Constants.DEBUG == false)
                        {
                            Crashlytics.log(call.request().url().toString());
                        }

                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
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
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }

        private ArrayList<Gourmet> makeGourmetList(JSONArray jsonArray, String imageUrl, JSONArray stickerJSONArray) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            SparseArray<String> stickerSparseArray = new SparseArray<>();
            if (stickerJSONArray != null && stickerJSONArray.length() > 0)
            {
                boolean isLowResource = false;

                if (ScreenUtils.getScreenWidth(mContext) < 1440)
                {
                    isLowResource = true;
                }

                int length = stickerJSONArray.length();


                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = stickerJSONArray.getJSONObject(i);

                    int index = jsonObject.getInt("idx");

                    String url;

                    if (isLowResource == true)
                    {
                        url = jsonObject.getString("lowResolutionImageUrl");
                    } else
                    {
                        url = jsonObject.getString("defaultImageUrl");
                    }

                    stickerSparseArray.append(index, url);
                }
            }

            int length = jsonArray.length();
            ArrayList<Gourmet> gourmetList = new ArrayList<>(length);
            JSONObject jsonObject;
            Gourmet gourmet;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                gourmet = new Gourmet();

                if (gourmet.setData(jsonObject, imageUrl, stickerSparseArray) == true)
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
