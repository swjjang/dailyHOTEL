package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Stay.Grade;
import com.twoheart.dailyhotel.network.model.StayProduct;

import org.json.JSONArray;
import org.json.JSONObject;

public class StayBookingDetail extends PlaceBookingDetail
{
    public static final String STATUS_NO_CHARGE_REFUND = "NO_CHARGE_REFUND"; // 무료 환불
    public static final String STATUS_SURCHARGE_REFUND = "SURCHARGE_REFUND"; // 부분 환불
    public static final String STATUS_NRD = "NRD";
    public static final String STATUS_WAIT_REFUND = "WAIT_REFUND";
    public static final String STATUS_NONE = "NONE";

    public static final String VISIT_TYPE_NONE = "NONE";
    public static final String VISIT_TYPE_WALKING = "PARKING";
    public static final String VISIT_TYPE_CAR = "CAR";
    public static final String VISIT_TYPE_NO_PARKING = "NO_PARKING";

    public String checkInDate;
    public String checkOutDate;

    public Grade grade;
    public String roomName;
    public boolean isNRD;
    public String transactionType;
    public String refundPolicy;
    public boolean readyForRefund; // 환불 대기 상태

    public int roomIndex;
    public boolean isVisibleRefundPolicy; // 하단에 정책을 보여줄지 말지.
    public String mRefundComment; // 환분 불가 내용

    public String visitType = VISIT_TYPE_NONE; // 방문 타입 "NONE", "WALKING". "CAR", "NO_PARKING"
    public boolean waitingForBooking;

    // reward
    public boolean activeReward;
    public int rewardStickerCount;

    public StayBookingDetail()
    {
    }

