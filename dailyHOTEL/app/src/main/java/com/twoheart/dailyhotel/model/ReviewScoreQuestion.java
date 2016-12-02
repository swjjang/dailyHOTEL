package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewScoreQuestion extends ReviewQuestion
{
    public int selectedScore; // 선택된 스코어 - none server data

    public ReviewScoreQuestion(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewScoreQuestion(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return;
        }

        title = jsonObject.getString("title");
        description = jsonObject.getString("description");
        answerCode = jsonObject.getString("answerCode");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(selectedScore);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        selectedScore = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ReviewScoreQuestion createFromParcel(Parcel in)
        {
            return new ReviewScoreQuestion(in);
        }

        @Override
        public ReviewScoreQuestion[] newArray(int size)
        {
            return new ReviewScoreQuestion[size];
        }

    };

    public JSONObject toJSONObject()
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", answerCode);
        map.put("score", selectedScore);

        return new JSONObject(map);
    }
}
