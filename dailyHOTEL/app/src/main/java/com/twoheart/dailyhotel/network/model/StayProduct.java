package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.daily.base.util.DailyTextUtils;

@JsonObject
public class StayProduct implements Parcelable
{
    public static final String NRD = "nrd";

    @JsonField(name = "description1")
    public String option;

    @JsonField(name = "description2")
    public String amenities;

    @JsonField(name = "discountAverage")
    public int averageDiscount;

    @JsonField(name = "discountTotal")
    public int totalDiscount;

    @JsonField(name = "karaoke")
    public boolean hasKaraoke;

    @JsonField(name = "partyRoom")
    public boolean hasPartyRoom;

    @JsonField(name = "pc")
    public boolean hasPC;

    @JsonField(name = "price")
    public int price;

    @JsonField(name = "privateBbq")
    public boolean hasPrivateBBQ;

    @JsonField(name = "refundType")
    public String refundType;

    @JsonField(name = "roomBenefit")
    public String roomBenefit;

    @JsonField(name = "roomIdx")
    public int roomIndex;

    @JsonField(name = "roomName")
    public String roomName;

    @JsonField(name = "spaWallpool")
    public boolean hasSpaWhirlpool;

    @JsonField(name = "tv")
    public boolean hasTV;

    @JsonField(name = "provideRewardSticker")
    public boolean provideRewardSticker;

    //
    @JsonIgnore
    public boolean isNRD;
    //

    public StayProduct()
    {

    }

    public StayProduct(Parcel in)
    {
        readFromParcel(in);
    }

    @OnJsonParseComplete
    void onParseComplete()
    {
        roomName = DailyTextUtils.isTextEmpty(roomName) == true ? "" : roomName.trim();
        option = DailyTextUtils.isTextEmpty(option) == true ? "" : option.trim();
        amenities = DailyTextUtils.isTextEmpty(amenities) == true ? "" : amenities.trim();
        roomBenefit = DailyTextUtils.isTextEmpty(roomBenefit) == true ? "" : roomBenefit.trim();
        isNRD = DailyTextUtils.isTextEmpty(refundType) == false && NRD.equalsIgnoreCase(refundType) == true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(option);
        dest.writeString(amenities);
        dest.writeInt(averageDiscount);
        dest.writeInt(totalDiscount);
        dest.writeInt(hasKaraoke ? 1 : 0);
        dest.writeInt(hasPartyRoom ? 1 : 0);
        dest.writeInt(hasPC ? 1 : 0);
        dest.writeInt(price);
        dest.writeInt(hasPrivateBBQ ? 1 : 0);
        dest.writeString(refundType);
        dest.writeString(roomBenefit);
        dest.writeInt(roomIndex);
        dest.writeString(roomName);
        dest.writeInt(hasSpaWhirlpool ? 1 : 0);
        dest.writeInt(hasTV ? 1 : 0);
        dest.writeInt(provideRewardSticker ? 1 : 0);
        dest.writeInt(isNRD ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        option = in.readString();
        amenities = in.readString();
        averageDiscount = in.readInt();
        totalDiscount = in.readInt();
        hasKaraoke = in.readInt() == 1;
        hasPartyRoom = in.readInt() == 1;
        hasPC = in.readInt() == 1;
        price = in.readInt();
        hasPrivateBBQ = in.readInt() == 1;
        refundType = in.readString();
        roomBenefit = in.readString();
        roomIndex = in.readInt();
        roomName = in.readString();
        hasSpaWhirlpool = in.readInt() == 1;
        hasTV = in.readInt() == 1;
        provideRewardSticker = in.readInt() == 1;
        isNRD = in.readInt() == 1;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayProduct createFromParcel(Parcel in)
        {
            return new StayProduct(in);
        }

        @Override
        public StayProduct[] newArray(int size)
        {
            return new StayProduct[size];
        }
    };
}
