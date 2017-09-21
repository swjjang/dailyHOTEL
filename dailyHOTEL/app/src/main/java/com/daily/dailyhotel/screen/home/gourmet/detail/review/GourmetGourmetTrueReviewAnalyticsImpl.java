package com.daily.dailyhotel.screen.home.gourmet.detail.review;

import android.app.Activity;

import com.daily.dailyhotel.parcel.analytics.GourmetTrueReviewAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class GourmetGourmetTrueReviewAnalyticsImpl implements GourmetTrueReviewPresenter.GourmetTrueReviewAnalyticsInterface
{
    private GourmetTrueReviewAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(GourmetTrueReviewAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
        params.put(AnalyticsManager.KeyType.CATEGORY, mAnalyticsParam.category);

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.TRUE_REVIEW_LIST, null, params);
    }

    @Override
    public void onEventTermsClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.TRUE_REVIEW_POLICY_CLICK, AnalyticsManager.Label.GOURMET, null);
    }

    @Override
    public void onEventBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.TRUE_REVIEW_BACK_BUTTON_CLICK, AnalyticsManager.Label.GOURMET, null);
    }
}
