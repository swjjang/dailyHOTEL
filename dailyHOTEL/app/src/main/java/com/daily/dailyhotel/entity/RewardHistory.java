package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class RewardHistory
{
    public enum Type
    {
        CREATED_STICKER // 스티커 적립
        ,
        EXPIRED_STICKER // 스티커 만료
        ,
        PUBLISHED_COUPON // 쿠폰 발행
    }

    public enum ServiceType
    {
        HOTEL,
        GOURMET,
        OUTBOUND,
        ALL
    }

    public String aggregationId;
    public int couponPrice;
    public int expiredStickerCount;
    public String date;
    public Type type;
    public String reservationName;
    public int nights;
    public ServiceType serviceType;
    public String rewardStickerType; // R(reward), E(event)
}
