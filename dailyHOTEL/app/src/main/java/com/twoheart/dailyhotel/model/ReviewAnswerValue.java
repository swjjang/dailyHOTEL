package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewAnswerValue implements Parcelable
{
    public String code;
    public String description;

    public ReviewAnswerValue(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewAnswerValue(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return;
        }

        code = jsonObject.getString("code");
        description = jsonObject.getString("description");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(code);
        dest.writeString(description);
    }

    protected void readFromParcel(Parcel in)
    {
        code = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ReviewAnswerValue createFromParcel(Parcel in)
        {
            return new ReviewAnswerValue(in);
        }

        @Override
        public ReviewAnswerValue[] newArray(int size)
        {
            return new ReviewAnswerValue[size];
        }

    };
}
