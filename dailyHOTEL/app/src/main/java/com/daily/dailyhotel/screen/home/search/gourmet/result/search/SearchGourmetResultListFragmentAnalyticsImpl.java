package com.daily.dailyhotel.screen.home.search.gourmet.result.search;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabPresenter;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class SearchGourmetResultListFragmentAnalyticsImpl implements SearchGourmetResultListFragmentInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, SearchGourmetResultTabPresenter.ViewType viewType, GourmetBookDateTime gourmetBookDateTime, boolean empty)
    {
        if (activity == null || gourmetBookDateTime == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();

            params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.GOURMET);

            String screen = empty ? AnalyticsManager.Screen.SEARCH_RESULT_EMPTY : AnalyticsManager.Screen.SEARCH_RESULT;

            AnalyticsManager.getInstance(activity).recordScreen(activity, screen + "_gourmet", null, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onEventWishClick(Activity activity, boolean wish)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
            , AnalyticsManager.Action.WISH_GOURMET, wish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
    }

    @Override
    public void onEventMarkerClick(Activity activity, String name)
    {

    }

    @Override
    public void onEventCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CALL, null);
    }

    @Override
    public void onEventGourmetClick(Activity activity, Gourmet gourmet, GourmetSuggest suggest)
    {
        if (activity == null || gourmet == null || suggest == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.GOURMET_ITEM_CLICK, Integer.toString(gourmet.index), null);

        // 할인 쿠폰이 보이는 경우
        if (DailyTextUtils.isTextEmpty(gourmet.couponDiscountText) == false)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.COUPON_GOURMET, Integer.toString(gourmet.index), null);
        }

        if (gourmet.reviewCount > 0)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.TRUE_REVIEW_GOURMET, Integer.toString(gourmet.index), null);
        }

        if (gourmet.discountRate > 0)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.DISCOUNT_GOURMET, Integer.toString(gourmet.index), null);
        }

        if (gourmet.soldOut == true)
        {
            switch (suggest.menuType)
            {
                case LOCATION:
                case REGION_LOCATION:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                        , AnalyticsManager.Action.NEARBY, Integer.toString(gourmet.index), null);
                    break;

                case SUGGEST:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                        , AnalyticsManager.Action.AUTO_SEARCH, Integer.toString(gourmet.index), null);
                    break;

                case DIRECT:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                        , AnalyticsManager.Action.KEYWORD, Integer.toString(gourmet.index), null);
                    break;

                case RECENTLY_GOURMET:
                case RECENTLY_SEARCH:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                        , AnalyticsManager.Action.RECENT, Integer.toString(gourmet.index), null);
                    break;
            }
        }
    }

    @Override
    public void onEventSearchResult(Activity activity, GourmetBookDateTime gourmetBookDateTime, GourmetSuggest suggest, String inputKeyword//
        , int searchCount, int searchMaxCount)
    {
        if (activity == null || gourmetBookDateTime == null || suggest == null)
        {
            return;
        }

        boolean empty = searchCount == 0;

        Map<String, String> params = new HashMap<>();
        try
        {
            params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);

            params.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(searchCount > searchMaxCount ? searchMaxCount : searchCount));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        if (suggest.isLocationSuggestType() == true)
        {
            params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AROUND);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , empty ? "AroundSearchNotFound_LocationList_gourmet" : "AroundSearchClicked_LocationList_gourmet"//
                , suggest.getText1(), params);
        }

        String displayName = suggest.getText1();

        switch (suggest.menuType)
        {
            case RECENTLY_SEARCH:
                recordEventSearchResultByRecentKeyword(activity, displayName, empty, params);
                break;

            case RECENTLY_GOURMET:
                recordEventSearchResultByRecentGourmet(activity, displayName, empty, params);
                break;

            case DIRECT:
                recordEventSearchResultByKeyword(activity, displayName, empty, params);
                break;

            case SUGGEST:
                recordEventSearchResultByAutoSearch(activity, suggest, inputKeyword, empty, params);
                break;
        }
    }

    @Override
    public void onEventSearchResultCountOneAndSoldOut(Activity activity, String gourmetName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , "gourmet_selling_completion", gourmetName, null);
    }

    @Override
    public void onEventSearchResultAllSoldOut(Activity activity, String inputKeyword)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , "gourmet_selling_completion", inputKeyword, null);
    }

    private void recordEventSearchResultByRecentKeyword(Activity activity, String displayName, boolean empty, Map<String, String> params)
    {
        String action = empty ? AnalyticsManager.Action.RECENT_KEYWORD_NOT_FOUND : AnalyticsManager.Action.RECENT_KEYWORD;

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.RECENT);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, displayName);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, displayName);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , action + "_gourmet", displayName, params);
    }

    private void recordEventSearchResultByRecentGourmet(Activity activity, String displayName, boolean empty, Map<String, String> params)
    {
        String action = empty ? "RecentSearchPlaceNotFound" : "RecentSearchPlace";

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.RECENT);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, displayName);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, displayName);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , action + "_gourmet", displayName, params);
    }

    private void recordEventSearchResultByKeyword(Activity activity, String displayName, boolean empty, Map<String, String> params)
    {
        String action = empty ? AnalyticsManager.Action.KEYWORD_NOT_FOUND : AnalyticsManager.Action.KEYWORD_;

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.DIRECT);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, displayName);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, displayName);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , action + "_gourmet", displayName, params);
    }

    private void recordEventSearchResultByAutoSearch(Activity activity, GourmetSuggest suggest, String inputKeyword, boolean empty, Map<String, String> params)
    {
        if (activity == null || suggest == null || params == null)
        {
            return;
        }

        String category = empty ? AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND : AnalyticsManager.Category.AUTO_SEARCH;
        String displayName = suggest.getText1();

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AUTO);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, inputKeyword);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, displayName);

        String suggestType;

        switch (suggest.getSuggestType())
        {
            case GOURMET:
                suggestType = "업장";
                break;

            case AREA_GROUP:
                suggestType = "도시/지역";
                break;

            default:
                suggestType = "";
                break;
        }

        AnalyticsManager.getInstance(activity).recordEvent(category//
            , "gourmet_" + suggestType + "_" + displayName, inputKeyword, params);
    }
}
