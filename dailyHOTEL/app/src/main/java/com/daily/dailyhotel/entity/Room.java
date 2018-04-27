package com.daily.dailyhotel.entity;

import java.util.List;

public class Room
{
    //    amenities (Array[string], optional): 객실 내 편의시설 ,
    public List<String> amemities;

    //    amount (AmountData, optional): 객실 가격 ,
    //        discountAverage (integer, optional): 연박 일일 결제 금액 ,
    public int discountAverage;

    //        discountTotal (integer, optional): 연박 최종 금액 ,
    public int discountTotal;

    //        discountRate (integer, optional): 정가대비 할인율 ,
    public int discountRate;

    //        price (integer, optional): 정가
    public int price;

    //    bedCount (integer, optional): 침대 수 ,
    public int bedCount;

    //    benefit (string, optional): 객실 베네핏 ,
    public String benefit;

    //    checkTime (CheckTime, optional): 객실 체크인/체크아웃 정보 ,
    public String checkInTime;
    public String checkOutTime;

    //    consecutive (Consecutive, optional): 연박 추가 요금 ,
    public Consecutive consecutive;

    //    descriptions (string, optional): 객실 설명 ,
    public String descriptions;

    //    hasUsableCoupon (boolean, optional): 쿠폰 사용 가능 여부 ,
    public boolean hasUsableCoupon;

    //    image (Image, optional): 객실 이미지 ,
    public Image image;

    //    needToKnows (Array[string], optional): 필수 확인사항 ,
    public List<String> needToKnows;

    //    persons (Person, optional): 객실 이용 인원수 정보 ,
    public Person persons;

    //    provideRewardSticker (boolean, optional): 리워드 스티커 적립 여부 ,
    public boolean provideRewardSticker;

    //    roomCharge (RoomCharge, optional),
    public RoomCharge roomCharge;

    //    roomIdx (integer, optional): 객실 Index ,
    public int roomIdx;

    //    roomType (string, optional): 객실 유형 ,
    public String roomType;

    //    squareMeter (number, optional): 객실 크기 ,
    public int squareMeter;

    //    vr (Vr, optional): 객실 VR
    public List<String> vrUrlList;

    //    public class CheckTime
    //    {
    //        public String checkIn;
    //
    //        public String checkOut;
    //    }

    public static class Consecutive
    {
        public int charge;

        public boolean enable;
    }

    public static class Image
    {
        public String description;

        public String url;

        public boolean primary;
    }


    public static class Person
    {
        //    fixed (integer, optional): 연박 일일 결제 금액
        public int fixed;

        //    extra (integer, optional): 정가대비 할인율 ,
        public int extra;

        //    extraCharge (boolean, optional): 연박 최종 금액 ,
        public boolean extraCharge;

        //    breakfast (integer, optional): 조식 제공 인원수 ,
        public int breakfast;
    }

    public static class RoomCharge
    {
        //    descriptions (string, optional),
        public String descriptions;

        //        extraBed (integer, optional),
        public int extraBed;

        //        extraBedEnable (boolean, optional),
        public boolean extraBedEnable;

        //    extraBedding (integer, optional),
        public int extraBedding;

        //        extraBeddingEnable (boolean, optional)
        public boolean extraBeddingEnable;
    }
}
