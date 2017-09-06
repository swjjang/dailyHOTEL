package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.app.Activity;

import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;

public class GourmetDetailAnalyticsImpl implements GourmetDetailPresenter.GourmetDetailAnalyticsInterface
{
    private GourmetDetailAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(GourmetDetailAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public GourmetDetailAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

    }
}
