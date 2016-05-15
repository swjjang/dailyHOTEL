package com.twoheart.dailyhotel.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class DailyHotelJsonArrayRequest extends DailyHotelRequest<JSONArray>
{
    private DailyHotelJsonArrayResponseListener mListener;

    public DailyHotelJsonArrayRequest(Object object, int method, String url, Map<String, String> parameters, DailyHotelJsonArrayResponseListener listener, ErrorListener errorListener)
    {
        super(object, method, url, parameters, errorListener);

        mListener = listener;
    }

    @Override
    protected void deliverResponse(JSONArray response)
    {
        if (mListener != null)
        {
            mListener.onResponse(getUrl(), response);
        }
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response)
    {
        String parsed;

        try
        {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e)
        {
            parsed = new String(response.data);
        }

        try
        {
            return Response.success(new JSONArray(parsed), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException je)
        {
            ExLog.e(parsed);
            return Response.error(new ParseError(je));
        }
    }
}
