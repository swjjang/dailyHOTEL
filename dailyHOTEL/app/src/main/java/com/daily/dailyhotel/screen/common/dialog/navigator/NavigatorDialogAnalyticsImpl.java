package com.daily.dailyhotel.screen.common.dialog.navigator;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class NavigatorDialogAnalyticsImpl implements NavigatorDialogPresenter.NavigatorDialogAnalyticsInterface
{
    private NavigatorAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(NavigatorAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public void onEventKakaoMapClick(Activity activity)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.category, mAnalyticsParam.action) == true)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.label) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "Daum", null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "Daum-" + mAnalyticsParam.label, null);
        }
    }

    @Override
    public void onEventNaverMapClick(Activity activity)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.category, mAnalyticsParam.action) == true)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.label) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "Naver", null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "Naver-" + mAnalyticsParam.label, null);
        }
    }

    @Override
    public void onEventGoogleMapClick(Activity activity)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.category, mAnalyticsParam.action) == true)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.label) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "Google", null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "Google-" + mAnalyticsParam.label, null);
        }
    }

    @Override
    public void onEventTMapMapClick(Activity activity)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.category, mAnalyticsParam.action) == true)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.label) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "TmapNavi", null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "TmapNavi-" + mAnalyticsParam.label, null);
        }
    }

    @Override
    public void onEventKakaoNaviClick(Activity activity)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.category, mAnalyticsParam.action) == true)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mAnalyticsParam.label) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "kakaoNavi", null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(mAnalyticsParam.category, mAnalyticsParam.action, "kakaoNavi-" + mAnalyticsParam.label, null);
        }
    }
}
