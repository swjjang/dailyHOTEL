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
    public int saleIndex;
    public int count;
    public int price;
    public int discountPrice;
    public String name;
    public int persons;

    public int minimumOrderQuantity;
    public int maximumOrderQuantity;
    public int availableTicketNumbers;


    public GourmetCartMenu()
    {

    }

    public GourmetCartMenu(JSONObject jsonObject)
    {
        if (jsonObject == null || jsonObject.length() == 0)
        {
            return;
        }

        try
        {
            index = jsonObject.getInt("index");
            saleIndex = jsonObject.getInt("saleIndex");
            count = jsonObject.getInt("count");
            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discountPrice");
            name = jsonObject.getString("name");
            persons = jsonObject.getInt("persons");
            minimumOrderQuantity = jsonObject.getInt("minimumOrderQuantity");
            maximumOrderQuantity = jsonObject.getInt("maximumOrderQuantity");
            availableTicketNumbers = jsonObject.getInt("availableTicketNumbers");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public int getTotalPrice()
    {
        return discountPrice * count;
    }

    public JSONObject toJSONObject()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("index", index);
            jsonObject.put("saleIndex", saleIndex);
            jsonObject.put("count", count);
            jsonObject.put("price", price);
            jsonObject.put("discountPrice", discountPrice);
            jsonObject.put("name", name);
            jsonObject.put("persons", persons);
            jsonObject.put("minimumOrderQuantity", minimumOrderQuantity);
            jsonObject.put("maximumOrderQuantity", maximumOrderQuantity);
            jsonObject.put("availableTicketNumbers", availableTicketNumbers);

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return jsonObject;
    }
}
