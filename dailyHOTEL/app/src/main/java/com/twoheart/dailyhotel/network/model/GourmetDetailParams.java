package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.TicketInformation;

import java.util.ArrayList;

@JsonObject
public class GourmetDetailParams implements Parcelable
{
    public String name;
    public String address;
    public boolean isOverseas; // 0 : 국내 , 1 : 해외
    public String benefit;
    public int ratingPersons;
    public int ratingValue;
    public double latitude;
    public double longitude;
    public boolean hasCoupon;
    public boolean myWish; // 위시리스트 클릭 상태
    public int wishCount; // 위시리스트 카운트

    protected ArrayList<ImageInformation> mImageInformationList;
    protected ArrayList<DetailInformation> mInformationList;
    protected ArrayList<String> mBenefitInformation;


    public Gourmet.Grade grade;
    public String category;
    public String subCategory;
    private ArrayList<GourmetDetail.Pictogram> mPictogramList;

    protected ArrayList<TicketInformation> mTicketInformationList;

    public GourmetDetailParams()
    {

    }

    public GourmetDetailParams(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    }

    protected void readFromParcel(Parcel in)
    {
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetDetailParams createFromParcel(Parcel in)
        {
            return new GourmetDetailParams(in);
        }

        @Override
        public GourmetDetailParams[] newArray(int size)
        {
            return new GourmetDetailParams[size];
        }
    };
}