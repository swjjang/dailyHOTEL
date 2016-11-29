package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewItemType implements Parcelable
{
    public String code;
    public String title;
    public String description;

    public ReviewItemType(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewItemType(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return;
        }

        code = jsonObject.getString("code");
        //        title = jsonObject.getString("title");
        description = jsonObject.getString("description");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(code);
        dest.writeString(title);
        dest.writeString(description);
    }

    protected void readFromParcel(Parcel in)
    {
        code = in.readString();
        title = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ReviewItemType createFromParcel(Parcel in)
        {
            return new ReviewItemType(in);
        }

        @Override
        public ReviewItemType[] newArray(int size)
        {
            return new ReviewItemType[size];
        }

    };
}
