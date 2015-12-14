package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AreaItem implements Parcelable
{
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public AreaItem createFromParcel(Parcel in)
        {
            return new AreaItem(in);
        }

        @Override
        public AreaItem[] newArray(int size)
        {
            return new AreaItem[size];
        }

    };
    private Province province;
    private ArrayList<Area> areaList;
    public boolean mIsExpandGroup;

    public AreaItem()
    {
        super();
    }

    public AreaItem(Parcel in)
    {
        readFromParcel(in);
    }

    public ArrayList<Area> getAreaList()
    {
        return areaList;
    }

    public void setAreaList(ArrayList<Area> areaList)
    {
        this.areaList = areaList;
    }

    public Province getProvince()
    {
        return province;
    }

    public void setProvince(Province province)
    {
        this.province = province;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(province, flags);
        dest.writeList(areaList);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    private void readFromParcel(Parcel in)
    {
        province = in.readParcelable(Province.class.getClassLoader());
        areaList = in.readArrayList(Area.class.getClassLoader());
    }
}
