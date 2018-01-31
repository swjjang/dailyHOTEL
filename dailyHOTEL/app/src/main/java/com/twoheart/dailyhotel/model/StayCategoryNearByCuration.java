package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StaySuggestParcel;

public class StayCategoryNearByCuration extends StayCategoryCuration
{
    private double mRadius;

    public StayCategoryNearByCuration()
    {
        mStayCurationOption = new StayCurationOption();

        clear();
    }

    @Override
    public PlaceCurationOption getCurationOption()
    {
        return mStayCurationOption;
    }

    public double getRadius()
    {
        return mRadius;
    }

    public void setRadius(double radius)
    {
        this.mRadius = radius;
    }

    @Override
    public PlaceParams toPlaceParams(int page, int limit, boolean isDetails)
    {
        StayCategoryNearByParams stayCategoryNearByParams = new StayCategoryNearByParams(this);
        stayCategoryNearByParams.setPageInformation(page, limit, isDetails);

        return stayCategoryNearByParams;
    }

    @Override
    public void clear()
    {
        super.clear();

        mRadius = 0d;
    }

    public StayCategoryNearByCuration(Parcel in)
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
        super.writeToParcel(dest, flags);

        dest.writeDouble(mRadius);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mRadius = in.readDouble();
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayCategoryNearByCuration createFromParcel(Parcel in)
        {
            return new StayCategoryNearByCuration(in);
        }

        @Override
        public StayCategoryNearByCuration[] newArray(int size)
        {
            return new StayCategoryNearByCuration[size];
        }
    };
}