    public void setData(com.daily.dailyhotel.entity.StayBookingDetail stayBookingDetail)
    {
        if (stayBookingDetail == null)
        {
            return;
        }

        placeName = stayBookingDetail.stayName;

        try
        {
            grade = stayBookingDetail.stayGrade;
        } catch (Exception e)
        {
            grade = Grade.etc;
        }

        address = stayBookingDetail.stayAddress;
        setSpecification(stayBookingDetail.getSpecificationMap());
        isOverseas = stayBookingDetail.overseas;
        latitude = stayBookingDetail.latitude;
        longitude = stayBookingDetail.longitude;
        roomName = stayBookingDetail.roomName;
        guestPhone = stayBookingDetail.guestPhone;
        guestName = stayBookingDetail.guestName;
        guestEmail = stayBookingDetail.guestEmail;
        checkInDate = stayBookingDetail.checkInDateTime;
        checkOutDate = stayBookingDetail.checkOutDateTime;
        roomIndex = stayBookingDetail.roomIndex;

        // phone1은 프론트
        phone1 = stayBookingDetail.phone1;

        // phone2는 예약실
        phone2 = stayBookingDetail.phone2;

        // phone3은 사용하지 않음
        phone3 = stayBookingDetail.phone3;

        price = stayBookingDetail.discountTotal;
        bonus = stayBookingDetail.bonusAmount;
        coupon = stayBookingDetail.couponAmount;
        paymentPrice = stayBookingDetail.priceTotal;
        paymentDate = stayBookingDetail.paymentDateTime;
        isNRD = StayProduct.NRD.equalsIgnoreCase(stayBookingDetail.refundType) == true;
        placeIndex = stayBookingDetail.stayIndex;
        readyForRefund = stayBookingDetail.readyForRefund;
        transactionType = stayBookingDetail.transactionType;
        reservationIndex = stayBookingDetail.reservationIndex;
        waitingForBooking = stayBookingDetail.waitingForBooking;

        if (DailyTextUtils.isTextEmpty(stayBookingDetail.reviewStatusType) == false)
        {
            reviewStatusType = stayBookingDetail.reviewStatusType;
        } else
        {
            reviewStatusType = ReviewStatusType.NONE;
        }

        if (DailyTextUtils.isTextEmpty(stayBookingDetail.guestTransportation) == false)
        {
            switch (stayBookingDetail.guestTransportation)
            {
                case "CAR":
                    visitType = VISIT_TYPE_CAR;
                    break;

                case "NO_PARKING":
                    visitType = VISIT_TYPE_NO_PARKING;
                    break;

                case "WALKING":
                    visitType = VISIT_TYPE_WALKING;
                    break;

                default:
                    visitType = VISIT_TYPE_NONE;
                    break;
            }
        } else
        {
            visitType = VISIT_TYPE_NONE;
        }

        cancelDateTime = stayBookingDetail.cancelDateTime;

        rewardStickerCount = stayBookingDetail.rewardStickerCount;
        activeReward = stayBookingDetail.activeReward;
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
        phone1 = jsonObject.getString("hotelPhone1");

        // phone2는 예약실
        phone2 = jsonObject.getString("hotelPhone2");

        // phone3은 사용하지 않음
        phone3 = jsonObject.getString("hotelPhone3");

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

        isNRD = jsonObject.has("refundType") && StayProduct.NRD.equalsIgnoreCase(jsonObject.getString("refundType"));

        if (jsonObject.has("hotelIdx") == true)
        {
            placeIndex = jsonObject.getInt("hotelIdx");
        }

        readyForRefund = jsonObject.getBoolean("readyForRefund");
        transactionType = jsonObject.getString("transactionType");
        reservationIndex = jsonObject.getInt("hotelReservationIdx");

        if (jsonObject.has("reviewStatusType") == true)
        {
            reviewStatusType = jsonObject.getString("reviewStatusType");
        } else
        {
            reviewStatusType = ReviewStatusType.NONE;
        }

        if (jsonObject.has("waitingForBooking") == true)
        {
            waitingForBooking = jsonObject.getBoolean("waitingForBooking");
        }

        if (jsonObject.has("guestTransportation") == true && jsonObject.isNull("guestTransportation") == false)
        {
            String guestTransportation = jsonObject.getString("guestTransportation");

            if (DailyTextUtils.isTextEmpty(guestTransportation) == true)
            {
                visitType = VISIT_TYPE_NONE;
            } else
            {
                switch (guestTransportation)
                {
                    case "CAR":
                        visitType = VISIT_TYPE_CAR;
                        break;

                    case "NO_PARKING":
                        visitType = VISIT_TYPE_NO_PARKING;
                        break;

                    case "WALKING":
                        visitType = VISIT_TYPE_WALKING;
                        break;

                    default:
                        visitType = VISIT_TYPE_NONE;
                        break;
                }
            }
        } else
        {
            visitType = VISIT_TYPE_NONE;
        }

        if (jsonObject.has("cancelDateTime") == true)
        {
            cancelDateTime = jsonObject.getString("cancelDateTime");
        }

        if (jsonObject.has("configurations") == true)
        {
            activeReward = jsonObject.getJSONObject("configurations").getBoolean("activeReward");
        }

        if (jsonObject.has("rewardCard") == true)
        {
            rewardStickerCount = jsonObject.getJSONObject("rewardCard").getInt("rewardStickerCount");
        }
    }

    public StayBookingDetail(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(roomName);
        dest.writeString(checkInDate);
        dest.writeString(checkOutDate);
        dest.writeString(grade.name());
        dest.writeInt(bonus);
        dest.writeInt(coupon);
        dest.writeInt(isNRD ? 1 : 0);
        dest.writeString(refundPolicy);
        dest.writeString(transactionType);
        dest.writeInt(roomIndex);
        dest.writeInt(readyForRefund ? 1 : 0);
        dest.writeInt(isVisibleRefundPolicy ? 1 : 0);
        dest.writeString(mRefundComment);

        dest.writeInt(activeReward ? 1 : 0);
        dest.writeInt(rewardStickerCount);
    }

    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        roomName = in.readString();
        checkInDate = in.readString();
        checkOutDate = in.readString();
        grade = Grade.valueOf(in.readString());
        bonus = in.readInt();
        coupon = in.readInt();
        isNRD = in.readInt() == 1;
        refundPolicy = in.readString();
        transactionType = in.readString();
        roomIndex = in.readInt();
        readyForRefund = in.readInt() == 1;
        isVisibleRefundPolicy = in.readInt() == 1;
        mRefundComment = in.readString();

        activeReward = in.readInt() == 1;
        rewardStickerCount = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayBookingDetail createFromParcel(Parcel in)
        {
            return new StayBookingDetail(in);
        }

        @Override
        public StayBookingDetail[] newArray(int size)
        {
            return new StayBookingDetail[size];
        }
    };
}
