package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class ProductImageInformation implements Parcelable
{
    @JsonField(name = "idx")
    public int index;

    @JsonField
    public String imageDescription;

    @JsonField
    public String imageUrl;

    @JsonField
    public boolean isPrimary;

    @JsonField
    public int restaurantTicketIdx;

    @JsonField
    public int seq;

    public ProductImageInformation()
    {

    }

    public ProductImageInformation(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeString(imageDescription);
        dest.writeString(imageUrl);
        dest.writeInt(isPrimary == true ? 1 : 0);
        dest.writeInt(restaurantTicketIdx);
        dest.writeInt(seq);
    }

    protected void readFromParcel(Parcel in)
    {
        index = in.readInt();
        imageDescription = in.readString();
        imageUrl = in.readString();
        isPrimary = in.readInt() == 1;
        restaurantTicketIdx = in.readInt();
        seq = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ProductImageInformation createFromParcel(Parcel in)
        {
            return new ProductImageInformation(in);
        }

        @Override
        public ProductImageInformation[] newArray(int size)
        {
            return new ProductImageInformation[size];
        }
    };
}