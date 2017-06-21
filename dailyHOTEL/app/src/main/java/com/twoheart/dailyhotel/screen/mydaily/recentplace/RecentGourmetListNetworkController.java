package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Context;
import android.util.SparseArray;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.RecentGourmetParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.model.Sticker;
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
 * Created by android_sam on 2016. 10. 12..
 */
@Deprecated
public class RecentGourmetListNetworkController extends BaseNetworkController
{
    public RecentGourmetListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRecentGourmetList(ArrayList<Gourmet> list);
    }

    public void requestRecentGourmetList(RecentGourmetParams params)
    {
        if (params == null)
        {
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestGourmetList(mNetworkTag, params.toParamsMap(), params.getCategoryList(), params.getTimeList(), params.getLuxuryList(), mRecentListCallback);
    }

    private retrofit2.Callback mRecentListCallback = new retrofit2.Callback<JSONObject>()
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

                        ((RecentGourmetListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentGourmetList(gourmetList);
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
