package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.support.v4.app.FragmentManager;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Category;

import java.util.List;

public interface StayTabInterface extends BaseDialogViewInterface
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
}
