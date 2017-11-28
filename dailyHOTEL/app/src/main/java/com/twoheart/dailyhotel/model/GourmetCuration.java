package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.model.time.GourmetBookingDay;

public class GourmetCuration extends PlaceCuration
{
    private GourmetBookingDay mGourmetBookingDay;
    private Province mProvince;

    protected GourmetCurationOption mGourmetCurationOption;

    public GourmetCuration(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetCuration()
    {
        mGourmetCurationOption = new GourmetCurationOption();

        clear();
    }

    public GourmetBookingDay getGourmetBookingDay()
    {
        return mGourmetBookingDay;
    }

    public void setGourmetBookingDay(GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null)
        {
            return;
        }

        mGourmetBookingDay = gourmetBookingDay;
    }

    @Override
    public PlaceCurationOption getCurationOption()
    {
        return mGourmetCurationOption;
    }

    @Override
    public void setCurationOption(PlaceCurationOption placeCurationOption)
    {
        if (mGourmetCurationOption == null)
        {
            mGourmetCurationOption = new GourmetCurationOption();
        }

        mGourmetCurationOption.setCurationOption((GourmetCurationOption) placeCurationOption);
    }

    @Override
    public PlaceParams toPlaceParams(int page, int limit, boolean isDetails)
    {
        GourmetParams gourmetParams = new GourmetParams(this);
        gourmetParams.setPageInformation(page, limit, isDetails);

        return gourmetParams;
    }

    public void clear()
    {
        super.clear();

        mGourmetCurationOption.clear();

        mGourmetBookingDay = null;
        mProvince = null;
    }

    public Province getProvince()
    {
        return mProvince;
    }

    public void setProvince(Province province)
    {
        mProvince = province;
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
        dest.writeParcelable(mGourmetBookingDay, flags);
        dest.writeParcelable(mGourmetCurationOption, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        mProvince = in.readParcelable(Province.class.getClassLoader());
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mGourmetBookingDay = in.readParcelable(GourmetBookingDay.class.getClassLoader());
        mGourmetCurationOption = in.readParcelable(GourmetCurationOption.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetCuration createFromParcel(Parcel in)
        {
            return new GourmetCuration(in);
        }

        @Override
        public GourmetCuration[] newArray(int size)
        {
            return new GourmetCuration[size];
        }
    };
}
