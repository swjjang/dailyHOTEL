package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.util.SparseArray;

import com.crashlytics.android.Crashlytics;
import com.daily.base.exception.BaseException;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

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

        DailyMobileAPI.getInstance(mContext).requestGourmetList(mNetworkTag, params.toParamsMap()//
            , params.getCategoryList(), params.getTimeList(), params.getLuxuryList(), mGourmetListCallback);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mGourmetListCallback = new retrofit2.Callback<JSONObject>()
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

                        int totalCount = dataJSONObject.getInt("gourmetSalesCount");
                        int maxCount = dataJSONObject.getInt("searchMaxCount");
                        int page;
                        String imageUrl;

                        ArrayList<Gourmet> gourmetList = new ArrayList<>();

                        if (gourmetJSONArray != null)
                        {
                            imageUrl = dataJSONObject.getString("imgUrl");
                            gourmetList = makeGourmetList(gourmetJSONArray, imageUrl, dataJSONObject.getJSONArray("stickers"));
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
                            String pageString = call.request().url().queryParameter("page");
                            page = Integer.parseInt(pageString);
                        } catch (Exception e)
                        {
                            page = 0;
                        }

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetList(gourmetList, page,//
                            totalCount, maxCount, categoryCodeMap, categorySequenceMap);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");

                        Crashlytics.logException(new BaseException(msgCode, "msgCode : " + msgCode + " , Call url : " + call.request().url().toString()));
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

                if (ScreenUtils.getScreenWidth(mContext) <= Sticker.DEFAULT_SCREEN_WIDTH)
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
}
