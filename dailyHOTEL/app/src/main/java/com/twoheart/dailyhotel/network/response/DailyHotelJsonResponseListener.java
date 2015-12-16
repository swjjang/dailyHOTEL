package com.twoheart.dailyhotel.network.response;

import org.json.JSONObject;

public interface DailyHotelJsonResponseListener
{
    void onResponse(String url, JSONObject response);

}
