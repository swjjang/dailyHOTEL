package com.twoheart.dailyhotel.util.network.request;

import java.util.Map;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.response.DailyHotelResponseListener;

public class DailyHotelRequest extends StringRequest {

	private DailyHotelResponseListener mListener;
	private Map<String, String> mParameters;

	public DailyHotelRequest(int method, String url,
			Map<String, String> parameters,
			DailyHotelResponseListener listener, ErrorListener errorListener) {
		super(method, url, null, errorListener);
		this.mParameters = parameters;
		this.mListener = listener;
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(getUrl(), response);
	}
	
	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		VolleyHttpClient.setSessionCookie();
		return super.parseNetworkResponse(response);
		
	}

	@Override
	protected Map<String, String> getParams() {
		return mParameters;
	}

}
