package com.twoheart.dailyhotel.screen.search.stay;

import android.content.Context;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StaySearchNetworkController extends PlaceSearchNetworkController
{
    public interface OnNetworkControllerListener extends PlaceSearchNetworkController.OnNetworkControllerListener
    {
        void onResponseAutoComplete(String keyword, List<StayKeyword> list);
    }

    public StaySearchNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAutoComplete(StayBookingDay stayBookingDay, String keyword)
    {
        if (stayBookingDay == null || DailyTextUtils.isTextEmpty(keyword) == true)
        {
            return;
        }

        try
        {
            DailyMobileAPI.getInstance(mContext).requestStaySearchAutoCompleteList(mNetworkTag//
                , stayBookingDay.getCheckInDay("yyyy-MM-dd"), stayBookingDay.getNights(), keyword, mHotelSearchAutoCompleteCallback);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private retrofit2.Callback mHotelSearchAutoCompleteCallback = new retrofit2.Callback<BaseListDto<StayKeyword>>()
    {
        @Override
        public void onResponse(Call<BaseListDto<StayKeyword>> call, Response<BaseListDto<StayKeyword>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                String term = call.request().url().queryParameter("term");

                BaseListDto<StayKeyword> baseListDto = response.body();

                if (baseListDto.msgCode == 100)
                {
                    if (baseListDto.data != null)
                    {
                        for (StayKeyword stayKeyword : baseListDto.data)
                        {
                            if (stayKeyword.index > 0)
                            {
                                stayKeyword.icon = PlaceSearchLayout.HOTEL_ICON;
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
        public void onFailure(Call<BaseListDto<StayKeyword>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, true);
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(null, null);
        }
    };
}
