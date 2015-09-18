package com.twoheart.dailyhotel.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonArrayResponseListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class DailyHotelJsonArrayRequest extends DailyHotelRequest<JSONArray>
{

    private DailyHotelJsonArrayResponseListener mListener;

    public DailyHotelJsonArrayRequest(int method, String url, Map<String, String> parameters, DailyHotelJsonArrayResponseListener listener, ErrorListener errorListener)
    {
        super(method, url, parameters, errorListener);
        this.mListener = listener;
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
        try
        {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        } catch (JSONException je)
        {
            return Response.error(new ParseError(je));
        }
    }

}
