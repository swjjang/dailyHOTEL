package com.daily.dailyhotel.screen.home.search.gourmet.result;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SearchGourmetResultTabAnalyticsImpl implements SearchGourmetResultTabInterface.AnalyticsInterface
{
    @Override
    public void onEventChangedViewType(Activity activity, SearchGourmetResultTabPresenter.ViewType viewType)
    {
        if (activity == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    , AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._GOURMET_LIST, null);
                break;

            case MAP:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    , AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._GOURMET_MAP, null);
                break;
        }
    }

    @Override
    public void onEventCalendarClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent( //
            AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED,//
            AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
    }

    @Override
    public void onEventFilterClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent( //
            AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED,//
            AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
    }

    @Override
    public void onEventBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.BACK_BUTTON, null);
    }

    @Override
    public void onEventCancelClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CANCEL_, null);
    }

    @Override
    public void onEventResearchClick(Activity activity, GourmetSuggest suggest)
    {
        if (activity == null || suggest == null)
        {
            return;
        }

        if (suggest.isLocationSuggestType() == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "gourmet_around_result_research", suggest.getText1(), null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "gourmet_research", null, null);
        }
    }

    @Override
    public void onEventChangedRadius(Activity activity, GourmetSuggest suggest, float radius)
    {
        if (activity == null)
        {
            return;
        }

        try
        {
            String action;
            if (radius > 5)
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_10; // 10km
            } else if (radius > 3)
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_5; // 5km
            } else if (radius > 1)
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_3; // 3km
            } else if (radius > 0.5)
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_1; // 1km
            } else
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_05; // 0.5km
            }

            String label;

            if (suggest.isLocationSuggestType() == true)
            {
                GourmetSuggest.Location suggestItem = (GourmetSuggest.Location) suggest.getSuggestItem();

                label = suggestItem.address;
            } else
            {
                label = suggest.getText1();
            }

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION, action, label, null);

            AnalyticsManager.getInstance(activity) //
                .recordEvent(AnalyticsManager.Category.SEARCH_, "gourmet_around_result_range_change", suggest.getText1(), null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventStayClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen", "gourmet_stay", null);
    }

    @Override
    public void onEventStayOutboundClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen", "gourmet_ob", null);
    }

    @Override
    public void onEventCampaignTagClick(Activity activity, int index)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen_location_gourmet", Integer.toString(index), null);
    }
}
