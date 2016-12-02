package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewPickQuestion extends ReviewQuestion
{
    public ArrayList<ReviewAnswerValue> mAnswerValueList;

    private String selectedValue; // 선택된 리뷰 AnswerValue 값

    public ReviewPickQuestion(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewPickQuestion(JSONObject jsonObject) throws JSONException
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
        super.writeToParcel(dest, flags);

        dest.writeList(mAnswerValueList);
        dest.writeString(selectedValue);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mAnswerValueList = in.readArrayList(ReviewAnswerValue.class.getClassLoader());
        selectedValue = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ReviewPickQuestion createFromParcel(Parcel in)
        {
            return new ReviewPickQuestion(in);
        }

        @Override
        public ReviewPickQuestion[] newArray(int size)
        {
            return new ReviewPickQuestion[size];
        }

    };

    public JSONObject toJSONObject()
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", answerCode);
        map.put("value", selectedValue);

        return new JSONObject(map);
    }
}
