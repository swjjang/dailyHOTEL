package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Area;
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
        Area areaGroup = mRegion.getAreaGroup();
        Area area = mRegion.getArea();

        dest.writeString(mRegion.getAreaType().name());

        if (areaGroup == null)
        {
            dest.writeInt(0);
            dest.writeString(null);
            dest.writeInt(0);
        } else
        {
            dest.writeInt(areaGroup.index);
            dest.writeString(areaGroup.name);
        }

        if (area == null)
        {
            dest.writeInt(0);
            dest.writeString(null);
            dest.writeInt(0);
        } else
        {
            dest.writeInt(area.index);
            dest.writeString(area.name);
        }
    }

    private void readFromParcel(Parcel in)
    {
        Area areaGroup;

        PreferenceRegion.AreaType areaType = PreferenceRegion.AreaType.valueOf(in.readString());

        int areaGroupIndex = in.readInt();
        String areaGroupName = in.readString();

        if (areaGroupIndex == 0 && areaGroupName == null)
        {
            areaGroup = null;
        } else
        {
            areaGroup = new Area();
            areaGroup.index = areaGroupIndex;
            areaGroup.name = areaGroupName;
        }

        Area area;

        int areaIndex = in.readInt();
        String areaName = in.readString();

        if (areaIndex == 0 && areaName == null)
        {
            area = null;
        } else
        {
            area = new Area();
            area.index = areaIndex;
            area.name = areaName;
        }

        mRegion = new StayRegion(areaType, areaGroup, area);
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
