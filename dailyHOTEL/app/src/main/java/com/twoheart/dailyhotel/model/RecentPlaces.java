package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2016. 10. 11..
 */

public class RecentPlaces
{
    public static final int MAX_RECENT_PLACE_COUNT = 30;
    public static final String RECENT_PLACE_DELIMITER = ",";

    private List<String> mPlaceIndexList;

    public RecentPlaces(String preferenceText)
    {
        mPlaceIndexList = new ArrayList<>();

        parse(preferenceText);
    }

    private void parse(String preferenceText)
    {
        if (Util.isTextEmpty(preferenceText) == true)
        {
            return;
        }

        String[] splitArray = preferenceText.split(RECENT_PLACE_DELIMITER);

        if (splitArray == null)
        {
            return;
        }

        for (String placeIndex : splitArray)
        {
            try
            {
                if (Util.isTextEmpty(placeIndex) == false && Util.isTextEmpty(placeIndex.trim()) == false)
                {
                    mPlaceIndexList.add(placeIndex);
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    public void add(int placeIndex)
    {
        String checkString = Integer.toString(placeIndex);

        if (mPlaceIndexList == null || mPlaceIndexList.size() == 0)
        {
            mPlaceIndexList.add(checkString);
        } else
        {
            if (mPlaceIndexList.contains(checkString) == true)
            {
                mPlaceIndexList.remove(checkString);
            }

            if (mPlaceIndexList.size() == MAX_RECENT_PLACE_COUNT)
            {
                mPlaceIndexList.remove(mPlaceIndexList.size() - 1);
            }

            mPlaceIndexList.add(0, checkString);
        }
    }

    public void remove(int position)
    {
        if (mPlaceIndexList == null || mPlaceIndexList.size() == 0)
        {
            return;
        }

        mPlaceIndexList.remove(position);
    }

    public String toString()
    {
        if (mPlaceIndexList == null || mPlaceIndexList.size() == 0)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (String text : mPlaceIndexList)
        {
            builder.append(text).append(RECENT_PLACE_DELIMITER);
        }

        int length = builder.length();
        if (builder.charAt(length - 1) == ',')
        {
            builder.setLength(length - 1);
        }

        return builder.toString();
    }

    public int size()
    {
        return mPlaceIndexList == null ? 0 : mPlaceIndexList.size();
    }

    public List<String> getList()
    {
        return mPlaceIndexList;
    }

    public void clear()
    {
        if (mPlaceIndexList != null)
        {
            mPlaceIndexList.clear();
        }
    }
}