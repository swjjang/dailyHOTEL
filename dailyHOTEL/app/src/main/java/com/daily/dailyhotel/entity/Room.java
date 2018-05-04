package com.daily.dailyhotel.entity;

import java.util.List;

public class Room
{
    public String name;

    public List<BedType> bedTypeList;
    public Attribute attribute;

    //    amenities (Array[string], optional): 객실 내 편의시설 ,
    public List<String> amenities;

    //    amount (AmountData, optional): 객실 가격 ,
    //        discountAverage (integer, optional): 연박 일일 결제 금액 ,
    public int discountAverage;

    //        discountTotal (integer, optional): 연박 최종 금액 ,
    public int discountTotal;

    //        discountRate (integer, optional): 정가대비 할인율 ,
    public int discountRate;

    //        price (integer, optional): 정가
    public int priceAverage;

    //    bedCount (integer, optional): 침대 수 ,
    public int bedCount;

    //    benefit (string, optional): 객실 베네핏 ,
    public String benefit;

    //    checkTime (CheckTime, optional): 객실 체크인/체크아웃 정보 ,
    public String checkInTime;
    public String checkOutTime;

    //    descriptions (string, optional): 객실 설명 ,
    public List<String> descriptions;

    //    hasUsableCoupon (boolean, optional): 쿠폰 사용 가능 여부 ,
    public boolean hasUsableCoupon;

    //    image (Image, optional): 객실 이미지 ,
    public DetailImageInformation image;

    //    needToKnows (Array[string], optional): 필수 확인사항 ,
    public List<String> needToKnows;

    //    persons (Person, optional): 객실 이용 인원수 정보 ,
    public Persons persons;

    //    provideRewardSticker (boolean, optional): 리워드 스티커 적립 여부 ,
    public boolean provideRewardSticker;

    //    roomCharge (RoomCharge, optional),
    public Charge charge;

    //    roomIdx (integer, optional): 객실 Index ,
    public int index;

    //    roomType (string, optional): 객실 유형 ,
    public String type;

    //    squareMeter (number, optional): 객실 크기 ,
    public float squareMeter;

    //    vr (Vr, optional): 객실 VR
    public List<StayDetailk.VRInformation> vrInformationList;

    public StayDetailk.RefundInformation refundInformation;

    public static class BedType
    {
        public String bedType;
        public int count;
    }

    public static class Attribute
    {
        public String roomStructure;
        public boolean isEntireHouse;
        public boolean isDuplex;
    }

    public static class Persons
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

    public static class Charge
    {
        public Consecutive consecutive;
        public Extra extra;
        public Persons persons;

        public static class Consecutive
        {
            public int charge;
            public boolean enable;
        }

        public static class Extra
        {
            public String descriptions;
            public int extraBed;
            public boolean extraBedEnable;
            public int extraBedding;
            public int extraBeddingEnable;
        }
    }
}
