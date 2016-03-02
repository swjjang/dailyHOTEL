package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class HotelFilter implements Parcelable
{
    public static final int MIN_PERSON = 2;
    public static final int MAX_PERSON = 10;

    public static int FLAG_HOTEL_FILTER_BED_NONE = 0x00;
    public static int FLAG_HOTEL_FILTER_BED_DOUBLE = 0x01;
    public static int FLAG_HOTEL_FILTER_BED_TWIN = 0x02;
    public static int FLAG_HOTEL_FILTER_BED_HEATEDFLOORS = 0x04;
    public static int FLAG_HOTEL_FILTER_BED_CHECKIN = 0x08;
    //
    public static int FLAG_HOTEL_FILTER_AMENITIES_NONE = 0x00;
    public static int FLAG_HOTEL_FILTER_AMENITIES_WIFI = 0x01;
    public static int FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST = 0x02;
    public static int FLAG_HOTEL_FILTER_AMENITIES_COOKING = 0x04;
    public static int FLAG_HOTEL_FILTER_AMENITIES_BATH = 0x08;
    public static int FLAG_HOTEL_FILTER_AMENITIES_PARKING = 0x10;
    public static int FLAG_HOTEL_FILTER_AMENITIES_POOL = 0x20;
    public static int FLAG_HOTEL_FILTER_AMENITIES_FITNESS = 0x40;

    public static final String DOUBLE = "더블";
    public static final String TWIN = "트윈";
    public static final String HEATEDFLOORS = "온돌";

    public int maxPerson;
    public int bedType;
    public int amenitiesFlag;

    public HotelFilter(JSONObject jsonObject) throws JSONException
    {
        setBedType(jsonObject.getString("bedType"));
        setAmenitiesFlag(jsonObject);
        maxPerson = jsonObject.getInt("personsMaxium");
    }

    public HotelFilter(Parcel in)
    {
        readFromParcel(in);
    }

    public boolean isPersonFiltered(int person)
    {
        if (person == MIN_PERSON)
        {
            return true;
        }

        return person <= maxPerson;
    }

    public boolean isBedTypeFiltered(int bedTypeFlag)
    {
        if (bedTypeFlag == FLAG_HOTEL_FILTER_BED_NONE)
        {
            return true;
        }

        return (bedType & bedTypeFlag) == bedType;
    }

    public boolean isAmenitiesFiltered(int flags)
    {
        if (flags == FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            return true;
        }

        return (amenitiesFlag & flags) != 0;
    }

    private void setBedType(String type)
    {
        if (DOUBLE.equalsIgnoreCase(type) == true)
        {
            bedType = FLAG_HOTEL_FILTER_BED_DOUBLE;
        } else if (TWIN.equalsIgnoreCase(type) == true)
        {
            bedType = FLAG_HOTEL_FILTER_BED_TWIN;
        } else if (HEATEDFLOORS.equalsIgnoreCase(type) == true)
        {
            bedType = FLAG_HOTEL_FILTER_BED_HEATEDFLOORS;
        } else
        {
            bedType = FLAG_HOTEL_FILTER_BED_CHECKIN;
        }
    }

    private void setAmenitiesFlag(JSONObject jsonObject) throws JSONException
    {
        boolean wifi = false;
        boolean breakfast = false;
        boolean cooking = false;
        boolean bath = false;
        boolean parking = false;
        boolean pool = false;
        boolean fitness = false;
        amenitiesFlag = FLAG_HOTEL_FILTER_AMENITIES_NONE;

        if (jsonObject.has("wifi") == true)
        {
            wifi = "Y".equalsIgnoreCase(jsonObject.getString("wifi")) ? true : false;
        }

        if (jsonObject.has("breakfast") == true)
        {
            breakfast = "Y".equalsIgnoreCase(jsonObject.getString("breakfast")) ? true : false;
        }

        if (jsonObject.has("cooking") == true)
        {
            cooking = "Y".equalsIgnoreCase(jsonObject.getString("cooking")) ? true : false;
        }

        if (jsonObject.has("bath") == true)
        {
            bath = "Y".equalsIgnoreCase(jsonObject.getString("bath")) ? true : false;
        }

        if (jsonObject.has("parking") == true)
        {
            parking = "Y".equalsIgnoreCase(jsonObject.getString("parking")) ? true : false;
        }

        if (jsonObject.has("pool") == true)
        {
            pool = "Y".equalsIgnoreCase(jsonObject.getString("pool")) ? true : false;
        }

        if (jsonObject.has("fitness") == true)
        {
            fitness = "Y".equalsIgnoreCase(jsonObject.getString("fitness")) ? true : false;
        }

        if (wifi == true)
        {
            amenitiesFlag |= FLAG_HOTEL_FILTER_AMENITIES_WIFI;
        }

        if (breakfast == true)
        {
            amenitiesFlag |= FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST;
        }

        if (cooking == true)
        {
            amenitiesFlag |= FLAG_HOTEL_FILTER_AMENITIES_COOKING;
        }

        if (bath == true)
        {
            amenitiesFlag |= FLAG_HOTEL_FILTER_AMENITIES_BATH;
        }

        if (parking == true)
        {
            amenitiesFlag |= FLAG_HOTEL_FILTER_AMENITIES_PARKING;
        }

        if (pool == true)
        {
            amenitiesFlag |= FLAG_HOTEL_FILTER_AMENITIES_POOL;
        }

        if (fitness == true)
        {
            amenitiesFlag |= FLAG_HOTEL_FILTER_AMENITIES_FITNESS;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(maxPerson);
        dest.writeInt(bedType);
        dest.writeInt(amenitiesFlag);
    }

    private void readFromParcel(Parcel in)
    {
        maxPerson = in.readInt();
        bedType = in.readInt();
        amenitiesFlag = in.readInt();
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
