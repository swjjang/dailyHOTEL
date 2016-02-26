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
    public int persons;
    public String category;
    public int categoryCode;
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

        dest.writeInt(persons);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        persons = in.readInt();
    }

    @Override
    public boolean setData(JSONObject jsonObject, String imageUrl)
    {
        try
        {
            index = jsonObject.getInt("restaurantIdx");
            name = jsonObject.getString("restaurantName");

            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discount");
            addressSummary = jsonObject.getString("addrSummary");
            grade = Grade.gourmet;
            districtName = jsonObject.getString("districtName");
            categoryCode = jsonObject.getInt("categoryCode");

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

            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = jsonObject.getBoolean("isDailychoice");
            isSoldOut = jsonObject.getBoolean("isSoldOut");
            persons = jsonObject.getInt("persons");
            category = jsonObject.getString("category");

            if (jsonObject.has("ratingValue") == true)
            {
                satisfaction = jsonObject.getInt("ratingValue");
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
