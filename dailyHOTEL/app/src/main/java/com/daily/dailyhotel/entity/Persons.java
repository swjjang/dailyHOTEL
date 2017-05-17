package com.daily.dailyhotel.entity;

import java.util.ArrayList;

public class Persons
{
    public static final int DEFAULT_PERSONS = 2;

    public int numberOfAdults;
    private ArrayList<String> mChildAgeList;

    public Persons(int numberOfAdults, ArrayList<String> childAgeList)
    {
        this.numberOfAdults = numberOfAdults;
        setChildAgeList(childAgeList);
    }

    public void setChildAgeList(ArrayList<String> childAgeList)
    {
        mChildAgeList = childAgeList;
    }

    public ArrayList<String> getChildAgeList()
    {
        return mChildAgeList;
    }
}
