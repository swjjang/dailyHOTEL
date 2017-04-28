package com.daily.dailyhotel.entity;

import java.util.ArrayList;

public class Persons
{
    public static final int DEFAULT_PERSONS = 2;

    public int numberOfAdults;
    private ArrayList<String> mChildList;

    public Persons(int numberOfAdults, ArrayList<String> childList)
    {
        this.numberOfAdults = numberOfAdults;
        setChildList(childList);
    }

    public void setChildList(ArrayList<String> childList)
    {
        mChildList = childList;
    }

    public ArrayList<String> getChildList()
    {
        return mChildList;
    }
}
