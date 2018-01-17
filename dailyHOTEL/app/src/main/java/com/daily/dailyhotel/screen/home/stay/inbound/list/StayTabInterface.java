package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Category;

import java.util.List;

public interface StayTabInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setToolbarDateText(String text);

        void setToolbarRegionText(String text);

        void setCategoryTabLayout(FragmentManager fragmentManager, List<Category> categoryList, Category selectedCategory);

        void setOptionFilterSelected(boolean selected);

        void setViewType(StayTabPresenter.ViewType viewType);

        void setCategoryTab(int position);

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

        void onCategoryFlicking(int position);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}