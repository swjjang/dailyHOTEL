package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class Review implements Parcelable
{
    public int reserveIdx = -1;
    public boolean requiredCommentReview;
    public int reviewAllCount;
    private ReviewItem mReviewItem;
    private ArrayList<ReviewPickQuestion> mReviewPickQuestionList;
    private ArrayList<ReviewScoreQuestion> mReviewScoreQuestionList;

    public boolean isSatisfaction; // 만족여부 - none server data
    public String comment; // review comment - none server data

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

        reviewAllCount = 0;
        reserveIdx = jsonObject.getInt("reserveIdx");
        requiredCommentReview = jsonObject.getBoolean("requiredCommentReview");

        if (requiredCommentReview == true)
        {
            reviewAllCount++;
        }

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
                reviewAllCount += pickLength;
                mReviewPickQuestionList = new ArrayList<>(pickLength);

                for (int i = 0; i < pickLength; i++)
                {
                    mReviewPickQuestionList.add(new ReviewPickQuestion(reviewPickQuestionArray.getJSONObject(i)));
                }
            }
        }

        if (jsonObject.has("reviewScoreQuestions") == true && jsonObject.isNull("reviewScoreQuestions") == false)
        {
            JSONArray reviewScoreQuestionArray = jsonObject.getJSONArray("reviewScoreQuestions");

            int scoreLength = reviewScoreQuestionArray.length();
            if (scoreLength > 0)
            {
                reviewAllCount += scoreLength;
                mReviewScoreQuestionList = new ArrayList<>(scoreLength);

                for (int i = 0; i < scoreLength; i++)
                {
                    mReviewScoreQuestionList.add(new ReviewScoreQuestion(reviewScoreQuestionArray.getJSONObject(i)));
                }
            }
        }
    }

    public ReviewItem getReviewItem()
    {
        return mReviewItem;
    }

    public ArrayList<ReviewPickQuestion> getReviewPickQuestionList()
    {
        return mReviewPickQuestionList;
    }

    public ArrayList<ReviewScoreQuestion> getReviewScoreQuestionList()
    {
        return mReviewScoreQuestionList;
    }

    public int getReviewAllCount()
    {
        return reviewAllCount;
    }

    public void clear()
    {
        reserveIdx = -1;
        reviewAllCount = 0;
        mReviewItem = null;
        mReviewPickQuestionList = null;
        mReviewScoreQuestionList = null;
        isSatisfaction = false;
        requiredCommentReview = false;
        comment = null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(reserveIdx);
        dest.writeInt(reviewAllCount);
        dest.writeParcelable(mReviewItem, flags);
        dest.writeList(mReviewPickQuestionList);
        dest.writeList(mReviewScoreQuestionList);
        dest.writeInt(isSatisfaction == true ? 1 : 0);
        dest.writeInt(requiredCommentReview == true ? 1 : 0);
        dest.writeString(comment);
    }

    protected void readFromParcel(Parcel in)
    {
        reserveIdx = in.readInt();
        reviewAllCount = in.readInt();
        mReviewItem = in.readParcelable(ReviewItem.class.getClassLoader());
        mReviewPickQuestionList = in.readArrayList(ReviewPickQuestion.class.getClassLoader());
        mReviewScoreQuestionList = in.readArrayList(ReviewScoreQuestion.class.getClassLoader());
        isSatisfaction = in.readInt() == 1 ? true : false;
        requiredCommentReview = in.readInt() == 1 ? true : false;
        comment = in.readString();
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

    private String getGrade()
    {
        return isSatisfaction == true ? "GOOD" : "BAD";
    }

    public JSONObject toCommonJSONObject()
    {
        if (mReviewItem == null)
        {
            return null;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("grade", getGrade());
        map.put("itemIdx", this.mReviewItem.itemIdx);
        map.put("reserveIdx", this.reserveIdx);
        map.put("serviceType", this.mReviewItem.getServiceType());

        return new JSONObject(map);
    }

    public JSONObject toDetailJSONObject()
    {
        if (mReviewItem == null || mReviewPickQuestionList == null || mReviewScoreQuestionList == null)
        {
            return null;
        }


        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", Util.isTextEmpty(comment) == true ? "" : comment);
        map.put("itemIdx", this.mReviewItem.itemIdx);
        map.put("reserveIdx", this.reserveIdx);
        map.put("reviewPicks", getQuestionJSONArray(mReviewPickQuestionList));
        map.put("reviewScores", getQuestionJSONArray(mReviewScoreQuestionList));
        map.put("serviceType", this.mReviewItem.getServiceType());

        return new JSONObject(map);
    }

    private JSONArray getQuestionJSONArray(ArrayList<? extends ReviewQuestion> list)
    {
        if (list == null || list.isEmpty() == true)
        {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        for (ReviewQuestion question : list)
        {
            jsonArray.put(question.toJSONObject());
        }

        return jsonArray;
    }
}
