package com.twoheart.dailyhotel.screen.search.stay;

import android.content.Context;

import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.util.Util;

import retrofit2.Call;
import retrofit2.Response;

public class StaySearchNetworkController extends PlaceSearchNetworkController
{
    public StaySearchNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAutoComplete(SaleTime saleTime, int stays, String keyword)
    {
        if (saleTime == null || stays == 0 || Util.isTextEmpty(keyword) == true)
        {
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestStaySearchAutoCompleteList(mNetworkTag//
            , saleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), stays, keyword, mHotelSearchAutoCompleteCallback);
    }

    private retrofit2.Callback mHotelSearchAutoCompleteCallback = new retrofit2.Callback<BaseListDto<Keyword>>()
    {
        @Override
        public void onResponse(Call<BaseListDto<Keyword>> call, Response<BaseListDto<Keyword>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                String term = call.request().url().queryParameter("term");

                BaseListDto<Keyword> baseListDto = response.body();

                if (baseListDto.msgCode == 100)
                {
                    if (baseListDto.data != null)
                    {
                        for (Keyword keyword : baseListDto.data)
                        {
                            if (keyword.price > 0)
                            {
                                keyword.icon = PlaceSearchLayout.HOTEL_ICON;
                            }
                        }
                    }
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(term, baseListDto.data);
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseListDto<Keyword>> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(null, null);
        }
    };

    //    private retrofit2.Callback mHotelSearchAutoCompleteCallback = new retrofit2.Callback<JSONObject>()
    //    {
    //        @Override
    //        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //        {
    //            if (response != null && response.isSuccessful() && response.body() != null)
    //            {
    //                String keyword = call.request().url().queryParameter("term");
    //
    //                List<Keyword> keywordList = null;
    //
    //                try
    //                {
    //                    JSONObject responseJSONObject = response.body();
    //
    //                    int msgCode = responseJSONObject.getInt("msgCode");
    //
    //                    if (msgCode == 100)
    //                    {
    //                        JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //
    //                        int length = dataJSONArray.length();
    //
    //                        keywordList = new ArrayList<>(length);
    //
    //                        for (int i = 0; i < length; i++)
    //                        {
    //                            try
    //                            {
    //                                keywordList.add(new Keyword(dataJSONArray.getJSONObject(i), PlaceSearchLayout.HOTEL_ICON));
    //                            } catch (Exception e)
    //                            {
    //                                ExLog.d(e.toString());
    //                            }
    //                        }
    //                    }
    //                } catch (Exception e)
    //                {
    //                    ExLog.d(e.toString());
    //                }
    //
    //                ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(keyword, keywordList);
    //            } else
    //            {
    //                mOnNetworkControllerListener.onErrorResponse(call, response);
    //            }
    //        }
    //
    //        @Override
    //        public void onFailure(Call<JSONObject> call, Throwable t)
    //        {
    //            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(null, null);
    //        }
    //    };
}
