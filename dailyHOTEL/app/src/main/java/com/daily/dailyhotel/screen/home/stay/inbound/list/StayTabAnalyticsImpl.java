package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.app.Activity;

import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayTabAnalyticsImpl implements StayTabInterface.AnalyticsInterface
{
    @Override
    public void onBackClick(Activity activity, DailyCategoryType categoryType)
    {
        if (activity == null)
        {
            return;
        }

        String label;

        if (categoryType == null || categoryType == DailyCategoryType.STAY_ALL)
        {
            label = AnalyticsManager.Label.HOME;
        } else
        {
            label = activity.getString(categoryType.getCodeResId());
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION, //
            AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK, label, null);
    }

    @Override
    public void onRegionChanged(Activity activity, DailyCategoryType categoryType, String areaName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).onRegionChanged(AnalyticsManager.ValueType.DOMESTIC, areaName);
    }

    @Override
    public void onCalendarClick(Activity activity, DailyCategoryType categoryType)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
    }

    @Override
    public void onViewTypeClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType)
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
    public void onRegionClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType)
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
    public void onSearchClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType)
    {
        if (activity == null || categoryType == null || viewType == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
            {
                String label;

                switch (categoryType)
                {
                    case STAY_ALL:
                        label = AnalyticsManager.Label.STAY_LIST;
                        break;

                    case STAY_HOTEL:
                        label = AnalyticsManager.Label.HOTEL_LIST;
                        break;

                    case STAY_BOUTIQUE:
                        label = AnalyticsManager.Label.BOUTIQUE_LIST;
                        break;

                    case STAY_PENSION:
                        label = AnalyticsManager.Label.PENSION_LIST;
                        break;

                    case STAY_RESORT:
                        label = AnalyticsManager.Label.RESORT_LIST;
                        break;

                    default:
                        label = AnalyticsManager.Label.STAY_LIST;
                        break;
                }

                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
                    , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, label, null);
                break;
            }

            case MAP:
            {
                String label;

                switch (categoryType)
                {
                    case STAY_ALL:
                        label = AnalyticsManager.Label.STAY_MAP_VIEW;
                        break;

                    case STAY_HOTEL:
                        label = AnalyticsManager.Label.HOTEL_LIST_MAP;
                        break;
                    case STAY_BOUTIQUE:
                        label = AnalyticsManager.Label.BOUTIQUE_LIST_MAP;
                        break;
                    case STAY_PENSION:
                        label = AnalyticsManager.Label.PENSION_LIST_MAP;
                        break;
                    case STAY_RESORT:
                        label = AnalyticsManager.Label.RESORT_LIST_MAP;
                        break;

                    default:
                        label = AnalyticsManager.Label.STAY_MAP_VIEW;
                        break;
                }

                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
                    , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, label, null);
                break;
            }
        }
    }

    @Override
    public void onFilterClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType)
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
    public void onCategoryFlicking(Activity activity, DailyCategoryType categoryType, String categoryName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.DAILY_HOTEL_CATEGORY_FLICKING, categoryName, null);
    }

    @Override
    public void onCategoryClick(Activity activity, DailyCategoryType categoryType, String categoryName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_CATEGORY_CLICKED, categoryName, null);
    }
}
