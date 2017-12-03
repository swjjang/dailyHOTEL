package com.daily.dailyhotel.entity;

import android.content.Context;

import com.twoheart.dailyhotel.R;

public class Stay
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
    public int satisfaction;
    public String districtName;
    public int entryPosition;
    public boolean trueVR;
    public String stickerUrl;

    // 신규 추가
    public int reviewCount;
    public int discountRate;
    public boolean newStay;
    public boolean myWish;
    public String couponDiscountText;

    public String dBenefitText;
    public double distance; // 정렬시에 보여주는 내용
    public String categoryCode;
    public int availableRooms;

    public Grade grade;

    public String displayText;
    public int roomIndex;
    public String regionName;
    public boolean overseas;
    public boolean provideRewardSticker; // 데일리 리워드 아이콘 여부

    public String createAtWish; // ISO-8601 위시 등록 시간

    public enum Grade
    {
        special(R.string.grade_special, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        special1(R.string.grade_special1, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        special2(R.string.grade_special2, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        //
        biz(R.string.grade_biz, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        hostel(R.string.grade_hostel, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        grade1(R.string.grade_1, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        grade2(R.string.grade_2, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        grade3(R.string.grade_3, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        //
        resort(R.string.grade_resort, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        pension(R.string.grade_pension, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        fullvilla(R.string.grade_fullvilla, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        condo(R.string.grade_condo, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        //
        boutique(R.string.grade_boutique, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        motel(R.string.grade_motel, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        //
        design(R.string.grade_design, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        //
        residence(R.string.grade_residence, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        //
        guest_house(R.string.grade_guesthouse, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034),
        //
        etc(R.string.grade_not_yet, R.color.default_background_c929292, R.drawable.bg_hotel_price_900034);

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
}
