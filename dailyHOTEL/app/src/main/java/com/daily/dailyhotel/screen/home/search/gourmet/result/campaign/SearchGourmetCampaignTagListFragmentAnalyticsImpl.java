package com.daily.dailyhotel.screen.home.search.gourmet.result.campaign;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabPresenter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class SearchGourmetCampaignTagListFragmentAnalyticsImpl implements SearchGourmetCampaignTagListFragmentInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, SearchGourmetResultTabPresenter.ViewType viewType, GourmetBookDateTime gourmetBookDateTime, GourmetFilter gourmetFilter)
    {

    }

    @Override
    public void onEventGourmetClick(Activity activity, SearchGourmetResultTabPresenter.ViewType viewType, Gourmet gourmet)
    {

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
    public void onEventGourmetClick(Activity activity, Gourmet gourmet, GourmetSuggestV2 suggest)
    {
        if (activity == null || gourmet == null || suggest == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.GOURMET_ITEM_CLICK, Integer.toString(gourmet.index), null);

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
    public void onEventSearchResult(Activity activity, GourmetBookDateTime gourmetBookDateTime, GourmetSuggestV2 suggest//
        , CampaignTag campaignTag, int searchCount)
    {
        if (activity == null || campaignTag == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        int index = campaignTag.index;
        String tagIndex = Integer.toString(index);

        String category = campaignTag.serviceType;
        if (DailyTextUtils.isTextEmpty(category) == true)
        {
            // do nothing
        } else if (Constants.ServiceType.HOTEL.name().equalsIgnoreCase(category.toUpperCase()))
        {
            category = AnalyticsManager.ValueType.STAY;
        } else if (Constants.ServiceType.GOURMET.name().equalsIgnoreCase(category.toUpperCase()))
        {
            category = AnalyticsManager.ValueType.GOURMET;
        }

        params.put(AnalyticsManager.KeyType.CATEGORY, category);
        params.put(AnalyticsManager.KeyType.TAG, tagIndex);
        params.put(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, Integer.toString(searchCount));

        String action = searchCount == 0 ? AnalyticsManager.Action.TAG_SEARCH_NOT_FOUND : AnalyticsManager.Action.TAG_SEARCH;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH //
            , action + "_gourmet", tagIndex, params);
    }
}
