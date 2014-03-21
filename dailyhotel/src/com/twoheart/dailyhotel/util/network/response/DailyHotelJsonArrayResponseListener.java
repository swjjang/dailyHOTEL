package com.twoheart.dailyhotel.util.network.response;

import org.json.JSONArray;

public interface DailyHotelJsonArrayResponseListener {
	public void onResponse(String url, JSONArray response);
	
}
