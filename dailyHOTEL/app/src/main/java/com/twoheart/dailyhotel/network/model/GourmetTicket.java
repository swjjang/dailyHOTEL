package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

import java.util.List;

@JsonObject
public class GourmetTicket implements Parcelable
{
    //    @JsonField(name = "idx")
    //    public int index;

    @JsonField
    public int saleIdx;

    @JsonField
    public String ticketName;

    @JsonField
    public int price;

    @JsonField(name = "discount")
    public int discountPrice;

    @JsonField
    public String benefit;

    @JsonField
    public String option;

    @JsonField
    public String checkList;

//    @JsonField
//    public String startEatingTime;
//
//    @JsonField
//    public String endEatingTime;
//
//    @JsonField
//    public int timeInterval;
//
//    @JsonField
//    public String openTime;
//
//    @JsonField
//    public String closeTime;
//
//    @JsonField
//    public String lastOrderTime;
//
//    @JsonField
//    public String expiryTime;

    @JsonField
    public List<ProductImageInformation> images;

    @JsonField
    public String menuSummary;

    @JsonField
    public String menuDetail;

    private int mDefaultImageIndex;

    public GourmetTicket()
    {
    }

    public GourmetTicket(Parcel in)
    {
        readFromParcel(in);
    }

    @OnJsonParseComplete
    void onParseComplete()
    {
        if (images != null && images.size() > 0)
        {
            int size = images.size();

            for (int i = 0; i < size; i++)
            {
                if (images.get(i).isPrimary == true)
                {
                    mDefaultImageIndex = i;
                    break;
                }
            }
        }
    }

    public List<ProductImageInformation> getImageList()
    {
        return images;
    }

    public ProductImageInformation getPrimaryImage()
    {
        if (images == null || images.size() == 0)
        {
            return null;
        }

        return images.get(mDefaultImageIndex);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        //        dest.writeInt(index);
        dest.writeInt(saleIdx);
        dest.writeString(ticketName);
        dest.writeInt(price);
        dest.writeInt(discountPrice);
        dest.writeString(benefit);
        dest.writeString(option);
//        dest.writeString(startEatingTime);
//        dest.writeString(endEatingTime);
//        dest.writeInt(timeInterval);
//        dest.writeString(openTime);
//        dest.writeString(closeTime);
//        dest.writeString(lastOrderTime);
//        dest.writeString(expiryTime);
        dest.writeTypedList(images);
        dest.writeString(menuSummary);
        dest.writeString(menuDetail);
    }

    protected void readFromParcel(Parcel in)
    {
        //        index = in.readInt();
        saleIdx = in.readInt();
        ticketName = in.readString();
        price = in.readInt();
        discountPrice = in.readInt();
        benefit = in.readString();
        option = in.readString();
//        startEatingTime = in.readString();
//        endEatingTime = in.readString();
//        timeInterval = in.readInt();
//        openTime = in.readString();
//        closeTime = in.readString();
//        lastOrderTime = in.readString();
//        expiryTime = in.readString();
        images = in.createTypedArrayList(ProductImageInformation.CREATOR);
        menuSummary = in.readString();
        menuDetail = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetTicket createFromParcel(Parcel in)
        {
            return new GourmetTicket(in);
        }

        @Override
        public GourmetTicket[] newArray(int size)
        {
            return new GourmetTicket[size];
        }
    };
}