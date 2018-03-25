package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundRoom;
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
    public void onScreen(Activity activity, StayBookDateTime stayBookDateTime, StayOutboundDetail stayOutboundDetail, List<StayOutboundRoom> roomList, int priceFromList)
    {
        if (activity == null || stayOutboundDetail == null || mAnalyticsParam == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, stayOutboundDetail.name);
            params.put(AnalyticsManager.KeyType.GRADE, mAnalyticsParam.grade); // 14
            params.put(AnalyticsManager.KeyType.DBENEFIT, mAnalyticsParam.benefit ? "yes" : "no"); // 3
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);

            if (roomList == null || roomList.size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(roomList.get(0).nightly));
            }

            int nights = stayBookDateTime.getNights();

            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayOutboundDetail.index)); // 15

            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")); // 1
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")); // 2

            params.put(AnalyticsManager.KeyType.ADDRESS, stayOutboundDetail.address);

            params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);

            params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(priceFromList));
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookDateTime.getCheckInDateTime("yyyyMMdd"));

            String listIndex = mAnalyticsParam.rankingPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.rankingPosition);
            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

            String placeCount = mAnalyticsParam.listSize < 0 ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.listSize);
            params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

            params.put(AnalyticsManager.KeyType.RATING, DailyTextUtils.isTextEmpty(mAnalyticsParam.rating) == true ? AnalyticsManager.ValueType.EMPTY : mAnalyticsParam.rating);
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.nightlyBaseRate > 0 ? "y" : "n");
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.dailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.OUTBOUND);

            boolean hasNRD = false;

            for (StayOutboundRoom stayOutboundRoom : roomList)
            {
                if (stayOutboundRoom.nonRefundable == true)
                {
                    hasNRD = true;
                    break;
                }
            }

            params.put(AnalyticsManager.KeyType.NRD, hasNRD ? "y" : "n");

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND, null, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
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
        if (activity == null || DailyTextUtils.isTextEmpty(checkInDate) == true)
        {
            return;
        }

        String label = String.format(Locale.KOREA, "%s-%s", stayName, roomName);

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, stayName);
        params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayIndex));

        params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.toString(discountPrice));
        params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, checkInDate);
        params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.OVERSEAS);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.BOOKING_CLICKED_OUTBOUND, label, params);

        if (provideRewardSticker == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.ORDER_PROCEED, Integer.toString(stayIndex), null);
        }
    }

    @Override
    public void onEventWishClick(Activity activity, int stayIndex, boolean isWish)
    {
        if (activity == null)
        {
            return;
        }

        String action = isWish ? AnalyticsManager.Action.WISHLIST_ON_DETAIL : AnalyticsManager.Action.WISHLIST_OFF_DETAIL;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_ //
            , action, Integer.toString(stayIndex), null);
    }

    @Override
    public void onEventShareKakaoClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.OB_ITEM_SHARE, AnalyticsManager.ValueType.KAKAO, null);
    }

    @Override
    public void onEventMoreShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.OB_ITEM_SHARE, AnalyticsManager.ValueType.ETC, null);
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
