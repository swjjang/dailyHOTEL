package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        analyticsParam.name = stayOutbound.name;
        analyticsParam.nightlyRate = stayOutbound.nightlyRate;

        return analyticsParam;
    }

    @Override
    public void onScreen(Activity activity, String checkInDate, int nights)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.DBENEFIT, mAnalyticsParam.benefit ? "yes" : "no");
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
        params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.OVERSEAS);
        params.put(AnalyticsManager.KeyType.GRADE, mAnalyticsParam.grade);
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mAnalyticsParam.index));
        params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
        params.put(AnalyticsManager.KeyType.RATING, DailyTextUtils.isTextEmpty(mAnalyticsParam.rating) == true ? AnalyticsManager.ValueType.EMPTY : mAnalyticsParam.rating);
        params.put(AnalyticsManager.KeyType.PLACE_COUNT, mAnalyticsParam.listSize < 0 ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.listSize));
        params.put(AnalyticsManager.KeyType.NAME, mAnalyticsParam.name);
        params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(mAnalyticsParam.nightlyRate));
        params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, checkInDate);
        params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND, null, params);
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
    public void onEventBookingClick(Activity activity, int stayIndex, String stayName, String roomName //
        , int discountPrice, boolean provideRewardSticker, String checkInDate, int nights)
    {
        if (activity == null || mAnalyticsParam == null || DailyTextUtils.isTextEmpty(checkInDate) == true)
        {
            return;
        }

        String label = String.format(Locale.KOREA, "%s-%s", stayName, roomName);

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, mAnalyticsParam.name);
        params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mAnalyticsParam.index));

        params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.toString(discountPrice));
        params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, checkInDate);
        params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.OVERSEAS);

        // TODO : 임시 지정 - Eric.Ann 확인 후 재 지정
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.BOOKING_CLICKED, label, params);

        if (provideRewardSticker == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.ORDER_PROCEED, Integer.toString(stayIndex), null);
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
