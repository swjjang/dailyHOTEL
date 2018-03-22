package com.daily.dailyhotel.screen.home.campaigntag.stay;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.CampaignTag;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by android_sam on 2017. 8. 15..
 */

@Deprecated
public class StayCampaignTagListAnalyticsImpl implements StayCampaignTagListPresenter.StayCampaignTagListAnalyticsInterface
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
            , action + "_stay", tagIndex, params);
    }

    @Override
    public void onEventStayClickOption(Activity activity, int index, boolean hasCoupon, boolean hasReview, boolean trueVR, boolean provideRewardSticker, boolean discount)
    {
        if (activity == null)
        {
            return;
        }

        // 할인 쿠폰이 보이는 경우
        if (hasCoupon == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.COUPON_STAY, Integer.toString(index), null);
        }

        if (hasReview == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.TRUE_REVIEW_STAY, Integer.toString(index), null);
        }

        if (trueVR == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(index), null);
        }

        if (provideRewardSticker == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.THUMBNAIL_CLICK, Integer.toString(index), null);
        }

        if (discount == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.DISCOUNT_STAY, Integer.toString(index), null);
        }
    }

    @Override
    public void onEventStayWishClick(Activity activity, boolean wish)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
            , AnalyticsManager.Action.WISH_STAY, wish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
    }
}
