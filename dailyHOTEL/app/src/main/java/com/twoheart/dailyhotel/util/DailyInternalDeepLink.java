package com.twoheart.dailyhotel.util;

import android.net.Uri;

import com.twoheart.dailyhotel.model.PlacePaymentInformation;

import java.net.URLEncoder;
import java.util.Set;

public class DailyInternalDeepLink extends DailyDeepLink
{


    ///////////////////////////////////////////////////////////////////////////////////
    // DAILYHOTEL INTERNAL DEEP LINK
    ///////////////////////////////////////////////////////////////////////////////////

    private static final String PARAM_VIEW = "v";
    private static final String PARAM_PLACE_TYPE = "pt";
    private static final String PARAM_PLACE_NAME = "pn";
    private static final String PARAM_PAYMENT_METHOD = "pm";
    private static final String PARAM_CHECK_IN_TIME = "ci";
    private static final String PARAM_CHECK_OUT_TIME = "co";
    private static final String PARAM_VISIT_TIME = "vt";

    private static final String VIEW_BOOKING_DETAIL = "bd"; // 예약 상세화면
    private static final String VIEW_STAMP = "stamp"; // 스탬프.

    public DailyInternalDeepLink(Uri uri)
    {
        super(uri);
    }

    @Override
    public void setDeepLink(Uri uri)
    {
        if (uri == null)
        {
            clear();
            return;
        }

        mDeepLinkUri = uri;

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (HOST_INTERNAL_DAILYHOTEL.equalsIgnoreCase(host) == true)
        {
            decodingLink(uri);
        } else
        {
            clear();
        }
    }

    @Override
    public void clear()
    {
        super.clear();
    }

    private String getView()
    {
        return mParamsMap.get(PARAM_VIEW);
    }

    public boolean isBookingDetailView()
    {
        String view = getView();

        return VIEW_BOOKING_DETAIL.equalsIgnoreCase(view);
    }

    public boolean isStampView()
    {
        String view = getView();

        return VIEW_STAMP.equalsIgnoreCase(view);
    }

    public String getPlaceType()
    {
        return mParamsMap.get(PARAM_PLACE_TYPE);
    }

    public String getPlaceName()
    {
        return mParamsMap.get(PARAM_PLACE_NAME);
    }

    public String getPaymentType()
    {
        return mParamsMap.get(PARAM_PAYMENT_METHOD);
    }

    public String getCheckInTime()
    {
        return mParamsMap.get(PARAM_CHECK_IN_TIME);
    }

    public String getCheckOutTime()
    {
        return mParamsMap.get(PARAM_CHECK_OUT_TIME);
    }

    public String getVisitTime()
    {
        return mParamsMap.get(PARAM_VISIT_TIME);
    }

    private boolean decodingLink(Uri uri)
    {
        mParamsMap.clear();

        if (uri == null)
        {
            clear();
            return false;
        }

        Set<String> keySet = uri.getQueryParameterNames();
        if (keySet == null || keySet.isEmpty() == true)
        {
            clear();
            return false;
        }

        for (String key : keySet)
        {
            putParams(uri, key);
        }

        return true;
    }

    public static String getStayBookingDetailScreenLink(String placeName//
        , PlacePaymentInformation.PaymentType paymentType, String checkInTime, String checkOutTime)
    {
        if (Util.isTextEmpty(placeName, checkInTime, checkOutTime) == true || paymentType == null)
        {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?v=bd");
        stringBuilder.append("&pt=stay");
        stringBuilder.append("&pn=" + URLEncoder.encode(placeName));
        stringBuilder.append("&pm=" + URLEncoder.encode(paymentType.name()));
        stringBuilder.append("&ci=" + URLEncoder.encode(checkInTime));
        stringBuilder.append("&co=" + URLEncoder.encode(checkOutTime));

        return stringBuilder.toString();
    }

    public static String getGourmetBookingDetailScreenLink(String placeName//
        , PlacePaymentInformation.PaymentType paymentType, String visitTime)
    {
        if (Util.isTextEmpty(placeName, visitTime) == true || paymentType == null)
        {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?v=bd");
        stringBuilder.append("&pt=gourmet");
        stringBuilder.append("&pn=" + URLEncoder.encode(placeName));
        stringBuilder.append("&pm=" + URLEncoder.encode(paymentType.name()));
        stringBuilder.append("&vt=" + URLEncoder.encode(visitTime));

        return stringBuilder.toString();
    }

    public static String getStampScreenLink()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?v=stamp");

        return stringBuilder.toString();
    }
}