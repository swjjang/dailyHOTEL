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

    private int saleWeek = 1; // 1 : 1주일,  2 : 2주일

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
        }

        if (jsonObject.has("seq") == true)
        {
            sequence = jsonObject.getInt("seq");
        } else
        {
            sequence = 0;
        }

        saleWeek = 1;

        if (jsonObject.has("is_overseas") == true)
        {
            isOverseas = jsonObject.getBoolean("is_overseas");
        } else
        {
            isOverseas = false;
        }

        if (Util.isTextEmpty(url) == false)
        {
            imageUrl = url + jsonObject.getString("imagePath");
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
        dest.writeInt(saleWeek);
        dest.writeInt(isOverseas ? 1 : 0);
        dest.writeString(imageUrl);
    }

    protected void readFromParcel(Parcel in)
    {
        index = in.readInt();
        name = in.readString();
        englishName = in.readString();
        sequence = in.readInt();
        saleWeek = in.readInt();
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
