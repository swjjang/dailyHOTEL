package com.daily.dailyhotel.screen.home.search.stay.inbound.result.campaign;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabPresenter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class SearchStayCampaignTagListFragmentAnalyticsImpl implements SearchStayCampaignTagListFragmentInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, SearchStayResultTabPresenter.ViewType viewType, StayBookDateTime bookDateTime, StayFilter filter)
    {

    }

    @Override
    public void onEventStayClick(Activity activity, SearchStayResultTabPresenter.ViewType viewType, Stay stay)
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
            , AnalyticsManager.Action.WISH_STAY, wish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
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
    public void onEventStayClick(Activity activity, Stay stay, StaySuggest suggest)
    {
        if (activity == null || stay == null || suggest == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.STAY_ITEM_CLICK, Integer.toString(stay.index), null);

        if (DailyTextUtils.isTextEmpty(stay.couponDiscountText) == false)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.COUPON_STAY, Integer.toString(stay.index), null);
        }

        if (stay.reviewCount > 0)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.TRUE_REVIEW_STAY, Integer.toString(stay.index), null);
        }

        if (stay.discountRate > 0)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.DISCOUNT_STAY, Integer.toString(stay.index), null);
        }

        if (stay.soldOut == true)
        {
            switch (suggest.menuType)
            {
                case LOCATION:
                case REGION_LOCATION:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                        , AnalyticsManager.Action.NEARBY, Integer.toString(stay.index), null);
                    break;

                case SUGGEST:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                        , AnalyticsManager.Action.AUTO_SEARCH, Integer.toString(stay.index), null);
                    break;

                case DIRECT:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                        , AnalyticsManager.Action.KEYWORD, Integer.toString(stay.index), null);
                    break;

                case RECENTLY_STAY:
                case RECENTLY_SEARCH:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                        , AnalyticsManager.Action.RECENT, Integer.toString(stay.index), null);
                    break;
            }
        }
    }

    @Override
    public void onEventSearchResult(Activity activity, StayBookDateTime bookDateTime, StaySuggest suggest//
        , CampaignTag campaignTag, int searchCount)
    {
        if (activity == null || campaignTag == null)
        {
            return;
        }

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
            , action + "_stay", tagIndex, params);
    }
}
