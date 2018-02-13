package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundListAnalyticsParam;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayOutboundListAnalyticsImpl implements StayOutboundListPresenter.StayOutboundListAnalyticsInterface
{
    private StayOutboundListAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(StayOutboundListAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public StayOutboundListAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELLIST_OUTBOUND, null);
    }

    @Override
    public void onEventStayClick(Activity activity, int index, boolean provideRewardSticker, boolean dailyChoice)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.STAY_ITEM_CLICK_OUTBOUND, Integer.toString(index), null);

        if (DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerEnabled() && provideRewardSticker == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.THUMBNAIL_CLICK, Integer.toString(index), null);
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , dailyChoice ? AnalyticsManager.Action.OB_STAY_DAILYCHOICE_CLICK_Y : AnalyticsManager.Action.OB_STAY_DAILYCHOICE_CLICK_N, Integer.toString(index), null);
    }

    @Override
    public void onEventDestroy(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCHRESULTVIEW_OUTBOUND, AnalyticsManager.Label.BACK_BUTTON, null);
    }

    @Override
    public void onEventList(Activity activity, StayOutboundSuggest suggest, int size)
    {
        if (activity == null || suggest == null || mAnalyticsParam == null)
        {
            return;
        }

        String category = mAnalyticsParam.analyticsClickType;
        String keyword = DailyTextUtils.isTextEmpty(mAnalyticsParam.keyword) ? AnalyticsManager.ValueType.EMPTY : mAnalyticsParam.keyword;

        AnalyticsManager.getInstance(activity).recordEvent(size == 0 ? AnalyticsManager.Category.OB_SEARCH_NO_RESULT : AnalyticsManager.Category.OB_SEARCH_RESULT//
            , suggest.display, keyword, null);

        if (AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECENT.equalsIgnoreCase(category) == false //
            && AnalyticsManager.Category.OB_SEARCH_ORIGIN_AUTO.equalsIgnoreCase(category) == false//
            && AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND.equalsIgnoreCase(category) == false)
        {
            category = AnalyticsManager.Category.OB_SEARCH_ORIGIN_ETC;
        }

        AnalyticsManager.getInstance(activity).recordEvent(category, suggest.display, keyword, null);

        if (StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(suggest.categoryKey) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , size == 0 ? "AroundSearchNotFound_LocationList_ob" : "AroundSearchClicked_LocationList_ob"//
                , suggest.display, null);
        }

        switch (suggest.menuType)
        {
            case StayOutboundSuggest.MENU_TYPE_RECENTLY_SEARCH:
            {
                String recentlyAction = size == 0 ? AnalyticsManager.Action.RECENT_KEYWORD_NOT_FOUND : AnalyticsManager.Action.RECENT_KEYWORD;

                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , recentlyAction, suggest.display, null);

                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , recentlyAction + "_ob", suggest.display, null);
                break;
            }

            case StayOutboundSuggest.MENU_TYPE_SUGGEST:
            {
                String suggestCategory = size == 0 ? AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND : AnalyticsManager.Category.AUTO_SEARCH;

                AnalyticsManager.getInstance(activity).recordEvent(suggestCategory//
                    , suggest.display, mAnalyticsParam.keyword, null);
                break;
            }

            case StayOutboundSuggest.MENU_TYPE_POPULAR_AREA:
            {
                String popularCategory = size == 0 ? AnalyticsManager.Category.OB_SEARCH_RECOMMEND_NO_RESULT : AnalyticsManager.Category.OB_SEARCH_RECOMMEND;

                AnalyticsManager.getInstance(activity).recordEvent(popularCategory//
                    , suggest.display, null, null);
                break;
            }
        }
    }

    @Override
    public void onEventWishClick(Activity activity, int stayIndex, boolean isWish)
    {
        if (activity == null)
        {
            return;
        }

        String action = isWish ? AnalyticsManager.Action.WISHLIST_ON_LIST : AnalyticsManager.Action.WISHLIST_OFF_LIST;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , action, Integer.toString(stayIndex), null);
    }

    @Override
    public void onEventMapClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_MAP, null);
    }

    @Override
    public void onEventFilterClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.VIEWTYPE_LIST, null);
    }

    @Override
    public void onEventCalendarClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.Label.OB, null);
    }

    @Override
    public void onEventPeopleClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.MEMBER_SELECT, null, null);
    }

    @Override
    public void onEventStayClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen", "ob_stay", null);
    }

    @Override
    public void onEventGourmetClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen", "ob_gourmet", null);
    }

    @Override
    public void onEventPopularAreaClick(Activity activity, String areaName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen_location_ob", areaName, null);
    }

    @Override
    public StayOutboundDetailAnalyticsParam getDetailAnalyticsParam(StayOutbound stayOutbound, String grade, int rankingPosition, int listSize)
    {
        StayOutboundDetailAnalyticsParam analyticsParam = new StayOutboundDetailAnalyticsParam();

        if (stayOutbound != null)
        {
            analyticsParam.index = stayOutbound.index;
            analyticsParam.benefit = false;
            analyticsParam.rating = stayOutbound.tripAdvisorRating == 0.0f ? null : Float.toString(stayOutbound.tripAdvisorRating);
        }

        analyticsParam.grade = grade;
        analyticsParam.rankingPosition = rankingPosition;
        analyticsParam.listSize = listSize;
        analyticsParam.dailyChoice = stayOutbound.dailyChoice;
        analyticsParam.nightlyRate = stayOutbound.nightlyRate;
        analyticsParam.nightlyBaseRate = stayOutbound.nightlyBaseRate;

        return analyticsParam;
    }
}
