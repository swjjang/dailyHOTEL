package com.daily.dailyhotel.entity;

import java.util.ArrayList;

public class People
{
    public static final int DEFAULT_ADULTS = 2;

    public int numberOfAdults;
    private ArrayList<Integer> mChildAgeList;

    public People(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        this.numberOfAdults = numberOfAdults;
        setChildAgeList(childAgeList);
    }

    public void setChildAgeList(ArrayList<Integer> childAgeList)
    {
        mChildAgeList = childAgeList;
    }

    public ArrayList<Integer> getChildAgeList()
    {
        return mChildAgeList;
    }
}
