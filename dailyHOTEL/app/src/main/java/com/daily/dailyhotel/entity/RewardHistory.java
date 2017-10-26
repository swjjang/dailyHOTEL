package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class RewardHistory
{
    public enum Type
    {
        RESERVATION_STICKER, // 스티커 적립
        EVENT_STICKER,  // 이벤트 스티커
        EXPIRED_STICKER, // 스티커 만료
        PUBLISHED_COUPON // 쿠폰 발행
    }

    public String aggregationId;
    public int couponPrice;
    public int expiredStickerCount;
    public String date;
    public Type type;
    public String reservationName;
    public int position;
}
