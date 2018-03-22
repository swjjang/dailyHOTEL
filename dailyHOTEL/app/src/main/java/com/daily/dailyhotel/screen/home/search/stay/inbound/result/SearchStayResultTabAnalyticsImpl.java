package com.daily.dailyhotel.screen.home.search.stay.inbound.result;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.parcel.SearchStayResultAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SearchStayResultTabAnalyticsImpl implements SearchStayResultTabInterface.AnalyticsInterface
{
    private SearchStayResultAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(SearchStayResultAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public SearchStayResultAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onEventChangedViewType(Activity activity, SearchStayResultTabPresenter.ViewType viewType)
    {
        if (activity == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    , AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_LIST, null);
                break;

            case MAP:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    , AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_MAP, null);
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
            AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED,//
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
            AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED,//
            AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
    }

    @Override
    public void onEventBackClick(Activity activity, boolean locationSuggestType)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.BACK_BUTTON, null);

        if (mAnalyticsParam != null)
        {
            if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(mAnalyticsParam.mCallByScreen) == true && locationSuggestType == true)
            {
                AnalyticsManager.getInstance(activity).recordEvent( //
                    AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK //
                    , AnalyticsManager.Label.NEAR_BY, null);
            }
        }
    }

    @Override
    public void onEventCancelClick(Activity activity, boolean locationSuggestType)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CANCEL_, null);

        if (mAnalyticsParam != null)
        {
            if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(mAnalyticsParam.mCallByScreen) == true && locationSuggestType == true)
            {
                AnalyticsManager.getInstance(activity).recordEvent( //
                    AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK //
                    , AnalyticsManager.Label.NEAR_BY, null);
            }
        }
    }

    @Override
    public void onEventResearchClick(Activity activity, StaySuggestV2 suggest)
    {
        if (activity == null || suggest == null)
        {
            return;
        }

        if (suggest.isLocationSuggestType() == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "stay_around_result_research", suggest.getText1(), null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "stay_research", null, null);
        }
    }

    @Override
    public void onEventChangedRadius(Activity activity, StaySuggestV2 suggest, float radius)
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
                StaySuggestV2.Location suggestItem = (StaySuggestV2.Location) suggest.getSuggestItem();

                label = suggestItem.address;
            } else
            {
                label = suggest.getText1();
            }

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION, action, label, null);

            AnalyticsManager.getInstance(activity) //
                .recordEvent(AnalyticsManager.Category.SEARCH_, "stay_around_result_range_change", suggest.getText1(), null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventGourmetClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen", "stay_gourmet", null);
    }

    @Override
    public void onEventStayOutboundClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen", "stay_ob", null);
    }

    @Override
    public void onEventCampaignTagClick(Activity activity, int index)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "no_result_switch_screen_location_stay", Integer.toString(index), null);
    }
}
