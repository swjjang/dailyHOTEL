package com.twoheart.dailyhotel.network.request;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class DailyHotelRequest_v2<T> extends Request<T> implements Constants
{
    protected static final String PROTOCOL_CHARSET = "UTF-8";
    private static final String PROTOCOL_CONTENT_TYPE = "application/json; charset=UTF-8";
    private String mRequestBody;

    // uiLock을 띄우고 API를 콜하였는데 제한 시간 안에 리턴을 받지 못한경우. error 발생.
    private static final int REQUEST_EXPIRE_JUDGE = 60000;

    // Volley의 최대 retry 횟수,  여기서 0은 리퀘스트를 리트라이 하지 않음을 말함.
    private static final int REQUEST_MAX_RETRY = 0;

    private boolean mIsUsedAuthorization;
    private boolean mIsUsedAccept;

    public DailyHotelRequest_v2(Object tag, int method, String url, Map<String, Object> params, ErrorListener errorListener)
    {
        super(method, getUrlDecoderEx(url), errorListener);

        setTag(tag);

        mRequestBody = new JSONObject(params).toString();
        mIsUsedAuthorization = false;
        mIsUsedAccept = false;

        setRetryPolicy(new DefaultRetryPolicy(REQUEST_EXPIRE_JUDGE, REQUEST_MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected abstract Response<T> parseNetworkResponse(NetworkResponse response);

    @Override
    protected abstract void deliverResponse(T response);

    /**
     * @deprecated
     */
    public String getPostBodyContentType()
    {
        return this.getBodyContentType();
    }

    /**
     * @deprecated
     */
    public byte[] getPostBody()
    {
        return this.getBody();
    }

    @Override
    public String getBodyContentType()
    {
        return PROTOCOL_CONTENT_TYPE;
    }

    public byte[] getBody()
    {
        try
        {
            return this.mRequestBody == null ? null : this.mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("Os-Type", "android");
        map.put("App-Version", DailyHotel.VERSION);

        if (mIsUsedAccept == true)
        {
            map.put("accept", "application/json");
            map.put("Content-Type", "application/json; charset=UTF-8");
        }

        if (mIsUsedAuthorization == true && Util.isTextEmpty(DailyHotel.AUTHORIZATION) == false)
        {
            map.put("Authorization", DailyHotel.AUTHORIZATION);
        }

        return map;
    }

    public void setUsedAuthorization(boolean isUsed)
    {
        mIsUsedAuthorization = isUsed;
    }

    public void setIsUsedAccept(boolean isUsed)
    {
        mIsUsedAccept = isUsed;
    }

    public static void makeUrlEncoder()
    {
        DailyHotelRequest_v2.getUrlEncoder("");
    }

    private static String getUrlEncoder(final String url)
    {
        final int SEED_LENGTH = 5;
        StringBuilder encodeUrl = new StringBuilder();
        StringBuilder seedLocationNumber = new StringBuilder();

        try
        {
            String alphas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            Random random = new Random(System.currentTimeMillis());
            StringBuilder seed = new StringBuilder();

            for (int i = 0; i < SEED_LENGTH; i++)
            {
                int number = random.nextInt(alphas.length());
                seed.append(alphas.charAt(number));
            }

            String firstUrl = Crypto.encrypt(seed.toString(), url);
            encodeUrl.append(firstUrl);

            for (int i = 0; i < SEED_LENGTH; i++)
            {
                int number = random.nextInt(encodeUrl.length());

                encodeUrl.insert(number, seed.charAt(i));
                seedLocationNumber.append(number).append('$');
            }

            String base64LocationNumber = Base64.encodeToString(seedLocationNumber.toString().getBytes(), Base64.NO_WRAP);
            encodeUrl.insert(0, base64LocationNumber + "$");
            encodeUrl.append('$');

            ExLog.d("encoderUrl : " + encodeUrl.toString());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return encodeUrl.toString();
    }

    public static String getUrlDecoderEx(String url)
    {
        if (Constants.UNENCRYPTED_URL == true)
        {
            return url;
        }

        String param = null;
        String encoderUrl;

        if (url.contains("/") == true)
        {
            int index = url.indexOf('/');
            param = url.substring(index);
            encoderUrl = url.substring(0, index);
        } else if (url.contains("?") == true)
        {
            int index = url.indexOf('?');
            param = url.substring(index);
            encoderUrl = url.substring(0, index);
        } else
        {
            encoderUrl = url;
        }

        StringBuilder decodeUrl = new StringBuilder();
        String[] seperateUrl = encoderUrl.split("\\$");

        int count = seperateUrl.length / 2;

        // 앞의것 2개는 Url, 뒤의것 2개는 API
        for (int i = 0; i < count; i++)
        {
            String locatinoNumber = new String(Base64.decode(seperateUrl[i * 2], Base64.NO_WRAP));
            decodeUrl.append(getUrlDecoder(locatinoNumber + seperateUrl[i * 2 + 1]));
        }

        if (param != null)
        {
            decodeUrl.append(param);
        }

        return decodeUrl.toString();
    }

    private static String getUrlDecoder(String url)
    {
        final int SEED_LENGTH = 5;

        String decodeUrl = null;
        String[] text = url.split("\\$");

        // 앞의 5개가 위치키이다.
        StringBuilder seed = new StringBuilder();
        StringBuilder base64Url = new StringBuilder(text[SEED_LENGTH]);
        char[] alpha = new char[1];

        for (int i = SEED_LENGTH - 1; i >= 0; i--)
        {
            try
            {
                int location = Integer.parseInt(text[i]);

                base64Url.getChars(location, location + 1, alpha, 0);
                base64Url.delete(location, location + 1);

                seed.insert(0, alpha);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        try
        {
            decodeUrl = Crypto.decrypt(seed.toString(), base64Url.toString());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return decodeUrl;
    }

    public static String urlEncrypt(final String url)
    {
        final int SEED_LENGTH = 5;
        StringBuilder encodeUrl = new StringBuilder();
        StringBuilder seedLocationNumber = new StringBuilder();

        try
        {
            String alphas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            Random random = new Random(System.currentTimeMillis());
            StringBuilder seed = new StringBuilder();

            for (int i = 0; i < SEED_LENGTH; i++)
            {
                int number = random.nextInt(alphas.length());
                seed.append(alphas.charAt(number));
            }

            String firstUrl = Crypto.encrypt(seed.toString(), url);
            encodeUrl.append(firstUrl);

            for (int i = 0; i < SEED_LENGTH; i++)
            {
                int number = random.nextInt(encodeUrl.length());

                encodeUrl.insert(number, seed.charAt(i));
                seedLocationNumber.append(number).append('$');
            }

            String base64LocationNumber = Base64.encodeToString(seedLocationNumber.toString().getBytes(), Base64.NO_WRAP);
            encodeUrl.insert(0, base64LocationNumber + "$");
            encodeUrl.append('$');

            ExLog.d(url + " : " + encodeUrl.toString());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return encodeUrl.toString();
    }

    public static String urlDecrypt(String url)
    {
        if (Util.isTextEmpty(url) == true)
        {
            return null;
        }

        String param = null;
        String encoderUrl;

        if (url.contains("/") == true)
        {
            int index = url.indexOf('/');
            param = url.substring(index);
            encoderUrl = url.substring(0, index);
        } else if (url.contains("?") == true)
        {
            int index = url.indexOf('?');
            param = url.substring(index);
            encoderUrl = url.substring(0, index);
        } else
        {
            encoderUrl = url;
        }

        StringBuilder decodeUrl = new StringBuilder();
        String[] seperateUrl = encoderUrl.split("\\$");

        int count = seperateUrl.length / 2;

        // 앞의것 2개는 Url, 뒤의것 2개는 API
        for (int i = 0; i < count; i++)
        {
            String locatinoNumber = new String(Base64.decode(seperateUrl[i * 2], Base64.NO_WRAP));
            decodeUrl.append(getUrlDecoder(locatinoNumber + seperateUrl[i * 2 + 1]));
        }

        if (param != null)
        {
            decodeUrl.append(param);
        }

        return decodeUrl.toString();
    }
}
