package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetDetail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@JsonObject
public class GourmetDetailParams implements Parcelable
{
    @JsonField(name = "idx")
    public int index;

    @JsonField
    public String name;

    @JsonField
    public double latitude;

    @JsonField
    public double longitude;

    @JsonField
    public String address;

    @JsonField
    public String category;

    @JsonField
    public String categorySub;

    // 직접 접근 금지
    @JsonField
    public String grade;

    // 직접 접근 금지
    // 주의 : Parcelable 후에 해당 값은 사라집니다.
    @JsonField
    public Map<String, List<ImageInformation>> imgPath;

    @JsonField
    public int price;

    @JsonField
    public int discount;

    @JsonField
    public int ratingPersons;

    @JsonField
    public int ratingValue;

    @JsonField
    public boolean ratingShow;

    // 직접 접근 금지
    @JsonField
    public boolean parking;

    // 직접 접근 금지
    @JsonField
    public boolean valet;

    // 직접 접근 금지
    @JsonField
    public boolean babySeat;

    // 직접 접근 금지
    @JsonField
    public boolean privateRoom;

    // 직접 접근 금지
    @JsonField
    public boolean groupBooking;

    // 직접 접근 금지
    @JsonField
    public boolean corkage;

    // 직접 접근 금지
    @JsonField
    public List<GourmetProduct> tickets;

    // 직접 접근 금지
    // 주의 : Parcelable 후에 해당 값은 사라집니다.
    @JsonField
    public List<Map<String, List<String>>> details;

    @JsonField
    public String imgUrl;

    @JsonField
    public String benefit;

    @JsonField
    public List<String> benefitContents;

    @JsonField
    public int wishCount;

    @JsonField
    public boolean myWish;

    protected ArrayList<DetailInformation> mDetailList;
    private ArrayList<GourmetDetail.Pictogram> mPictogramList;
    private ArrayList<ImageInformation> mImageList;


    public GourmetDetailParams()
    {
    }

    public GourmetDetailParams(Parcel in)
    {
        readFromParcel(in);
    }

    @OnJsonParseComplete
    void onParseComplete()
    {
        if (mPictogramList == null)
        {
            mPictogramList = new ArrayList<>();
        }

        mPictogramList.clear();

        // 주차가능
        if (parking == true)
        {
            mPictogramList.add(GourmetDetail.Pictogram.parking);
        }
        // 발렛가능
        if (valet == true)
        {
            mPictogramList.add(GourmetDetail.Pictogram.valet);
        }
        // 베이비시트
        if (babySeat == true)
        {
            mPictogramList.add(GourmetDetail.Pictogram.babySeat);
        }
        // 프라이빗룸
        if (privateRoom == true)
        {
            mPictogramList.add(GourmetDetail.Pictogram.privateRoom);
        }
        // 단체예약
        if (groupBooking == true)
        {
            mPictogramList.add(GourmetDetail.Pictogram.groupBooking);
        }
        // 코르키지
        if (corkage == true)
        {
            mPictogramList.add(GourmetDetail.Pictogram.corkage);
        }

        // 이미지 리스트
        if (mImageList == null)
        {
            mImageList = new ArrayList<>();
        }

        mImageList.clear();

        if (imgPath != null && imgPath.size() > 0)
        {
            Iterator<String> iterator = imgPath.keySet().iterator();
            if (iterator.hasNext())
            {
                String key = iterator.next();

                List<ImageInformation> imageInformationList = imgPath.get(key);

                if (imageInformationList != null)
                {
                    for (ImageInformation imageInformation : imageInformationList)
                    {
                        imageInformation.setImageUrl(imgUrl + key + imageInformation.name);
                        mImageList.add(imageInformation);
                    }
                }
            }
        }

        // 상세 내용 리스트
        if (mDetailList == null)
        {
            mDetailList = new ArrayList<>();
        }

        mDetailList.clear();

        for (Map<String, List<String>> detail : details)
        {
            Iterator<String> iterator = detail.keySet().iterator();
            if (iterator.hasNext())
            {
                String key = iterator.next();

                List<String> contentsList = detail.get(key);

                if (contentsList != null)
                {
                    mDetailList.add(new DetailInformation(key, contentsList));
                }
            }
        }
    }

    public Gourmet.Grade getGrade()
    {
        return Gourmet.Grade.gourmet;
    }

    public List<GourmetDetail.Pictogram> getPictogramList()
    {
        return mPictogramList;
    }

    public List<ImageInformation> getImageList()
    {
        return mImageList;
    }

    public List<DetailInformation> getDetailList()
    {
        return mDetailList;
    }

    public List<String> getBenefitList()
    {
        return benefitContents;
    }

    public List<GourmetProduct> getTicketList()
    {
        return tickets;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
        dest.writeString(category);
        dest.writeString(categorySub);
        dest.writeString(grade);
        dest.writeTypedList(mImageList);
        dest.writeInt(price);
        dest.writeInt(discount);
        dest.writeInt(ratingPersons);
        dest.writeInt(ratingValue);
        dest.writeInt(ratingShow == true ? 1 : 0);
        dest.writeInt(parking == true ? 1 : 0);
        dest.writeInt(valet == true ? 1 : 0);
        dest.writeInt(babySeat == true ? 1 : 0);
        dest.writeInt(privateRoom == true ? 1 : 0);
        dest.writeInt(groupBooking == true ? 1 : 0);
        dest.writeInt(corkage == true ? 1 : 0);
        dest.writeTypedList(tickets);
        dest.writeTypedList(mDetailList);
        dest.writeString(imgUrl);
        dest.writeString(benefit);
        dest.writeStringList(benefitContents);
        dest.writeInt(wishCount);
        dest.writeInt(myWish == true ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        index = in.readInt();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
        category = in.readString();
        categorySub = in.readString();
        grade = in.readString();
        mImageList = in.createTypedArrayList(ImageInformation.CREATOR);
        price = in.readInt();
        discount = in.readInt();
        ratingPersons = in.readInt();
        ratingValue = in.readInt();
        ratingShow = in.readInt() == 1 ? true : false;
        parking = in.readInt() == 1 ? true : false;
        valet = in.readInt() == 1 ? true : false;
        babySeat = in.readInt() == 1 ? true : false;
        privateRoom = in.readInt() == 1 ? true : false;
        groupBooking = in.readInt() == 1 ? true : false;
        corkage = in.readInt() == 1 ? true : false;
        tickets = in.createTypedArrayList(GourmetProduct.CREATOR);
        mDetailList = in.createTypedArrayList(DetailInformation.CREATOR);
        imgUrl = in.readString();
        benefit = in.readString();
        benefitContents = in.createStringArrayList();
        wishCount = in.readInt();
        myWish = in.readInt() == 1 ? true : false;
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