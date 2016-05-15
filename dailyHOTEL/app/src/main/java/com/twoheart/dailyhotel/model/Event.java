package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

public class Event implements Parcelable
{
    public int index;
    public boolean isJoin;
    public String imageUrl;
    public String name;

    public Event(Parcel in)
    {
        readFromParcel(in);
    }

    public Event(JSONObject jsonObject)
    {
        try
        {
            index = jsonObject.getInt("idx");
            imageUrl = jsonObject.getString("img_url");
            isJoin = jsonObject.getInt("is_event_join") == 0 ? false : true;
            name = jsonObject.getString("name");

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeString(imageUrl);
        dest.writeInt(isJoin ? 1 : 0);
    }

    private void readFromParcel(Parcel in)
    {
        index = in.readInt();
        imageUrl = in.readString();
        isJoin = in.readInt() == 0 ? false : true;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Event createFromParcel(Parcel in)
        {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size)
        {
            return new Event[size];
        }

    };
}
