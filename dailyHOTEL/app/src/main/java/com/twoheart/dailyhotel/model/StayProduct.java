package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.twoheart.dailyhotel.util.Util;

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

    @JsonField
    public int price;

    @JsonField(name = "privateBbq")
    public boolean hasPrivateBBQ;

    @JsonField
    public String refundType;

    @JsonField
    public String roomBenefit;

    @JsonField(name = "roomIdx")
    public int roomIndex;

    @JsonField
    public String roomName;

    @JsonField(name = "spaWallpool")
    public boolean hasSpaWhirlpool;

    @JsonField(name = "tv")
    public boolean hasTV;
    //
    public boolean isNRD;
    //
    public boolean isOverseas;
    public String hotelName;
    public int nights;
    public String categoryCode; // GA를 위해서 payment로 진행시에 값을 넣는다

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
        roomName = Util.isTextEmpty(roomName) == true ? "" : roomName.trim();
        option = Util.isTextEmpty(option) == true ? "" : option.trim();
        amenities = Util.isTextEmpty(amenities) == true ? "" : amenities.trim();

        roomBenefit = Util.isTextEmpty(roomBenefit) == true ? "" : roomBenefit.trim();

        if (Util.isTextEmpty(refundType) == false && NRD.equalsIgnoreCase(refundType) == true)
        {
            isNRD = true;
        } else
        {
            isNRD = false;
        }
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
        dest.writeInt(isOverseas ? 1 : 0);
        dest.writeString(hotelName);
        dest.writeInt(nights);
        dest.writeInt(isNRD ? 1 : 0);
        dest.writeString(categoryCode);
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

        isOverseas = in.readInt() == 1;
        hotelName = in.readString();
        nights = in.readInt();
        isNRD = in.readInt() == 1;
        categoryCode = in.readString();
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
