package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class GourmetDetailAnalyticsImpl implements GourmetDetailPresenter.GourmetDetailAnalyticsInterface
{
    private StayOutboundDetailAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(StayOutboundDetailAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public StayOutboundDetailAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.DBENEFIT, mAnalyticsParam.benefit ? "yes" : "no");
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, "stay");
        params.put(AnalyticsManager.KeyType.COUNTRY, "overseas");
        params.put(AnalyticsManager.KeyType.GRADE, mAnalyticsParam.grade);
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mAnalyticsParam.index));
        params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
        params.put(AnalyticsManager.KeyType.RATING, DailyTextUtils.isTextEmpty(mAnalyticsParam.rating) == true ? AnalyticsManager.ValueType.EMPTY : mAnalyticsParam.rating);
        params.put(AnalyticsManager.KeyType.PLACE_COUNT, mAnalyticsParam.listSize < 0 ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.listSize));

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND, null, params);
    }

    @Override
    public void onScreenRoomList(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELROOMTYPELIST_OUTBOUND, null);
    }

    @Override
    public StayOutboundPaymentAnalyticsParam getPaymentAnalyticsParam(String grade, boolean nrd, boolean showOriginalPrice)
    {
        StayOutboundPaymentAnalyticsParam analyticsParam = new StayOutboundPaymentAnalyticsParam();

        analyticsParam.grade = grade;
        analyticsParam.nrd = nrd;
        analyticsParam.showOriginalPrice = showOriginalPrice;
        analyticsParam.rankingPosition = mAnalyticsParam.rankingPosition;
        analyticsParam.rating = mAnalyticsParam.rating;

        return analyticsParam;
    }
}
