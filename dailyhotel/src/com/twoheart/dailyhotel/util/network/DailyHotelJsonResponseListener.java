package com.twoheart.dailyhotel.util.network;

import org.json.JSONObject;

public interface DailyHotelJsonResponseListener {
	public void onResponse(String url, JSONObject response);
	
}
