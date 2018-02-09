package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.CommonDateTime;

@JsonObject
public class TodayDateTime implements Parcelable
{
    @JsonField(name = "openDateTime")
    public String openDateTime; // ISO-8601

    @JsonField(name = "closeDateTime")
    public String closeDateTime; // ISO-8601

    @JsonField(name = "currentDateTime")
    public String currentDateTime; // ISO-8601

    @JsonField(name = "dailyDateTime")
    public String dailyDateTime; // ISO-8601

    public TodayDateTime()
    {
    }

    public TodayDateTime(Parcel in)
    {
        readFromParcel(in);
    }

    public TodayDateTime(String openDateTime, String closeDateTime, String currentDateTime, String dailyDateTime)
    {
        setToday(openDateTime, closeDateTime, currentDateTime, dailyDateTime);
    }

    public TodayDateTime getClone()
    {
        return new TodayDateTime(openDateTime, closeDateTime, currentDateTime, dailyDateTime);
    }

    public void setToday(String openDateTime, String closeDateTime, String currentDateTime, String dailyDateTime)
    {
        this.openDateTime = openDateTime;
        this.closeDateTime = closeDateTime;
        this.currentDateTime = currentDateTime;
        this.dailyDateTime = dailyDateTime;
    }

    public CommonDateTime getCommonDateTime()
    {
        return new CommonDateTime(openDateTime, closeDateTime, currentDateTime, dailyDateTime);
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
        public TodayDateTime createFromParcel(Parcel in)
        {
            return new TodayDateTime(in);
        }

        @Override
        public TodayDateTime[] newArray(int size)
        {
            return new TodayDateTime[size];
        }
    };
}
