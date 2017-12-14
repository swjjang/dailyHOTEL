package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2017. 12. 11..
 * 기존 스웨거에도 없는 API 라 현재 사용 중인 값과 틀려서 현재 값(GourmetReceipt) 와 비슷하게 맞춤
 */

public class StayReceiptItem
{
    public int supplyValue; // "supply_value": 230545, 공급가액
    public String paidAt; // "value_date": "2017\/07\/12", 결제일
    public int vat; // "vat": 23055, 부가세
    public String stayAddress; // "hotel_address": "서울특별시 서대문구 연희로 353", 호텔 주소
    public String stayName; // "hotel_name": "[★트루VR] 그랜드 힐튼 서울", 호텔 명
    public String currency; // "currency": "KRW",
    public int price; // "discount": 253600, 총금액
    public String checkInDate; // "checkin": "2017\/08\/03", 체크인 날짜
    public String userName; // "user_name": "dh00", 유저 이름
    public int paymentAmount; // "price": 0, 실 결제 금
    public String userPhone; // "user_phone": "+82 (0)1033435072", 유저 전화번호
    public String paymentType; // "payment_name": "적립금",
    public int nights; // "nights": 1, 연박 수
    public int bonusAmount; // "bonus": 253600,
    public int couponAmount; // "coupon_amount": 0,
    public String checkOutDate; // "checkout": "2017\/08\/04", 체크아웃 날짜
    public int rooms; // "rooms": 1 객실 수
}
