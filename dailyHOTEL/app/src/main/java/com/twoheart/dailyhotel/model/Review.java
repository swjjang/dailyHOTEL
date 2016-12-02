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

public class Review implements Parcelable
{
    public int reserveIdx = -1;
    private ReviewItem mReviewItem;
    private ArrayList<ReviewQuestion> mReviewPickQuestionList;
    private ArrayList<ReviewQuestion> mReviewScoreQuestionList;

    public Review(Parcel in)
    {
        readFromParcel(in);
    }

    public Review(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return;
        }

        reserveIdx = jsonObject.getInt("reserveIdx");

        if (jsonObject.has("reviewItem") == true && jsonObject.isNull("reviewItem") == false)
        {
            mReviewItem = new ReviewItem(jsonObject.getJSONObject("reviewItem"));
        }

        if (jsonObject.has("reviewPickQuestions") == true && jsonObject.isNull("reviewPickQuestions") == false)
        {
            JSONArray reviewPickQuestionArray = jsonObject.getJSONArray("reviewPickQuestions");

            int pickLength = reviewPickQuestionArray.length();
            if (pickLength > 0)
            {
                mReviewPickQuestionList = new ArrayList<>();

                for (int i = 0; i < pickLength; i++)
                {
                    mReviewPickQuestionList.add(new ReviewQuestion(reviewPickQuestionArray.getJSONObject(i)));
                }
            }
        }

        if (jsonObject.has("reviewScoreQuestions") == true && jsonObject.isNull("reviewScoreQuestions") == false)
        {
            JSONArray reviewScoreQuestionArray = jsonObject.getJSONArray("reviewScoreQuestions");

            int scoreLength = reviewScoreQuestionArray.length();
            if (scoreLength > 0)
            {
                mReviewScoreQuestionList = new ArrayList<>();

                for (int i = 0; i < scoreLength; i++)
                {
                    mReviewScoreQuestionList.add(new ReviewQuestion(reviewScoreQuestionArray.getJSONObject(i)));
                }
            }
        }
    }

    public ReviewItem getReviewItem()
    {
        return mReviewItem;
    }

    public ArrayList<ReviewQuestion> getReviewPickQuestionList()
    {
        return mReviewPickQuestionList;
    }

    public ArrayList<ReviewQuestion> getReviewScoreQuestionList()
    {
        return mReviewScoreQuestionList;
    }

    public void clear()
    {
        reserveIdx = -1;
        mReviewItem = null;
        mReviewPickQuestionList = null;
        mReviewScoreQuestionList = null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(reserveIdx);
        dest.writeParcelable(mReviewItem, flags);
        dest.writeList(mReviewPickQuestionList);
        dest.writeList(mReviewScoreQuestionList);
    }

    protected void readFromParcel(Parcel in)
    {
        reserveIdx = in.readInt();
        mReviewItem = in.readParcelable(ReviewItem.class.getClassLoader());
        mReviewPickQuestionList = in.readArrayList(ReviewQuestion.class.getClassLoader());
        mReviewScoreQuestionList = in.readArrayList(ReviewQuestion.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Review createFromParcel(Parcel in)
        {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size)
        {
            return new Review[size];
        }

    };
}
