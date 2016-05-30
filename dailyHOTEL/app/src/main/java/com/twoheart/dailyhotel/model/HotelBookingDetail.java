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
    public long checkInDate;
    public long checkOutDate;
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
        placeName = jsonObject.getString("hotel_name");

        try
        {
            grade = HotelGrade.valueOf(jsonObject.getString("cat"));
        } catch (Exception e)
        {
            grade = HotelGrade.etc;
        }

        address = jsonObject.getString("address");

        //
        JSONObject wrapJSONObject = new JSONObject(jsonObject.getString("spec"));
        JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap");

        setSpecification(jsonArray);

        latitude = jsonObject.getDouble("lat");
        longitude = jsonObject.getDouble("lng");

        roomName = jsonObject.getString("room_name");
        guestPhone = jsonObject.getString("guest_phone");
        guestName = jsonObject.getString("guest_name");

        checkInDate = jsonObject.getLong("checkin_date");
        checkOutDate = jsonObject.getLong("checkout_date");

        // phone1은 프론트
        String phone1 = jsonObject.getString("phone1");

        // phone2는 예약실
        String phone2 = jsonObject.getString("phone2");

        // phone3은 사용하지 않음
        String phone3 = jsonObject.getString("phone3");

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

        //        paymentDate;
        //        price;
        //        bonus;
        //        coupon;
        //        totalPrice;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(isOverseas);
        dest.writeString(roomName);
        dest.writeLong(checkInDate);
        dest.writeLong(checkOutDate);
        dest.writeString(hotelPhone);
        dest.writeString(grade.name());
    }

    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        isOverseas = in.readInt();
        roomName = in.readString();
        checkInDate = in.readLong();
        checkOutDate = in.readLong();
        hotelPhone = in.readString();
        grade = HotelGrade.valueOf(in.readString());
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
