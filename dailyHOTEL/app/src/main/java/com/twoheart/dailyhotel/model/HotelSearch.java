package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class HotelSearch extends Stay
{
    public boolean setStay(JSONObject jsonObject, String imageUrl, int nights)
    {
        this.nights = nights;

        try
        {
            name = jsonObject.getString("hotel_name");
            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discount_avg");
            addressSummary = jsonObject.getString("addr_summary");

            try
            {
                mGrade = Grade.valueOf(jsonObject.getString("grade"));
            } catch (Exception e)
            {
                mGrade = Grade.etc;
            }

            index = jsonObject.getInt("hotel_idx");
            districtName = jsonObject.getString("district_name");
            categoryCode = jsonObject.getString("category");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = jsonObject.getInt("is_dailychoice") == 1;
            satisfaction = jsonObject.getInt("rating_value");

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

            dBenefitText = jsonObject.getString("hotel_benefit");
        } catch (JSONException e)
        {
            ExLog.e(e.toString());

            return false;
        }

        return true;
    }
}
