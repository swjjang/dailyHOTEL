package com.twoheart.dailyhotel.model;

import android.os.Parcel;

public class GourmetSearchCuration extends GourmetCuration
{
    private Keyword mKeyword;
    private double mRadius;

    public GourmetSearchCuration()
    {
        mGourmetCurationOption = new GourmetCurationOption();

        clear();
    }

    @Override
    public PlaceCurationOption getCurationOption()
    {
        return mGourmetCurationOption;
    }

    public Keyword getKeyword()
    {
        return mKeyword;
    }

    public void setKeyword(Keyword keyword)
    {
        this.mKeyword = keyword;
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
        GourmetSearchParams gourmetSearchParams = new GourmetSearchParams(this);
        gourmetSearchParams.setPageInformation(page, limit, isDetails);

        return gourmetSearchParams;
    }

    @Override
    public void clear()
    {
        super.clear();

        mKeyword = null;
        mRadius = 0d;
    }

    public GourmetSearchCuration(Parcel in)
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

        dest.writeParcelable(mKeyword, flags);
        dest.writeDouble(mRadius);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mKeyword = in.readParcelable(Keyword.class.getClassLoader());
        mRadius = in.readDouble();
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetSearchCuration createFromParcel(Parcel in)
        {
            return new GourmetSearchCuration(in);
        }

        @Override
        public GourmetSearchCuration[] newArray(int size)
        {
            return new GourmetSearchCuration[size];
        }
    };
}
