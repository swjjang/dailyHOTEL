package com.twoheart.dailyhotel.network.response;

import org.json.JSONObject;

public interface DailyHotelJsonResponseListener
{
    public void onResponse(String url, JSONObject response);

}
