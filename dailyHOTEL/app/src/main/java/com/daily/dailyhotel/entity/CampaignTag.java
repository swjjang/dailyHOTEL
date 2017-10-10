package com.daily.dailyhotel.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android_sam on 2017. 8. 8..
 */

public class CampaignTag implements Parcelable
{
    public int index;
    public String startDate; // ISO-8601
    public String endDate; // ISO-8601
    public String campaignTag;
    public String serviceType;

    public CampaignTag()
    {
    }

    public CampaignTag(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(campaignTag);
        dest.writeString(serviceType);
    }

    protected void readFromParcel(Parcel in)
    {
        index = in.readInt();
        startDate = in.readString();
        endDate = in.readString();
        campaignTag = in.readString();
        serviceType = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public CampaignTag createFromParcel(Parcel in)
        {
            return new CampaignTag(in);
        }

        @Override
        public CampaignTag[] newArray(int size)
        {
            return new CampaignTag[size];
        }
    };
}
