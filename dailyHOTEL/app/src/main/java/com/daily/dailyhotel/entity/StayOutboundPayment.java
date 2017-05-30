package com.daily.dailyhotel.entity;

import java.util.List;

public class StayOutboundPayment
{
    public int stayIndex;
    public int availableRooms;
    public String checkInDate;
    public String checkInTime;
    public String checkOutDate;
    public String checkOutTime;
    public boolean nonRefundable;
    public List<String> nonRefundableDescription;
    public int totalPrice;
    public int discountPrice;
    public int paymentPrice;
    public double feeTotalAmountUsd;
    public String rateKey;
    public String roomTypeCode;
    public String rateCode;
    public int roomBedTypeId;

    public StayOutboundPayment()
    {

    }

    public void setRefundDescriptionList(List<String> descriptionList)
    {
        nonRefundableDescription = descriptionList;
    }

    public List<String> getRefundDescriptionList()
    {
        return nonRefundableDescription;
    }

    // 명칭 변경하면 안됨 서버와 약속되어있음.
    public enum PaymentType
    {
        EASY_CARD("EasyCardPay"),
        CARD("CardPay"),
        PHONE_PAY("PhoneBillPay"),
        VBANK("VirtualAccountPay");

        private String mName;

        PaymentType(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
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