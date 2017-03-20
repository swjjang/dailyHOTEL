package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class DailyDateTime implements Parcelable
{
    @JsonField
    public String openDateTime; // ISO-8601

    @JsonField
    public String closeDateTime; // ISO-8601

    @JsonField
    public String currentDateTime; // ISO-8601

    @JsonField
    public String dailyDateTime; // ISO-8601

    public DailyDateTime()
    {
    }

    public DailyDateTime(String openDateTime, String closeDateTime, String currentDateTime, String dailyDateTime)
    {
        this.openDateTime = openDateTime;
        this.closeDateTime = closeDateTime;
        this.currentDateTime = currentDateTime;
        this.dailyDateTime = dailyDateTime;
    }

    public DailyDateTime(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(openDateTime);
        dest.writeString(closeDateTime);
        dest.writeString(currentDateTime);
        dest.writeString(dailyDateTime);
    }

    private void readFromParcel(Parcel in)
    {
        openDateTime = in.readString();
        closeDateTime = in.readString();
        currentDateTime = in.readString();
        dailyDateTime = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @JsonIgnore
    public static final Creator CREATOR = new Creator()
    {
        public DailyDateTime createFromParcel(Parcel in)
        {
            return new DailyDateTime(in);
        }

        @Override
        public DailyDateTime[] newArray(int size)
        {
            return new DailyDateTime[size];
        }

    };
}
