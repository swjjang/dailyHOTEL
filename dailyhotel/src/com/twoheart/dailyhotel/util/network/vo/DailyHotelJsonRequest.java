package com.twoheart.dailyhotel.util.network.vo;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.twoheart.dailyhotel.util.network.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.DailyHotelResponseListener;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class DailyHotelJsonRequest extends JsonObjectRequest {

	private DailyHotelJsonResponseListener mListener;
	private Map<String, String> mParameters;

	public DailyHotelJsonRequest(int method, String url,
			Map<String, String> parameters,
			DailyHotelJsonResponseListener listener, ErrorListener errorListener) {
		super(method, url, null, null, errorListener);
		this.mParameters = parameters;
		this.mListener = listener;
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(getUrl(), response);
	}
	
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		VolleyHttpClient.setSessionCookie();
		return super.parseNetworkResponse(response);
		
	}

	@Override
	protected Map<String, String> getParams() {
		return mParameters;
	}

}
