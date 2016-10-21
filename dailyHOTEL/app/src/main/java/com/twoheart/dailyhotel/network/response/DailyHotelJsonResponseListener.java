package com.twoheart.dailyhotel.network.response;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

public interface DailyHotelJsonResponseListener extends Response.ErrorListener
{
    void onResponse(String url, Map<String, String> params, JSONObject response);
}
