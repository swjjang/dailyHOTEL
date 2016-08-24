package com.twoheart.dailyhotel.network.response;

import com.android.volley.Response;

public interface DailyHotelStringResponseListener extends Response.ErrorListener
{
    void onResponse(String url, String response);

}
