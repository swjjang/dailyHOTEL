package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

public class EventBanner implements Parcelable
{
    public int index;
    public int type;
    public String link;

    public EventBanner()
    {
    }

    public EventBanner(Parcel in)
    {
        readFromParcel(in);
    }

    public EventBanner(JSONObject jsonObject)
    {
        try
        {
            index = jsonObject.getInt("idx");
            type = jsonObject.getInt("type");
            link = jsonObject.getString("link");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeInt(type);
        dest.writeString(link);
    }

    private void readFromParcel(Parcel in)
    {
        index = in.readInt();
        type = in.readInt();
        link = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public EventBanner createFromParcel(Parcel in)
        {
            return new EventBanner(in);
        }

        @Override
        public EventBanner[] newArray(int size)
        {
            return new EventBanner[size];
        }

    };
}
