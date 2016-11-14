package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONException;
import org.json.JSONObject;

public class Bank
{
    public String name;
    public String code;

    public Bank(JSONObject jsonObject)
    {
        try
        {
            code = jsonObject.getString("code");
            name = jsonObject.getString("name");
        } catch (JSONException e)
        {
            ExLog.d(e.toString());
        }
    }
}
