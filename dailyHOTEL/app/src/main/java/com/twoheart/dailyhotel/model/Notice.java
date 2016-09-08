package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

public class Notice
{
    public int index;
    public String title;
    public String linkUrl;
    public String createdAt;
    public boolean isNew;

    public Notice(JSONObject jsonObject)
    {
        if (jsonObject == null)
        {
            return;
        }

        try
        {
            index = jsonObject.getInt("idx");
            title = jsonObject.getString("title");
            linkUrl = jsonObject.getString("linkUrl");
            createdAt = jsonObject.getString("createdAt");
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
