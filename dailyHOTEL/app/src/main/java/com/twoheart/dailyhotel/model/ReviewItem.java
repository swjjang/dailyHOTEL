package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ReviewItem implements Parcelable
{
    public int itemIdx;
    public String itemName;
    public String imageUrl;
    public Constants.ServiceType serviceType; // serviceType
    public String useEndDate;
    public String useStartDate;

    public ReviewItem()
    {

    }

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

        if (DailyTextUtils.isTextEmpty(serviceType) == false)
        {
            switch (serviceType)
            {
                case "HOTEL":
                    this.serviceType = Constants.ServiceType.HOTEL;
                    break;

                case "GOURMET":
                    this.serviceType = Constants.ServiceType.GOURMET;
                    break;

                case "OUTBOUND":
                    this.serviceType = Constants.ServiceType.OB_STAY;
                    break;

                default:
                    ExLog.d("unKnown service type");
                    break;
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
        switch (serviceType)
        {
            case HOTEL:
                return "HOTEL";

            case GOURMET:
                return "GOURMET";

            case OB_STAY:
                return "OUTBOUND";
        }

        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(itemIdx);
        dest.writeString(itemName);
        dest.writeString(imageUrl);
        dest.writeString(serviceType.name());
        dest.writeString(useEndDate);
        dest.writeString(useStartDate);
    }

    protected void readFromParcel(Parcel in)
    {
        itemIdx = in.readInt();
        itemName = in.readString();
        imageUrl = in.readString();
        serviceType = Constants.ServiceType.valueOf(in.readString());
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