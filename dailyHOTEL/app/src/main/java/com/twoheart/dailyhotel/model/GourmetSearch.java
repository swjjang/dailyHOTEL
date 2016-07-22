package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class GourmetSearch extends Gourmet
{
    @Override
    public boolean setData(JSONObject jsonObject, String imageUrl)
    {
        try
        {
            index = jsonObject.getInt("restaurant_idx");
            name = jsonObject.getString("restaurant_name");

            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discount");
            addressSummary = jsonObject.getString("addr_summary");
            grade = Grade.gourmet;
            districtName = jsonObject.getString("district_name");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = jsonObject.getInt("is_dailychoice") == 1 ? true : false;
            persons = jsonObject.getInt("persons");
            category = jsonObject.getString("category");
            isSoldOut = jsonObject.getBoolean("is_sold_out");

            JSONObject imageJSONObject = jsonObject.getJSONObject("img_path_main");

            Iterator<String> iterator = imageJSONObject.keys();
            while (iterator.hasNext())
            {
                String key = iterator.next();

                try
                {
                    JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
                    this.imageUrl = imageUrl + key + pathJSONArray.getString(0);
                    break;
                } catch (JSONException e)
                {
                }
            }
        } catch (JSONException e)
        {
            ExLog.d(e.toString());

            return false;
        }

        return true;
    }
}
