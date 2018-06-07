package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.Prices;
import com.twoheart.dailyhotel.network.model.StayWishDetails;
import com.twoheart.dailyhotel.network.model.StayWishItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Stay extends Place
{
    public String dBenefitText;
    //    public int nights = 1;
    public double distance; // 정렬시에 보여주는 내용
    public String categoryCode;
    //    public String sday;
    public boolean isLocalPlus;
    public int availableRooms;

    protected Grade mGrade;

    public String displayText;
    public int roomIndex;
    public String regionName;
    public String sday;
    public boolean isOverseas;
    public boolean provideRewardSticker; // 데일리 리워드 아이콘 여부

    public Stay()
    {
        super();
    }

    public Stay(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public int getGradeMarkerResId()
    {
        return mGrade.getMarkerResId();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeSerializable(mGrade);
        dest.writeString(dBenefitText);
        dest.writeString(categoryCode);
        //        dest.writeString(sday);
        dest.writeInt(isLocalPlus == true ? 1 : 0);
        dest.writeString(displayText);
        dest.writeInt(roomIndex);
        dest.writeString(regionName);
        dest.writeString(sday);
        dest.writeInt(isOverseas == true ? 1 : 0);
        dest.writeInt(provideRewardSticker == true ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mGrade = (Grade) in.readSerializable();
        dBenefitText = in.readString();
        categoryCode = in.readString();
        //        sday = in.readString();
        isLocalPlus = in.readInt() == 1;
        displayText = in.readString();
        roomIndex = in.readInt();
        regionName = in.readString();
        sday = in.readString();
        isOverseas = in.readInt() == 1;
        provideRewardSticker = in.readInt() == 1;
    }

    public Grade getGrade()
    {
        return mGrade;
    }

    public void setGrade(Grade grade)
    {
        if (grade == null)
        {
            mGrade = Grade.etc;
        } else
        {
            mGrade = grade;
        }
    }

    public boolean setStay(JSONObject jsonObject, String imageUrl)
    {
        //        this.nights = nights;

        try
        {
            name = jsonObject.getString("name");
            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discount"); // discountAvg ????
            addressSummary = jsonObject.getString("addrSummary");

            // 인트라넷에서 값을 잘못 넣는 경우가 있다.
            if (DailyTextUtils.isTextEmpty(addressSummary) == false)
            {
                if (addressSummary.indexOf('|') >= 0)
                {
                    addressSummary = addressSummary.replace(" | ", "ㅣ");
                } else if (addressSummary.indexOf('l') >= 0)
                {
                    addressSummary = addressSummary.replace(" l ", "ㅣ");
                }
            }

            try
            {
                mGrade = Grade.valueOf(jsonObject.getString("grade"));
            } catch (Exception e)
            {
                mGrade = Grade.etc;
            }

            index = jsonObject.getInt("hotelIdx");

            // 해당 정보 내려오지 않음.
            //            if (jsonObject.has("isSoldOut") == true)
            //            {
            //                isSoldOut = jsonObject.getBoolean("isSoldOut"); //
            //            }

            districtName = jsonObject.getString("districtName");
            categoryCode = jsonObject.getString("category");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = jsonObject.getBoolean("isDailyChoice");
            satisfaction = jsonObject.getInt("rating"); // ratingValue ??
            //            sday = jsonObject.getString("sday");
            distance = jsonObject.getDouble("distance");

            if (jsonObject.has("truevr") == true)
            {
                truevr = jsonObject.getBoolean("truevr");
            }

            if (jsonObject.has("availableRooms") == true)
            {
                availableRooms = jsonObject.getInt("availableRooms");
            } else
            {
                availableRooms = -1;
            }

            try
            {
                JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");

                Iterator<String> iterator = imageJSONObject.keys();
                while (iterator.hasNext())
                {
                    String key = iterator.next();

                    try
                    {
                        JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
                        this.imageUrl = imageUrl + key + pathJSONArray.getString(0);
                        break;
                    } catch (JSONException e)
                    {
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            if (jsonObject.has("benefit") == true) // hotelBenefit ?
            {
                dBenefitText = jsonObject.getString("benefit");
            } else
            {
                dBenefitText = null;
            }

            reviewCount = jsonObject.getInt("reviewCount");
            discountRate = jsonObject.getInt("discountRate");
            newItem = jsonObject.getBoolean("newItem");
            myWish = jsonObject.getBoolean("myWish");
            couponDiscountText = jsonObject.getString("couponDiscountText");

            if (jsonObject.has("provideRewardSticker") == true)
            {
                provideRewardSticker = jsonObject.getBoolean("provideRewardSticker");
            }
        } catch (JSONException e)
        {
            ExLog.d(e.toString());
            return false;
        }

        return true;
    }

    public boolean setStay(StayWishItem stayWishItem, String imageUrl)
    {
        try
        {
            name = stayWishItem.title;

            Prices prices = stayWishItem.prices;

            price = prices == null ? 0 : prices.normalPrice;
            discountPrice = prices == null ? 0 : prices.discountPrice;

            addressSummary = stayWishItem.addrSummary;

            StayWishDetails stayWishDetails = stayWishItem.getDetails();

            mGrade = stayWishDetails != null ? stayWishDetails.stayGrade : Grade.etc;

            index = stayWishItem.index;
            districtName = stayWishItem.regionName;
            categoryCode = stayWishDetails != null ? stayWishDetails.category : "";
            satisfaction = stayWishItem.rating;
            truevr = stayWishDetails != null && stayWishDetails.isTrueVR;

            try
            {
                this.imageUrl = imageUrl + stayWishItem.imageUrl;
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            dBenefitText = null;

            reviewCount = stayWishItem.reviewCount;
            newItem = stayWishItem.newItem;
            myWish = stayWishItem.myWish;

        } catch (Exception e)
        {
            ExLog.d(e.toString());
            return false;
        }

        return true;
    }

    public enum Grade
    {
        special(R.string.grade_special, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        special1(R.string.grade_special1, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        special2(R.string.grade_special2, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        //
        biz(R.string.grade_biz, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        hostel(R.string.grade_hostel, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        grade1(R.string.grade_1, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        grade2(R.string.grade_2, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        grade3(R.string.grade_3, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        //
        resort(R.string.grade_resort, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        pension(R.string.grade_pension, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        fullvilla(R.string.grade_fullvilla, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        condo(R.string.grade_condo, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        //
        boutique(R.string.grade_boutique, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        motel(R.string.grade_motel, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        //
        design(R.string.grade_design, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        //
        residence(R.string.grade_residence, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        //
        guest_house(R.string.grade_guesthouse, R.color.default_background_c929292, R.drawable.bg_hotel_price_off),
        //
        etc(R.string.grade_not_yet, R.color.default_background_c929292, R.drawable.bg_hotel_price_off);

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Stay createFromParcel(Parcel in)
        {
            return new Stay(in);
        }

        @Override
        public Stay[] newArray(int size)
        {
            return new Stay[size];
        }

    };
}
