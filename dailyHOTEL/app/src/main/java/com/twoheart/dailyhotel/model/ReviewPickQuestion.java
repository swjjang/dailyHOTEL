package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewPickQuestion extends ReviewQuestion
{
    private ArrayList<ReviewAnswerValue> mAnswerValueList;

    public ReviewPickQuestion()
    {
        super();
    }

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

            // 항상 짝수개로 맞춘다.
            if (valueLength % 2 == 1)
            {
                mAnswerValueList.add(new ReviewAnswerValue((JSONObject) null));
            }
        }
    }

    public ArrayList<ReviewAnswerValue> getAnswerValueList()
    {
        return mAnswerValueList;
    }

    public void setAnswerValueList(ArrayList<ReviewAnswerValue> answerValueList)
    {
        this.mAnswerValueList = answerValueList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeList(mAnswerValueList);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mAnswerValueList = in.readArrayList(ReviewAnswerValue.class.getClassLoader());
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

    @Override
    public JSONObject toReviewAnswerJSONObject(int value) throws JSONException
    {
        if (mAnswerValueList == null || mAnswerValueList.size() < value || value < 0)
        {
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", answerCode);
        jsonObject.put("value", mAnswerValueList.get(value).code);

        return jsonObject;
    }
}
