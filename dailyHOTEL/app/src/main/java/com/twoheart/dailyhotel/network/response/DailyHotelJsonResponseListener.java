package com.twoheart.dailyhotel.network.response;

import com.android.volley.Response;

import org.json.JSONObject;

public interface DailyHotelJsonResponseListener extends Response.ErrorListener
{
    void onResponse(String url, JSONObject response);
}
