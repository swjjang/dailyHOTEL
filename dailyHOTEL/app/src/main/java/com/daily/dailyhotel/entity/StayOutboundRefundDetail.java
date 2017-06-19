package com.daily.dailyhotel.entity;

import android.support.v4.util.Pair;

import java.util.List;

public class StayOutboundRefundDetail
{
    public int stayIndex;
    public int bookingIndex;
    public String name;
    public String roomName;
    public String address;
    public int paymentPrice;
    public int bonus;
    public int totalPrice;
    public StayOutboundBookingDetail.RefundType refundStatus;
    public String checkInDate;
    public String checkInTime;
    public String checkOutDate;
    public String checkOutTime;

    public StayOutboundBookingDetail.PaymentType paymentType;
    public String paymentDate;
    private List<Pair<String, String>> mCancelReasonTypes;

    public StayOutboundRefundDetail()
    {

    }

    public void setCancelReasonTypeList(List<Pair<String, String>> cancelReasonTypeList)
    {
        mCancelReasonTypes = cancelReasonTypeList;
    }

    public List<Pair<String, String>> getCancelReasonTypeList()
    {
        return mCancelReasonTypes;
    }
}