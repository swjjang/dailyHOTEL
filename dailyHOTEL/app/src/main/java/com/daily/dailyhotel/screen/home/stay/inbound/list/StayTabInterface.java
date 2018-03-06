package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.app.Activity;
import android.support.design.widget.TabLayout;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Category;
import com.twoheart.dailyhotel.model.DailyCategoryType;

import java.util.List;

import io.reactivex.Observable;

public interface StayTabInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setToolbarDateText(String text);

        void setToolbarRegionText(String text);

        Observable<Boolean> getCategoryTabLayout(List<Category> categoryList, Category selectedCategory);

        void setOptionFilterSelected(boolean selected);

        void setViewType(StayTabPresenter.ViewType viewType);

        void setCategoryTabSelect(int position);

        void onSelectedCategory();

        void refreshCurrentCategory();

        void scrollTopCurrentCategory();

        void showPreviewGuide();

        boolean onFragmentBackPressed();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onCategoryTabSelected(TabLayout.Tab tab);

        void onCategoryTabReselected(TabLayout.Tab tab);

        void onRegionClick();

        void onCalendarClick();

        void onFilterClick();

        void onViewTypeClick();

        void onSearchClick();

        // by Analytics
        void onCategoryFlicking(String categoryName);

        // by Analytics
        void onCategoryClick(String categoryName);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onBackClick(Activity activity, DailyCategoryType categoryType);

        void onRegionChanged(Activity activity, DailyCategoryType categoryType, String areaName);

        void onCalendarClick(Activity activity, DailyCategoryType categoryType);

        void onViewTypeClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType);

        void onRegionClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType);

        void onSearchClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType);

        void onFilterClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType);

        void onCategoryFlicking(Activity activity, DailyCategoryType categoryType, String categoryName);

        void onCategoryClick(Activity activity, DailyCategoryType categoryType, String categoryName);
    }
}