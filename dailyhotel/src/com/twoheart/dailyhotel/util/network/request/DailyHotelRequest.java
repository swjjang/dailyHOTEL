package com.twoheart.dailyhotel.util.network.request;

import java.util.Map;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public abstract class DailyHotelRequest<T> extends Request<T> implements Constants {

	private Map<String, String> mParameters;
	
	public DailyHotelRequest(int method, String url, Map<String, String> parameters,
								ErrorListener errorListener) {
		this(method, url, errorListener);
		mParameters = parameters;
//		android.util.Log.e("URL",url);
		setRetryPolicy(new DefaultRetryPolicy(REQUEST_EXPIRE_JUDGE,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
	

	private DailyHotelRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
		setRetryPolicy(new DefaultRetryPolicy(VolleyHttpClient.TIME_OUT,
				VolleyHttpClient.MAX_RETRY,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		setTag(listener);
		
		if (!VolleyHttpClient.isAvailableNetwork())
			cancel();
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
