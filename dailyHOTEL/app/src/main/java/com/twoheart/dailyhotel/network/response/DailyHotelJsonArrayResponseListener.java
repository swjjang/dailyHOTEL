package com.twoheart.dailyhotel.network.response;

import org.json.JSONArray;

public interface DailyHotelJsonArrayResponseListener
{
    void onResponse(String url, JSONArray response);

}
