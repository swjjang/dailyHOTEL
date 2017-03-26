package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class PlaceReviewScore implements Parcelable
{
    @JsonField
    public String type;

    @JsonField
    public float scoreAvg;

    public PlaceReviewScore()
    {
    }

    public PlaceReviewScore(Parcel in)
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
        dest.writeString(type);
        dest.writeFloat(scoreAvg);
    }

    protected void readFromParcel(Parcel in)
    {
        type = in.readString();
        scoreAvg = in.readFloat();
    }

    public static final Creator CREATOR = new Creator()
    {
        public PlaceReviewScore createFromParcel(Parcel in)
        {
            return new PlaceReviewScore(in);
        }

        @Override
        public PlaceReviewScore[] newArray(int size)
        {
            return new PlaceReviewScore[size];
        }
    };
}
