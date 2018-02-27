package com.daily.dailyhotel.entity;

import java.util.List;

public interface AreaCategoryInterface
{
    List<Category> getCategoryList();

    void setCategoryList(List<Category> categoryList);

    int getCategoryCount();
}
