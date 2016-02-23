package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class HotelFilter implements Parcelable
{
    public static final int MIN_PERSON = 2;
    public static final int MAX_PERSON = 8;

    public int minPerson;
    public int maxPerson;
    public int bedType;

    public HotelFilter(int bedType, int minPerson, int maxPerson)
    {
        this.bedType = bedType;
        this.minPerson = minPerson;
        this.maxPerson = maxPerson;
    }

    public HotelFilter(JSONObject jsonObject) throws JSONException
    {
        bedType = jsonObject.getInt("bedType");
        minPerson = jsonObject.getInt("minPerson");
        maxPerson = jsonObject.getInt("maxPerson");
    }

    public HotelFilter(Parcel in)
    {
        readFromParcel(in);
    }

    public boolean isFiltered(int bedType, int person)
    {
        if(this.bedType != bedType//
            || person < minPerson && person > maxPerson)
        {
            return false;
        }

        return true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(minPerson);
        dest.writeInt(maxPerson);
        dest.writeInt(bedType);
    }

    private void readFromParcel(Parcel in)
    {
        minPerson = in.readInt();
        maxPerson = in.readInt();
        bedType = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public HotelFilter createFromParcel(Parcel in)
        {
            return new HotelFilter(in);
        }

        @Override
        public HotelFilter[] newArray(int size)
        {
            return new HotelFilter[size];
        }
    };
}
