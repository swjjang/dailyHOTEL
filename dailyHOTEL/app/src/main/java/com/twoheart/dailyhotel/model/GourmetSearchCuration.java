package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.parcel.GourmetSuggestParcelV2;

public class GourmetSearchCuration extends GourmetCuration
{
    private GourmetSuggestV2 mSuggest;
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

    public GourmetSuggestV2 getSuggest()
    {
        return mSuggest;
    }

    public void setSuggest(GourmetSuggestV2 suggest)
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
        GourmetSearchParams gourmetSearchParams = new GourmetSearchParams(this);
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

        dest.writeParcelable(new GourmetSuggestParcelV2(mSuggest), flags);
        dest.writeDouble(mRadius);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        GourmetSuggestParcelV2 gourmetSuggestParcel = in.readParcelable(GourmetSuggestParcelV2.class.getClassLoader());

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
