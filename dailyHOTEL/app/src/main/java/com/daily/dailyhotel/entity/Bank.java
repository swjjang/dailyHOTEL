package com.daily.dailyhotel.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android_sam on 2018. 1. 11..
 */

public class Bank
{
    public String name;
    public String code;

    public JSONObject getJsonObject()
    {
        JSONObject object = new JSONObject();

        try
        {
            object.put("code", code);
            object.put("name", name);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return object;
    }
}
