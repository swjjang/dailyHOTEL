package com.daily.dailyhotel.screen.home.gourmet.filter;

import android.app.Activity;

import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GourmetFilterAnalyticsImpl implements GourmetFilterInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYGOURMET_CURATION, null);
    }

    @Override
    public void onConfirmClick(Activity activity, GourmetSuggest suggest, GourmetFilter filter, int listCountByFilter)
    {
        if (activity == null || suggest == null || filter == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.SORTING, filter.sortType.name());
        params.put(AnalyticsManager.KeyType.SEARCH_COUNT, String.valueOf(listCountByFilter));

        if (suggest.getSuggestType() == GourmetSuggest.SuggestType.AREA_GROUP)
        {
            GourmetSuggest.AreaGroup suggestItem = (GourmetSuggest.AreaGroup) suggest.getSuggestItem();

            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, suggestItem.name);
            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);

            if (suggestItem.area != null)
            {
                GourmetSuggest.Area area = suggestItem.area;

                params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
            }
        } else
        {
            params.put(AnalyticsManager.KeyType.PROVINCE, suggest.getSuggestItem().name);
        }

        final char DELIMITER = '-';

        StringBuilder stringBuilder = new StringBuilder();

        String filterSortString = getFilterSortString(filter.sortType);
        stringBuilder.append(filterSortString);
        stringBuilder.append(DELIMITER);

        String filterCategoryString = getFilterCategoryString(filter.getCategoryFilterMap());
        stringBuilder.append(filterCategoryString);
        stringBuilder.append(DELIMITER);

        String filterTimeString = getFilterTimer(filter.flagTimeFilter);
        stringBuilder.append(filterTimeString);
        stringBuilder.append(DELIMITER);

        String filterAmenityString = getFilterAmenityString(filter.flagAmenitiesFilters);
        stringBuilder.append(filterAmenityString);
        stringBuilder.append(DELIMITER);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_APPLY_BUTTON_CLICKED, stringBuilder.toString(), params);

        // 추가 항목
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
            , AnalyticsManager.Action.GOURMET_SORT, filterSortString, null);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
            , AnalyticsManager.Action.GOURMET_CATEGORY, filterCategoryString, null);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
            , AnalyticsManager.Action.GOURMET_TIME, filterTimeString, null);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
            , AnalyticsManager.Action.GOURMET_AMENITIES, filterAmenityString, null);
    }

    @Override
    public void onBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);
    }

    @Override
    public void onResetClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.RESET_BUTTON_CLICKED, null);
    }

    @Override
    public void onEmptyResult(Activity activity, GourmetFilter filter)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER //
            , AnalyticsManager.Action.GOURMET_NO_RESULT, getFilter(filter), null);
    }

    private String getFilter(GourmetFilter filter)
    {
        if (filter == null)
        {
            return null;
        }

        final char DELIMITER = '-';

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getFilterSortString(filter.sortType));
        stringBuilder.append(DELIMITER);
        stringBuilder.append(getFilterCategoryString(filter.getCategoryFilterMap()));
        stringBuilder.append(DELIMITER);
        stringBuilder.append(getFilterTimer(filter.flagTimeFilter));
        stringBuilder.append(DELIMITER);
        stringBuilder.append(getFilterAmenityString(filter.flagAmenitiesFilters));
        stringBuilder.append(DELIMITER);

        return stringBuilder.toString();
    }


    private String getFilterSortString(GourmetFilter.SortType sortType)
    {
        if (sortType == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        switch (sortType)
        {
            case DEFAULT:
                return AnalyticsManager.Label.SORTFILTER_DISTRICT;

            case DISTANCE:
                return AnalyticsManager.Label.SORTFILTER_DISTANCE;

            case LOW_PRICE:
                return AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE;

            case HIGH_PRICE:
                return AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE;

            case SATISFACTION:
                return AnalyticsManager.Label.SORTFILTER_RATING;

            default:
                return AnalyticsManager.Label.SORTFILTER_DISTRICT;
        }
    }

    private String getFilterCategoryString(HashMap<String, Integer> categoryFilterMap)
    {
        if (categoryFilterMap == null || categoryFilterMap.size() == 0)
        {
            return AnalyticsManager.Label.SORTFILTER_NONE;
        } else
        {
            final char DELIMITER = ',';

            StringBuilder stringBuilder = new StringBuilder();

            List<String> categoryFilterList = new ArrayList<>(categoryFilterMap.keySet());

            for (String categoryFilter : categoryFilterList)
            {
                stringBuilder.append(categoryFilter).append(DELIMITER);
            }

            if (stringBuilder.charAt(stringBuilder.length() - 1) == DELIMITER)
            {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
        }
    }

    private String getFilterTimer(int flagTimeFilter)
    {
        if (flagTimeFilter == GourmetFilter.FLAG_TIME_NONE)
        {
            return AnalyticsManager.Label.SORTFILTER_NONE;
        } else
        {
            final char DELIMITER = ',';

            StringBuilder stringBuilder = new StringBuilder();

            if ((flagTimeFilter & GourmetFilter.FLAG_TIME_06_11) == GourmetFilter.FLAG_TIME_06_11)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_0611).append(DELIMITER);
            }

            if ((flagTimeFilter & GourmetFilter.FLAG_TIME_11_15) == GourmetFilter.FLAG_TIME_11_15)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_1115).append(DELIMITER);
            }

            if ((flagTimeFilter & GourmetFilter.FLAG_TIME_15_17) == GourmetFilter.FLAG_TIME_15_17)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_1517).append(DELIMITER);
            }

            if ((flagTimeFilter & GourmetFilter.FLAG_TIME_17_21) == GourmetFilter.FLAG_TIME_17_21)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_1721).append(DELIMITER);
            }

            if ((flagTimeFilter & GourmetFilter.FLAG_TIME_21_06) == GourmetFilter.FLAG_TIME_21_06)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_2106).append(DELIMITER);
            }

            if (stringBuilder.charAt(stringBuilder.length() - 1) == DELIMITER)
            {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
        }
    }

    private String getFilterAmenityString(int flagAmenitiesFilters)
    {
        if (flagAmenitiesFilters == GourmetFilter.FLAG_AMENITIES_NONE)
        {
            return AnalyticsManager.Label.SORTFILTER_NONE;
        } else
        {
            final char DELIMITER = ',';

            StringBuilder stringBuilder = new StringBuilder();

            if ((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_PARKING) == GourmetFilter.FLAG_AMENITIES_PARKING)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_VALET) == GourmetFilter.FLAG_AMENITIES_VALET)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_VALET).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_BABYSEAT) == GourmetFilter.FLAG_AMENITIES_BABYSEAT)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BABYSEAT).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_PRIVATEROOM) == GourmetFilter.FLAG_AMENITIES_PRIVATEROOM)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PRIVATEROOM).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_GROUPBOOKING) == GourmetFilter.FLAG_AMENITIES_GROUPBOOKING)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_GROUP).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_CORKAGE) == GourmetFilter.FLAG_AMENITIES_CORKAGE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_CORKAGE).append(DELIMITER);
            }

            if (stringBuilder.charAt(stringBuilder.length() - 1) == DELIMITER)
            {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
        }
    }
}
