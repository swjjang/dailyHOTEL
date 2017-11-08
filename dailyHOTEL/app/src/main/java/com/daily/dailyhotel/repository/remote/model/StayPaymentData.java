package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayPayment;

@JsonObject
public class StayPaymentData
{
    @JsonField(name = "hotelIdx")
    public int hotelIdx;

    @JsonField(name = "roomIdx")
    public int roomIdx;

    @JsonField(name = "discountTotal")
    public int discountTotal;

    @JsonField(name = "provideTransportation")
    public boolean provideTransportation;

    @JsonField(name = "parking")
    public boolean parking;

    @JsonField(name = "noParking")
    public boolean noParking;

    @JsonField(name = "businessName")
    public String businessName;

    @JsonField(name = "checkInDateTime")
    public String checkInDateTime;

    @JsonField(name = "checkOutDateTime")
    public String checkOutDateTime;

    @JsonField(name = "refundType")
    public String refundType;

    @JsonField(name = "onSale")
    public boolean onSale;

    @JsonField(name = "availableRooms")
    public int availableRooms;

    @JsonField(name = "waitingForBooking")
    public boolean waitingForBooking;

    @JsonField(name = "providableRewardStickerCount")
    public int providableRewardStickerCount;

    @JsonField(name = "provideRewardSticker")
    public boolean provideRewardSticker;

    @JsonField(name = "rewardCard")
    public RewardCardData rewardCard;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public StayPaymentData()
    {

    }

    public StayPayment getStayPayment()
    {
        StayPayment stayPayment = new StayPayment();

        stayPayment.soldOut = onSale == false || availableRooms == 0;

        stayPayment.checkInDate = checkInDateTime;
        stayPayment.checkOutDate = checkOutDateTime;

        stayPayment.refundType = refundType;
        stayPayment.totalPrice = discountTotal;

        if (provideTransportation == true)
        {
            if (noParking == true)
            {
                stayPayment.transportation = StayPayment.VISIT_TYPE_NO_PARKING;
            } else if (parking == true)
            {
                stayPayment.transportation = StayPayment.VISIT_TYPE_PARKING;
            } else
            {
                stayPayment.transportation = StayPayment.VISIT_TYPE_NONE;
            }
        } else
        {
            stayPayment.transportation = StayPayment.VISIT_TYPE_NONE;
        }

        stayPayment.businessName = businessName;
        stayPayment.waitingForBooking = waitingForBooking;
        stayPayment.waitingForBooking = waitingForBooking;

        if (rewardCard != null)
        {
            stayPayment.rewardStickerCount = rewardCard.rewardStickerCount;
        }

        if (configurations != null)
        {
            stayPayment.activeReward = configurations.activeReward;
        }

        stayPayment.provideRewardSticker = provideRewardSticker;
        stayPayment.providableRewardStickerCount = providableRewardStickerCount;

        return stayPayment;
    }

    @JsonObject
    static class RewardCardData
    {
        @JsonField(name = "expiredAt")
        public String expiredAt;

        @JsonField(name = "rewardStickerCount")
        public int rewardStickerCount;

        public RewardCardData()
        {

        }
    }
}
