package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class PlaceReviewScores implements Parcelable
{
    @JsonField
    public List<PlaceReviewScore> reviewScoreAvgs;

    @JsonField
    public int reviewScoreTotalCount;

    public PlaceReviewScores()
    {
    }

    public PlaceReviewScores(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedList(reviewScoreAvgs);
        dest.writeInt(reviewScoreTotalCount);
    }

    protected void readFromParcel(Parcel in)
    {
        reviewScoreAvgs = in.createTypedArrayList(PlaceReviewScore.CREATOR);
        reviewScoreTotalCount = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public PlaceReviewScores createFromParcel(Parcel in)
        {
            return new PlaceReviewScores(in);
        }

        @Override
        public PlaceReviewScores[] newArray(int size)
        {
            return new PlaceReviewScores[size];
        }
    };
}
