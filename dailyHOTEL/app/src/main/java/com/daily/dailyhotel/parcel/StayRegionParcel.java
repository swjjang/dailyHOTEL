package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.AreaElement;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.entity.StayRegion;

public class StayRegionParcel implements Parcelable
{
    private StayRegion mRegion;

    public StayRegionParcel(@NonNull StayRegion region)
    {
        if (region == null)
        {
            throw new NullPointerException("region == null");
        }

        mRegion = region;
    }

    public StayRegionParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StayRegion getRegion()
    {
        return mRegion;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        AreaElement areaGroupElement = mRegion.getAreaGroupElement();
        AreaElement areaElement = mRegion.getAreaElement();

        dest.writeString(mRegion.getAreaType().name());

        if (areaGroupElement == null)
        {
            dest.writeInt(0);
            dest.writeString(null);
            dest.writeInt(0);
        } else
        {
            dest.writeInt(areaGroupElement.index);
            dest.writeString(areaGroupElement.name);
        }

        if (areaElement == null)
        {
            dest.writeInt(0);
            dest.writeString(null);
            dest.writeInt(0);
        } else
        {
            dest.writeInt(areaElement.index);
            dest.writeString(areaElement.name);
        }
    }

    private void readFromParcel(Parcel in)
    {
        AreaElement areaGroupElement;

        PreferenceRegion.AreaType areaType = PreferenceRegion.AreaType.valueOf(in.readString());

        int areaGroupIndex = in.readInt();
        String areaGroupName = in.readString();

        if (areaGroupIndex == 0 && areaGroupName == null)
        {
            areaGroupElement = null;
        } else
        {
            areaGroupElement = new Area();
            areaGroupElement.index = areaGroupIndex;
            areaGroupElement.name = areaGroupName;
        }

        AreaElement areaElement;

        int areaIndex = in.readInt();
        String areaName = in.readString();

        if (areaIndex == 0 && areaName == null)
        {
            areaElement = null;
        } else
        {
            areaElement = new Area();
            areaElement.index = areaIndex;
            areaElement.name = areaName;
        }

        mRegion = new StayRegion(areaType, areaGroupElement, areaElement);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayRegionParcel createFromParcel(Parcel in)
        {
            return new StayRegionParcel(in);
        }

        @Override
        public StayRegionParcel[] newArray(int size)
        {
            return new StayRegionParcel[size];
        }

    };
}
