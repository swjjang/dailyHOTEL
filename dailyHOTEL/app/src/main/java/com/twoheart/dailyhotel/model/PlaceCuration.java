package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android_sam on 2016. 7. 20..
 */
public class PlaceCuration implements Parcelable
{
    protected Province mProvince;
    protected Location mLocation;

    public PlaceCuration()
    {
    }

    public void clear()
    {
        mProvince = null;
        mLocation = null;
    }

    public Province getProvince()
    {
        return mProvince;
    }

    public void setProvince(Province province)
    {
        mProvince = province;
    }

    public Location getLocation()
    {
        return mLocation;
    }

    public void setLocation(Location location)
    {
        mLocation = location;
    }

    public PlaceCuration(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mProvince, flags);
        dest.writeParcelable(mLocation, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        mProvince = in.readParcelable(Province.class.getClassLoader());
        mLocation = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public PlaceCuration createFromParcel(Parcel in)
        {
            return new PlaceCuration(in);
        }

        @Override
        public PlaceCuration[] newArray(int size)
        {
            return new PlaceCuration[size];
        }
    };
}
