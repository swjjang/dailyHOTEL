package com.daily.dailyhotel.entity;

public class StayOutboundPayment
{
    public int stayIndex;
    public int availableRooms;
    public String checkInDate;
    public String checkInTime;
    public String checkOutDate;
    public String checkOutTime;
    public boolean nonRefundable;
    public String nonRefundableDescription;
    public int totalPrice; // 결재할 총금액
    public int discountPrice; // 할인 총금액(보너스, 쿠폰)
    public double feeTotalAmountUsd;
    public String rateKey;
    public String roomTypeCode;
    public String rateCode;
    public int roomBedTypeId;

    public StayOutboundPayment()
    {

    }

    public enum PaymentType
    {
        EASY_CARD,
        CARD,
        PHONE_PAY,
    }

    /**
     * 할인 타입 , NONE, BONUS, COUPON
     */
    public enum DiscountType
    {
        NONE("None"),
        BONUS("Bonus"),
        COUPON("Coupon");

        private String mName;

        DiscountType(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }
}