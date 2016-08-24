package com.twoheart.dailyhotel.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class DailyHotelJsonRequest extends DailyHotelRequest<JSONObject>
{
    private DailyHotelJsonResponseListener mListener;

    public DailyHotelJsonRequest(Object tag, int method, String url, Map<String, String> urlparameters, String parameters, DailyHotelJsonResponseListener listener)
    {
        super(tag, method, url, urlparameters, parameters, listener);

        mListener = listener;
    }

    public DailyHotelJsonRequest(Object tag, int method, String url, Map<String, String> urlparameters, Map<String, String> parameters, DailyHotelJsonResponseListener listener)
    {
        super(tag, method, url, urlparameters, listener);

        mListener = listener;
    }

    public DailyHotelJsonRequest(Object tag, int method, String url, String parameters, DailyHotelJsonResponseListener listener)
    {
        super(tag, method, url, parameters, listener);

        mListener = listener;
    }

    public DailyHotelJsonRequest(Object tag, int method, String url, Map<String, String> parameters, DailyHotelJsonResponseListener listener)
    {
        super(tag, method, url, parameters, listener);

        mListener = listener;
    }

    public DailyHotelJsonRequest(Object tag, int method, String url, DailyHotelJsonResponseListener listener)
    {
        super(tag, method, url, listener);

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
        String parsed;

        try
        {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (OutOfMemoryError e)
        {
            return Response.error(new VolleyError(getUrl() + ":" + e.toString()));
        } catch (UnsupportedEncodingException ue)
        {
            parsed = new String(response.data);
        }

        try
        {
            return Response.success(new JSONObject(parsed), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException je)
        {
            return Response.error(new ParseError(je));
        }
    }
}
