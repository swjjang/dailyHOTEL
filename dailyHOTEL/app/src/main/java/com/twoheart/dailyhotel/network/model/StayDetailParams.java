package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 2. 20..
 */
@JsonObject
public class StayDetailParams extends PlaceDetailParams
{
    //    @JsonField(name = "idx")
    //    public int index;
    //
    //    @JsonField
    //    public String name;
    //
    //    @JsonField
    //    public double latitude;
    //
    //    @JsonField
    //    public double longitude;
    //
    //    @JsonField
    //    public String address;
    //
    //    // 직접 접근 금지
    //    // 주의 : Parcelable 후에 해당 값은 사라집니다.
    //    @JsonField
    //    public Map<String, List<ImageInformation>> imgPath;
    //
    //    @JsonField
    //    public String grade;
    //
    //    @JsonField
    //    public int ratingPersons;
    //
    //    @JsonField
    //    public int ratingValue;
    //
    //    @JsonField
    //    public boolean ratingShow;
    //
    //    @JsonField
    //    public String category;

    // 직접 접근 금지
    @JsonField
    public boolean parking;

    // 직접 접근 금지
    @JsonField
    public boolean noParking;

    // 직접 접근 금지
    @JsonField
    public boolean pool;

    // 직접 접근 금지
    @JsonField
    public boolean fitness;

    // 직접 접근 금지
    @JsonField
    public boolean pet;

    // 직접 접근 금지
    @JsonField(name = "sharedBbq")
    public boolean sharedBBQ;

    // 직접 접근 금지
    @JsonField
    public boolean businessCenter;

    // 직접 접근 금지
    @JsonField
    public boolean sauna;

    // 직접 접근 금지
    @JsonField(name = "kidsPlayroom")
    public boolean kidsPlayRoom;
    //
    //    @JsonField
    //    public String benefit;
    //
    //    @JsonField
    //    public List<String> benefitContents;

    @JsonField
    public String benefitWarning;
    //
    //    // 직접 접근 금지
    //    // 주의 : Parcelable 후에 해당 값은 사라집니다.
    //    @JsonField
    //    public List<Map<String, List<String>>> details;

    // 직접 접근 금지
    @JsonField
    public List<StayProduct> rooms;
    //
    //    @JsonField
    //    public String imgUrl;
    //
    //    @JsonField
    //    public int wishCount; // 위시리스트 카운트
    //
    //    @JsonField
    //    public boolean myWish; // 위시리스트 클릭 상태

    @JsonField(name = "singleStay")
    public boolean isSingleStay; // 연박 불가 여부

    @JsonField(name = "overseas")
    public boolean isOverseas; // 0 : 국내 , 1 : 해외

    // 결제 전 해당 값을 조회하여 stayProduct에 넣어 줄 수 있도록 해야 함
    public int nights;

    //    protected ArrayList<DetailInformation> mDetailList;
    private ArrayList<StayDetail.Pictogram> mPictogramList;
    //    private ArrayList<ImageInformation> mImageList;
    private ArrayList<String> mBenefitInformation;

    public StayDetailParams()
    {

    }

    public StayDetailParams(Parcel in)
    {
        readFromParcel(in);
    }

    @OnJsonParseComplete
    void onParseComplete()
    {
        // Pictogram
        if (mPictogramList == null)
        {
            mPictogramList = new ArrayList<>();
        }

        mPictogramList.clear();

        // 주차
        if (parking == true)
        {
            mPictogramList.add(StayDetail.Pictogram.PARKING);
        }

        // 주차금지
        if (noParking == true)
        {
            mPictogramList.add(StayDetail.Pictogram.NO_PARKING);
        }

        // 바베큐
        if (sharedBBQ == true)
        {
            mPictogramList.add(StayDetail.Pictogram.SHARED_BBQ);
        }

        // 수영장
        if (pool == true)
        {
            mPictogramList.add(StayDetail.Pictogram.POOL);
        }

        // 비지니스 센터
        if (businessCenter == true)
        {
            mPictogramList.add(StayDetail.Pictogram.BUSINESS_CENTER);
        }

        // 피트니스
        if (fitness == true)
        {
            mPictogramList.add(StayDetail.Pictogram.FITNESS);
        }

        // 사우나
        if (sauna == true)
        {
            mPictogramList.add(StayDetail.Pictogram.SAUNA);
        }

        // 애완동물
        if (pet == true)
        {
            mPictogramList.add(StayDetail.Pictogram.PET);
        }

        // 키즈 플레이 룸
        if (kidsPlayRoom == true)
        {
            mPictogramList.add(StayDetail.Pictogram.KIDS_PLAY_ROOM);
        }

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

        // benefit
        if (Util.isTextEmpty(benefit) == false)
        {
            int length = benefitContents == null ? 0 : benefitContents.size();
            if (length > 0)
            {
                mBenefitInformation = new ArrayList<>(length);

                for (int i = 0; i < length; i++)
                {
                    mBenefitInformation.add(benefitContents.get(i));
                }
            } else
            {
                mBenefitInformation = new ArrayList<>();
            }

            if (Util.isTextEmpty(benefitWarning) == false)
            {
                mBenefitInformation.add(benefitWarning);
            }
        }

        // Detail
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

    public Stay.Grade getGrade()
    {
        Stay.Grade stayGrade;
        // 등급
        try
        {
            stayGrade = Stay.Grade.valueOf(grade);
        } catch (Exception e)
        {
            stayGrade = Stay.Grade.etc;
        }

        return stayGrade;
    }

    public List<StayDetail.Pictogram> getPictogramList()
    {
        return mPictogramList;
    }

    public List<String> getBenefitList()
    {
        return benefitContents;
    }

    public List<StayProduct> getProductList()
    {
        return rooms;
    }

    public void setNights(int nights)
    {
        this.nights = nights;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(parking == true ? 1 : 0);
        dest.writeInt(noParking == true ? 1 : 0);
        dest.writeInt(pool == true ? 1 : 0);
        dest.writeInt(fitness == true ? 1 : 0);
        dest.writeInt(pet == true ? 1 : 0);
        dest.writeInt(sharedBBQ == true ? 1 : 0);
        dest.writeInt(businessCenter == true ? 1 : 0);
        dest.writeInt(sauna == true ? 1 : 0);
        dest.writeInt(kidsPlayRoom == true ? 1 : 0);
        dest.writeString(benefitWarning);
        dest.writeTypedList(rooms);
        dest.writeInt(isSingleStay == true ? 1 : 0);
        dest.writeInt(isOverseas == true ? 1 : 0);
        dest.writeInt(nights);
        dest.writeTypedList(mPictogramList);
        dest.writeStringList(mBenefitInformation);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        parking = in.readInt() == 1;
        noParking = in.readInt() == 1;
        pool = in.readInt() == 1;
        fitness = in.readInt() == 1;
        pet = in.readInt() == 1;
        sharedBBQ = in.readInt() == 1;
        businessCenter = in.readInt() == 1;
        sauna = in.readInt() == 1;
        kidsPlayRoom = in.readInt() == 1;
        benefitWarning = in.readString();
        rooms = in.createTypedArrayList(StayProduct.CREATOR);
        isSingleStay = in.readInt() == 1;
        isOverseas = in.readInt() == 1;
        nights = in.readInt();
        mPictogramList = in.createTypedArrayList(StayDetail.Pictogram.CREATOR);
        mBenefitInformation = in.createStringArrayList();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayDetailParams createFromParcel(Parcel in)
        {
            return new StayDetailParams(in);
        }

        @Override
        public StayDetailParams[] newArray(int size)
        {
            return new StayDetailParams[size];
        }
    };
}
