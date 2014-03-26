package com.twoheart.dailyhotel.util.network.request;

import java.util.Map;
import java.util.Set;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public abstract class DailyHotelRequest<T> extends Request<T> {

	private Map<String, String> mParameters;
	
	public DailyHotelRequest(int method, String url, Map<String, String> parameters, ErrorListener listener) {
		this(method, url, listener);
		this.mParameters = parameters;
		
	}

	private DailyHotelRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
		setRetryPolicy(new DefaultRetryPolicy(VolleyHttpClient.TIME_OUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	@Override
	protected abstract Response<T> parseNetworkResponse(NetworkResponse response);

	@Override
	protected abstract void deliverResponse(T response);

	@Override
	protected Map<String, String> getParams() {
		return mParameters;
	}

}
