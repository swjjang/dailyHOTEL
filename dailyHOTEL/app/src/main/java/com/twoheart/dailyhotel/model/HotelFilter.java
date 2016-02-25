package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class HotelFilter implements Parcelable
{
    public static final int MIN_PERSON = 2;
    public static final int MAX_PERSON = 10;

    // 필터 목록
    private static final String DOUBLE = "더블";
    private static final String TWIN = "트윈";
    private static final String HEATEDFLOORS = "온돌";

    public int maxPerson;
    public int bedType;

    public HotelFilter(JSONObject jsonObject) throws JSONException
    {
        String type = jsonObject.getString("bedType");
        setType(type);

        maxPerson = jsonObject.getInt("personsMaxium");
    }

    public HotelFilter(Parcel in)
    {
        readFromParcel(in);
    }

    public boolean isFiltered(int bedType, int person)
    {
        if(bedType == HotelFilters.FLAG_HOTEL_FILTER_BED_NONE)
        {
            if (person > maxPerson)
            {
                return false;
            }
        } else
        {
            if (this.bedType != bedType || person > maxPerson)
            {
                return false;
            }
        }

        return true;
    }

    private void setType(String type)
    {
        if(DOUBLE.equalsIgnoreCase(type) == true)
        {
            bedType = HotelFilters.FLAG_HOTEL_FILTER_BED_DOUBLE;
        } else if(TWIN.equalsIgnoreCase(type) == true)
        {
            bedType = HotelFilters.FLAG_HOTEL_FILTER_BED_TWIN;
        } else if(HEATEDFLOORS.equalsIgnoreCase(type) == true)
        {
            bedType = HotelFilters.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS;
        } else
        {
            bedType = HotelFilters.FLAG_HOTEL_FILTER_BED_CHECKIN;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(maxPerson);
        dest.writeInt(bedType);
    }

    private void readFromParcel(Parcel in)
    {
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
