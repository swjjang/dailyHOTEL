package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class GourmetFilter implements Parcelable
{
    public static final int FLAG_GOURMET_FILTER_TIME_NONE = 0x00;
    public static final int FLAG_GOURMET_FILTER_TIME_06_11 = 0x01;
    public static final int FLAG_GOURMET_FILTER_TIME_11_15 = 0x02;
    public static final int FLAG_GOURMET_FILTER_TIME_15_17 = 0x04;
    public static final int FLAG_GOURMET_FILTER_TIME_17_21 = 0x08;
    public static final int FLAG_GOURMET_FILTER_TIME_21_06 = 0x10;

    public int timeFlag;
    public boolean isParking;

    public GourmetFilter(JSONObject jsonObject) throws JSONException
    {
        long openTimeInMillis = jsonObject.getLong("startEatingTime");
        long closeTimeInMillis = jsonObject.getLong("endEatingTime");

        timeFlag = getTimeFlag(openTimeInMillis) | getTimeFlag(closeTimeInMillis);

        if (jsonObject.has("parking") == true)
        {
            isParking = "Y".equalsIgnoreCase(jsonObject.getString("parking")) ? true : false;
        }
    }

    private int getTimeFlag(long timeInMillis)
    {
        int flag = FLAG_GOURMET_FILTER_TIME_NONE;

        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeInMillis);

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HHmm", Locale.KOREA);
        simpleTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        int time = Integer.parseInt(simpleTimeFormat.format(calendar.getTime()));

        if (time >= 600 && time < 1100)
        {
            flag |= FLAG_GOURMET_FILTER_TIME_06_11;
        }

        if (time >= 1100 && time < 1500)
        {
            flag |= FLAG_GOURMET_FILTER_TIME_11_15;
        }

        if (time >= 1500 && time < 1700)
        {
            flag |= FLAG_GOURMET_FILTER_TIME_15_17;
        }

        if (time >= 1700 && time < 2100)
        {
            flag |= FLAG_GOURMET_FILTER_TIME_17_21;
        }

        if ((time >= 2100 && time < 2400) || (time >= 0 && time < 600))
        {
            flag |= FLAG_GOURMET_FILTER_TIME_21_06;
        }

        return flag;
    }

    public GourmetFilter(Parcel in)
    {
        readFromParcel(in);
    }

    public boolean isTimeFiltered(int flags)
    {
        if (flags == FLAG_GOURMET_FILTER_TIME_NONE)
        {
            return true;
        }

        return (timeFlag & flags) != 0;
    }

    public boolean isParkingFiltered(boolean isParking)
    {
        if (isParking == false)
        {
            return true;
        }

        return this.isParking == isParking;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(timeFlag);
        dest.writeInt(isParking ? 1 : 0);
    }

    private void readFromParcel(Parcel in)
    {
        timeFlag = in.readInt();
        isParking = in.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetFilter createFromParcel(Parcel in)
        {
            return new GourmetFilter(in);
        }

        @Override
        public GourmetFilter[] newArray(int size)
        {
            return new GourmetFilter[size];
        }
    };
}
