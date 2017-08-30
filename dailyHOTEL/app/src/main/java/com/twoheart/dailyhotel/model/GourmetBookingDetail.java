package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class GourmetBookingDetail extends PlaceBookingDetail
{
    public Gourmet.Grade grade;
    public int ticketCount;
    public String ticketName;
    public String reservationTime;
    public String category;
    public String subCategory;

    public GourmetBookingDetail()
    {
    }

    public GourmetBookingDetail(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(grade.name());
        dest.writeInt(ticketCount);
        dest.writeString(ticketName);
        dest.writeString(reservationTime);
        dest.writeString(category);
        dest.writeString(subCategory);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        grade = Gourmet.Grade.valueOf(in.readString());
        ticketCount = in.readInt();
        ticketName = in.readString();
        reservationTime = in.readString();
        category = in.readString();
        subCategory = in.readString();
    }

    public void setData(com.daily.dailyhotel.entity.GourmetBookingDetail gourmetBookingDetail)
    {
        if (gourmetBookingDetail == null)
        {
            return;
        }

        address = gourmetBookingDetail.gourmetAddress;
        latitude = gourmetBookingDetail.latitude;
        longitude = gourmetBookingDetail.longitude;
        placeName = gourmetBookingDetail.gourmetName;
        grade = Gourmet.Grade.gourmet;
        category = gourmetBookingDetail.category;
        guestName = gourmetBookingDetail.guestName;
        guestPhone = gourmetBookingDetail.guestPhone;
        guestEmail = gourmetBookingDetail.guestEmail;
        addressSummary = gourmetBookingDetail.addressSummary;
        subCategory = gourmetBookingDetail.categorySub;
        setSpecification(gourmetBookingDetail.getDescriptionMap());
        ticketCount = gourmetBookingDetail.ticketCount;
        ticketName = gourmetBookingDetail.ticketName;
        reservationTime = gourmetBookingDetail.arrivalDateTime;

        // phone1은 프론트
        phone1 = gourmetBookingDetail.phone1;

        // phone2는 예약실
        phone2 = gourmetBookingDetail.phone2;

        // phone3은 사용하지 않음
        phone3 = gourmetBookingDetail.phone3;

        coupon = gourmetBookingDetail.couponAmount;
        price = gourmetBookingDetail.discountTotal;
        paymentPrice = gourmetBookingDetail.priceTotal;
        paymentDate = gourmetBookingDetail.pamentDateTime;
        placeIndex = gourmetBookingDetail.gourmetIndex;
        reservationIndex = gourmetBookingDetail.reservationIndex;

        if (DailyTextUtils.isTextEmpty(gourmetBookingDetail.reviewStatusType) == false)
        {
            reviewStatusType = gourmetBookingDetail.reviewStatusType;
        } else
        {
            reviewStatusType = ReviewStatusType.NONE;
        }
    }

    public void setData(JSONObject jsonObject) throws Exception
    {
        address = jsonObject.getString("restaurantAddress");
        latitude = jsonObject.getDouble("latitude");
        longitude = jsonObject.getDouble("longitude");
        placeName = jsonObject.getString("restaurantName");

        grade = Gourmet.Grade.gourmet;
        category = jsonObject.getString("category");
        guestName = jsonObject.getString("customerName");
        guestPhone = jsonObject.getString("customerPhone");
        guestEmail = jsonObject.getString("customerEmail");
        addressSummary = jsonObject.getString("addrSummary");
        subCategory = jsonObject.getString("categorySub");

        //
        JSONObject wrapJSONObject = new JSONObject(jsonObject.getString("description"));
        JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap");

        setSpecification(jsonArray);

        ticketCount = jsonObject.getInt("ticketCount");
        ticketName = jsonObject.getString("ticketName");
        reservationTime = jsonObject.getString("arrivalTime");

        // phone1은 프론트
        phone1 = jsonObject.getString("restaurantPhone1");

        // phone2는 예약실
        phone2 = jsonObject.getString("restaurantPhone2");

        // phone3은 사용하지 않음
        phone3 = jsonObject.getString("restaurantPhone3");

        if (jsonObject.has("couponAmount") == true)
        {
            coupon = jsonObject.getInt("couponAmount");
        }

        price = jsonObject.getInt("discountTotal");
        paymentPrice = jsonObject.getInt("paymentAmount");
        paymentDate = jsonObject.getString("paidAt");

        if (jsonObject.has("restaurantIdx") == true)
        {
            placeIndex = jsonObject.getInt("restaurantIdx");
        }

        if (jsonObject.has("fnbReservationIdx") == true)
        {
            reservationIndex = jsonObject.getInt("fnbReservationIdx");
        }

        if (jsonObject.has("reviewStatusType") == true)
        {
            reviewStatusType = jsonObject.getString("reviewStatusType");
        } else
        {
            reviewStatusType = ReviewStatusType.NONE;
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetBookingDetail createFromParcel(Parcel in)
        {
            return new GourmetBookingDetail(in);
        }

        @Override
        public GourmetBookingDetail[] newArray(int size)
        {
            return new GourmetBookingDetail[size];
        }
    };
}
