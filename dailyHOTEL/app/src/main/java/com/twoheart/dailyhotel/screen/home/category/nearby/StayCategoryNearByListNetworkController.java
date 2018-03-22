package com.twoheart.dailyhotel.screen.home.category.nearby;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCategoryNearByParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 5. 19..
 */

@Deprecated
public class StayCategoryNearByListNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayList(ArrayList<Stay> list, int page, int totalCount, int maxCount, boolean activeReward);
    }

    public StayCategoryNearByListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStaySearchList(StayCategoryNearByParams params, String abTestType)
    {
        if (params == null)
        {
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestStayCategoryList(mNetworkTag //
            , params.getCategoryCode(), params.toParamsMap() //
            , params.getBedTypeList(), params.getLuxuryList(), abTestType, mStayCategoryListCallback);

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mStayCategoryListCallback = new retrofit2.Callback<JSONObject>()
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
                        JSONArray hotelJSONArray = null;

                        if (dataJSONObject.has("hotelSales") == true)
                        {
                            hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
                        }

                        boolean activeReward = false;

                        if (dataJSONObject.has("configurations") == true)
                        {
                            activeReward = dataJSONObject.getJSONObject("configurations").getBoolean("activeReward");
                        }

                        int totalCount = 0;
                        int maxCount = dataJSONObject.getInt("searchMaxCount");
                        int page;
                        String imageUrl;

                        // 카테고리 목록을 만든다
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

                                    int count = categoryJSONObject.getInt("count");

                                    totalCount += count;
                                }
                            }
                        }

                        // 스테이 목록을 만든다.
                        ArrayList<Stay> stayList;

                        if (hotelJSONArray != null)
                        {
                            imageUrl = dataJSONObject.getString("imgUrl");
                            stayList = makeStayList(hotelJSONArray, imageUrl);
                        } else
                        {
                            stayList = new ArrayList<>();
                        }

                        try
                        {
                            String pageString = call.request().url().queryParameter("page");
                            page = Integer.parseInt(pageString);
                        } catch (Exception e)
                        {
                            page = 0;
                        }

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayList(stayList, page, totalCount, maxCount, activeReward);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");

                        Crashlytics.log(call.request().url().toString());
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
                    Crashlytics.log(call.request().url().toString());
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

        private ArrayList<Stay> makeStayList(JSONArray jsonArray, String imageUrl) throws JSONException
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

                if (stay.setStay(jsonObject, imageUrl) == true)
                {
                    stayList.add(stay); // 추가.
                }
            }

            return stayList;
        }
    };
}

