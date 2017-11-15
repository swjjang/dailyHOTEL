package com.daily.dailyhotel.entity;


import com.daily.base.util.ExLog;

import org.json.JSONObject;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetCartMenu
{
    public int index;
    public int count;
    public int price;
    public int discountPrice;
    public String name;
    public int persons;

    public GourmetCartMenu()
    {

    }

    public GourmetCartMenu(JSONObject jsonObject)
    {
        if (jsonObject == null)
        {
            return;
        }

        try
        {
            index = jsonObject.getInt("index");
            count = jsonObject.getInt("count");
            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discountPrice");
            name = jsonObject.getString("name");
            persons = jsonObject.getInt("persons");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public JSONObject toJSONObject()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("index", index);
            jsonObject.put("count", count);
            jsonObject.put("price", price);
            jsonObject.put("discountPrice", discountPrice);
            jsonObject.put("name", name);
            jsonObject.put("persons", persons);

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return jsonObject;
    }
}
