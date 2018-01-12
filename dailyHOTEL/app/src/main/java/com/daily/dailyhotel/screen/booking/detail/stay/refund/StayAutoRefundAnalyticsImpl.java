package com.daily.dailyhotel.screen.booking.detail.stay.refund;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayAutoRefundAnalyticsImpl implements StayAutoRefundPresenter.StayAutoRefundAnalyticsInterface
{
    @Override
    public void onRefundPositiveButtonClick(Activity activity, String roomName, int price, boolean isOverseas, String cancelMessage)
    {
        if (activity == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, roomName);
        params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(price));
        params.put(AnalyticsManager.KeyType.COUNTRY, isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);

        // Analytics 전송 시 메세지는 빼고 보냄 - 사유만 보냄
        params.put(AnalyticsManager.KeyType.REASON_CANCELLATION, cancelMessage);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.FREE_CANCELLATION, cancelMessage, params);
    }
}
