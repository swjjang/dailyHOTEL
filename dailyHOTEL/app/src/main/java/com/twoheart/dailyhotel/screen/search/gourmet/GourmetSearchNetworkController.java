package com.twoheart.dailyhotel.screen.search.gourmet;

import android.content.Context;

import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetSearchNetworkController extends PlaceSearchNetworkController
{
    public GourmetSearchNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAutoComplete(SaleTime saleTime, String keyword)
    {
        if (saleTime == null || Util.isTextEmpty(keyword) == true)
        {
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestGourmetSearchAutoCompleteList(mNetworkTag//
            , saleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), keyword, mGourmetSearchAutoCompleteCallback);
    }

    private retrofit2.Callback mGourmetSearchAutoCompleteCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                String keyword = call.request().url().queryParameter("term");

                List<Keyword> keywordList = null;

                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");

                        int length = dataJSONArray.length();

                        keywordList = new ArrayList<>(length);

                        for (int i = 0; i < length; i++)
                        {
                            try
                            {
                                keywordList.add(new Keyword(dataJSONArray.getJSONObject(i), PlaceSearchLayout.GOURMET_ICON));
                            } catch (Exception e)
                            {
                                ExLog.d(e.toString());
                            }
                        }
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(keyword, keywordList);
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(null, null);
        }
    };
}
