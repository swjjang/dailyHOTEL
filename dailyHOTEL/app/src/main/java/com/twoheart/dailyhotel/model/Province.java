package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class Province implements Parcelable
{
    public int index;
    public String name;
    public String englishName;
    public int sequence;
    public boolean isOverseas;
    public String imageUrl;

    public Province()
    {
        super();
    }

    public Province(Parcel in)
    {
        readFromParcel(in);
    }

    public Province(JSONObject jsonObject, String url) throws JSONException
    {
        index = jsonObject.getInt("idx");
        name = jsonObject.getString("name");

        if (jsonObject.has("nameEng") == true)
        {
            englishName = jsonObject.getString("nameEng");
        } else
        {
            englishName = "";
        }

        if (jsonObject.has("seq") == true)
        {
            sequence = jsonObject.getInt("seq");
        } else
        {
            sequence = 0;
        }

        if (jsonObject.has("isOverseas") == true)
        {
            isOverseas = jsonObject.getInt("isOverseas") == 1;
        } else
        {
            isOverseas = false;
        }

        if (Util.isTextEmpty(url) == false)
        {
            imageUrl = url + jsonObject.getString("imagePath");
        } else
        {
            imageUrl = "";
        }
    }

    public int getProvinceIndex()
    {
        return index;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeString(englishName);
        dest.writeInt(sequence);
        dest.writeInt(isOverseas ? 1 : 0);
        dest.writeString(imageUrl);
    }

    protected void readFromParcel(Parcel in)
    {
        index = in.readInt();
        name = in.readString();
        englishName = in.readString();
        sequence = in.readInt();
        isOverseas = in.readInt() == 1 ? true : false;
        imageUrl = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Province createFromParcel(Parcel in)
        {
            return new Province(in);
        }

        @Override
        public Province[] newArray(int size)
        {
            return new Province[size];
        }

    };
}
