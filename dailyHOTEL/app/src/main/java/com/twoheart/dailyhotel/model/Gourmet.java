package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Gourmet extends Place implements Parcelable
{
    public String saleDay;
    public int persons;
    public String category;
    public int categoryIcon;
    public float distance;

    public Gourmet()
    {
        super();
    }

    public Gourmet(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(saleDay);
        dest.writeInt(persons);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        saleDay = in.readString();
        persons = in.readInt();
    }

    @Override
    public boolean setData(JSONObject jsonObject, String imageUrl)
    {
        try
        {
            index = jsonObject.getInt("restaurant_idx");
            name = jsonObject.getString("restaurant_name");

            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discount");
            address = jsonObject.getString("addr_summary");
            grade = Grade.gourmet;
            districtName = jsonObject.getString("district_name");

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

            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = "Y".equalsIgnoreCase(jsonObject.getString("is_dailychoice"));
            isSoldOut = "Y".equalsIgnoreCase(jsonObject.getString("is_soldout"));

            saleDay = jsonObject.getString("sday");
            persons = jsonObject.getInt("persons");
            category = jsonObject.getString("category");

            if (jsonObject.has("rating_value") == true)
            {
                satisfaction = jsonObject.getInt("rating_value");
            }
        } catch (JSONException e)
        {
            ExLog.d(e.toString());

            return false;
        }

        return true;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Gourmet createFromParcel(Parcel in)
        {
            return new Gourmet(in);
        }

        @Override
        public Gourmet[] newArray(int size)
        {
            return new Gourmet[size];
        }
    };
}
