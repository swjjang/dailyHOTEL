package com.daily.dailyhotel.entity;

import android.content.Context;

import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

public class People
{
    public static final int DEFAULT_ADULT_MIN_COUNT = 1;
    public static final int DEFAULT_ADULT_MAX_COUNT = 8;

    public static final int DEFAULT_CHILD_MIN_COUNT = 0;
    public static final int DEFAULT_CHILD_MAX_COUNT = 3;

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

    public String toString(Context context)
    {
        StringBuilder stringBuilder = new StringBuilder();

        if (context == null)
        {
            return stringBuilder.toString();
        }

        stringBuilder.append(context.getString(R.string.label_stay_outbound_search_adult_count, numberOfAdults));

        int childCount;

        if (mChildAgeList == null)
        {
            childCount = 0;
        } else
        {
            childCount = mChildAgeList.size();
        }

        stringBuilder.append(", ");
        stringBuilder.append(context.getString(R.string.label_stay_outbound_search_child_count, childCount));

        if (childCount > 0)
        {
            int childAge;
            StringBuilder childrenAgeStringBuilder = new StringBuilder();

            for (int i = 0; i < childCount; i++)
            {
                childAge = mChildAgeList.get(i);

                if (i != 0)
                {
                    childrenAgeStringBuilder.append(", ");
                }

                if (childAge == 0)
                {
                    childrenAgeStringBuilder.append(context.getString(R.string.label_stay_outbound_search_under_of_1_age));
                } else
                {
                    childrenAgeStringBuilder.append(context.getString(R.string.label_stay_outbound_search_child_age, childAge));
                }
            }

            stringBuilder.append(' ');
            stringBuilder.append(context.getString(R.string.label_stay_outbound_search_children_age, childrenAgeStringBuilder.toString()));
        }

        return stringBuilder.toString();
    }

    public String toShortString(Context context)
    {
        StringBuilder stringBuilder = new StringBuilder();

        if (context == null)
        {
            return stringBuilder.toString();
        }

        stringBuilder.append(context.getString(R.string.label_stay_outbound_list_adult_count, numberOfAdults));

        int childCount;

        if (mChildAgeList == null)
        {
            childCount = 0;
        } else
        {
            childCount = mChildAgeList.size();
        }

        stringBuilder.append(", ");
        stringBuilder.append(context.getString(R.string.label_stay_outbound_list_child_count, childCount));

        return stringBuilder.toString();
    }
}
