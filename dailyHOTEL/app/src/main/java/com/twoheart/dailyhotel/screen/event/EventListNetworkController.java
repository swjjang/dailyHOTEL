package com.twoheart.dailyhotel.screen.event;

import android.content.Context;

import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
@Deprecated
public class EventListNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onEventListResponse(List<Event> eventList);
    }

    public EventListNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestEventList()
    {
        String store;

        if (Setting.getStore() == Setting.Stores.PLAY_STORE)
        {
            store = "GOOGLE";
        } else
        {
            store = "ONE";
        }

        DailyMobileAPI.getInstance(mContext).requestEventList(mNetworkTag, store, mDailyEventListCallback);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mDailyEventListCallback = new retrofit2.Callback<BaseListDto<Event>>()
    {
        @Override
        public void onResponse(Call<BaseListDto<Event>> call, Response<BaseListDto<Event>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseListDto<Event> baseListDto = response.body();

                    switch (baseListDto.msgCode)
                    {
                        case 100:
                            ArrayList<Event> arrayList = (ArrayList<Event>) baseListDto.data;

                            if (arrayList == null || arrayList.size() == 0)
                            {
                                ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
                            } else
                            {
                                ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(arrayList);
                            }
                            break;

                        default:
                            mOnNetworkControllerListener.onErrorPopupMessage(baseListDto.msgCode, baseListDto.msg);
                            break;
                    }
                } catch (Exception e)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseListDto<Event>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };
}