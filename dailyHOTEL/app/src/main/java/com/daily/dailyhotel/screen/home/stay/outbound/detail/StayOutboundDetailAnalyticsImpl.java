package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.List;

public class StayOutboundDetailAnalyticsImpl implements StayOutboundDetailPresenter.StayOutboundDetailAnalyticsInterface
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
    public StayOutboundDetailAnalyticsParam getAnalyticsParam(StayOutbound stayOutbound, String grade)
    {
        StayOutboundDetailAnalyticsParam analyticsParam = new StayOutboundDetailAnalyticsParam();

        if (stayOutbound != null)
        {
            analyticsParam.index = stayOutbound.index;
            analyticsParam.benefit = false;
            analyticsParam.rating = stayOutbound.tripAdvisorRating == 0.0f ? null : Float.toString(stayOutbound.tripAdvisorRating);
        }

        analyticsParam.grade = grade;
        analyticsParam.rankingPosition = -1;
        analyticsParam.listSize = -1;

        return analyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.CANCEL_DETAIL, null);
    }

    @Override
    public void onScreenRoomList(Activity activity, int stayIndex, boolean provideRewardSticker)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELROOMTYPELIST_OUTBOUND, null);

        if (DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerEnabled() && provideRewardSticker == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.ROOM_SELECTION, Integer.toString(stayIndex), null);
        }
    }

    @Override
    public void onEventHasRecommendList(Activity activity, boolean hasData)
    {
        if (activity == null)
        {
            return;
        }

        String action = hasData == true ? AnalyticsManager.Action.RECOMMEND_RESULT : AnalyticsManager.Action.NO_RECOMMEND_RESULT;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_RECOMMEND, action, null, null);
    }

    @Override
    public void onEventRecommendItemClick(Activity activity, int stayIndex, int clickStayIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_RECOMMEND_PRODUCT_CLICK //
            , Integer.toString(stayIndex), Integer.toString(clickStayIndex), null);
    }

    @Override
    public void onEventRecommendItemList(Activity activity, int stayIndex, List<Integer> stayIndexList)
    {
        if (activity == null || stayIndexList == null || stayIndexList.size() == 0)
        {
            return;
        }

        for (int index : stayIndexList)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_RECOMMEND_PRODUCT //
                , Integer.toString(stayIndex), Integer.toString(index), null);
        }
    }

    @Override
    public StayOutboundPaymentAnalyticsParam getPaymentAnalyticsParam(String grade, boolean nrd, boolean showOriginalPrice)
    {
        StayOutboundPaymentAnalyticsParam analyticsParam = new StayOutboundPaymentAnalyticsParam();

        analyticsParam.grade = grade;
        analyticsParam.nrd = nrd;
        analyticsParam.showOriginalPrice = showOriginalPrice;

        if (mAnalyticsParam != null)
        {
            analyticsParam.rankingPosition = mAnalyticsParam.rankingPosition;
            analyticsParam.rating = mAnalyticsParam.rating;
        }

        return analyticsParam;
    }
}
