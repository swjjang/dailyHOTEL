package com.daily.dailyhotel.screen.home.gourmet.detail.review;

import android.app.Activity;

import com.daily.dailyhotel.parcel.analytics.GourmetTrueReviewAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class TrueReviewAnalyticsImpl implements TrueReviewPresenter.TrueReviewAnalyticsInterface
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
        if(activity == null || mAnalyticsParam == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
        params.put(AnalyticsManager.KeyType.CATEGORY, mAnalyticsParam.category);

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.TRUE_REVIEW_LIST, null, params);
    }
}
