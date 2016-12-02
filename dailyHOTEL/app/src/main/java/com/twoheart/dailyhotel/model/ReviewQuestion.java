package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewQuestion implements Parcelable
{
    public String title;
    public String description;
    public String answerCode;
    public ArrayList<ReviewAnswerValue> mAnswerValueList;

    public ReviewQuestion(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewQuestion(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return;
        }

        title = jsonObject.getString("title");
        description = jsonObject.getString("description");
        answerCode = jsonObject.getString("answerCode");

        JSONArray answerValues = jsonObject.getJSONArray("answerValues");

        if (answerValues != null)
        {
            mAnswerValueList = new ArrayList<>();

            int valueLength = answerValues.length();
            for (int i = 0; i < valueLength; i++)
            {
                mAnswerValueList.add(new ReviewAnswerValue(answerValues.getJSONObject(i)));
            }
        }
    }

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

    public static final Creator CREATOR = new Creator()
    {
        public ReviewQuestion createFromParcel(Parcel in)
        {
            return new ReviewQuestion(in);
        }

        @Override
        public ReviewQuestion[] newArray(int size)
        {
            return new ReviewQuestion[size];
        }

    };
}
