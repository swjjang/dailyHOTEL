package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayTabAnalyticsImpl implements StayTabInterface.AnalyticsInterface
{
    @Override
    public void onBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION, //
            AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK, AnalyticsManager.Label.HOME, null);
    }

    @Override
    public void onRegionChanged(Activity activity, String areaName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).onRegionChanged(AnalyticsManager.ValueType.DOMESTIC, areaName);
    }

    @Override
    public void onCalendarClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
    }

    @Override
    public void onViewTypeClick(Activity activity, StayTabPresenter.ViewType viewType)
    {
        if (activity == null || viewType == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_LIST, null);
                break;

            case MAP:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_MAP, null);
                break;
        }
    }

    @Override
    public void onRegionClick(Activity activity, StayTabPresenter.ViewType viewType)
    {
        if (activity == null || viewType == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._HOTEL_LIST, null);
                break;

            case MAP:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._HOTEL_MAP, null);
                break;
        }
    }

    @Override
    public void onSearchClick(Activity activity, StayTabPresenter.ViewType viewType)
    {
        if (activity == null || viewType == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
                    , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.STAY_LIST, null);
                break;

            case MAP:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
                    , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.STAY_MAP_VIEW, null);
                break;
        }
    }

    @Override
    public void onFilterClick(Activity activity, StayTabPresenter.ViewType viewType)
    {
        if (activity == null || viewType == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.VIEWTYPE_LIST, null);
                break;

            case MAP:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.VIEWTYPE_MAP, null);
                break;
        }
    }

    @Override
    public void onCategoryFlicking(Activity activity, String categoryName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.DAILY_HOTEL_CATEGORY_FLICKING, categoryName, null);
    }

    @Override
    public void onCategoryClick(Activity activity, String categoryName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_CATEGORY_CLICKED, categoryName, null);
    }
}
