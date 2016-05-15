package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RegionViewItem implements Parcelable
{
    private Province province;
    private ArrayList<Area[]> areaList;
    public boolean isExpandGroup;

    public RegionViewItem()
    {
        super();
    }

    public RegionViewItem(Parcel in)
    {
        readFromParcel(in);
    }

    public ArrayList<Area[]> getAreaList()
    {
        return areaList;
    }

    public void setAreaList(ArrayList<Area[]> areaList)
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

    private void readFromParcel(Parcel in)
    {
        province = in.readParcelable(Province.class.getClassLoader());
        areaList = (ArrayList<Area[]>) in.readArrayList(Area.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public RegionViewItem createFromParcel(Parcel in)
        {
            return new RegionViewItem(in);
        }

        @Override
        public RegionViewItem[] newArray(int size)
        {
            return new RegionViewItem[size];
        }

    };
}
