package com.twoheart.dailyhotel.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.twoheart.dailyhotel.network.response.DailyHotelGsonResponseListener;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class DailyHotelGsonRequest<T> extends DailyHotelRequest<T>
{
    private Class<T> mClassOfT;
    private DailyHotelGsonResponseListener<T> mListener;

    public DailyHotelGsonRequest(Object tag, int method, Class<T> classOfT, //
                                 String url, Map<String, String> urlparameters, String parameters, //
                                 DailyHotelGsonResponseListener<T> listener)
    {
        super(tag, method, url, urlparameters, parameters, listener);

        mClassOfT = classOfT;
        mListener = listener;
    }

    public DailyHotelGsonRequest(Object tag, int method, Class<T> classOfT, //
                                 String url, Map<String, String> urlparameters, Map<String, String> parameters, //
                                 DailyHotelGsonResponseListener<T> listener)
    {
        super(tag, method, url, urlparameters, listener);

        mClassOfT = classOfT;
        mListener = listener;
    }

    public DailyHotelGsonRequest(Object tag, int method, Class<T> classOfT, //
                                 String url, String parameters, //
                                 DailyHotelGsonResponseListener<T> listener)
    {
        super(tag, method, url, parameters, listener);

        mClassOfT = classOfT;
        mListener = listener;
    }

    public DailyHotelGsonRequest(Object tag, int method, Class<T> classOfT, //
                                 String url, Map<String, String> parameters,//
                                 DailyHotelGsonResponseListener<T> listener)
    {
        super(tag, method, url, parameters, listener);

        mClassOfT = classOfT;
        mListener = listener;
    }

    public DailyHotelGsonRequest(Object tag, int method, Class<T> classOfT, //
                                 String url, //
                                 DailyHotelGsonResponseListener<T> listener)
    {
        super(tag, method, url, listener);

        mClassOfT = classOfT;
        mListener = listener;
    }

    @Override
    protected void deliverResponse(T response)
    {
        if (mListener != null)
        {
            mListener.onResponse(getUrl(), getParams(), response);
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new Gson().fromJson(parsed, mClassOfT), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e)
        {
            return Response.error(new ParseError(e));
        }
    }
}
