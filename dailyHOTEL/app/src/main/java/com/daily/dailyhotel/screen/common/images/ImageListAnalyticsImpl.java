package com.daily.dailyhotel.screen.common.images;

import android.app.Activity;

import com.daily.dailyhotel.parcel.analytics.ImageListAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class ImageListAnalyticsImpl implements ImageListPresenter.ImageListAnalyticsInterface
{
    private ImageListAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(ImageListAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        switch (mAnalyticsParam.serviceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELIMAGEVIEW, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYGOURMET_GOURMETIMAGEVIEW, null);
                break;

            case OB_STAY:
                break;
        }
    }

    @Override
    public void onEventBackClick(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        switch (mAnalyticsParam.serviceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                    AnalyticsManager.Action.HOTEL_IMAGE_CLOSED, AnalyticsManager.Label.BACK, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                    AnalyticsManager.Action.GOURMET_IMAGE_CLOSED, AnalyticsManager.Label.BACK, null);
                break;

            case OB_STAY:
                break;
        }
    }

    @Override
    public void onEventSwipe(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        switch (mAnalyticsParam.serviceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                    AnalyticsManager.Action.HOTEL_IMAGE_CLOSED, AnalyticsManager.Label.SWIPE, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                    AnalyticsManager.Action.GOURMET_IMAGE_CLOSED, AnalyticsManager.Label.SWIPE, null);
                break;

            case OB_STAY:
                break;
        }
    }

    @Override
    public void onEventCloseClick(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        switch (mAnalyticsParam.serviceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                    AnalyticsManager.Action.HOTEL_IMAGE_CLOSED, AnalyticsManager.Label.CLOSE_, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                    AnalyticsManager.Action.GOURMET_IMAGE_CLOSED, AnalyticsManager.Label.CLOSE_, null);
                break;

            case OB_STAY:
                break;
        }
    }
}
