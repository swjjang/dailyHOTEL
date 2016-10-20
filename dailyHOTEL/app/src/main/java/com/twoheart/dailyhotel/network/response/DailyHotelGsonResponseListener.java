package com.twoheart.dailyhotel.network.response;

import com.android.volley.Response;

import java.util.Map;

public interface DailyHotelGsonResponseListener<T> extends Response.ErrorListener
{
    void onResponse(String url, Map<String, String> params, T classOfT);
}
