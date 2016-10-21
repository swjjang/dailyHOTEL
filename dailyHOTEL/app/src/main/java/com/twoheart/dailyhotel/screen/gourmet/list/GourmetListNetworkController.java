package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GourmetListNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onGourmetList(ArrayList<Gourmet> list, int page, int totalCount, int maxCount, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap);
    }

    public GourmetListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestGourmetList(GourmetParams params)
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

                    int totalCount = dataJSONObject.getInt("searchCount");
                    int maxCount = dataJSONObject.getInt("searchMaxCount");
                    int page;
                    String imageUrl;

                    ArrayList<Gourmet> gourmetList = new ArrayList<>();

                    if (gourmetJSONArray != null)
                    {
                        imageUrl = dataJSONObject.getString("imgUrl");
                        gourmetList = makeGourmetList(gourmetJSONArray, imageUrl);
                    }

                    JSONArray categoryJSONArray = dataJSONObject.getJSONObject("filter").getJSONArray("categories");
                    HashMap<String, Integer> categoryCodeMap = new HashMap<>(12);
                    HashMap<String, Integer> categorySequenceMap = new HashMap<>(12);

                    int categoryCount = categoryJSONArray.length();

                    // 필터 정보 넣기
                    for (int i = 0; i < categoryCount; i++)
                    {
                        JSONObject categoryJSONObject = categoryJSONArray.getJSONObject(i);

                        int categoryCode = categoryJSONObject.getInt("code");
                        int categorySeq = categoryJSONObject.getInt("sequence");
                        String categoryName = categoryJSONObject.getString("name");

                        categoryCodeMap.put(categoryName, categoryCode);
                        categorySequenceMap.put(categoryName, categorySeq);
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

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetList(gourmetList, page,//
                        totalCount, maxCount, categoryCodeMap, categorySequenceMap);
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
}
