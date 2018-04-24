package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.Room;

public class RoomParcel implements Parcelable
{
    private Room room;

    public RoomParcel(Room room)
    {
        if (room == null)
        {
            throw new NullPointerException("room == null");
        }

        this.room = room;
    }

    public RoomParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public Room getRoom()
    {
        return room;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeStringList(room.amemities);
        dest.writeInt(room.discountAverage);
        dest.writeInt(room.discountTotal);
        dest.writeInt(room.discountRate);
        dest.writeInt(room.price);
        dest.writeInt(room.bedCount);
        dest.writeString(room.benefit);
        dest.writeString(room.checkInTime);
        dest.writeString(room.checkOutTime);

        boolean hasConsecutive = room.consecutive != null;
        dest.writeInt(hasConsecutive ? 1 : 0);
        if (hasConsecutive)
        {
            dest.writeInt(room.consecutive.charge);
            dest.writeInt(room.consecutive.enable ? 1 : 0);
        }

        dest.writeString(room.descriptions);

        dest.writeInt(room.hasUsableCoupon ? 1 : 0);

        boolean hasImage = room.image != null;
        dest.writeInt(hasImage ? 1 : 0);
        if (hasImage)
        {
            dest.writeString(room.image.description);
            dest.writeString(room.image.url);
            dest.writeInt(room.image.primary ? 1 : 0);
        }

        dest.writeStringList(room.needToKnows);

        boolean hasPerson = room.persons != null;
        dest.writeInt(hasPerson ? 1 : 0);
        if (hasPerson)
        {
            dest.writeInt(room.persons.fixed);
            dest.writeInt(room.persons.extra);
            dest.writeInt(room.persons.extraCharge ? 1 : 0);
            dest.writeInt(room.persons.breakfast);
        }

        dest.writeInt(room.provideRewardSticker ? 1 : 0);

        boolean hasRoomCharge = room.roomCharge != null;
        dest.writeInt(hasRoomCharge ? 1 : 0);
        if (hasRoomCharge)
        {
            dest.writeString(room.roomCharge.descriptions);
            dest.writeInt(room.roomCharge.extraBed);
            dest.writeInt(room.roomCharge.extraBedEnable ? 1 : 0);
            dest.writeInt(room.roomCharge.extraBedding);
            dest.writeInt(room.roomCharge.extraBeddingEnable ? 1 : 0);
        }

        dest.writeInt(room.roomIdx);
        dest.writeString(room.roomType);
        dest.writeInt(room.squareMeter);
        dest.writeStringList(room.vrUrlList);
    }

    private void readFromParcel(Parcel in)
    {
        room = new Room();

        in.readStringList(room.amemities);
        room.discountAverage = in.readInt();
        room.discountTotal = in.readInt();
        room.discountRate = in.readInt();
        room.price = in.readInt();
        room.bedCount = in.readInt();
        room.benefit = in.readString();
        room.checkInTime = in.readString();
        room.checkOutTime = in.readString();

        boolean hasConsecutive = in.readInt() == 1;
        if (hasConsecutive)
        {
            Room.Consecutive consecutive = new Room.Consecutive();

            consecutive.charge = in.readInt();
            consecutive.enable = in.readInt() == 1;
            room.consecutive = consecutive;
        }

        room.descriptions = in.readString();

        room.hasUsableCoupon = in.readInt() == 1;

        boolean hasImage = in.readInt() == 1;
        if (hasImage)
        {
            Room.Image image = new Room.Image();
            image.description = in.readString();
            image.url = in.readString();
            image.primary = in.readInt() == 1;
            room.image = image;
        }

        in.readStringList(room.needToKnows);

        boolean hasPerson = in.readInt() == 1;
        if (hasPerson)
        {
            Room.Person person = new Room.Person();
            person.fixed = in.readInt();
            person.extra = in.readInt();
            person.extraCharge = in.readInt() == 1;
            person.breakfast = in.readInt();
            room.persons = person;
        }

        room.provideRewardSticker = in.readInt() == 1;

        boolean hasRoomCharge = in.readInt() == 1;
        if (hasRoomCharge)
        {
            room.roomCharge.descriptions = in.readString();
            room.roomCharge.extraBed = in.readInt();
            room.roomCharge.extraBedEnable = in.readInt() == 1;
            room.roomCharge.extraBedding = in.readInt();
            room.roomCharge.extraBeddingEnable = in.readInt() == 1;
        }

        room.roomIdx = in.readInt();
        room.roomType = in.readString();
        room.squareMeter = in.readInt();
        in.readStringList(room.vrUrlList);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public RoomParcel createFromParcel(Parcel in)
        {
            return new RoomParcel(in);
        }

        @Override
        public RoomParcel[] newArray(int size)
        {
            return new RoomParcel[size];
        }
    };
}
