package com.twoheart.dailyhotel.place.manager;

import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.util.DailyAssert;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon on 2016. 6. 17..
 */
public abstract class PlaceEventBannerManager
{
    private List<EventBanner> mEventBannerList;

    public PlaceEventBannerManager()
    {
        mEventBannerList = new ArrayList<>();
    }

    public List<EventBanner> getList()
    {
        return mEventBannerList;
    }

    public int getCount()
    {
        return mEventBannerList.size();
    }

    public void setList(List<EventBanner> eventBannerList)
    {
        clear();

        if (eventBannerList == null)
        {
            return;
        }

        mEventBannerList.addAll(eventBannerList);
    }

    public EventBanner getEventBanner(int index)
    {
        return mEventBannerList.get(index);
    }

    public void clear()
    {
        mEventBannerList.clear();
    }

    public static List<EventBanner> makeEventBannerList(JSONObject jsonObject)
    {
        List<EventBanner> eventBannerList = null;

        try
        {
            int msgCode = jsonObject.getInt("msgCode");
            DailyAssert.assertEquals(100, msgCode);

            if (msgCode == 100)
            {
                JSONObject dataJSONObject = jsonObject.getJSONObject("data");
                DailyAssert.assertNotNull(dataJSONObject);

                String baseUrl = dataJSONObject.getString("imgUrl");
                DailyAssert.assertNotNull(baseUrl);

                JSONArray jsonArray = dataJSONObject.getJSONArray("eventBanner");

                int length = jsonArray.length();
                DailyAssert.assertTrue("has EventBanner", length > 0);
                if (length > 0)
                {
                    eventBannerList = new ArrayList<>();

                    for (int i = 0; i < length; i++)
                    {
                        try
                        {
                            EventBanner eventBanner = new EventBanner(jsonArray.getJSONObject(i), baseUrl);
                            eventBannerList.add(eventBanner);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return eventBannerList;
    }
}
