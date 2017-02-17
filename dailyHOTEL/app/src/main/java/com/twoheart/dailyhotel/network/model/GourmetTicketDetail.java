package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class GourmetTicketDetail implements Parcelable
{
    @JsonField
    public String menuSummary;

    @JsonField
    public String openTime;

    @JsonField
    public String closeTime;

    @JsonField
    public String primaryTicketImageDescription;

    @JsonField
    public String primaryTicketImageUrl;

    public GourmetTicketDetail()
    {
    }

    public GourmetTicketDetail(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(menuSummary);
        dest.writeString(openTime);
        dest.writeString(closeTime);
        dest.writeString(primaryTicketImageDescription);
        dest.writeString(primaryTicketImageUrl);
    }

    protected void readFromParcel(Parcel in)
    {
        menuSummary = in.readString();
        openTime = in.readString();
        closeTime = in.readString();
        primaryTicketImageDescription = in.readString();
        primaryTicketImageUrl = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetTicketDetail createFromParcel(Parcel in)
        {
            return new GourmetTicketDetail(in);
        }

        @Override
        public GourmetTicketDetail[] newArray(int size)
        {
            return new GourmetTicketDetail[size];
        }
    };
}