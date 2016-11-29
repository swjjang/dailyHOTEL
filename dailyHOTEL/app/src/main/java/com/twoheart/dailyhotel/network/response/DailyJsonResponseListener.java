package com.twoheart.dailyhotel.network.response;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

public interface DailyJsonResponseListener extends Response.ErrorListener
{
    void onResponse(String url, JSONObject params, JSONObject response);
}
