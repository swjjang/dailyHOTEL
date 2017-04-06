package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.Date;
import java.util.TimeZone;

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

    /**
     * getTime 시에 Java에서는 GMT 0시간으로 처리 되도록 되어있다. 특정 시간을 부여하면 해당 long값에 더해진 값으로 반환된다.
     * 기존에 시간값을 정리하는데 미정리된 부분들을 처리하기 위한 메소드.
     *
     * @param timeZone
     * @return
     */
    public long getCurrentTime(@NonNull TimeZone timeZone)
    {
        if (timeZone == null)
        {
            return 0;
        }

        try
        {
            Date date = DailyCalendar.convertDate(currentDateTime, DailyCalendar.ISO_8601_FORMAT, timeZone);

            return date.getTime() + timeZone.getRawOffset();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return 0;
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
