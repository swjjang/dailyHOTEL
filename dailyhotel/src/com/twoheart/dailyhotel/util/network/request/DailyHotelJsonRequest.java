package com.twoheart.dailyhotel.util.network.request;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;

public class DailyHotelJsonRequest extends Request<JSONObject> {

	private DailyHotelJsonResponseListener mListener;
	private Map<String, String> mParameters;

	public DailyHotelJsonRequest(int method, String url,
			Map<String, String> parameters,
			DailyHotelJsonResponseListener listener, ErrorListener errorListener) {
		super(method, url, errorListener);
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
        try {
            String jsonString =
                new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
	
	@Override
	protected Map<String, String> getParams() {
		return mParameters;
	}

}
