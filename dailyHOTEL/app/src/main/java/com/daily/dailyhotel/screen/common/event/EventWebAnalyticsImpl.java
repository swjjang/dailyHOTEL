package com.daily.dailyhotel.screen.common.event;

import android.app.Activity;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventWebAnalyticsImpl implements EventWebInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, String eventName, EventWebActivity.EventType eventType, String eventUrl)
    {
        if (activity == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(eventName) == true)
        {
            eventName = AnalyticsManager.ValueType.EMPTY;
        }

        Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.EVENT_NAME, eventName);

        switch (eventType)
        {
            case EVENT:
                AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.EVENT_DETAIL, null);
                AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.EVENT_DETAIL, null, params);
                break;

            case HOME_EVENT:
                AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.HOME_EVENT_DETAIL, null);
                break;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.EVENTPAGE_SHARE, "entrance", eventUrl, null);
    }

    @Override
    public void onDownLoadCoupon(Activity activity, String couponCode, String validTo)
    {
        if (activity == null)
        {
            return;
        }

        try
        {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, AnalyticsManager.ValueType.EMPTY);
            paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, AnalyticsManager.ValueType.EMPTY);
            paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, "0");
            //                    paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, Util.simpleDateFormat(new Date(), "yyyyMMddHHmm"));
            paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(new Date(), "yyyyMMddHHmm"));
            //                    paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, Util.simpleDateFormatISO8601toFormat(validTo, "yyyyMMddHHmm"));
            paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"));
            paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "event");
            paramsMap.put(AnalyticsManager.KeyType.COUPON_CODE, couponCode);
            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.EMPTY);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "event-NULL", paramsMap);
        } catch (ParseException e)
        {
            Crashlytics.log("requestDownloadEventCoupon::CouponCode: " + couponCode + ", validTo: " + validTo);
            ExLog.d(e.toString());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onRecordDeepLink(Activity activity, DailyDeepLink dailyDeepLink)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordDeepLink(dailyDeepLink);
    }

    @Override
    public void onShareClick(Activity activity, String eventUrl)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.EVENTPAGE_SHARE, "share_button_click", eventUrl, null);
    }

    @Override
    public void onShareKakaoClick(Activity activity, String eventUrl)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.EVENTPAGE_SHARE, "share_kakao", eventUrl, null);
    }

    @Override
    public void onShareCopyLinkClick(Activity activity, String eventUrl)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.EVENTPAGE_SHARE, "share_link_copy", eventUrl, null);
    }

    @Override
    public void onShareSeeMoreClick(Activity activity, String eventUrl)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.EVENTPAGE_SHARE, "share_see_more", eventUrl, null);
    }
}
