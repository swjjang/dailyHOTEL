package com.twoheart.dailyhotel.util;

import android.net.Uri;

import com.daily.base.util.DailyTextUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class DailyDeepLink
{
    protected static final String HOST_INTERNAL_DAILYHOTEL = "internal.dailyhotel.co.kr";
    protected static final String HOST_DAILYHOTEL = "dailyhotel.co.kr";
    protected static final String HOST_KAKAOLINK = "kakaolink";

    public static final String STAY = "stay";
    public static final String GOURMET = "gourmet";
    public static final String STAY_OUTBOUND = "stayOutbound";

    protected Uri mDeepLinkUri;
    protected Map<String, String> mParamsMap;

    protected DailyDeepLink(Uri uri)
    {
        mParamsMap = new HashMap<>();

        setDeepLink(uri);
    }

    public static DailyDeepLink getNewInstance(Uri uri)
    {
        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (HOST_DAILYHOTEL.equalsIgnoreCase(host) == true || HOST_KAKAOLINK.equalsIgnoreCase(host) == true)
        {
            return new DailyExternalDeepLink(uri);
        } else if (HOST_INTERNAL_DAILYHOTEL.equalsIgnoreCase(host) == true)
        {
            return new DailyInternalDeepLink(uri);
        } else
        {
            return null;
        }
    }

    public abstract void setDeepLink(Uri uri);

    /**
     * 꼭 setDeepLink 후에 호출해야한다
     *
     * @return
     */
    public boolean isValidateLink()
    {
        return mDeepLinkUri != null;
    }

    public String getDeepLink()
    {
        return mDeepLinkUri.toString();
    }

    public void clear()
    {
        mDeepLinkUri = null;
        mParamsMap.clear();
    }

    protected boolean putParams(Uri uri, String param)
    {
        String value = uri.getQueryParameter(param);

        if (DailyTextUtils.isTextEmpty(value) == false)
        {
            mParamsMap.put(param, value);
            return true;
        } else
        {
            return false;
        }
    }

    public boolean isExternalDeepLink()
    {
        return this instanceof DailyExternalDeepLink;
    }

    public boolean isInternalDeepLink()
    {
        return this instanceof DailyInternalDeepLink;
    }
}
