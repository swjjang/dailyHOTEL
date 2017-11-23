package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.repository.remote.model.ConfigurationsData;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 2. 20..
 */
@JsonObject
public class StayDetailParams extends PlaceDetailParams<StayProduct>
{
    // 직접 접근 금지
    @JsonField(name = "parking")
    public boolean parking;

    // 직접 접근 금지
    @JsonField(name = "noParking")
    public boolean noParking;

    // 직접 접근 금지
    @JsonField(name = "pool")
    public boolean pool;

    // 직접 접근 금지
    @JsonField(name = "fitness")
    public boolean fitness;

    // 직접 접근 금지
    @JsonField(name = "pet")
    public boolean pet;

    // 직접 접근 금지
    @JsonField(name = "sharedBbq")
    public boolean sharedBBQ;

    // 직접 접근 금지
    @JsonField(name = "businessCenter")
    public boolean businessCenter;

    // 직접 접근 금지
    @JsonField(name = "sauna")
    public boolean sauna;

    // 직접 접근 금지
    @JsonField(name = "kidsPlayroom")
    public boolean kidsPlayRoom;

    @JsonField(name = "benefitWarning")
    public String benefitWarning;

    // 직접 접근 금지
    @JsonField(name = "rooms")
    public List<StayProduct> rooms;

    @JsonField(name = "singleStay")
    public boolean isSingleStay; // 연박 불가 여부

    @JsonField(name = "overseas")
    public boolean isOverseas; // 0 : 국내 , 1 : 해외

    @JsonField(name = "waitingForBooking")
    public boolean waitingForBooking; // 예약 대기

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    @JsonIgnore
    private ArrayList<StayDetail.Pictogram> mPictogramList;

    @JsonIgnore
    private ArrayList<String> mBenefitInformation;

    @JsonIgnore
    public boolean provideRewardSticker;

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

        // 수영장
        if (pool == true)
        {
            mPictogramList.add(StayDetail.Pictogram.POOL);
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

        // 비지니스 센터
        if (businessCenter == true)
        {
            mPictogramList.add(StayDetail.Pictogram.BUSINESS_CENTER);
        }

        // 키즈 플레이 룸
        if (kidsPlayRoom == true)
        {
            mPictogramList.add(StayDetail.Pictogram.KIDS_PLAY_ROOM);
        }

        // 바베큐
        if (sharedBBQ == true)
        {
            mPictogramList.add(StayDetail.Pictogram.SHARED_BBQ);
        }

        // 애완동물
        if (pet == true)
        {
            mPictogramList.add(StayDetail.Pictogram.PET);
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
        if (DailyTextUtils.isTextEmpty(benefit) == false)
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

            if (DailyTextUtils.isTextEmpty(benefitWarning) == false)
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

        // Reward
        if (configurations != null && configurations.activeReward == true)
        {
            if (rooms != null)
            {
                for (StayProduct stayProduct : rooms)
                {
                    if (stayProduct.provideRewardSticker == true)
                    {
                        provideRewardSticker = true;
                        break;
                    }
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
        return mBenefitInformation;
    }

    @Override
    public List<StayProduct> getProductList()
    {
        return rooms;
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
