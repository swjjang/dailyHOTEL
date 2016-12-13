package com.twoheart.dailyhotel.network.request;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.network.response.DailyJsonResponseListener;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DailyJsonObjectRequest extends Request<JSONObject> implements Constants
{
    // uiLock을 띄우고 API를 콜하였는데 제한 시간 안에 리턴을 받지 못한경우. error 발생.
    private static final int REQUEST_EXPIRE_JUDGE = 60000;

    // Volley의 최대 retry 횟수,  여기서 0은 리퀘스트를 리트라이 하지 않음을 말함.
    private static final int REQUEST_MAX_RETRY = 0;

    private JSONObject mParameters;
    private boolean mIsUsedAccept;

    private DailyJsonResponseListener mListener;

    public DailyJsonObjectRequest(Object tag, int method, String url, Map<String, String> urlparameters, JSONObject parameters, DailyJsonResponseListener listener)
    {
        this(method, DailyHotelRequest.getUrlDecoderEx(url, urlparameters), listener);

        mParameters = parameters;

        setTag(tag);
    }

    public DailyJsonObjectRequest(Object tag, int method, String url, JSONObject parameters, DailyJsonResponseListener listener)
    {
        this(method, DailyHotelRequest.getUrlDecoderEx(url), listener);

        mParameters = parameters;

        setTag(tag);
    }

    public DailyJsonObjectRequest(Object tag, int method, String url, DailyJsonResponseListener listener)
    {
        this(method, DailyHotelRequest.getUrlDecoderEx(url), listener);

        setTag(tag);
    }

    private DailyJsonObjectRequest(int method, String url, DailyJsonResponseListener listener)
    {
        super(method, url, listener);

        mListener = listener;
        mIsUsedAccept = false;

        setRetryPolicy(new DefaultRetryPolicy(REQUEST_EXPIRE_JUDGE, REQUEST_MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected void deliverResponse(JSONObject response)
    {
        if (mListener != null)
        {
            mListener.onResponse(getUrl(), mParameters, response);
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

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        if (mParameters == null)
        {
            return null;
        }

        try
        {
            return mParameters.toString().getBytes(getParamsEncoding());
        } catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), e);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("Os-Type", "android");
        map.put("App-Version", DailyHotel.VERSION_CODE);
        map.put("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID);

        if (mIsUsedAccept == true)
        {
            map.put("Accept", "application/json;charset=UTF-8");
            map.put("Content-type", "application/json;charset=UTF-8");
        }

        if (DailyHotel.isLogin() == true)
        {
            map.put("Authorization", DailyHotel.AUTHORIZATION);
        }

        return map;
    }

    public void setIsUsedAccept(boolean isUsed)
    {
        mIsUsedAccept = isUsed;
    }
}
