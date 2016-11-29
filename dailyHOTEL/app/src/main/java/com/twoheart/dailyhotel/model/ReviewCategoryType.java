package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewCategoryType implements Parcelable
{
    private ArrayList<ReviewItemType> mReviewItemTypeList;

    public ReviewCategoryType(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewCategoryType(JSONArray jsonArray) throws JSONException
    {
        if (jsonArray == null)
        {
            return;
        }

        int length = jsonArray.length();

        if (length > 0)
        {
            mReviewItemTypeList = new ArrayList<>();

            for (int i = 0; i < length; i++)
            {
                mReviewItemTypeList.add(new ReviewItemType(jsonArray.getJSONObject(i)));
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeList(mReviewItemTypeList);
    }

    protected void readFromParcel(Parcel in)
    {
        mReviewItemTypeList = in.readArrayList(ReviewItemType.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ReviewCategoryType createFromParcel(Parcel in)
        {
            return new ReviewCategoryType(in);
        }

        @Override
        public ReviewCategoryType[] newArray(int size)
        {
            return new ReviewCategoryType[size];
        }

    };
}
