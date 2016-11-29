package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ReviewItem implements Parcelable
{
    public int itemIdx;
    public String itemName;
    public String baseImagePath;
    public String itemImagePath;
    public Constants.PlaceType placeType; // serviceType
    public String useEndDate;
    public String useStartDate;

    public ReviewItem(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewItem(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return;
        }

        itemIdx = jsonObject.getInt("itemIdx");
        itemName = jsonObject.getString("itemName");
        baseImagePath = jsonObject.getString("baseImagePath");
        itemImagePath = jsonObject.getString("itemImagePath");

        String serviceTypeString = jsonObject.getString("serviceTypeString");
        Constants.PlaceType placeType = null;

        if (Util.isTextEmpty(serviceTypeString) == false)
        {
            if ("HOTEL".equalsIgnoreCase(serviceTypeString) == true)
            {
                placeType = Constants.PlaceType.HOTEL;
            } else if ("GOURMET".equalsIgnoreCase(serviceTypeString) == true)
            {
                placeType = Constants.PlaceType.FNB;
            } else
            {
                ExLog.d("unKnown service type");
            }
        } else
        {
            ExLog.d("serviceTypeString is null");
        }

        useStartDate = jsonObject.getString("useStartDate");
        useEndDate = jsonObject.getString("useEndDate");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(itemIdx);
        dest.writeString(itemName);
        dest.writeString(baseImagePath);
        dest.writeString(itemImagePath);
        dest.writeString(placeType.name());
        dest.writeString(useEndDate);
        dest.writeString(useStartDate);
    }

    protected void readFromParcel(Parcel in)
    {
        itemIdx = in.readInt();
        itemName = in.readString();
        baseImagePath = in.readString();
        itemImagePath = in.readString();
        placeType = Constants.PlaceType.valueOf(in.readString());
        useEndDate = in.readString();
        useStartDate = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ReviewItem createFromParcel(Parcel in)
        {
            return new ReviewItem(in);
        }

        @Override
        public ReviewItem[] newArray(int size)
        {
            return new ReviewItem[size];
        }

    };
}