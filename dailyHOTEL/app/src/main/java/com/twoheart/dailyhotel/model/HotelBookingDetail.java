package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.model.Hotel.HotelGrade;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

public class HotelBookingDetail extends PlaceBookingDetail
{
    public int isOverseas; // 0 : 국내 , 1 : 해외
    public String checkInDate;
    public String checkOutDate;
    public String hotelPhone;
    public Hotel.HotelGrade grade;
    public String roomName;

    public HotelBookingDetail()
    {
    }

    public HotelBookingDetail(Parcel in)
    {
        readFromParcel(in);
    }

    public void setData(JSONObject jsonObject) throws Exception
    {
        placeName = jsonObject.getString("hotelName");

        try
        {
            grade = HotelGrade.valueOf(jsonObject.getString("hotelGrade"));
        } catch (Exception e)
        {
            grade = HotelGrade.etc;
        }

        address = jsonObject.getString("hotelAddress");

        //
        JSONObject wrapJSONObject = new JSONObject(jsonObject.getString("hotelSpec"));
        JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap"); // 해당 코드 없음

        setSpecification(jsonArray);

        latitude = jsonObject.getDouble("latitude");
        longitude = jsonObject.getDouble("longitude");

        roomName = jsonObject.getString("roomName");
        guestPhone = jsonObject.getString("guestPhone");
        guestName = jsonObject.getString("guestName");

        checkInDate = jsonObject.getString("checkIn");
        checkOutDate = jsonObject.getString("checkOut");

        // phone1은 프론트
        String phone1 = jsonObject.getString("hotelPhone1");

        // phone2는 예약실
        String phone2 = jsonObject.getString("hotelPhone2");

        // phone3은 사용하지 않음
        String phone3 = jsonObject.getString("hotelPhone3");

        if (Util.isTextEmpty(phone2) == false)
        {
            hotelPhone = phone2;
        } else if (Util.isTextEmpty(phone1) == false)
        {
            hotelPhone = phone1;
        } else if (Util.isTextEmpty(phone3) == false)
        {
            hotelPhone = phone3;
        }

        price = jsonObject.getInt("priceTotal");

        if (jsonObject.has("bonus") == true)
        {
            bonus = jsonObject.getInt("bonus");
        }

        if (jsonObject.has("couponAmount") == true)
        {
            coupon = jsonObject.getInt("couponAmount");
        }

        paymentPrice = jsonObject.getInt("discountTotal"); // 해당 값없음
        paymentDate = jsonObject.getString("paidAt");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(isOverseas);
        dest.writeString(roomName);
        dest.writeString(checkInDate);
        dest.writeString(checkOutDate);
        dest.writeString(hotelPhone);
        dest.writeString(grade.name());
        dest.writeInt(bonus);
        dest.writeInt(coupon);
    }

    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        isOverseas = in.readInt();
        roomName = in.readString();
        checkInDate = in.readString();
        checkOutDate = in.readString();
        hotelPhone = in.readString();
        grade = HotelGrade.valueOf(in.readString());
        bonus = in.readInt();
        coupon = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public HotelBookingDetail createFromParcel(Parcel in)
        {
            return new HotelBookingDetail(in);
        }

        @Override
        public HotelBookingDetail[] newArray(int size)
        {
            return new HotelBookingDetail[size];
        }
    };
}
