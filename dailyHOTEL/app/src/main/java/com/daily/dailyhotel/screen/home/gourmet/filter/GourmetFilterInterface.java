package com.daily.dailyhotel.screen.home.gourmet.filter;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggest;

import java.util.HashMap;
import java.util.LinkedHashMap;

public interface GourmetFilterInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setCategory(LinkedHashMap<String, GourmetFilter.Category> categoryMap);

        void setSortCheck(GourmetFilter.SortType sortType);

        void setSortLayoutEnabled(boolean enabled);

        void setCategoriesCheck(GourmetFilter.Category category);

        void setCategoriesCheck(HashMap<String, Integer> flagCategoryFilterMap);

        void setTimesCheck(int flagBedTypeFilters);

        void setAmenitiesCheck(int flagAmenitiesFilters);

        void setConfirmText(String text);

        void setConfirmEnabled(boolean enabled);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onResetClick();

        void onConfirmClick();

        void onCheckedChangedSort(GourmetFilter.SortType sortType);

        void onCheckedChangedCategories(GourmetFilter.Category category);

        void onCheckedChangedTimes(int flag);

        void onCheckedChangedAmenities(int flag);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onConfirmClick(Activity activity, GourmetSuggest suggest, GourmetFilter filter, int listCountByFilter);

        void onBackClick(Activity activity);

        void onResetClick(Activity activity);

        void onEmptyResult(Activity activity, GourmetFilter filter);
    }
}