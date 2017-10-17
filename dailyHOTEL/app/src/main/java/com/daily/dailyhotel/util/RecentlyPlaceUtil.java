package com.daily.dailyhotel.util;

import android.content.Context;
import android.database.Cursor;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.local.model.RecentlyList;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 6. 8..
 */

public class RecentlyPlaceUtil
{
    public static final String RECENT_PLACE_DELIMITER = ",";
    public static final int MAX_RECENT_PLACE_COUNT = 30;

    public static JSONArray getDbRecentlyJsonArray(ArrayList<RecentlyDbPlace> list, int maxSize)
    {
        JSONArray jsonArray = new JSONArray();

        if (list == null || list.size() == 0 || maxSize == 0)
        {
            // dummy Data 생성
            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put("serviceType", "HOTEL");
                jsonObject.put("idx", 0);

                jsonArray.put(jsonObject);
            } catch (JSONException e)
            {
                ExLog.d(e.getMessage());
            }

            return jsonArray;
        }

        int size = list.size();

        if (maxSize > size)
        {
            maxSize = size;
        }

        for (int i = 0; i < maxSize; i++)
        {
            JSONObject jsonObject = new JSONObject();

            RecentlyDbPlace recentlyDbPlace = list.get(i);

            try
            {
                String serviceTypeString = recentlyDbPlace.serviceType.name();

                if (Constants.ServiceType.HOTEL.name().equalsIgnoreCase(serviceTypeString) == true //
                    || Constants.ServiceType.GOURMET.name().equalsIgnoreCase(serviceTypeString) == true)
                {
                    int index = recentlyDbPlace.index;

                    jsonObject.put("serviceType", serviceTypeString);
                    jsonObject.put("idx", index);

                    jsonArray.put(jsonObject);
                }
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        }

        return jsonArray;
    }

    public static String getDbTargetIndices(Context context, Constants.ServiceType serviceType, int maxSize)
    {
        if (context == null || serviceType == null || maxSize <= 0)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

        Cursor cursor = null;

        try
        {
            cursor = dailyDb.getRecentlyPlaces(-1, serviceType);

            if (cursor == null || cursor.getCount() == 0 || maxSize <= 0)
            {
                return "";
            }

            int size = cursor.getCount();
            if (maxSize > size)
            {
                maxSize = size;
            }

            for (int i = 0; i < maxSize; i++)
            {
                cursor.moveToPosition(i);

                int index = cursor.getInt(cursor.getColumnIndex(RecentlyList.PLACE_INDEX));

                if (i != 0)
                {
                    builder.append(RECENT_PLACE_DELIMITER);
                }

                builder.append(index);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
            }
        }

        DailyDbHelper.getInstance().close();

        return builder.toString();
    }

    public static ArrayList<Integer> getDbRecentlyIndexList(Context context, Constants.ServiceType... serviceTypes)
    {
        if (context == null || serviceTypes == null || serviceTypes.length == 0)
        {
            return null;
        }

        ArrayList<Integer> indexList = new ArrayList<>();

        DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

        Cursor cursor = null;

        try
        {
            cursor = dailyDb.getRecentlyPlaces(-1, serviceTypes);

            if (cursor == null || cursor.getCount() == 0)
            {
                return null;
            }

            int size = cursor.getCount();
            if (MAX_RECENT_PLACE_COUNT < size)
            {
                size = MAX_RECENT_PLACE_COUNT;
            }

            for (int i = 0; i < size; i++)
            {
                cursor.moveToPosition(i);

                int index = cursor.getInt(cursor.getColumnIndex(RecentlyList.PLACE_INDEX));

                indexList.add(index);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
            }
        }

        DailyDbHelper.getInstance().close();

        return indexList;
    }
}
