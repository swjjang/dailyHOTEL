package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.CampaignTag;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class GourmetCampaignTagListAnalyticsImpl implements GourmetCampaignTagListPresenter.GourmetCampaignTagListAnalyticsInterface
{
    @Override
    public void onCampaignTagEvent(Activity activity, CampaignTag campaignTag, int listCount)
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
        params.put(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, Integer.toString(listCount));

        String action = listCount == 0 ? AnalyticsManager.Action.TAG_SEARCH_NOT_FOUND : AnalyticsManager.Action.TAG_SEARCH;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH //
            , action + "_gourmet", tagIndex, params);
    }

    @Override
    public void onEventGourmetClickOption(Activity activity, int index, boolean hasCoupon, boolean hasReview, boolean trueVR, boolean discount)
    {
        if (activity == null)
        {
            return;
        }

        // 할인 쿠폰이 보이는 경우
        if (hasCoupon == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.COUPON_GOURMET, Integer.toString(index), null);
        }

        if (hasReview == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.TRUE_REVIEW_GOURMET, Integer.toString(index), null);
        }

        if (discount == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.DISCOUNT_GOURMET, Integer.toString(index), null);
        }
    }

    @Override
    public void onEventGourmetWishClick(Activity activity, boolean wish)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
            , AnalyticsManager.Action.WISH_GOURMET, wish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
    }
}
