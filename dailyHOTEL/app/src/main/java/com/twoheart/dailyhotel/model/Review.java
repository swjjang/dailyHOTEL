package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class Review implements Parcelable
{
    public static final String GRADE_GOOD = "GOOD";
    public static final String GRADE_BAD = "BAD";
    public static final String GRADE_NONE = "NONE";

    public int reserveIdx = -1;
    public boolean requiredCommentReview;
    public int reviewAllCount;
    private ReviewItem mReviewItem;
    private ArrayList<ReviewPickQuestion> mReviewPickQuestionList;
    private ArrayList<ReviewScoreQuestion> mReviewScoreQuestionList;

    // 임시 저장 또는 화면에 작성 된 comment;
    public String selectedComment;

    public Review()
    {

    }

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

    public void setReviewItem(ReviewItem reviewItem)
    {
        mReviewItem = reviewItem;
    }

    public ReviewItem getReviewItem()
    {
        return mReviewItem;
    }

    public void setReviewPickQuestionList(ArrayList<ReviewPickQuestion> reviewPickQuestionList)
    {
        mReviewPickQuestionList = reviewPickQuestionList;
    }

    public ArrayList<ReviewPickQuestion> getReviewPickQuestionList()
    {
        return mReviewPickQuestionList;
    }

    public void setReviewScoreQuestionList(ArrayList<ReviewScoreQuestion> reviewScoreQuestionList)
    {
        mReviewScoreQuestionList = reviewScoreQuestionList;
    }

    public ArrayList<ReviewScoreQuestion> getReviewScoreQuestionList()
    {
        return mReviewScoreQuestionList;
    }

    public void clear()
    {
        reserveIdx = -1;
        reviewAllCount = 0;
        mReviewItem = null;
        mReviewPickQuestionList = null;
        mReviewScoreQuestionList = null;
        requiredCommentReview = false;
        selectedComment = null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(reserveIdx);
        dest.writeInt(reviewAllCount);
        dest.writeParcelable(mReviewItem, flags);
        dest.writeList(mReviewPickQuestionList);
        dest.writeList(mReviewScoreQuestionList);
        dest.writeInt(requiredCommentReview == true ? 1 : 0);
        dest.writeString(selectedComment);
    }

    protected void readFromParcel(Parcel in)
    {
        reserveIdx = in.readInt();
        reviewAllCount = in.readInt();
        mReviewItem = in.readParcelable(ReviewItem.class.getClassLoader());
        mReviewPickQuestionList = in.readArrayList(ReviewPickQuestion.class.getClassLoader());
        mReviewScoreQuestionList = in.readArrayList(ReviewScoreQuestion.class.getClassLoader());
        requiredCommentReview = in.readInt() == 1;
        selectedComment = in.readString();
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

    public JSONObject toReviewJSONObject(String grade)
    {
        ReviewItem reviewItem = getReviewItem();

        if (reviewItem == null)
        {
            return null;
        }

        JSONObject jsonObject = new JSONObject();

        try
        {
            switch (reviewItem.serviceType)
            {
                case HOTEL:
                case GOURMET:
                    jsonObject.put("grade", grade);
                    jsonObject.put("itemIdx", reviewItem.itemIdx);
                    jsonObject.put("reserveIdx", reserveIdx);
                    jsonObject.put("serviceType", reviewItem.getServiceType());
                    break;

                case OB_STAY:
                    jsonObject.put("grade", grade);
                    jsonObject.put("itemIdx", reviewItem.itemIdx);
                    jsonObject.put("reserveIdx", reserveIdx);
                    break;
            }
        } catch (JSONException e)
        {
            ExLog.e(e.toString());
            return null;
        }

        return jsonObject;
    }

    public JSONObject toReviewDetailJSONObject(JSONArray scoreJSONArray, JSONArray pickJSONArray, String comment)
    {
        if (scoreJSONArray == null || pickJSONArray == null)
        {
            return null;
        }

        ReviewItem reviewItem = getReviewItem();

        if (reviewItem == null)
        {
            return null;
        }

        JSONObject jsonObject = new JSONObject();

        try
        {
            if (requiredCommentReview == true)
            {
                jsonObject.put("comment", DailyTextUtils.isTextEmpty(comment) == true ? "" : comment);
            }

            jsonObject.put("itemIdx", reviewItem.itemIdx);
            jsonObject.put("reserveIdx", reserveIdx);
            jsonObject.put("reviewScores", scoreJSONArray);
            jsonObject.put("reviewPicks", pickJSONArray);

            jsonObject.put("serviceType", reviewItem.getServiceType());
        } catch (JSONException e)
        {
            ExLog.e(e.toString());
            return null;
        }

        return jsonObject;
    }
}
