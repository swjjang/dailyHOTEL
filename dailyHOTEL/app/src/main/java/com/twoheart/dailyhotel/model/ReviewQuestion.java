package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public abstract class ReviewQuestion implements Parcelable
{
    public String title;
    public String description;
    public String answerCode;

    protected abstract JSONObject toReviewAnswerJSONObject(int value) throws JSONException;

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(answerCode);
    }

    protected void readFromParcel(Parcel in)
    {
        title = in.readString();
        description = in.readString();
        answerCode = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
