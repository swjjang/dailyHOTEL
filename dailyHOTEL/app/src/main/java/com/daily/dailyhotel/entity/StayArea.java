package com.daily.dailyhotel.entity;

import java.util.List;

public class StayArea extends Area
{
    public static final int ALL = -1;

    private List<Category> mCategoryList; // 스테이의 카테고리 목록

    public StayArea()
    {

    }

    /**
     * 전체 인경우
     *
     * @param stayArea
     */
    public StayArea(StayArea stayArea)
    {
        name = stayArea.name;
        index = StayArea.ALL;

        setCategoryList(stayArea.getCategoryList());
    }

    public List<Category> getCategoryList()
    {
        return mCategoryList;
    }

    public void setCategoryList(List<Category> categoryList)
    {
        mCategoryList = categoryList;
    }

    public int getCategoryCount()
    {
        return mCategoryList == null ? 0 : mCategoryList.size();
    }
}
