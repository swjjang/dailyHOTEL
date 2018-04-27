package com.daily.dailyhotel.entity;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Stay;

import java.util.LinkedHashMap;
import java.util.List;

public class StayDetail extends Configurations
{
    public int index;
    public String name;
    public double latitude;
    public double longitude;
    public String address;
    public String category;
    public Stay.Grade grade;
    public int price;
    public int discount;
    public int ratingPersons;
    public int ratingValue;
    public boolean ratingShow;
    public String benefit;
    public int wishCount;
    public boolean myWish;
    public boolean singleStay;
    public boolean overseas;
    public boolean waitingForBooking;
    public int couponPrice;
    public boolean provideRewardSticker;
    public int rewardStickerCount;
    public TrueAwards awards;
    public PlaceDetailProvince province;

    private List<DetailImageInformation> mDetailImageInformationList;
    private List<LinkedHashMap<String, List<String>>> mDescriptionMap;
    private List<StayRoom> mStayRoomList;
    private List<Pictogram> mPictogramList;
    private List<String> mBenefitContentList;

    public StayDetail()
    {

    }

    public List<LinkedHashMap<String, List<String>>> getDescriptionList()
    {
        return mDescriptionMap;
    }

    public void setDescriptionList(List<LinkedHashMap<String, List<String>>> specificationMap)
    {
        mDescriptionMap = specificationMap;
    }

    public List<Pictogram> getPictogramList()
    {
        return mPictogramList;
    }

    public void setPictogramList(List<Pictogram> pictogramList)
    {
        mPictogramList = pictogramList;
    }

    public boolean hasRooms()
    {
        return mStayRoomList != null && mStayRoomList.size() > 0;
    }

    public List<StayRoom> getRoomList()
    {
        return mStayRoomList;
    }

    public void setRoomList(List<StayRoom> stayRoomList)
    {
        mStayRoomList = stayRoomList;
    }

    public List<DetailImageInformation> getImageInformationList()
    {
        return mDetailImageInformationList;
    }

    public boolean hasImageInformation()
    {
        return mDetailImageInformationList != null && mDetailImageInformationList.size() > 0;
    }

    public void setImageInformationList(List<DetailImageInformation> detailImageInformationList)
    {
        mDetailImageInformationList = detailImageInformationList;
    }

    public String getDefaultImageUrl()
    {
        return hasImageInformation() ? mDetailImageInformationList.get(0).mImageMap.smallUrl : null;
    }

    public List<String> getBenefitContentList()
    {
        return mBenefitContentList;
    }

    public void setBenefitContentList(List<String> benefitContentList)
    {
        mBenefitContentList = benefitContentList;
    }

    public boolean hasTrueVR()
    {
        return false;
    }

    public List<StayRoom> getRoom(String filter)
    {
        return null;
    }

    public static class DetailInformation {
        public List<String> mContentList;
    }

    public static class BreakfastInformation {
        public List<String> mContentList;
    }

    public enum Pictogram
    {
        PARKING(R.string.label_parking, R.drawable.f_ic_facilities_05),
        NO_PARKING(R.string.label_unabled_parking, R.drawable.f_ic_facilities_05_no_parking),
        POOL(R.string.label_pool, R.drawable.f_ic_facilities_06),
        FITNESS(R.string.label_fitness, R.drawable.f_ic_facilities_07),
        SAUNA(R.string.label_sauna, R.drawable.f_ic_facilities_16),
        BUSINESS_CENTER(R.string.label_business_center, R.drawable.f_ic_facilities_15),
        KIDS_PLAY_ROOM(R.string.label_kids_play_room, R.drawable.f_ic_facilities_17),
        SHARED_BBQ(R.string.label_allowed_barbecue, R.drawable.f_ic_facilities_09),
        PET(R.string.label_allowed_pet, R.drawable.f_ic_facilities_08),
        NONE(0, 0);

        private int mNameResId;
        private int mImageResId;

        Pictogram(int nameResId, int imageResId)
        {
            mNameResId = nameResId;
            mImageResId = imageResId;
        }

        public String getName(Context context)
        {
            if (mNameResId == 0)
            {
                return null;
            }

            return context.getString(mNameResId);
        }

        public int getImageResId()
        {
            return mImageResId;
        }
    }
}
