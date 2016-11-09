package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Stay;
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
        DailyNetworkAPI.getInstance(mContext).requestWishList(mNetworkTag, Constants.PlaceType.HOTEL, mListJsonResponseListener);
    }

    public void requestRemoveStayWishListItem(int placeIndex)
    {
        DailyNetworkAPI.getInstance(mContext).requestRemoveWishList(mNetworkTag, //
            Constants.PlaceType.HOTEL, placeIndex, mRemoveWishListJsonResponseListener);
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
                    JSONArray hotelJSONArray = null;

                    if (dataJSONObject.has("hotelSales") == true)
                    {
                        hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
                    }

                    //                    int page;
                    String imageUrl;

                    ArrayList<Stay> stayList;

                    if (hotelJSONArray != null)
                    {
                        imageUrl = dataJSONObject.getString("imgUrl");
                        int nights = dataJSONObject.getInt("stays");
                        stayList = makeStayList(hotelJSONArray, imageUrl, nights);
                    } else
                    {
                        stayList = new ArrayList<>();
                    }

                    //                    try
                    //                    {
                    //                        Uri uri = Uri.parse(url);
                    //                        String pageString = uri.getQueryParameter("page");
                    //                        page = Integer.parseInt(pageString);
                    //
                    //                    } catch (Exception e)
                    //                    {
                    //                        page = 0;
                    //                    }

                    ((StayWishListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onStayWishList(stayList);
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
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

        private ArrayList<Stay> makeStayList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<Stay> stayList = new ArrayList<>(length);
            JSONObject jsonObject;
            Stay stay;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                stay = new Stay();

                if (stay.setStay(jsonObject, imageUrl, nights) == true)
                {
                    stayList.add(stay); // 추가.
                }
            }

            return stayList;
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

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onRemoveStayWishListItem(isSuccess, message, placeIndex);
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
