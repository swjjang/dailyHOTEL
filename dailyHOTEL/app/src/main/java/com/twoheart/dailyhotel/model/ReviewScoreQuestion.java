package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.util.DailyAssert;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewScoreQuestion extends ReviewQuestion
{
    public ReviewScoreQuestion(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewScoreQuestion(JSONObject jsonObject) throws JSONException
    {
        DailyAssert.assertNotNull(jsonObject);
        if (jsonObject == null)
        {
            return;
        }

        title = jsonObject.getString("title");
        DailyAssert.assertNotNull(title);

        description = jsonObject.getString("description");
        DailyAssert.assertNotNull(description);

        answerCode = jsonObject.getString("answerCode");
        DailyAssert.assertNotNull(answerCode);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);
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

    @Override
    public JSONObject toReviewAnswerJSONObject(int value) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", answerCode);
        jsonObject.put("score", value);

        return jsonObject;
    }
}
