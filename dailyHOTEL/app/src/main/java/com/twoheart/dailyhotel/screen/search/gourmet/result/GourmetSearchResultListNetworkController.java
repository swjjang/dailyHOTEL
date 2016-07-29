package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.model.GourmetSearch;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GourmetSearchResultListNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onGourmetList(ArrayList<Gourmet> list, int page, int totalCount, int maxCount);
    }

    public GourmetSearchResultListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestGourmetList(GourmetSearchParams params)
    {
        if (params == null)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestGourmetList(mNetworkTag, params.toParamsString(), mGourmetListJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    JSONArray gourmetJSONArray = null;

                    if (dataJSONObject.has("gourmetSales") == true)
                    {
                        gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSales");
                    }

                    int totalCount = dataJSONObject.getInt("searchTotalCount");
                    int maxCount = dataJSONObject.getInt("searchMaxCount");
                    int page;
                    String imageUrl;

                    ArrayList<Gourmet> gourmetList = new ArrayList<>();

                    if (gourmetJSONArray != null)
                    {
                        imageUrl = dataJSONObject.getString("imgUrl");
                        gourmetList = makeGourmetList(gourmetJSONArray, imageUrl);
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

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetList(gourmetList, page, totalCount, maxCount);
                } else
                {
                    String message = response.getString("msg");
                    Crashlytics.log(url);
                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
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

                gourmet = new GourmetSearch();

                if (gourmet.setData(jsonObject, imageUrl) == true)
                {
                    gourmetList.add(gourmet);
                }
            }

            return gourmetList;
        }
    };
}
