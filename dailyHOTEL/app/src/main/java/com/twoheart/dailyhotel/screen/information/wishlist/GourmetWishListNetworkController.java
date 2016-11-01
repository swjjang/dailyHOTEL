package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;

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

        void onDeleteGourmetWishListItem(int position);
    }

    public void requestGourmetWishList()
    {
        ((GourmetWishListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetWishList(null);
    }

    public void requestDeleteGourmetWishListItem()
    {
        ((GourmetWishListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onDeleteGourmetWishListItem(-1);
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

    DailyHotelJsonResponseListener mDeleteItemJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    //                    JSONObject dataJSONObject = response.getJSONObject("data");

                    ((GourmetWishListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onDeleteGourmetWishListItem(-1);
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
    };
}
