package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcelable;

/**
 * Created by android_sam on 2016. 7. 20..
 */
public abstract class PlaceCuration implements Parcelable
{
    protected Location mLocation;

    public PlaceCuration()
    {
    }

    public abstract PlaceCurationOption getCurationOption();

    public abstract void setCurationOption(PlaceCurationOption placeCurationOption);

    public abstract PlaceParams toPlaceParams(int page, int limit, boolean isDetails);

    public void clear()
    {
        mLocation = null;
    }

    public Location getLocation()
    {
        return mLocation;
    }

    public void setLocation(Location location)
    {
        mLocation = location;
    }
}
