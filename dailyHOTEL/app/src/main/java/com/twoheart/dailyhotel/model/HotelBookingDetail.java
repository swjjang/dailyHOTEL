package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.model.Stay.Grade;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.twoheart.dailyhotel.screen.information.coupon.SelectCouponDialogActivity.INTENT_EXTRA_HOTEL_IDX;

public class HotelBookingDetail extends PlaceBookingDetail
{
    public static final String STATUS_NO_CHARGE_REFUND = "NO_CHARGE_REFUND"; // 무료 환불
    public static final String STATUS_SURCHARGE_REFUND = "SURCHARGE_REFUND"; // 부분 환불
    public static final String STATUS_NRD = "NRD";
    public static final String STATUS_WAIT_REFUND = "WAIT_REFUND";

    public boolean isOverseas;
    public String checkInDate;
    public String checkOutDate;
    public String hotelPhone;
    public Grade grade;
    public String roomName;
    public boolean isNRD;
    public String transactionType;
    public String refundPolicy;
    public boolean readyForRefund; // 환불 대기 상태

    public int roomIndex;
    public boolean isOverCheckOutDate; // 해당 예약이 체크아웃 시간이 지난 경우
    public String mRefundComment; // 환분 불가 내용
    public int reservationIndex;

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
            grade = Grade.valueOf(jsonObject.getString("hotelGrade"));
        } catch (Exception e)
        {
            grade = Grade.etc;
        }

        address = jsonObject.getString("hotelAddress");

        //
        JSONObject wrapJSONObject = new JSONObject(jsonObject.getString("hotelSpec"));
        JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap"); // 해당 코드 없음

        setSpecification(jsonArray);

        if (jsonObject.has("overseas") == true)
        {
            isOverseas = jsonObject.getBoolean("overseas");
        }

        latitude = jsonObject.getDouble("latitude");
        longitude = jsonObject.getDouble("longitude");

        roomName = jsonObject.getString("roomName");
        guestPhone = jsonObject.getString("guestPhone");
        guestName = jsonObject.getString("guestName");
        guestEmail = jsonObject.getString("guestEmail");

        checkInDate = jsonObject.getString("checkIn");
        checkOutDate = jsonObject.getString("checkOut");

        roomIndex = jsonObject.getInt("roomIdx");

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

        price = jsonObject.getInt("discountTotal");

        if (jsonObject.has("bonus") == true)
        {
            bonus = jsonObject.getInt("bonus");
        }

        if (jsonObject.has("couponAmount") == true)
        {
            coupon = jsonObject.getInt("couponAmount");
        }

        paymentPrice = jsonObject.getInt("priceTotal");
        paymentDate = jsonObject.getString("paidAt");

        if (jsonObject.has("refundType") == true && RoomInformation.NRD.equalsIgnoreCase(jsonObject.getString("refundType")) == true)
        {
            isNRD = true;
        } else
        {
            isNRD = false;
        }

        if (jsonObject.has("hotelIdx") == true)
        {
            placeIndex = jsonObject.getInt("hotelIdx");
        }

        readyForRefund = jsonObject.getBoolean("readyForRefund");
        transactionType = jsonObject.getString("transactionType");
        reservationIndex = jsonObject.getInt("hotelReservationIdx");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(isOverseas ? 1 : 0);
        dest.writeString(roomName);
        dest.writeString(checkInDate);
        dest.writeString(checkOutDate);
        dest.writeString(hotelPhone);
        dest.writeString(grade.name());
        dest.writeInt(bonus);
        dest.writeInt(coupon);
        dest.writeInt(isNRD ? 1 : 0);
        dest.writeString(refundPolicy);
        dest.writeString(transactionType);
        dest.writeInt(roomIndex);
        dest.writeInt(readyForRefund ? 1 : 0);
        dest.writeInt(isOverCheckOutDate ? 1 : 0);
        dest.writeString(mRefundComment);
        dest.writeInt(reservationIndex);
    }

    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        isOverseas = in.readInt() == 1;
        roomName = in.readString();
        checkInDate = in.readString();
        checkOutDate = in.readString();
        hotelPhone = in.readString();
        grade = Grade.valueOf(in.readString());
        bonus = in.readInt();
        coupon = in.readInt();
        isNRD = in.readInt() == 1;
        refundPolicy = in.readString();
        transactionType = in.readString();
        roomIndex = in.readInt();
        readyForRefund = in.readInt() == 1;
        isOverCheckOutDate = in.readInt() == 1;
        mRefundComment = in.readString();
        reservationIndex = in.readInt();
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
