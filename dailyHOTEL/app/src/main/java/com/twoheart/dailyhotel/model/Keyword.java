package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Keyword implements Parcelable
{
    public int icon;
    public String name;
    public int price;

    public Keyword(int icon, String name)
    {
        this.icon = icon;
        this.name = name;
    }

    public Keyword(JSONObject jsonObject) throws JSONException
    {
        name = jsonObject.getString("display_text");

        if (jsonObject.has("sale_price") == true)
        {
            price = jsonObject.getInt("sale_price");
        }

        if (price > 0)
        {
            icon = 1;
        } else
        {
            icon = 0;
        }
    }

    public Keyword(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(icon);
        dest.writeString(name);
        dest.writeInt(price);
    }

    private void readFromParcel(Parcel in)
    {
        icon = in.readInt();
        name = in.readString();
        price = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public Keyword createFromParcel(Parcel in)
        {
            return new Keyword(in);
        }

        @Override
        public Keyword[] newArray(int size)
        {
            return new Keyword[size];
        }

    };
}
