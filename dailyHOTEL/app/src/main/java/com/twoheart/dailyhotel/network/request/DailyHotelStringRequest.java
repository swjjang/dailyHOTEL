package com.twoheart.dailyhotel.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class DailyHotelStringRequest extends DailyHotelRequest<String>
{
    private DailyHotelStringResponseListener mListener;

    public DailyHotelStringRequest(Object tag, int method, String url, Map<String, String> parameters, DailyHotelStringResponseListener listener, ErrorListener errorListener)
    {

        super(tag, method, url, parameters, errorListener);
        this.mListener = listener;
    }

    @Override
    protected void deliverResponse(String response)
    {
        if (mListener != null)
        {
            mListener.onResponse(getUrl(), response);
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response)
    {

        String parsed;
        try
        {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e)
        {
            parsed = new String(response.data);
        }
        return Response.success(parsed.trim(), HttpHeaderParser.parseCacheHeaders(response));
    }

}
