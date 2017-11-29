package com.daily.dailyhotel.entity;

import java.util.List;

public class StayTown extends Town
{
    public static final int ALL = -1;

    public StayTown()
    {

    }

    /**
     * Town 과 District 가 같은 경우
     *
     * @param stayDistrict
     */
    public StayTown(StayDistrict stayDistrict)
    {
        name = stayDistrict.name;
        index = StayTown.ALL;
        setDistrict(stayDistrict);

        setCategoryList(stayDistrict.getCategoryList());
    }

    private List<Category> mCategoryList; // 스테이의 카테고리 목록

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
