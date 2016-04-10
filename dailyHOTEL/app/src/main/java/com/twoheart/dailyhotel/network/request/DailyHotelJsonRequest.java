package com.twoheart.dailyhotel.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class DailyHotelJsonRequest extends DailyHotelRequest<JSONObject>
{
    private DailyHotelJsonResponseListener mListener;

    public DailyHotelJsonRequest(int method, String url, Map<String, String> parameters, DailyHotelJsonResponseListener listener, ErrorListener errorListener)
    {
        super(method, url, parameters, errorListener);

        mListener = listener;
    }

    public DailyHotelJsonRequest(Object object, int method, String url, Map<String, String> parameters, DailyHotelJsonResponseListener listener, ErrorListener errorListener)
    {
        super(object, method, url, parameters, errorListener);

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
        } catch (UnsupportedEncodingException e)
        {
            parsed = new String(response.data);
        }

        try
        {
            return Response.success(new JSONObject(parsed), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException je)
        {
            ExLog.e(parsed);
            return Response.error(new ParseError(je));
        }
    }
}
