package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.ReviewScore;
import com.daily.dailyhotel.entity.ReviewScores;

import java.util.ArrayList;
import java.util.List;

public class ReviewScoresParcel implements Parcelable
{
    private ReviewScores mReviewScores;

    public ReviewScoresParcel(@NonNull ReviewScores reviewScores)
    {
        if (reviewScores == null)
        {
            throw new NullPointerException("user == null");
        }

        mReviewScores = reviewScores;
    }

    public ReviewScoresParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public ReviewScores getReviewScores()
    {
        return mReviewScores;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mReviewScores.reviewScoreTotalCount);

        List<ReviewScoreParcel> reviewScoreParcelList = new ArrayList<>();
        List<ReviewScore> reviewScoreList = mReviewScores.getReviewScoreList();

        if (reviewScoreList != null)
        {
            for (ReviewScore reviewScore : reviewScoreList)
            {
                reviewScoreParcelList.add(new ReviewScoreParcel(reviewScore));
            }
        }

        dest.writeTypedList(reviewScoreParcelList);
    }

    private void readFromParcel(Parcel in)
    {
        mReviewScores = new ReviewScores();

        mReviewScores.reviewScoreTotalCount = in.readInt();

        List<ReviewScoreParcel> reviewScoreParcelList = in.createTypedArrayList(ReviewScoreParcel.CREATOR);
        List<ReviewScore> reviewScoreList = new ArrayList<>();

        if (reviewScoreParcelList != null)
        {
            for (ReviewScoreParcel reviewScoreParcel : reviewScoreParcelList)
            {
                reviewScoreList.add(reviewScoreParcel.getReviewScore());
            }
        }

        mReviewScores.setReviewScoreList(reviewScoreList);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ReviewScoresParcel createFromParcel(Parcel in)
        {
            return new ReviewScoresParcel(in);
        }

        @Override
        public ReviewScoresParcel[] newArray(int size)
        {
            return new ReviewScoresParcel[size];
        }

    };
}
