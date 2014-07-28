package com.twoheart.dailyhotel.util.network.request;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;

public class DailyHotelJsonRequest extends DailyHotelRequest<JSONObject> {

	private DailyHotelJsonResponseListener mListener;

	public DailyHotelJsonRequest(int method, String url,
			Map<String, String> parameters,
			DailyHotelJsonResponseListener listener, ErrorListener errorListener) {
		super(method, url, parameters, errorListener);
		this.mListener = listener;
		
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		if (mListener != null) mListener.onResponse(getUrl(), response);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		
		String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
		
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			Log.e(parsed);
			return Response.error(new ParseError(je));
		}
	}

}
