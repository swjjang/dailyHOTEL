package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

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
        DailyNetworkAPI.getInstance(mContext).requestWishList(mNetworkTag, Constants.PlaceType.FNB, mListJsonResponseListener);
    }

    public void requestRemoveGourmetWishListItem(int placeIndex)
    {
        DailyNetworkAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, //
            Constants.PlaceType.FNB, placeIndex, mRemoveWishListJsonResponseListener);
    }

    private DailyHotelJsonResponseListener mListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
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
                        gourmetList = makeGourmetList(gourmetJSONArray, imageUrl);
                    } else
                    {
                        gourmetList = new ArrayList<>();
                    }

                    ((GourmetWishListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetWishList(gourmetList);
                } else
                {
                    String message = response.getString("msg");

                    if (Constants.DEBUG == false)
                    {
                        Crashlytics.log(url);
                    }

                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                if (Constants.DEBUG == false)
                {
                    Crashlytics.log(url);
                }

                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

        private ArrayList<Gourmet> makeGourmetList(JSONArray jsonArray, String imageUrl) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<Gourmet> gourmetList = new ArrayList<>(length);
            JSONObject jsonObject;
            Gourmet gourmet;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                gourmet = new Gourmet();

                if (gourmet.setData(jsonObject, imageUrl) == true)
                {
                    gourmetList.add(gourmet);
                }
            }

            return gourmetList;
        }
    };

    private DailyHotelJsonResponseListener mRemoveWishListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                boolean isSuccess = msgCode == 100 ? true : false;

                String message = null;
                if (response.has("msg") == true)
                {
                    message = response.getString("msg");
                }

                int placeIndex = -1;
                if (Util.isTextEmpty(url) == false)
                {
                    Uri uri = Uri.parse(url);
                    String indexString = uri.getLastPathSegment();

                    try
                    {
                        placeIndex = Integer.parseInt(indexString);
                    } catch (Exception e)
                    {
                    }
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onRemoveGourmetWishListItem(isSuccess, message, placeIndex);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };
}
