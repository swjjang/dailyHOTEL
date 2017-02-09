package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.util.Pair;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by android_sam on 2016. 10. 11..
 */

public class RecentPlaces
{
    public static final int MAX_RECENT_PLACE_COUNT = 30;
    public static final String RECENT_PLACE_DELIMITER = ",";
    public static final String RECENT_KEY_DELIMITER = "=";

    private Context mContext;
    private ArrayList<Pair<Integer, String>> mPlaceList;

    public RecentPlaces(Context context)
    {
        mContext = context;

        mPlaceList = new ArrayList<>();

        String preferenceText = DailyPreference.getInstance(mContext).getAllRecentPlaces();

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
                int placeIndex = Integer.parseInt(keyValueArray[0]);
                String serviceType = keyValueArray[1];

                mPlaceList.add(new Pair<>(placeIndex, serviceType));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    public static String getServiceType(Constants.PlaceType placeType)
    {
        if (Constants.PlaceType.HOTEL.equals(placeType) == true)
        {
            return "HOTEL";
        } else if (Constants.PlaceType.FNB.equals(placeType) == true)
        {
            return "GOURMET";
        } else
        {
            return null;
        }
    }

    public void add(Constants.PlaceType placeType, int placeIndex)
    {
        if (placeIndex < 0)
        {
            return;
        }

        String serviceType = getServiceType(placeType);
        if (Util.isTextEmpty(serviceType) == true)
        {
            return;
        }

        int size = size();

        Pair<Integer, String> expactedPair = new Pair<>(placeIndex, serviceType);

        // 사이즈가 1 이하이면 for를 동작 하지 않음
        if (size == 1)
        {
            Pair<Integer, String> pair = mPlaceList.get(0);

            if (expactedPair.equals(pair) == true)
            {
                return;
            }

            mPlaceList.add(0, new Pair<>(placeIndex, serviceType));
            return;
        }

        // 서비스 타입 개수
        int sameTypeItemCount = 0;
        // 기존 저장된 포지션
        int oldPlacePosition = -1;
        // 같은 서비스 타입의 마지막 place 위치
        int lastSameTypePlacePosition = -1;

        // 59일때 목은 29, 나머지는 1;
        int maxSearchSize = (size / 2) + (size % 2);

        // value1 = 30, value2 = 60;
        for (int first = maxSearchSize - 1; first >= 0; first--)
        {
            Pair<Integer, String> secondPair; // first 59; <-- outOfLength error;

            int second = first + maxSearchSize;
            if (second < size) // 2번째 인자가 리스트 사이즈보다 작을때
            {
                secondPair = mPlaceList.get(first + maxSearchSize);

                if (expactedPair.equals(secondPair) == true)
                {
                    // 기존 포함 여부 검사
                    if (oldPlacePosition == -1)
                    {
                        oldPlacePosition = second;
                    }
                }

                if (serviceType.equalsIgnoreCase(secondPair.second) == true)
                {
                    // 같은 서비스 타입의 마지막 포지션
                    if (second > lastSameTypePlacePosition)
                    {
                        lastSameTypePlacePosition = second;
                    }

                    sameTypeItemCount++;
                }
            }

            Pair<Integer, String> firstPair = mPlaceList.get(first); // first 29;

            if (expactedPair.equals(firstPair) == true)
            {
                // 기존 포함 여부 검사
                if (oldPlacePosition == -1)
                {
                    oldPlacePosition = first;
                }
            }

            if (serviceType.equalsIgnoreCase(firstPair.second) == true)
            {
                // 같은 서비스 타입의 마지막 포지션
                if (first > lastSameTypePlacePosition)
                {
                    lastSameTypePlacePosition = first;
                }

                sameTypeItemCount++;
            }
        }

        if (oldPlacePosition != -1)
        {
            mPlaceList.remove(oldPlacePosition);
            sameTypeItemCount--;
        } else if (sameTypeItemCount == MAX_RECENT_PLACE_COUNT)
        {
            mPlaceList.remove(lastSameTypePlacePosition);
        }

        mPlaceList.add(0, expactedPair);
    }

    public void remove(Pair<Integer, String> pair)
    {
        if (mPlaceList == null || mPlaceList.size() == 0)
        {
            return;
        }

        int lastIndex = mPlaceList.lastIndexOf(pair);
        if (lastIndex != -1 && lastIndex < mPlaceList.size())
        {
            mPlaceList.remove(lastIndex);
        }
    }

    public void savePreference()
    {
        DailyPreference.getInstance(mContext).setAllRecentPlaces(toString());
    }

    @Override
    public String toString()
    {
        if (mPlaceList == null || mPlaceList.size() == 0)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        Iterator<Pair<Integer, String>> iterator = mPlaceList.iterator();

        while (iterator.hasNext() == true)
        {
            Pair<Integer, String> pair = iterator.next();
            builder.append(pair.first).append(RECENT_KEY_DELIMITER).append(pair.second);

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

    public void clear()
    {
        mPlaceList.clear();
    }

    public ArrayList<HomeRecentParam> getParamList(int size)
    {
        if (size <= 0)
        {
            return new ArrayList<>();
        }

        if (mPlaceList == null || mPlaceList.size() == 0)
        {
            return new ArrayList<>();
        }

        size = Math.min(size, size());

        ArrayList<HomeRecentParam> resultList = new ArrayList<>();

        Pair<Integer, String> item;
        for (int i = 0; i < size; i++)
        {
            item = mPlaceList.get(i);

            if (item == null)
            {
                continue;
            }

            HomeRecentParam homeRecentParam = new HomeRecentParam();
            homeRecentParam.index = item.first;
            homeRecentParam.savingTime = -1;
            homeRecentParam.serviceType = item.second;

            resultList.add(homeRecentParam);
        }

        return resultList;
    }

    public ArrayList<Pair<Integer, String>> getRecentTypeList(Constants.PlaceType placeType)
    {
        if (mPlaceList == null || mPlaceList.size() == 0)
        {
            return new ArrayList<>();
        }

        String serviceType = getServiceType(placeType);
        if (Util.isTextEmpty(serviceType) == true)
        {
            return new ArrayList<>();
        }

        ArrayList<Pair<Integer, String>> resultList = new ArrayList<>();

        for (Pair<Integer, String> pair : mPlaceList)
        {
            if (serviceType.equalsIgnoreCase(pair.second) == true)
            {
                resultList.add(pair);
            }
        }

        return resultList;
    }
}