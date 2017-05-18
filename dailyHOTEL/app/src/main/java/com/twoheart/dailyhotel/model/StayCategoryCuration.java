package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android_sam on 2017. 5. 18..
 */

public class StayCategoryCuration extends StayCuration
{
    public StayCategoryCuration()
    {
        super();
    }

    public StayCategoryCuration(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public PlaceParams toPlaceParams(int page, int limit, boolean isDetails)
    {
        StayCategoryParams stayCategoryParams = new StayCategoryParams(this);
        stayCategoryParams.setPageInformation(page, limit, isDetails);

        return stayCategoryParams;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayCategoryCuration createFromParcel(Parcel in)
        {
            return new StayCategoryCuration(in);
        }

        @Override
        public StayCategoryCuration[] newArray(int size)
        {
            return new StayCategoryCuration[size];
        }
    };
}
