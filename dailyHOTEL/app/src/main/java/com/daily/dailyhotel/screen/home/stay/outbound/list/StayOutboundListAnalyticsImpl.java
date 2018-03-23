package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundListAnalyticsParam;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;

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
    public void onScreen(Activity activity, boolean empty)
    {
        if (activity == null)
        {
            return;
        }

        String screenName = empty ? "SearchResultView_Empty_ob" : "SearchResultView_ob";

        AnalyticsManager.getInstance(activity).recordScreen(activity, screenName, null);
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
    public void onEventList(Activity activity, StayBookDateTime stayBookDateTime, StayOutboundSuggest suggest, int size)
    {
        if (activity == null || stayBookDateTime == null || suggest == null || mAnalyticsParam == null)
        {
            return;
        }

        try
        {
            String keyword = DailyTextUtils.isTextEmpty(mAnalyticsParam.keyword) ? AnalyticsManager.ValueType.EMPTY : mAnalyticsParam.keyword;

            AnalyticsManager.getInstance(activity).recordEvent(size == 0 ? AnalyticsManager.Category.OB_SEARCH_NO_RESULT : AnalyticsManager.Category.OB_SEARCH_RESULT//
                , suggest.display, keyword, null);

            String category;

            switch (suggest.menuType)
            {
                case StayOutboundSuggest.MENU_TYPE_RECENTLY_SEARCH:
                case StayOutboundSuggest.MENU_TYPE_RECENTLY_STAY:
                    category = AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECENT;
                    break;

                case StayOutboundSuggest.MENU_TYPE_POPULAR_AREA:
                    category = AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND;
                    break;

                case StayOutboundSuggest.MENU_TYPE_SUGGEST:
                    category = AnalyticsManager.Category.OB_SEARCH_ORIGIN_AUTO;
                    break;

                default:
                    category = AnalyticsManager.Category.OB_SEARCH_ORIGIN_ETC;
                    break;
            }

            //            if (AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECENT.equalsIgnoreCase(category) == false //
            //                && AnalyticsManager.Category.OB_SEARCH_ORIGIN_AUTO.equalsIgnoreCase(category) == false//
            //                && AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND.equalsIgnoreCase(category) == false)
            //            {
            //                category = AnalyticsManager.Category.OB_SEARCH_ORIGIN_ETC;
            //            }

            AnalyticsManager.getInstance(activity).recordEvent(category, suggest.display, keyword, null);

            HashMap<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.COUNTRY, "outbound");
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookDateTime.getNights()));
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);

            if (mAnalyticsParam != null)
            {
                params.put(AnalyticsManager.KeyType.SEARCH_WORD, mAnalyticsParam.keyword);
            }

            params.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(size));
            params.put(AnalyticsManager.KeyType.SEARCH_RESULT, suggest.display);

            switch (suggest.menuType)
            {
                case StayOutboundSuggest.MENU_TYPE_LOCATION:
                {
                    params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AROUND);

                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                        , size == 0 ? "AroundSearchNotFound_LocationList_ob" : "AroundSearchClicked_LocationList_ob"//
                        , suggest.display, params);
                    break;
                }

                case StayOutboundSuggest.MENU_TYPE_RECENTLY_SEARCH:
                {
                    params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.RECENT);

                    String recentlyAction = size == 0 ? AnalyticsManager.Action.RECENT_KEYWORD_NOT_FOUND : AnalyticsManager.Action.RECENT_KEYWORD;

                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                        , recentlyAction + "_ob", suggest.display, params);
                    break;
                }

                case StayOutboundSuggest.MENU_TYPE_RECENTLY_STAY:
                {
                    params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.RECENT);

                    String recentlyAction = size == 0 ? "RecentSearchPlaceNotFound" : "RecentSearchPlace";

                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                        , recentlyAction + "_ob", suggest.display, params);
                    break;
                }

                case StayOutboundSuggest.MENU_TYPE_SUGGEST:
                {
                    params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AUTO);

                    String suggestCategory = size == 0 ? AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND : AnalyticsManager.Category.AUTO_SEARCH;

                    String suggestType;

                    switch (suggest.categoryKey)
                    {
                        case StayOutboundSuggest.CATEGORY_HOTEL:
                            suggestType = "업장";
                            break;

                        case StayOutboundSuggest.CATEGORY_POINT:
                            suggestType = "주요지점";
                            break;

                        case StayOutboundSuggest.CATEGORY_REGION:
                            suggestType = "도시/지역";
                            break;

                        case StayOutboundSuggest.CATEGORY_STATION:
                            suggestType = "역";
                            break;

                        default:
                            suggestType = "";
                            break;
                    }

                    AnalyticsManager.getInstance(activity).recordEvent(suggestCategory//
                        , "ob_" + suggestType + "_" + suggest.display, mAnalyticsParam.keyword, params);
                    break;
                }

                case StayOutboundSuggest.MENU_TYPE_POPULAR_AREA:
                {
                    String popularCategory = size == 0 ? AnalyticsManager.Category.OB_SEARCH_RECOMMEND_NO_RESULT : AnalyticsManager.Category.OB_SEARCH_RECOMMEND;

                    AnalyticsManager.getInstance(activity).recordEvent(popularCategory//
                        , suggest.display, null, params);
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
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
    public void onEventChangedRadius(Activity activity, String areaName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "ob_around_result_range_change", areaName, null);
    }

    @Override
    public void onEventResearchClick(Activity activity, StayOutboundSuggest suggest)
    {
        if (activity == null || suggest == null)
        {
            return;
        }

        switch (suggest.menuType)
        {
            case StayOutboundSuggest.MENU_TYPE_LOCATION:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , "ob_around_result_research", suggest.display, null);
                break;

            default:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , "ob_research", null, null);
                break;
        }
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
