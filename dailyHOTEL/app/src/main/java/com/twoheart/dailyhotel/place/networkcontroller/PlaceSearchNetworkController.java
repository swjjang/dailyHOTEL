package com.twoheart.dailyhotel.place.networkcontroller;

import android.content.Context;

import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import java.util.List;

public abstract class PlaceSearchNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onResponseAutoComplete(String keyword, List<Keyword> list);
    }

    public PlaceSearchNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }
}
