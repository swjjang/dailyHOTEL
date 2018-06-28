package com.daily.dailyhotel.entity;

import android.content.Context;

import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

public class People
{
    public static final int ADULT_MIN_COUNT = 1;
    public static final int ADULT_MAX_COUNT = 8;

    public static final int CHILD_MIN_COUNT = 0;
    public static final int CHILD_MAX_COUNT = 3;

    public static final int DEFAULT_ADULTS = 2;
    public static final int DEFAULT_CHILD_AGE = 0;

    public int numberOfAdults;
    private ArrayList<Integer> mChildAgeList;

    public People(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        this.numberOfAdults = numberOfAdults;
        setChildAgeList(childAgeList);
    }
    //
    //    public People(JSONObject jsonObject)
    //    {
    //        if (jsonObject == null)
    //        {
    //            this.numberOfAdults = DEFAULT_ADULTS;
    //            setChildAgeList(null);
    //            return;
    //        }
    //
    //        try
    //        {
    //            numberOfAdults = (int) jsonObject.get("numberOfAdults");
    //            JSONArray jsonArray = (JSONArray) jsonObject.get("childAgeList");
    //
    //            int length = jsonArray.length();
    //            if (length == 0)
    //            {
    //                mChildAgeList = null;
    //                return;
    //            }
    //
    //            ArrayList subList = new ArrayList();
    //
    //            for (int i = 0; i < length; i++)
    //            {
    //                subList.add(jsonArray.get(i));
    //            }
    //
    //            mChildAgeList = subList;
    //        } catch (Exception e)
    //        {
    //            ExLog.e(e.toString());
    //        }
    //    }

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

    public String toTooShortString(Context context)
    {
        StringBuilder stringBuilder = new StringBuilder();

        if (context == null)
        {
            return stringBuilder.toString();
        }

        stringBuilder.append(context.getString(R.string.label_people_adult));
        stringBuilder.append(numberOfAdults);

        int childCount;

        if (mChildAgeList == null)
        {
            childCount = 0;
        } else
        {
            childCount = mChildAgeList.size();
        }

        stringBuilder.append(", ");
        stringBuilder.append(context.getString(R.string.label_people_child));
        stringBuilder.append(childCount);

        return stringBuilder.toString();
    }

    //    private JSONObject getJsonObject() throws Exception
    //    {
    //        JSONObject jsonObject = new JSONObject();
    //        jsonObject.put("numberOfAdults", numberOfAdults);
    //        jsonObject.put("childAgeList", new JSONArray(mChildAgeList));
    //
    //        return jsonObject;
    //    }
    //
    //    public String toJsonString()
    //    {
    //        String jsonString;
    //
    //        try
    //        {
    //            jsonString = getJsonObject().toString();
    //        } catch (Exception e)
    //        {
    //            jsonString = null;
    //        }
    //
    //        return jsonString;
    //    }
}
