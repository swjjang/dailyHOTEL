package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Area extends Province
{
    private static final String PROVINCE_INDEXKEY = "provinceIdx";

    public String tag;

    private int mProvinceIndex;
    private Province mProvince;

    public Area()
    {
        super();
    }

    public Area(Parcel in)
    {
        readFromParcel(in);
    }

    public Area(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject, null);

        mProvinceIndex = jsonObject.getInt(PROVINCE_INDEXKEY);
        tag = jsonObject.getString("tag");
    }

    public Province getProvince()
    {
        return mProvince;
    }

    public void setProvince(Province province)
    {
        mProvince = province;
    }

    @Override
    public int getProvinceIndex()
    {
        return mProvinceIndex;
    }

    public void setProvinceIndex(int provinceIndex)
    {
        mProvinceIndex = provinceIndex;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(mProvinceIndex);
        dest.writeString(tag);
        dest.writeParcelable(mProvince, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mProvinceIndex = in.readInt();
        tag = in.readString();
        mProvince = in.readParcelable(Province.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Area createFromParcel(Parcel in)
        {
            return new Area(in);
        }

        @Override
        public Area[] newArray(int size)
        {
            return new Area[size];
        }
    };
}
