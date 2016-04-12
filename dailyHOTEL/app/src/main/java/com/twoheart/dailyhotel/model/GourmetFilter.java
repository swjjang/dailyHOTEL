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

    public GourmetFilter(JSONObject jsonObject) throws JSONException
    {
        long openTimeInMillis = jsonObject.getLong("startEatingTime");
        long closeTimeInMillis = jsonObject.getLong("endEatingTime");

        timeFlag = getTimeFlag(openTimeInMillis, closeTimeInMillis);
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

    private int getTimeFlag(long openTimeInMillis, long closeTimeInMillis)
    {
        int timeFlag = FLAG_GOURMET_FILTER_TIME_NONE;

        int openFlag = getOpenTimeFlag(openTimeInMillis);
        int closeFlag = getCloseTimeFlag(closeTimeInMillis);
        int[] flags = {FLAG_GOURMET_FILTER_TIME_06_11, FLAG_GOURMET_FILTER_TIME_11_15, FLAG_GOURMET_FILTER_TIME_15_17, FLAG_GOURMET_FILTER_TIME_17_21, FLAG_GOURMET_FILTER_TIME_21_06};

        boolean includeFlag = false;

        for (int flag : flags)
        {
            if (includeFlag == false)
            {
                if (openFlag == flag)
                {
                    includeFlag = true;
                    timeFlag |= openFlag;

                    if (closeFlag == flag)
                    {
                        break;
                    }
                }
            } else
            {
                timeFlag |= flag;

                if (closeFlag == flag)
                {
                    break;
                }
            }
        }

        return timeFlag;
    }

    private int getOpenTimeFlag(long openTimeInMillis)
    {
        int time = getHHmmByMillis(openTimeInMillis);

        int flag = FLAG_GOURMET_FILTER_TIME_NONE;

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

    private int getCloseTimeFlag(long closeTimeInMillis)
    {
        int time = getHHmmByMillis(closeTimeInMillis);

        int flag = FLAG_GOURMET_FILTER_TIME_NONE;

        if (time > 600 && time <= 1100)
        {
            flag |= FLAG_GOURMET_FILTER_TIME_06_11;
        }

        if (time > 1100 && time <= 1500)
        {
            flag |= FLAG_GOURMET_FILTER_TIME_11_15;
        }

        if (time > 1500 && time <= 1700)
        {
            flag |= FLAG_GOURMET_FILTER_TIME_15_17;
        }

        if (time > 1700 && time <= 2100)
        {
            flag |= FLAG_GOURMET_FILTER_TIME_17_21;
        }

        if ((time > 2100 && time <= 2400) || (time >= 0 && time <= 600))
        {
            flag |= FLAG_GOURMET_FILTER_TIME_21_06;
        }

        return flag;
    }

    private int getHHmmByMillis(long timeInMillis)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeInMillis);

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HHmm", Locale.KOREA);
        simpleTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return Integer.parseInt(simpleTimeFormat.format(calendar.getTime()));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(timeFlag);
    }

    private void readFromParcel(Parcel in)
    {
        timeFlag = in.readInt();
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
