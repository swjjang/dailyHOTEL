package com.twoheart.dailyhotel.place.networkcontroller;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;

import java.util.ArrayList;
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
