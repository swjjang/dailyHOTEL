package com.daily.dailyhotel.entity;

import android.content.Context;

import com.twoheart.dailyhotel.R;

import java.util.LinkedHashMap;
import java.util.List;

public class GourmetDetail
{
    public int index;
    public String name;
    public double latitude;
    public double longitude;
    public String address;
    public String category;
    public String categorySub;
    public int price;
    public int discount;
    public int ratingPersons;
    public int ratingValue;
    public boolean ratingShow;
    public String benefit;
    public int wishCount;
    public boolean myWish;
    public int couponPrice;
    public TrueAwards awards;
    public PlaceDetailProvince province;

    private Sticker mSticker;
    private List<DetailImageInformation> mDetailImageInformationList;
    private List<LinkedHashMap<String, List<String>>> mDescriptionMap;
    private List<GourmetMenu> mGourmetMenuList;
    private List<Pictogram> mPictogramList;
    private List<String> mBenefitContentList;

    public GourmetDetail()
    {

    }

    public Sticker getSticker()
    {
        return mSticker;
    }

    public void setSticker(Sticker sticker)
    {
        mSticker = sticker;
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

    public List<GourmetMenu> getGourmetMenuList()
    {
        return mGourmetMenuList;
    }

    public void setGourmetMenuList(List<GourmetMenu> gourmetMenuList)
    {
        mGourmetMenuList = gourmetMenuList;
    }

    public List<DetailImageInformation> getImageInformationList()
    {
        return mDetailImageInformationList;
    }

    public void setImageInformationList(List<DetailImageInformation> detailImageInformationList)
    {
        mDetailImageInformationList = detailImageInformationList;
    }

    public List<String> getBenefitContentList()
    {
        return mBenefitContentList;
    }

    public void setBenefitContentList(List<String> benefitContentList)
    {
        mBenefitContentList = benefitContentList;
    }

    public enum Pictogram
    {
        parking(R.string.label_parking, R.drawable.f_ic_facilities_05),
        valet(R.string.label_valet_available, R.drawable.f_ic_facilities_10),
        privateRoom(R.string.label_private_room, R.drawable.f_ic_facilities_11),
        groupBooking(R.string.label_group_booking, R.drawable.f_ic_facilities_12),
        babySeat(R.string.label_baby_seat, R.drawable.f_ic_facilities_13),
        corkage(R.string.label_corkage, R.drawable.f_ic_facilities_14),
        none(0, 0);

        private int mNameResId;
        private int mImageResId;

        Pictogram(int nameResId, int imageResId)
        {
            this.mNameResId = nameResId;
            this.mImageResId = imageResId;
        }

        public String getName(Context context)
        {
            if (mNameResId <= 0)
            {
                return "";
            }
            return context.getString(mNameResId);
        }

        public int getImageResId()
        {
            return mImageResId;
        }
    }
}
