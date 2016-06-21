package com.twoheart.dailyhotel.place.manager;

import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon on 2016. 6. 17..
 */
public class PlaceEventBannerManager
{
    private static PlaceEventBannerManager mInstance;

    public List<EventBanner> mEventBannerList;

    public synchronized PlaceEventBannerManager getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new PlaceEventBannerManager();
        }

        return mInstance;
    }

    private PlaceEventBannerManager()
    {
        mEventBannerList = new ArrayList<>();
    }

    public List<EventBanner> getList()
    {
        return mEventBannerList;
    }

    public void setList(List<EventBanner> eventBannerList)
    {
        clear();
        mEventBannerList.addAll(eventBannerList);
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

            if (msgCode == 100)
            {
                JSONObject dataJSONObject = jsonObject.getJSONObject("data");

                String baseUrl = dataJSONObject.getString("imgUrl");

                JSONArray jsonArray = dataJSONObject.getJSONArray("eventBanner");

                int length = jsonArray.length();

                if(length > 0)
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
