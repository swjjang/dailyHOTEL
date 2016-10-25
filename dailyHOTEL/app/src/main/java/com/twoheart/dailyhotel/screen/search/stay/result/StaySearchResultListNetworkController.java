package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StaySearchParams;
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
import java.util.List;
import java.util.Map;

public class StaySearchResultListNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayList(ArrayList<Stay> list, int page, int totalCount, int maxCount, List<Category> categoryList);
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

                    int totalCount = 0;
                    int maxCount = dataJSONObject.getInt("searchMaxCount");
                    int page;
                    String imageUrl;

                    // 카테고리 목록을 만든다
                    ArrayList<Category> categoryList = new ArrayList<>();

                    if (hotelJSONArray != null && hotelJSONArray.length() > 0)
                    {
                        JSONArray categoryJSONArray = null;

                        if (dataJSONObject.isNull("categories") == false)
                        {
                            categoryJSONArray = dataJSONObject.getJSONArray("categories");
                        }

                        if (categoryJSONArray != null && categoryJSONArray.length() != 0)
                        {
                            int length = categoryJSONArray.length();
                            JSONObject categoryJSONObject;

                            for (int i = 0; i < length; i++)
                            {
                                categoryJSONObject = categoryJSONArray.getJSONObject(i);

                                String name = categoryJSONObject.getString("name");
                                String code = categoryJSONObject.getString("alias");
                                int count = categoryJSONObject.getInt("count");

                                if (count > 0 && Util.isTextEmpty(name, code) == false)
                                {
                                    categoryList.add(new Category(name, code));
                                }
                                totalCount += count;
                            }
                        }
                    }

                    // 스테이 목록을 만든다.
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

                    try
                    {
                        Uri uri = Uri.parse(url);
                        String pageString = uri.getQueryParameter("page");
                        page = Integer.parseInt(pageString);
                    } catch (Exception e)
                    {
                        page = 0;
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayList(stayList, page, totalCount, maxCount, categoryList);
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
}
