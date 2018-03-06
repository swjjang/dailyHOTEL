package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.Constants;

import java.util.Locale;

/**
 * Created by android_sam on 2016. 6. 30..
 */
public abstract class PlaceParams implements Parcelable
{
    protected int provinceIdx;
    protected int areaIdx;
    protected int subwayIndex;
    protected int persons;
    protected double longitude;
    protected double latitude;
    protected int page;
    protected int limit;
    protected String sortProperty;
    protected String sortDirection;
    protected boolean details;

    protected Constants.SortType mSort;

    public PlaceParams(PlaceCuration placeCuration)
    {
        setPlaceParams(placeCuration);
    }

    public PlaceParams(Parcel in)
    {
        readFromParcel(in);
    }

    public abstract void setPlaceParams(PlaceCuration placeCuration);

    public abstract String toParamsString();

    public abstract void setSortType(Constants.SortType sortType);

    public void setPageInformation(int page, int limit, boolean isDetails)
    {
        this.page = page;
        this.limit = limit;
        this.details = isDetails;
    }

    public Constants.SortType getSortType()
    {
        return mSort;
    }

    public boolean hasLocation()
    {
        return (latitude != 0d && longitude != 0d);
    }

    protected String getParamString(String key, Object value)
    {
        String stringValue = String.valueOf(value);
        if (DailyTextUtils.isTextEmpty(stringValue))
        {
            return "";
        }

        return String.format(Locale.KOREA, "%s=%s", key, stringValue);
    }

    protected void clear()
    {
        provinceIdx = 0;
        areaIdx = 0;
        subwayIndex = 0;
        persons = 0;
        longitude = 0.0d;
        latitude = 0.0d;
        page = 0;
        limit = 0;
        sortProperty = null;
        sortDirection = null;
        details = false;
    }

    protected void readFromParcel(Parcel in)
    {
        provinceIdx = in.readInt();
        areaIdx = in.readInt();
        subwayIndex = in.readInt();
        persons = in.readInt();
        longitude = in.readDouble();
        latitude = in.readDouble();
        page = in.readInt();
        limit = in.readInt();
        sortProperty = in.readString();
        sortDirection = in.readString();
        details = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(provinceIdx);
        dest.writeInt(areaIdx);
        dest.writeInt(subwayIndex);
        dest.writeInt(persons);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(page);
        dest.writeInt(limit);
        dest.writeString(sortProperty);
        dest.writeString(sortDirection);
        dest.writeInt(details ? 1 : 0);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
