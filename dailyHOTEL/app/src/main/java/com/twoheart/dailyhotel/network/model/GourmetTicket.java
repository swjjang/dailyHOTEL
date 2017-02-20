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
    public List<ProductImageInformation> images;

    @JsonField
    public GourmetTicketDetail menuDetail;

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
        dest.writeTypedList(images);
        dest.writeParcelable(menuDetail, flags);
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
        images = in.createTypedArrayList(ProductImageInformation.CREATOR);
        menuDetail = in.readParcelable(GourmetTicketDetail.class.getClassLoader());
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