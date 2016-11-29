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
    private ArrayList<ReviewItemType> mReviewScoreTypeList;
    private ArrayList<ReviewCategoryType> mReviewCategoryTypeList;

    public Review(Parcel in)
    {
        readFromParcel(in);
    }

    public Review(JSONObject jsonObject) throws JSONException
    {
        setData(jsonObject);
    }

    private void setData(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return;
        }

        reserveIdx = jsonObject.getInt("reserveIdx");

        if (jsonObject.has("reviewItem") == true && jsonObject.isNull("reviewItem") == false)
        {
            mReviewItem = new ReviewItem(jsonObject.getJSONObject("mReviewItem"));
        }

        if (jsonObject.has("reviewScoreTypes") == true && jsonObject.isNull("reviewScoreTypes") == false)
        {
            JSONArray reviewScoreTypeArray = jsonObject.getJSONArray("reviewScoreTypes");

            int scoreLength = reviewScoreTypeArray.length();
            if (scoreLength > 0)
            {
                mReviewScoreTypeList = new ArrayList<>();

                for (int i = 0; i < scoreLength; i++)
                {
                    mReviewScoreTypeList.add(new ReviewItemType(reviewScoreTypeArray.getJSONObject(i)));
                }
            }
        }

        if (jsonObject.has("categoryTypes") == true && jsonObject.isNull("categoryTypes") == false)
        {
            JSONArray categoryTypes = jsonObject.getJSONArray("categoryTypes");

            int categoryLength = categoryTypes.length();
            if (categoryLength > 0)
            {
                mReviewCategoryTypeList = new ArrayList<>();

                for (int i = 0; i < categoryLength; i++)
                {
                    mReviewCategoryTypeList.add(new ReviewCategoryType(categoryTypes.getJSONArray(i)));
                }
            }
        }

    }

    public ReviewItem getReviewItem()
    {
        return mReviewItem;
    }

    public ArrayList<ReviewItemType> getReviewScoreTypeList()
    {
        return mReviewScoreTypeList;
    }

    public ArrayList<ReviewCategoryType> getReviewCategoryTypeList()
    {
        return mReviewCategoryTypeList;
    }

    public void clear()
    {
        reserveIdx = -1;
        mReviewItem = null;
        mReviewScoreTypeList = null;
        mReviewCategoryTypeList = null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(reserveIdx);
        dest.writeParcelable(mReviewItem, flags);
        dest.writeList(mReviewScoreTypeList);
        dest.writeList(mReviewCategoryTypeList);
    }

    protected void readFromParcel(Parcel in)
    {
        reserveIdx = in.readInt();
        mReviewItem = in.readParcelable(ReviewItem.class.getClassLoader());
        mReviewScoreTypeList = in.readArrayList(ReviewItemType.class.getClassLoader());
        mReviewCategoryTypeList = in.readArrayList(ReviewCategoryType.class.getClassLoader());
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
