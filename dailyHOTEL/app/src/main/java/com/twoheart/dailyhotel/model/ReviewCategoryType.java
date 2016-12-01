package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewCategoryType extends ReviewItemType
{
    public ReviewCategoryType(Parcel in)
    {
        super(in);
    }

    public ReviewCategoryType(JSONObject jsonObject) throws JSONException
    {
       super(jsonObject);
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
