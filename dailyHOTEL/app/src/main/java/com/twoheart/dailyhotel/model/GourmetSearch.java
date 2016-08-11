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
            index = jsonObject.getInt("restaurantIdx");
            name = jsonObject.getString("name");
            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discount");
            addressSummary = jsonObject.getString("addrSummary");
            grade = Grade.gourmet;
            districtName = jsonObject.getString("districtName");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = jsonObject.getBoolean("isDailychoice");
            persons = jsonObject.getInt("persons");
            category = jsonObject.getString("category");
            categoryCode = jsonObject.getInt("categoryCode");
            categorySequence = jsonObject.getInt("categorySeq");
            isSoldOut = jsonObject.getBoolean("isSoldOut");
            distance = jsonObject.getDouble("distance");

            JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");

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
