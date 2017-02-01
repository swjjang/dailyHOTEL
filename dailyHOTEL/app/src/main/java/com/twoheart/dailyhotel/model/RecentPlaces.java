package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by android_sam on 2016. 10. 11..
 */

public class RecentPlaces
{
    public static final int MAX_RECENT_PLACE_COUNT = 30;
    public static final String RECENT_PLACE_DELIMITER = ",";
    public static final String RECENT_KEY_DELIMITER = "=";

    private HashMap<Integer, Long> mPlaceList;

    public RecentPlaces(String preferenceText)
    {
        mPlaceList = new HashMap<>();

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

        for (String recentPlace : splitArray)
        {
            if (Util.isTextEmpty(recentPlace) == true)
            {
                continue;
            }

            if (recentPlace.contains(RECENT_KEY_DELIMITER) == false)
            {
                continue;
            }

            String[] keyValueArray = recentPlace.split(RECENT_KEY_DELIMITER);

            if (keyValueArray == null || keyValueArray.length < 2)
            {
                continue;
            }

            try
            {
                int key = Integer.parseInt(keyValueArray[0]);
                long value = Long.parseLong(keyValueArray[1]);

                mPlaceList.put(key, value);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    public void add(int placeIndex)
    {
        if (placeIndex < 0)
        {
            return;
        }

        mPlaceList.put(placeIndex, System.currentTimeMillis());

        removeOverflow();
    }

    public void removeKey(int placeIndex)
    {
        if (mPlaceList.size() == 0)
        {
            return;
        }

        mPlaceList.remove(placeIndex);
    }

    @Override
    public String toString()
    {
        if (mPlaceList.size() == 0)
        {
            return "";
        }

        Iterator<Map.Entry<Integer, Long>> iterator = mPlaceList.entrySet().iterator();
        if (iterator.hasNext() == false)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        while (iterator.hasNext() == true)
        {
            Map.Entry entry = iterator.next();
            builder.append(entry.getKey()).append(RECENT_KEY_DELIMITER).append(entry.getValue());

            if (iterator.hasNext() == true)
            {
                builder.append(RECENT_PLACE_DELIMITER);
            }
        }

        return builder.toString();
    }

    public String toKeyString()
    {
        if (mPlaceList.size() == 0)
        {
            return "";
        }

        Iterator<Integer> iterator = mPlaceList.keySet().iterator();
        if (iterator.hasNext() == false)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        while (iterator.hasNext() == true)
        {
            builder.append(iterator.next());

            if (iterator.hasNext() == true)
            {
                builder.append(RECENT_PLACE_DELIMITER);
            }
        }

        return builder.toString();
    }

    public int size()
    {
        return mPlaceList == null ? 0 : mPlaceList.size();
    }

    public Long getValue(int placeIndex)
    {
        return mPlaceList.get(placeIndex);
    }

    public void clear()
    {
        mPlaceList.clear();
    }

    public void sortList(ArrayList<? extends Place> list)
    {
        if (list != null && list.size() > 0)
        {
            Collections.sort(list, new Comparator<Place>()
            {
                @Override
                public int compare(Place gourmet1, Place gourmet2)
                {
                    int index1 = gourmet1.index;
                    int index2 = gourmet2.index;

                    Long value1 = mPlaceList.get(index1);
                    Long value2 = mPlaceList.get(index2);

                    return value1.compareTo(value2);
                }
            });

            Collections.reverse(list);
        }
    }

    private void removeOverflow()
    {
        int removeSize = mPlaceList.size() - MAX_RECENT_PLACE_COUNT;
        if (removeSize <= 0)
        {
            return;
        }

        ArrayList<Map.Entry<Integer, Long>> list = new ArrayList<>();
        list.addAll(mPlaceList.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Integer, Long>>()
        {
            @Override
            public int compare(Map.Entry<Integer, Long> o1, Map.Entry<Integer, Long> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        for (int i = 0; i < removeSize; i++)
        {
            Map.Entry<Integer, Long> entry = list.get(i);
            removeKey(entry.getKey());
        }
    }
}