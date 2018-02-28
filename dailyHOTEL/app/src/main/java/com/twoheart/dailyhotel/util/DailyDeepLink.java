package com.twoheart.dailyhotel.util;

import android.net.Uri;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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

    public String getDeepLink()
    {
        return mDeepLinkUri == null ? null : mDeepLinkUri.toString();
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

    public StayBookDateTime getStayBookDateTime(CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (commonDateTime == null || dailyDeepLink == null)
        {
            return null;
        }

        StayBookDateTime stayBookDateTime = null;

        if (dailyDeepLink.isInternalDeepLink() == true)
        {

        } else if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            int nights = 1;

            try
            {
                nights = Integer.parseInt(externalDeepLink.getNights());
            } catch (Exception e)
            {
                Crashlytics.log(externalDeepLink.getDeepLink());
                Crashlytics.logException(e);
            } finally
            {
                if (nights <= 0)
                {
                    nights = 1;
                }
            }

            try
            {
                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();
                String week = externalDeepLink.getWeek();

                if (DailyTextUtils.isTextEmpty(date) == false)
                {
                    if (Integer.parseInt(date) >= Integer.parseInt(DailyCalendar.convertDateFormatString(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd")))
                    {
                        Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));

                        stayBookDateTime = new StayBookDateTime();
                        stayBookDateTime.setCheckInDateTime(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                        stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);
                    }
                } else if (DailyTextUtils.isTextEmpty(week) == false)
                {
                    String searchDateTime = DailyCalendar.searchClosedDayOfWeek(commonDateTime.currentDateTime, week.toCharArray());

                    if (DailyTextUtils.isTextEmpty(searchDateTime) == false)
                    {
                        stayBookDateTime = new StayBookDateTime();
                        stayBookDateTime.setCheckInDateTime(searchDateTime);
                        stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);
                    }
                } else if (datePlus >= 0)
                {
                    stayBookDateTime = new StayBookDateTime();
                    stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime, datePlus);
                    stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);
                }
            } catch (Exception e)
            {
                Crashlytics.log(externalDeepLink.getDeepLink());
                Crashlytics.logException(e);
            }
        }

        try
        {
            if (stayBookDateTime == null)
            {
                stayBookDateTime = new StayBookDateTime();
                stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime);
                stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), 1);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return stayBookDateTime;
    }

    public GourmetBookDateTime getGourmetBookDateTime(CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (commonDateTime == null || dailyDeepLink == null)
        {
            return null;
        }

        GourmetBookDateTime gourmetBookDateTime = null;

        if (dailyDeepLink.isInternalDeepLink() == true)
        {

        } else if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;


            String date = externalDeepLink.getDate();
            int datePlus = externalDeepLink.getDatePlus();
            String week = externalDeepLink.getWeek();

            try
            {
                if (DailyTextUtils.isTextEmpty(date) == false)
                {
                    if (Integer.parseInt(date) > Integer.parseInt(DailyCalendar.convertDateFormatString(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd")))
                    {
                        Date visitDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));

                        gourmetBookDateTime = new GourmetBookDateTime(DailyCalendar.format(visitDate, DailyCalendar.ISO_8601_FORMAT));
                    }
                } else if (DailyTextUtils.isTextEmpty(week) == false)
                {
                    String searchDateTime = DailyCalendar.searchClosedDayOfWeek(commonDateTime.currentDateTime, week.toCharArray());

                    if (DailyTextUtils.isTextEmpty(searchDateTime) == false)
                    {
                        gourmetBookDateTime = new GourmetBookDateTime(searchDateTime);
                    }
                } else if (datePlus >= 0)
                {
                    gourmetBookDateTime = new GourmetBookDateTime();
                    gourmetBookDateTime.setVisitDateTime(commonDateTime.dailyDateTime, datePlus);
                }
            } catch (Exception e)
            {
                Crashlytics.log(externalDeepLink.getDeepLink());
                Crashlytics.logException(e);
            }
        }

        try
        {
            if (gourmetBookDateTime == null)
            {
                gourmetBookDateTime = new GourmetBookDateTime(commonDateTime.dailyDateTime);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return gourmetBookDateTime;
    }
}
