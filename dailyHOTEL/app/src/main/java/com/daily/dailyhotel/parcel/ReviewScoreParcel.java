package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.ReviewScore;

class ReviewScoreParcel implements Parcelable
{
    private ReviewScore mReviewScore;

    public ReviewScoreParcel(@NonNull ReviewScore reviewScore)
    {
        if (reviewScore == null)
        {
            throw new NullPointerException("reviewScore == null");
        }

        mReviewScore = reviewScore;
    }

    public ReviewScoreParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewScore getReviewScore()
    {
        return mReviewScore;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mReviewScore.type);
        dest.writeFloat(mReviewScore.scoreAverage);
    }

    private void readFromParcel(Parcel in)
    {
        mReviewScore = new ReviewScore();

        mReviewScore.type = in.readString();
        mReviewScore.scoreAverage = in.readFloat();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public com.daily.dailyhotel.parcel.ReviewScoreParcel createFromParcel(Parcel in)
        {
            return new com.daily.dailyhotel.parcel.ReviewScoreParcel(in);
        }

        @Override
        public com.daily.dailyhotel.parcel.ReviewScoreParcel[] newArray(int size)
        {
            return new com.daily.dailyhotel.parcel.ReviewScoreParcel[size];
        }

    };
}
