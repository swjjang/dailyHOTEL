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
    private List<String> mRefundPolicyList;
    public int totalPrice; // 결재할 총금액
    public double feeTotalAmountUsd;
    public String rateKey;
    public String roomTypeCode;
    public String rateCode;
    public int roomBedTypeId;

    // reward
    public boolean activeReward;
    public boolean provideRewardSticker;
    public int rewardStickerCount;

    public StayOutboundPayment()
    {

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