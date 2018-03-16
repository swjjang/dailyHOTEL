package com.daily.dailyhotel.entity;

import android.content.Context;

import com.twoheart.dailyhotel.R;

public class Gourmet
{
    public int index;
    public String imageUrl;
    public String name;
    public int price;
    public int discountPrice;
    public String addressSummary;
    public double latitude;
    public double longitude;
    public boolean dailyChoice;
    public boolean soldOut;
    public int rating;
    public String districtName;
    public int entryPosition;
    public boolean trueVR;
    public String stickerUrl;
    public int stickerIndex;

    // 신규 추가
    public int reviewCount;
    public int discountRate;
    public boolean newItem;
    public boolean myWish;
    public String couponDiscountText;

    public String dBenefitText;
    public double distance;
    public String category;
    public String subCategory;
    public int persons;

    public Grade grade;

    public String regionName;

    public String createdWishDateTime; // ISO-8601 위시 등록 시간

    public enum Grade
    {
        gourmet(R.string.grade_not_yet, R.color.dh_theme_color, R.drawable.bg_hotel_price_900034);

        private int mNameResId;
        private int mColorResId;
        private int mMarkerResId;

        Grade(int nameResId, int colorResId, int markerResId)
        {
            mNameResId = nameResId;
            mColorResId = colorResId;
            mMarkerResId = markerResId;
        }

        public String getName(Context context)
        {
            return context.getString(mNameResId);
        }

        public int getColorResId()
        {
            return mColorResId;
        }

        public int getMarkerResId()
        {
            return mMarkerResId;
        }
    }

    public com.twoheart.dailyhotel.model.Gourmet toGourmet()
    {
        com.twoheart.dailyhotel.model.Gourmet gourmet = new com.twoheart.dailyhotel.model.Gourmet();

        gourmet.index = index;
        gourmet.imageUrl = imageUrl;
        gourmet.name = name;
        gourmet.price = price;
        gourmet.discountPrice = discountPrice;
        gourmet.addressSummary = addressSummary;
        gourmet.latitude = latitude;
        gourmet.longitude = longitude;
        gourmet.isDailyChoice = dailyChoice;
        gourmet.isSoldOut = soldOut;
        gourmet.satisfaction = rating;
        gourmet.districtName = districtName;
        gourmet.entryPosition = entryPosition;
        gourmet.truevr = trueVR;
        gourmet.stickerUrl = stickerUrl;
        gourmet.stickerIndex = stickerIndex;
        gourmet.reviewCount = reviewCount;
        gourmet.discountRate = discountRate;
        gourmet.newItem = newItem;
        gourmet.myWish = myWish;
        gourmet.couponDiscountText = couponDiscountText;
        gourmet.dBenefitText = dBenefitText;
        gourmet.persons = persons;
        gourmet.category = category;
        gourmet.subCategory = subCategory;
        gourmet.distance = distance;
        gourmet.regionName = regionName;

        try
        {
            gourmet.grade = com.twoheart.dailyhotel.model.Gourmet.Grade.valueOf(grade.name());
        } catch (Exception e)
        {
            gourmet.grade = com.twoheart.dailyhotel.model.Gourmet.Grade.gourmet;
        }

        return gourmet;
    }
}
