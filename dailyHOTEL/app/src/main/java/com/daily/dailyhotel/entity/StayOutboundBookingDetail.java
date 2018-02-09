package com.daily.dailyhotel.entity;

import java.util.List;

public class StayOutboundBookingDetail extends Configurations
{
    public int stayIndex;
    public int bookingIndex;
    public String name;
    public String roomName;
    public String address;
    public String guestFirstName;
    public String guestLastName;
    public String guestEmail;
    public String guestPhone;

    public double latitude;
    public double longitude;

    public int paymentPrice;
    public int bonus;
    public int couponAmount;
    public int totalPrice;
    public double fee;

    private People mPeople;

    public RefundType refundStatus;
    private List<String> mRefundPolicyList;

    public String checkInDate;
    public String checkInTime;
    public String checkOutDate;
    public String checkOutTime;

    public PaymentType paymentType;
    public String paymentDate;

    public String cancelDateTime; // 취소 일시(취소가 아닐때에는 내려오지 않음)

    public String reviewStatusType;
    public int rewardStickerCount;

    public enum PaymentType
    {
        CREDIT_CARD,
        VBANK,
        MOBILE_PHONE,
        BONUS,
        COUPON,
        ONE_CLICK,
        CREDIT_INICIS,
        CREDIT_KCP
    }

    public enum RefundType
    {
        FULL("FULL"),
        PARTIAL("PARTIAL"),
        NRD("NRD"),
        TIMEOVER("TIMEOVER");

        String mValue;

        RefundType(String value)
        {
            mValue = value;
        }

        public String getValue()
        {
            return mValue;
        }
    }

    public StayOutboundBookingDetail()
    {

    }

    public People getPeople()
    {
        return mPeople;
    }

    public void setPeople(People people)
    {
        mPeople = people;
    }

    public List<String> getRefundPolicyList()
    {
        return mRefundPolicyList;
    }

    public void setRefundPolicyList(List<String> refundPolicyList)
    {
        mRefundPolicyList = refundPolicyList;
    }
}
