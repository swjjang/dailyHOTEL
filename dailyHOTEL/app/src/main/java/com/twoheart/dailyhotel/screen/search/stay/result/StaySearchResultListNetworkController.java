package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StaySearchParams;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class StaySearchResultListNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayList(ArrayList<Stay> list, int page, int totalCount, int maxCount, HashSet<String> categorSet);
    }

    public StaySearchResultListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStaySearchList(StaySearchParams params)
    {
        if (params == null)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestStaySearchList(mNetworkTag, params.toParamsString(), mStayListJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mStayListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

        @Override
        public void onResponse(String url, JSONObject response)
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

                    int totalCount = dataJSONObject.getInt("searchTotalCount");
                    int maxCount = dataJSONObject.getInt("searchMaxCount");
                    int page;
                    String imageUrl;

                    ArrayList<Stay> stayList;
                    HashSet<String> categorySet = new HashSet<>();

                    if (hotelJSONArray != null)
                    {
                        imageUrl = dataJSONObject.getString("imgUrl");
                        int nights = dataJSONObject.getInt("stays");
                        stayList = makeStayList(hotelJSONArray, imageUrl, nights, categorySet);
                    } else
                    {
                        stayList = new ArrayList<>();
                    }

                    try
                    {
                        Uri uri = Uri.parse(url);
                        String pageString = uri.getQueryParameter("page");
                        page = Integer.parseInt(pageString);

                    } catch (Exception e)
                    {
                        page = 0;
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayList(stayList, page, totalCount, maxCount, categorySet);
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

        private ArrayList<Stay> makeStayList(JSONArray jsonArray, String imageUrl, int nights, HashSet<String> categorySet) throws JSONException
        {
            if (jsonArray == null || categorySet == null)
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

                    categorySet.add(stay.categoryCode);
                }
            }

            return stayList;
        }
    };
}
