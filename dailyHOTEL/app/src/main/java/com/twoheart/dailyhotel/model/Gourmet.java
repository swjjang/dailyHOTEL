package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONException;
import org.json.JSONObject;

public class Gourmet extends Place implements Parcelable
{
    public String saleDay;

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
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        saleDay = in.readString();
    }

    @Override
    public boolean setData(JSONObject jsonObject)
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
            imageUrl = jsonObject.getString("img");

            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = "Y".equalsIgnoreCase(jsonObject.getString("is_dailychoice"));
            isSoldOut = "Y".equalsIgnoreCase(jsonObject.getString("is_soldout"));

            saleDay = jsonObject.getString("sday");

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
