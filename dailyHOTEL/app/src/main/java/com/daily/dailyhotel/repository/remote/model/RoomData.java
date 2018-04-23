package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Room;

import java.util.List;

@JsonObject
public class RoomData
{
    //    amenities (Array[string], optional): 객실 내 편의시설 ,
    @JsonField(name = "amenities")
    public List<String> amemities;

    //    amount (AmountData, optional): 객실 가격 ,
    @JsonField(name = "amount")
    public AmountData amount;

    //    bedCount (integer, optional): 침대 수 ,
    @JsonField(name = "bedCount")
    public int bedCount;

    //    benefit (string, optional): 객실 베네핏 ,
    @JsonField(name = "benefit")
    public String benefit;

    //    checkTime (CheckTime, optional): 객실 체크인/체크아웃 정보 ,
    @JsonField(name = "checkTime")
    public CheckTimeData checkTime;

    //    consecutive (Consecutive, optional): 연박 추가 요금 ,
    @JsonField(name = "consecutive")
    public ConsecutiveData consecutive;

    //    descriptions (string, optional): 객실 설명 ,
    @JsonField(name = "descriptions")
    public String descriptions;

    //    hasUsableCoupon (boolean, optional): 쿠폰 사용 가능 여부 ,
    @JsonField(name = "hasUsableCoupon")
    public boolean hasUsableCoupon;

    //    image (Image, optional): 객실 이미지 ,
    @JsonField(name = "image")
    public ImageData image;

    //    needToKnows (Array[string], optional): 필수 확인사항 ,
    @JsonField(name = "needToKnows")
    public List<String> needToKnows;

    //    persons (Person, optional): 객실 이용 인원수 정보 ,
    @JsonField(name = "persons")
    public PersonData persons;

    //    provideRewardSticker (boolean, optional): 리워드 스티커 적립 여부 ,
    @JsonField(name = "provideRewardSticker")
    public boolean provideRewardSticker;

    //    roomCharge (RoomCharge, optional),
    @JsonField(name = "roomCharge")
    public RoomChargeData roomCharge;

    //    roomIdx (integer, optional): 객실 Index ,
    @JsonField(name = "roomIdx")
    public int roomIdx;

    //    roomType (string, optional): 객실 유형 ,
    @JsonField(name = "roomType")
    public String roomType;

    //    squareMeter (number, optional): 객실 크기 ,
    @JsonField(name = "squareMeter")
    public int squareMeter;

    //    vr (Vr, optional): 객실 VR
    @JsonField(name = "vr")
    public VrData vr;

    @JsonObject
    public class AmountData
    {
        //        discountAverage (integer, optional): 연박 일일 결제 금액 ,
        @JsonField(name = "discountAverage")
        public int discountAverage;

        //        discountTotal (integer, optional): 연박 최종 금액 ,
        @JsonField(name = "discountTotal")
        public int discountTotal;

        //        discountRate (integer, optional): 정가대비 할인율 ,
        @JsonField(name = "discountRate")
        public int discountRate;

        //        price (integer, optional): 정가
        @JsonField(name = "price")
        public int price;
    }

    @JsonObject
    public class CheckTimeData
    {
        @JsonField(name = "checkIn")
        public String checkIn;

        @JsonField(name = "checkOut")
        public String checkOut;
    }

    @JsonObject
    public class ConsecutiveData
    {
        @JsonField(name = "charge")
        public int charge;

        @JsonField(name = "enable")
        public boolean enable;

        public Room.Consecutive getConsecutive()
        {
            Room.Consecutive conse = new Room.Consecutive();

            conse.charge = consecutive.charge;
            conse.enable = consecutive.enable;

            return conse;
        }
    }

    @JsonObject
    public class ImageData
    {
        @JsonField(name = "description")
        public String description;

        @JsonField(name = "url")
        public String url;

        @JsonField(name = "primary")
        public boolean primary;

        public Room.Image getImage()
        {
            Room.Image roomImage = new Room.Image();

            roomImage.description = image.description;
            roomImage.url = image.url;
            roomImage.primary = image.primary;

            return roomImage;
        }
    }

    @JsonObject
    public class PersonData
    {
        //    fixed (integer, optional): 연박 일일 결제 금액
        @JsonField(name = "fixed")
        public int fixed;

        //    extra (integer, optional): 정가대비 할인율 ,
        @JsonField(name = "extra")
        public int extra;

        //    extraCharge (boolean, optional): 연박 최종 금액 ,
        @JsonField(name = "extraCharge")
        public boolean extraCharge;

        //    breakfast (integer, optional): 조식 제공 인원수 ,
        @JsonField(name = "breakfast")
        public int breakfast;

        public Room.Person getPerson()
        {
            Room.Person person = new Room.Person();

            person.fixed = persons.fixed;
            person.extra = persons.extra;
            person.extraCharge = persons.extraCharge;
            person.breakfast = persons.breakfast;

            return person;
        }
    }

    @JsonObject
    public class RoomChargeData
    {
        //    descriptions (string, optional),
        @JsonField(name = "descriptions")
        public String descriptions;

        //        extraBed (integer, optional),
        @JsonField(name = "extraBed")
        public int extraBed;

        //        extraBedEnable (boolean, optional),
        @JsonField(name = "extraBedEnable")
        public boolean extraBedEnable;

        //    extraBedding (integer, optional),
        @JsonField(name = "extraBedding")
        public int extraBedding;

        //        extraBeddingEnable (boolean, optional)
        @JsonField(name = "extraBeddingEnable")
        public boolean extraBeddingEnable;

        public Room.RoomCharge getRoomCharge()
        {
            Room.RoomCharge charge = new Room.RoomCharge();

            charge.descriptions = roomCharge.descriptions;
            charge.extraBed = roomCharge.extraBed;
            charge.extraBedEnable = roomCharge.extraBedEnable;
            charge.extraBedding = roomCharge.extraBedding;
            charge.extraBeddingEnable = roomCharge.extraBeddingEnable;

            return charge;
        }
    }

    @JsonObject
    public class VrData
    {
        //    urls (Array[string], optional)
        @JsonField(name = "urls")
        public List<String> urls;
    }

    public Room getRoom()
    {
        Room room = new Room();

        room.amemities = amemities;
        room.discountAverage = amount.discountAverage;
        room.discountTotal = amount.discountTotal;
        room.discountRate = amount.discountRate;
        room.price = amount.price;
        room.bedCount = bedCount;
        room.benefit = benefit;
        room.checkInTime = checkTime.checkIn;
        room.checkOutTime = checkTime.checkOut;
        room.consecutive = consecutive.getConsecutive();
        room.descriptions = descriptions;
        room.hasUsableCoupon = hasUsableCoupon;
        room.image = image.getImage();
        room.needToKnows = needToKnows;
        room.persons = persons.getPerson();
        room.provideRewardSticker = provideRewardSticker;
        room.roomCharge = roomCharge.getRoomCharge();
        room.roomIdx = roomIdx;
        room.roomType = roomType;
        room.squareMeter = squareMeter;
        room.vrUrlList = vr.urls;

        return room;
    }
}
