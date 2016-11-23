package com.twoheart.dailyhotel.network.request;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class DailyHotelRequest<T> extends Request<T> implements Constants
{
    // uiLock을 띄우고 API를 콜하였는데 제한 시간 안에 리턴을 받지 못한경우. error 발생.
    private static final int REQUEST_EXPIRE_JUDGE = 60000;

    // Volley의 최대 retry 횟수,  여기서 0은 리퀘스트를 리트라이 하지 않음을 말함.
    private static final int REQUEST_MAX_RETRY = 0;

    private static final int SEED_LENGTH = 16;

    private Map<String, String> mParameters;
    private boolean mIsUsedAccept;

    public DailyHotelRequest(Object tag, int method, String url, Map<String, String> urlparameters, String parameters, ErrorListener errorListener)
    {
        this(method, getUrlDecoderEx(url, urlparameters) + parameters, errorListener);

        setTag(tag);
    }

    public DailyHotelRequest(Object tag, int method, String url, String parameters, ErrorListener errorListener)
    {
        this(method, getUrlDecoderEx(url) + parameters, errorListener);

        setTag(tag);
    }

    public DailyHotelRequest(Object tag, int method, String url, Map<String, String> urlparameters, Map<String, String> parameters, ErrorListener errorListener)
    {
        this(method, getUrlDecoderEx(url, urlparameters), errorListener);

        mParameters = parameters;

        setTag(tag);
    }

    public DailyHotelRequest(Object tag, int method, String url, Map<String, String> parameters, ErrorListener errorListener)
    {
        this(method, getUrlDecoderEx(url), errorListener);

        mParameters = parameters;

        setTag(tag);
    }

    public DailyHotelRequest(Object tag, int method, String url, ErrorListener listener)
    {
        this(method, getUrlDecoderEx(url), listener);

        setTag(tag);
    }

    private DailyHotelRequest(int method, String url, ErrorListener listener)
    {
        super(method, url, listener);

        //        if (DEBUG == true)
        //        {
        //            ExLog.d("Request Url : " + getUrl());
        //        }

        mIsUsedAccept = false;

        setRetryPolicy(new DefaultRetryPolicy(REQUEST_EXPIRE_JUDGE, REQUEST_MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public static String getUrlEncoder(final String url)
    {
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

    public static String getUrlDecoderEx(String url, Map<String, String> urlparameters)
    {
        if (urlparameters == null || urlparameters.size() == 0)
        {
            return getUrlDecoderEx(url);
        } else
        {
            StringBuilder decodeUrl = new StringBuilder(getUrlDecoderEx(url));

            ArrayList<String> keyArrayList = new ArrayList<>(urlparameters.keySet());

            for (String key : keyArrayList)
            {
                String value = urlparameters.get(key);

                if (Util.isTextEmpty(key, value) == false)
                {
                    int startIndex = decodeUrl.indexOf(key);
                    if (startIndex >= 0)
                    {
                        decodeUrl.replace(startIndex, startIndex + key.length(), value);
                    } else
                    {
                        throw new StringIndexOutOfBoundsException("getUrlDecoderEx - Failed decoding : " //
                            + decodeUrl + " , key : " + key + ", value : " + value + " , index : " + startIndex);
                    }
                } else
                {
                    throw new InvalidParameterException("Invalid url parameter : key : " + key + ", value : " + value);
                }
            }

            return decodeUrl.toString();
        }
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
        String decodeUrl = null;
        String[] text = url.split("\\$");

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

    public static String oldUrlDecrypt(String url)
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
            decodeUrl.append(getOldUrlDecoder(locatinoNumber + seperateUrl[i * 2 + 1]));
        }

        if (param != null)
        {
            decodeUrl.append(param);
        }

        return decodeUrl.toString();
    }

    private static String getOldUrlDecoder(String url)
    {
        final int OLD_SEED_LENGTH = 5;

        String decodeUrl = null;
        String[] text = url.split("\\$");

        StringBuilder seed = new StringBuilder();
        StringBuilder base64Url = new StringBuilder(text[OLD_SEED_LENGTH]);
        char[] alpha = new char[1];

        for (int i = OLD_SEED_LENGTH - 1; i >= 0; i--)
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
            decodeUrl = Crypto.oldDecrypt(seed.toString(), base64Url.toString());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return decodeUrl;
    }

    @Override
    protected abstract Response<T> parseNetworkResponse(NetworkResponse response);

    @Override
    protected abstract void deliverResponse(T response);

    @Override
    protected Map<String, String> getParams()
    {
        return mParameters;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("Os-Type", "android");
        map.put("App-Version", DailyHotel.VERSION);
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
