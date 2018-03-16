package com.daily.dailyhotel.screen.home.search.gourmet.result.search;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.AreaElement;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabPresenter;
import com.daily.dailyhotel.screen.home.stay.inbound.list.StayTabPresenter;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SearchGourmetResultListFragmentAnalyticsImpl implements SearchGourmetResultListFragmentInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, SearchGourmetResultTabPresenter.ViewType viewType, GourmetBookDateTime gourmetBookDateTime, GourmetFilter gourmetFilter)
    {

    }

    @Override
    public void onEventStayClick(Activity activity, SearchGourmetResultTabPresenter.ViewType viewType, Gourmet gourmet)
    {

    }

    @Override
    public void onEventWishClick(Activity activity, boolean wish)
    {

    }

    @Override
    public void onEventMarkerClick(Activity activity, String name)
    {

    }
}
