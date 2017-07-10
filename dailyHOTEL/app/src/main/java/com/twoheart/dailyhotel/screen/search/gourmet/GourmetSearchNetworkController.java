package com.twoheart.dailyhotel.screen.search.gourmet;

import android.content.Context;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetSearchNetworkController extends PlaceSearchNetworkController
{
    public interface OnNetworkControllerListener extends PlaceSearchNetworkController.OnNetworkControllerListener
    {
        void onResponseAutoComplete(String keyword, List<GourmetKeyword> list);
    }

    public GourmetSearchNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAutoComplete(GourmetBookingDay gourmetBookingDay, String keyword)
    {
        if (gourmetBookingDay == null || DailyTextUtils.isTextEmpty(keyword) == true)
        {
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestGourmetSearchAutoCompleteList(mNetworkTag//
            , gourmetBookingDay.getVisitDay("yyyy-MM-dd"), keyword, mGourmetSearchAutoCompleteCallback);
    }

    private retrofit2.Callback mGourmetSearchAutoCompleteCallback = new retrofit2.Callback<BaseListDto<GourmetKeyword>>()
    {
        @Override
        public void onResponse(Call<BaseListDto<GourmetKeyword>> call, Response<BaseListDto<GourmetKeyword>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                String term = call.request().url().queryParameter("term");

                BaseListDto<GourmetKeyword> baseListDto = response.body();

                if (baseListDto.msgCode == 100)
                {
                    if (baseListDto.data != null)
                    {
                        for (GourmetKeyword gourmetKeyword : baseListDto.data)
                        {
                            if (gourmetKeyword.index > 0)
                            {
                                gourmetKeyword.icon = PlaceSearchLayout.GOURMET_ICON;
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
        public void onFailure(Call<BaseListDto<GourmetKeyword>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, true);
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(null, null);
        }
    };
}
