package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.Coupon;

/**
 * Created by android_sam on 2017. 9. 28..
 */
@JsonObject
public class CouponData
{
    @JsonField(name = "userCouponCode")
    public String userCouponCode; // (구 유저 쿠폰 코드 (이벤트 페이지의 쿠폰코드와 틀림),,

    @JsonField(name = "title")
    public String title; // 쿠폰명 ,,,

    @JsonField(name = "description")
    public String description; // 쿠폰 설명

    @JsonField(name = "couponCode")
    public String couponCode; // 이벤트 웹뷰, 쿠폰주의사항 사용용 쿠폰 코드 - 쿠폰별 유니크 코드

    @JsonField(name = "isDownloaded")
    public boolean isDownloaded; // 유저가 다운로드 했는지 여부 ,,

    @JsonField(name = "downloadedAt")
    public String downloadedAt; // 유저가 해당 쿠폰을 다운로드한 시각

    @JsonField(name = "isExpired")
    public boolean isExpired; // 만료된 쿠폰인지 여부

    @JsonField(name = "isRedeemed")
    public boolean isRedeemed; // 이미 사용한 쿠폰인지 여부

    @JsonField(name = "disabledAt")
    public String disabledAt; //쿠폰 사용 완료 또는 만료된 시각 (쿠폰 상태가 완료되면, 없어집니다.).... isExpired가 true이면 만료된 날짜, isRedeemed가 true이면 사용한 날짜. isExpired와 isRedeemed는 같은 값일 수 없음. (ISO-8601)

    @JsonField(name = "validFrom")
    public String validFrom; // 쿠폰 사용가능한 시작 시각 ,,,

    @JsonField(name = "validTo")
    public String validTo; // 쿠폰 사용가능한 마지막 시각 ,,,

    @JsonField(name = "amount")
    public int amount; // 쿠폰금액 ,,,

    @JsonField(name = "amountMinimum")
    public int amountMinimum; // 최소주문금액 ,,,

    @JsonField(name = "availableInDomestic")
    public boolean availableInDomestic; // 국내 업소만 쿠폰 적용 여부

    @JsonField(name = "availableInOverseas")
    public boolean availableInOverseas; // 해외 업소만 쿠폰 적용 여부

    @JsonField(name = "availableInHotel")
    public boolean availableInStay; // 호텔 쿠폰인지 여부 (아이콘으로 쓰세요)

    @JsonField(name = "availableInGourmet")
    public boolean availableInGourmet; // 고메 쿠폰인지 여부 (아이콘으로 쓰세요)

    @JsonField(name = "availableItem")
    public String availableItem; // 사용가능처 ,,

    @JsonField(name = "stayFrom")
    public String stayFrom; // 투숙일 정보 - 시작일

    @JsonField(name = "stayTo")
    public String stayTo; // 투숙일 정보 - 종료일

    //    public String serverDate; // 서버시간 ,, 이건 따로 들어옴

    public Coupon getCoupon(String serverDate)
    {
        Coupon coupon = new Coupon();
        coupon.userCouponCode = userCouponCode;
        coupon.title = title;
        coupon.description = description;
        coupon.couponCode = couponCode;
        coupon.isDownloaded = isDownloaded;
        coupon.downloadedAt = downloadedAt;
        coupon.isExpired = isExpired;
        coupon.isRedeemed = isRedeemed;
        coupon.disabledAt = disabledAt;
        coupon.validFrom = validFrom;
        coupon.validTo = validTo;
        coupon.amount = amount;
        coupon.amountMinimum = amountMinimum;
        coupon.availableInDomestic = availableInDomestic;
        coupon.availableInOverseas = availableInOverseas;
        coupon.availableInStay = availableInStay;
        coupon.availableInGourmet = availableInGourmet;
        coupon.availableItem = availableItem;
        coupon.stayFrom = stayFrom;
        coupon.stayTo = stayTo;

        coupon.serverDate = serverDate;

        return coupon;
    }
}
