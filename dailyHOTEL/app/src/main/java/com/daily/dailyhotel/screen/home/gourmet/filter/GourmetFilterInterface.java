package com.daily.dailyhotel.screen.home.gourmet.filter;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggestV2;

import java.util.HashMap;

public interface GourmetFilterInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setSortLayout(GourmetFilter.SortType sortType);

        void setSortLayoutEnabled(boolean enabled);

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

        void onCheckedChangedCategories(int flag);

        void onCheckedChangedTimes(int flag);

        void onCheckedChangedAmenities(int flag);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onConfirmClick(Activity activity, GourmetSuggestV2 suggest, GourmetFilter filter, int listCountByFilter);

        void onBackClick(Activity activity);

        void onResetClick(Activity activity);

        void onEmptyResult(Activity activity, GourmetFilter filter);
    }
}