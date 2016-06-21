package com.twoheart.dailyhotel.network.response;

import com.android.volley.Response;

import org.json.JSONArray;

public interface DailyHotelJsonArrayResponseListener extends Response.ErrorListener
{
    void onResponse(String url, JSONArray response);
}
