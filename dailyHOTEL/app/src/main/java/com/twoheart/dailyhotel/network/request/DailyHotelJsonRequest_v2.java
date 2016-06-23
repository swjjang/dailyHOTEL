package com.twoheart.dailyhotel.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class DailyHotelJsonRequest_v2 extends DailyHotelRequest_v2<JSONObject>
{
    private DailyHotelJsonResponseListener mListener;

    public DailyHotelJsonRequest_v2(Object object, int method, String url, Map<String, Object> parameters, DailyHotelJsonResponseListener listener, ErrorListener errorListener)
    {
        super(object, method, url, parameters, errorListener);

        mListener = listener;
    }

    public DailyHotelJsonRequest_v2(Object object, int method, String url, Map<String, Object> parameters, DailyHotelJsonResponseListener listener)
    {
        super(object, method, url, parameters, listener);

        mListener = listener;
    }

    @Override
    protected void deliverResponse(JSONObject response)
    {
        if (mListener != null)
        {
            mListener.onResponse(getUrl(), response);
        }
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            return Response.success(new JSONObject(data), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        } catch (JSONException e)
        {
            return Response.error(new ParseError(e));
        }
    }
}
