package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StaySuggestParcelV2;

public class StaySearchCuration extends StayCuration
{
    private StaySuggest mSuggest;
    private double mRadius;

    public StaySearchCuration()
    {
        mStayCurationOption = new StayCurationOption();

        clear();
    }

    public StaySearchCuration(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public PlaceCurationOption getCurationOption()
    {
        return mStayCurationOption;
    }

    public StaySuggest getSuggest()
    {
        return mSuggest;
    }

    public void setSuggest(StaySuggest suggest)
    {
        mSuggest = suggest;
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
        StaySearchParams staySearchParams = new StaySearchParams(this);
        staySearchParams.setPageInformation(page, limit, isDetails);

        return staySearchParams;
    }

    @Override
    public void clear()
    {
        super.clear();

        mSuggest = null;
        mRadius = 0d;
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

        dest.writeParcelable(new StaySuggestParcelV2(mSuggest), flags);
        dest.writeDouble(mRadius);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        StaySuggestParcelV2 staySuggestParcel = in.readParcelable(StaySuggestParcelV2.class.getClassLoader());

        if (staySuggestParcel != null)
        {
            mSuggest = staySuggestParcel.getSuggest();
        }

        mRadius = in.readDouble();
    }

    public static final Creator CREATOR = new Creator()
    {
        public StaySearchCuration createFromParcel(Parcel in)
        {
            return new StaySearchCuration(in);
        }

        @Override
        public StaySearchCuration[] newArray(int size)
        {
            return new StaySearchCuration[size];
        }
    };
}
