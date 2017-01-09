package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ReviewItem implements Parcelable
{
    public int itemIdx;
    public String itemName;
    public String imageUrl;
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

        String baseImagePath = jsonObject.getString("baseImagePath");
        JSONObject imageJSONObject = new JSONObject(jsonObject.getString("itemImagePath"));

        Iterator<String> iterator = imageJSONObject.keys();
        while (iterator.hasNext())
        {
            String key = iterator.next();

            try
            {
                JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
                imageUrl = baseImagePath + key + pathJSONArray.getString(0);
                break;
            } catch (JSONException e)
            {
            }
        }

        String serviceType = jsonObject.getString("serviceType");

        if (Util.isTextEmpty(serviceType) == false)
        {
            if ("HOTEL".equalsIgnoreCase(serviceType) == true)
            {
                placeType = Constants.PlaceType.HOTEL;
            } else if ("GOURMET".equalsIgnoreCase(serviceType) == true)
            {
                placeType = Constants.PlaceType.FNB;
            } else
            {
                ExLog.d("unKnown service type");
            }
        } else
        {
            ExLog.d("serviceType is null");
        }


        useStartDate = jsonObject.getString("useStartDate");
        useEndDate = jsonObject.getString("useEndDate");
    }

    public String getPlaceType()
    {
        return Constants.PlaceType.FNB.equals(placeType) == true ? "GOURMET" : "HOTEL";
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(itemIdx);
        dest.writeString(itemName);
        dest.writeString(imageUrl);
        dest.writeString(placeType.name());
        dest.writeString(useEndDate);
        dest.writeString(useStartDate);
    }

    protected void readFromParcel(Parcel in)
    {
        itemIdx = in.readInt();
        itemName = in.readString();
        imageUrl = in.readString();
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