package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.parcel.GourmetSuggestParcel;

public class GourmetSearchCuration extends GourmetCuration
{
    private GourmetSuggest mSuggest;
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

    public GourmetSuggest getSuggest()
    {
        return mSuggest;
    }

    public void setSuggest(GourmetSuggest suggest)
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
        GourmetSearchParams gourmetSearchParams = new GourmetSearchParams(this, true);
        gourmetSearchParams.setPageInformation(page, limit, isDetails);

        return gourmetSearchParams;
    }

    @Override
    public void clear()
    {
        super.clear();

        mSuggest = null;
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

        dest.writeParcelable(new GourmetSuggestParcel(mSuggest), flags);
        dest.writeDouble(mRadius);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        GourmetSuggestParcel gourmetSuggestParcel = in.readParcelable(GourmetSuggestParcel.class.getClassLoader());

        if (gourmetSuggestParcel != null)
        {
            mSuggest = gourmetSuggestParcel.getSuggest();
        }

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
