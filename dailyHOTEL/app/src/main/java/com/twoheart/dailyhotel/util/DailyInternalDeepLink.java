package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.screen.main.MainActivity;

import java.util.Set;

public class DailyInternalDeepLink extends DailyDeepLink
{


    ///////////////////////////////////////////////////////////////////////////////////
    // DAILYHOTEL INTERNAL DEEP LINK

    // dailyhotel://internal.dailyhotel.co.kr
    ///////////////////////////////////////////////////////////////////////////////////

    private static final String PARAM_VIEW = "v";
    private static final String PARAM_PLACE_TYPE = "pt";
    private static final String PARAM_PLACE_NAME = "pn";
    private static final String PARAM_PAYMENT_METHOD = "pm";
    private static final String PARAM_CHECK_IN_TIME = "ci";
    private static final String PARAM_CHECK_OUT_TIME = "co";
    private static final String PARAM_VISIT_TIME = "vt";
    private static final String PARAM_AGGREGATION_ID = "aid";
    private static final String PARAM_RESERVATION_INDEX = "ri";
    private static final String PARAM_KEYWORD = "k";

    // 이동하는 딥링크 화면들
    private static final String VIEW_BOOKING_DETAIL = "bd"; // 예약 상세화면
    //    private static final String VIEW_STAMP = "stamp"; // 스탬프.
    private static final String VIEW_HOME = "home"; // 홈.

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

    private int getIntValue(String valueName)
    {
        if (DailyTextUtils.isTextEmpty(valueName) == true)
        {
            return 0;
        }

        int value = 0;

        String stringValue = mParamsMap.get(valueName);

        if (DailyTextUtils.isTextEmpty(stringValue) == false)
        {
            try
            {
                value = Integer.parseInt(stringValue);
            } catch (NumberFormatException e)
            {
            }
        }

        return value;
    }

    private String getView()
    {
        return mParamsMap.get(PARAM_VIEW);
    }

    public boolean isBookingDetailView()
    {
        return VIEW_BOOKING_DETAIL.equalsIgnoreCase(getView());
    }

    public boolean isHomeView()
    {
        return VIEW_HOME.equalsIgnoreCase(getView());
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

    public String getKeyword()
    {
        return mParamsMap.get(PARAM_KEYWORD);
    }

    public String getAggregationId()
    {
        return mParamsMap.get(PARAM_AGGREGATION_ID);
    }

    public int getReservationIndex()
    {
        return getIntValue(PARAM_RESERVATION_INDEX);
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

    private static Intent getIntent(Context context, Uri uri)
    {
        if (context == null || uri == null)
        {
            return null;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent.setData(uri);
    }

    public static Intent getStayOutboundBookingDetailScreenLink(Context context, String aggregationId)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?" + PARAM_VIEW + "=" + VIEW_BOOKING_DETAIL);
        stringBuilder.append("&" + PARAM_PLACE_TYPE + "=" + STAY_OUTBOUND);
        stringBuilder.append("&" + PARAM_AGGREGATION_ID + "=" + aggregationId);

        return getIntent(context, Uri.parse(stringBuilder.toString()));
    }

    public static Intent getStayOutboundBookingDetailScreenLink(Context context, int reservationIndex)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?" + PARAM_VIEW + "=" + VIEW_BOOKING_DETAIL);
        stringBuilder.append("&" + PARAM_PLACE_TYPE + "=" + STAY_OUTBOUND);
        stringBuilder.append("&" + PARAM_RESERVATION_INDEX + "=" + reservationIndex);

        return getIntent(context, Uri.parse(stringBuilder.toString()));
    }

    public static Intent getStayBookingDetailScreenLink(Context context, String aggregationId)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?" + PARAM_VIEW + "=" + VIEW_BOOKING_DETAIL);
        stringBuilder.append("&" + PARAM_PLACE_TYPE + "=" + STAY);
        stringBuilder.append("&" + PARAM_AGGREGATION_ID + "=" + aggregationId);

        return getIntent(context, Uri.parse(stringBuilder.toString()));
    }

    public static Intent getStayBookingDetailScreenLink(Context context, int reservationIndex)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?" + PARAM_VIEW + "=" + VIEW_BOOKING_DETAIL);
        stringBuilder.append("&" + PARAM_PLACE_TYPE + "=" + STAY);
        stringBuilder.append("&" + PARAM_RESERVATION_INDEX + "=" + reservationIndex);

        return getIntent(context, Uri.parse(stringBuilder.toString()));
    }

    public static Intent getGourmetBookingDetailScreenLink(Context context, String aggregationId)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?" + PARAM_VIEW + "=" + VIEW_BOOKING_DETAIL);
        stringBuilder.append("&" + PARAM_PLACE_TYPE + "=" + GOURMET);
        stringBuilder.append("&" + PARAM_AGGREGATION_ID + "=" + aggregationId);

        return getIntent(context, Uri.parse(stringBuilder.toString()));
    }

    public static Intent getGourmetBookingDetailScreenLink(Context context, int reservationIndex)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?" + PARAM_VIEW + "=" + VIEW_BOOKING_DETAIL);
        stringBuilder.append("&" + PARAM_PLACE_TYPE + "=" + GOURMET);
        stringBuilder.append("&" + PARAM_RESERVATION_INDEX + "=" + reservationIndex);

        return getIntent(context, Uri.parse(stringBuilder.toString()));
    }

    public static Intent getHomeScreenLink(Context context)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dailyhotel://");
        stringBuilder.append(HOST_INTERNAL_DAILYHOTEL);
        stringBuilder.append("?" + PARAM_VIEW + "=" + VIEW_HOME);

        return getIntent(context, Uri.parse(stringBuilder.toString()));
    }
}
