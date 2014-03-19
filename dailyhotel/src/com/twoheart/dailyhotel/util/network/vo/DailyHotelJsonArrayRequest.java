package com.twoheart.dailyhotel.util.network.vo;

import java.util.Map;

import org.json.JSONArray;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.twoheart.dailyhotel.util.network.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.network.DailyHotelResponseListener;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class DailyHotelJsonArrayRequest extends JsonArrayRequest {

	private DailyHotelJsonArrayResponseListener mListener;
	private Map<String, String> mParameters;

	public DailyHotelJsonArrayRequest(int method, String url,
			Map<String, String> parameters,
			DailyHotelJsonArrayResponseListener listener, ErrorListener errorListener) {
		super(url, null, errorListener);
		this.mParameters = parameters;
		this.mListener = listener;
	}

	@Override
	protected void deliverResponse(JSONArray response) {
		mListener.onResponse(getUrl(), response);
	}
	
	@Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		VolleyHttpClient.setSessionCookie();
		return super.parseNetworkResponse(response);
    }
	
	@Override
	protected Map<String, String> getParams() {
		return mParameters;
	}

}
