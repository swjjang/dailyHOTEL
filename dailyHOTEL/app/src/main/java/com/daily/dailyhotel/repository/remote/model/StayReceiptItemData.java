package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayReceiptItem;

/**
 * Created by android_sam on 2017. 12. 11..
 * 기존 스웨거에도 없는 API 라 현재 사용 중인 값과 틀려서 현재 값(GourmetReceipt) 와 비슷하게 맞춤
 */
@JsonObject
public class StayReceiptItemData
{
    @JsonField(name = "supply_value")
    public int supplyValue; // "supply_value": 230545, 공급가액

    @JsonField(name = "value_date")
    public String paidAt; // "value_date": "2017\/07\/12", 결제일

    @JsonField(name = "vat")
    public int vat; // "vat": 23055, 부가세

    @JsonField(name = "hotel_address")
    public String stayAddress; // "hotel_address": "서울특별시 서대문구 연희로 353", 호텔 주소

    @JsonField(name = "hotel_name")
    public String stayName; // "hotel_name": "[★트루VR] 그랜드 힐튼 서울", 호텔 명

    @JsonField(name = "currency")
    public String currency; // "currency": "KRW",

    @JsonField(name = "discount")
    public int price; // "discount": 253600, 총금액

    @JsonField(name = "checkin")
    public String checkInDate; // "checkin": "2017\/08\/03", 체크인 날짜

    @JsonField(name = "user_name")
    public String userName; // "user_name": "dh00", 유저 이름

    @JsonField(name = "price")
    public int paymentAmount; // "price": 0, 실 결제 금

    @JsonField(name = "user_phone")
    public String userPhone; // "user_phone": "+82 (0)1033435072", 유저 전화번호

    @JsonField(name = "payment_name")
    public String paymentType; // "payment_name": "적립금",

    @JsonField(name = "nights")
    public int nights; // "nights": 1, 연박 수

    @JsonField(name = "bonus")
    public int bonusAmount; // "bonus": 253600,

    @JsonField(name = "coupon_amount")
    public int couponAmount; // "coupon_amount": 0,

    @JsonField(name = "checkout")
    public String checkOutDate; // "checkout": "2017\/08\/04", 체크아웃 날짜

    @JsonField(name = "rooms")
    public int rooms; // "rooms": 1 객실 수

    public StayReceiptItem getStayReceiptItem()
    {
        StayReceiptItem item = new StayReceiptItem();

        item.supplyValue = this.supplyValue;
        item.paidAt = this.paidAt;
        item.vat = this.vat;
        item.stayAddress = this.stayAddress;
        item.stayName = this.stayName;
        item.currency = this.currency;
        item.price = this.price;
        item.checkInDate = this.checkInDate;
        item.userName = this.userName;
        item.paymentAmount = this.paymentAmount;
        item.userPhone = this.userPhone;
        item.paymentType = this.paymentType;
        item.nights = this.nights;
        item.bonusAmount = this.bonusAmount;
        item.couponAmount = this.couponAmount;
        item.checkOutDate = this.checkOutDate;
        item.rooms = this.rooms;

        return item;
    }
}
