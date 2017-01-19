package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

@JsonObject
public class Keyword implements Parcelable
{
    @JsonIgnore
    public int icon;

    @JsonField(name = "displayText")
    public String name;

    @JsonField(name = "discount")
    public int price;

    public Keyword()
    {
    }

    public Keyword(int icon, String name)
    {
        this.icon = icon;
        this.name = name;
        this.price = -1;
    }

    public Keyword(JSONObject jsonObject, int defaultPlaceIcon) throws JSONException
    {
        name = jsonObject.getString("displayText");

        if (jsonObject.has("discount") == true)
        {
            price = jsonObject.getInt("discount");
        } else
        {
            price = 0;
        }

        if (price > 0)
        {
            icon = defaultPlaceIcon;
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

    @JsonIgnore
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
