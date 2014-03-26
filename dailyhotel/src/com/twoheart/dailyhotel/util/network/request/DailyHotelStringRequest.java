package com.twoheart.dailyhotel.util.network.request;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;

public class DailyHotelStringRequest extends DailyHotelRequest<String> {

	private DailyHotelStringResponseListener mListener;

	public DailyHotelStringRequest(int method, String url,
			Map<String, String> parameters,
			DailyHotelStringResponseListener listener, ErrorListener errorListener) {
		super(method, url, parameters, errorListener);
		this.mListener = listener;
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(getUrl(), response);
	}
	
	@Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed.trim(), HttpHeaderParser.parseCacheHeaders(response));
    }

}
