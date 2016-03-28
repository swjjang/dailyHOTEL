package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Hotel implements Parcelable
{
    public int index;
    public int averageDiscountPrice;
    public double latitude;
    public double longitude;
    public boolean isDailyChoice;
    public boolean isDBenefit;
    public int nights;
    public String imageUrl;
    public String name;
    public int price;
    public String addressSummary;
    public boolean isSoldOut;
    public String detailRegion;
    public int satisfaction;
    public float distance; // 정렬시에 보여주는 내용
    public String categoryCode;

    protected HotelGrade mGrade;
    protected HotelFilters mHotelFilters;

    public Hotel()
    {
        super();
    }

    public Hotel(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(averageDiscountPrice);
        dest.writeString(addressSummary);
        dest.writeSerializable(mGrade);
        dest.writeInt(index);
        dest.writeInt(isSoldOut ? 1 : 0);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(isDailyChoice ? 1 : 0);
        dest.writeInt(isDBenefit ? 1 : 0);
        dest.writeInt(satisfaction);
        dest.writeString(categoryCode);
    }

    protected void readFromParcel(Parcel in)
    {
        imageUrl = in.readString();
        name = in.readString();
        price = in.readInt();
        averageDiscountPrice = in.readInt();
        addressSummary = in.readString();
        mGrade = (HotelGrade) in.readSerializable();
        index = in.readInt();
        isSoldOut = in.readInt() == 1 ? true : false;
        latitude = in.readDouble();
        longitude = in.readDouble();
        isDailyChoice = in.readInt() == 1 ? true : false;
        isDBenefit = in.readInt() == 1 ? true : false;
        satisfaction = in.readInt();
        categoryCode = in.readString();
    }

    public HotelGrade getGrade()
    {
        return mGrade;
    }

    public boolean setHotel(JSONObject jsonObject, String imageUrl, int nights)
    {
        this.nights = nights;

        try
        {
            name = jsonObject.getString("name");
            price = jsonObject.getInt("price");
            averageDiscountPrice = jsonObject.getInt("discountAvg");
            addressSummary = jsonObject.getString("addrSummary");

            try
            {
                mGrade = HotelGrade.valueOf(jsonObject.getString("grade"));
            } catch (Exception e)
            {
                mGrade = HotelGrade.etc;
            }

            index = jsonObject.getInt("hotelIdx");
            isSoldOut = jsonObject.getBoolean("isSoldOut");
            detailRegion = jsonObject.getString("districtName");
            categoryCode = jsonObject.getString("category");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = jsonObject.getBoolean("isDailychoice");
            satisfaction = jsonObject.getInt("ratingValue");

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

            String dBenefit = jsonObject.getString("hotelBenefit");

            if (Util.isTextEmpty(dBenefit) == true)
            {
                isDBenefit = false;
            } else
            {
                isDBenefit = true;
            }

            mHotelFilters = makeHotelFilters(categoryCode, jsonObject);
        } catch (JSONException e)
        {
            ExLog.d(e.toString());

            return false;
        }

        return true;
    }

    public boolean isFiltered(HotelCurationOption curationOption)
    {
        if (mHotelFilters == null)
        {
            return false;
        }

        return mHotelFilters.isFiltered(curationOption);
    }

    public HotelFilters getFilters()
    {
        return mHotelFilters;
    }

    private HotelFilters makeHotelFilters(String categoryCode, JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return null;
        }

        JSONArray jsonArray = jsonObject.getJSONArray("hotelRoomElementList");

        if (jsonArray == null || jsonArray.length() == 0)
        {
            return null;
        }

        int length = jsonArray.length();
        HotelFilters hotelFilters = new HotelFilters(length);
        hotelFilters.categoryCode = categoryCode;

        for (int i = 0; i < length; i++)
        {
            JSONObject jsonFilter = jsonArray.getJSONObject(i);
            jsonFilter.put("parking", jsonObject.getString("parking"));
            jsonFilter.put("pool", jsonObject.getString("pool"));
            jsonFilter.put("fitness", jsonObject.getString("fitness"));

            hotelFilters.setHotelFilter(i, jsonFilter);
        }

        return hotelFilters;
    }

    public enum HotelGrade
    {
        special(R.string.grade_special, R.color.grade_special, R.drawable.bg_hotel_price_special1),
        special1(R.string.grade_special1, R.color.grade_special1, R.drawable.bg_hotel_price_special1),
        special2(R.string.grade_special2, R.color.grade_special2, R.drawable.bg_hotel_price_special2),
        //
        biz(R.string.grade_biz, R.color.grade_business, R.drawable.bg_hotel_price_business),
        hostel(R.string.grade_hostel, R.color.grade_business, R.drawable.bg_hotel_price_business),
        grade1(R.string.grade_1, R.color.grade_business, R.drawable.bg_hotel_price_business),
        grade2(R.string.grade_2, R.color.grade_business, R.drawable.bg_hotel_price_business),
        grade3(R.string.grade_3, R.color.grade_business, R.drawable.bg_hotel_price_business),
        //
        resort(R.string.grade_resort, R.color.grade_resort_pension_condo, R.drawable.bg_hotel_price_pension),
        pension(R.string.grade_pension, R.color.grade_resort_pension_condo, R.drawable.bg_hotel_price_pension),
        fullvilla(R.string.grade_fullvilla, R.color.grade_resort_pension_condo, R.drawable.bg_hotel_price_pension),
        condo(R.string.grade_condo, R.color.grade_resort_pension_condo, R.drawable.bg_hotel_price_pension),
        //
        boutique(R.string.grade_boutique, R.color.grade_boutique, R.drawable.bg_hotel_price_boutique),
        design(R.string.grade_design, R.color.grade_design, R.drawable.bg_hotel_price_design),
        //
        residence(R.string.grade_residence, R.color.grade_residence, R.drawable.bg_hotel_price_residence),
        //
        etc(R.string.grade_not_yet, R.color.grade_not_yet, R.drawable.bg_hotel_price_etc);

        private int mNameResId;
        private int mColorResId;
        private int mMarkerResId;

        private HotelGrade(int nameResId, int colorResId, int markerResId)
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
        public Hotel createFromParcel(Parcel in)
        {
            return new Hotel(in);
        }

        @Override
        public Hotel[] newArray(int size)
        {
            return new Hotel[size];
        }

    };
}
