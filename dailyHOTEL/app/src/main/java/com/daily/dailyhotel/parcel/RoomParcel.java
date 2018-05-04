package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.Room;
import com.daily.dailyhotel.entity.StayDetailk;

import java.util.ArrayList;
import java.util.List;

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
        dest.writeInt(room.index);
        dest.writeString(room.name);
        dest.writeInt(room.discountAverage);
        dest.writeInt(room.discountTotal);
        dest.writeInt(room.discountRate);
        dest.writeInt(room.priceAverage);
        dest.writeInt(room.bedCount);
        dest.writeString(room.benefit);
        dest.writeString(room.checkInTime);
        dest.writeString(room.checkOutTime);
        dest.writeString(room.type);
        dest.writeStringList(room.amenities);
        dest.writeStringList(room.descriptions);
        dest.writeStringList(room.needToKnows);
        dest.writeFloat(room.squareMeter);
        dest.writeInt(room.hasUsableCoupon ? 1 : 0);
        dest.writeInt(room.provideRewardSticker ? 1 : 0);

        boolean hasBeTypeList = room.bedTypeList != null && room.bedTypeList.size() > 0;
        dest.writeInt(hasBeTypeList ? 1 : 0);
        if (hasBeTypeList)
        {
            dest.writeInt(room.bedTypeList.size());

            for (Room.BedType bedType : room.bedTypeList)
            {
                dest.writeString(bedType.bedType);
                dest.writeInt(bedType.count);
            }
        }

        boolean hasAttribute = room.attribute != null;
        dest.writeInt(hasAttribute ? 1 : 0);
        if (hasAttribute)
        {
            dest.writeString(room.attribute.roomStructure);
            dest.writeInt(room.attribute.isEntireHouse ? 1 : 0);
            dest.writeInt(room.attribute.isDuplex ? 1 : 0);
        }

        boolean hasImage = room.image != null;
        dest.writeInt(hasImage ? 1 : 0);
        if (hasImage)
        {
            dest.writeString(room.image.caption);

            ImageMap imageMap = room.image.getImageMap();
            if (imageMap != null)
            {
                dest.writeString(imageMap.smallUrl);
                dest.writeString(imageMap.mediumUrl);
                dest.writeString(imageMap.bigUrl);
            } else
            {
                dest.writeString(null);
                dest.writeString(null);
                dest.writeString(null);
            }
        }

        boolean hasPersons = room.persons != null;
        dest.writeInt(hasPersons ? 1 : 0);
        if (hasPersons)
        {
            dest.writeInt(room.persons.fixed);
            dest.writeInt(room.persons.extra);
            dest.writeInt(room.persons.extraCharge ? 1 : 0);
            dest.writeInt(room.persons.breakfast);
        }

        boolean hasRoomCharge = room.charge != null;
        dest.writeInt(hasRoomCharge ? 1 : 0);
        if (hasRoomCharge)
        {
            boolean hasRoomChargeConsecutive = room.charge.consecutive != null;
            dest.writeInt(hasRoomChargeConsecutive ? 1 : 0);
            if (hasRoomChargeConsecutive)
            {
                dest.writeInt(room.charge.consecutive.charge);
                dest.writeInt(room.charge.consecutive.enable ? 1 : 0);
            }

            boolean hasRoomChargeExtra = room.charge.extra != null;
            dest.writeInt(hasRoomChargeExtra ? 1 : 0);
            if (hasRoomChargeExtra)
            {
                dest.writeString(room.charge.extra.descriptions);
                dest.writeInt(room.charge.extra.extraBed);
                dest.writeInt(room.charge.extra.extraBedEnable ? 1 : 0);
                dest.writeInt(room.charge.extra.extraBedding);
                dest.writeInt(room.charge.extra.extraBeddingEnable);
            }

            boolean hasRoomChargePersons = room.charge.persons != null;
            dest.writeInt(hasRoomChargePersons ? 1 : 0);
            if (hasRoomChargePersons)
            {
                dest.writeInt(room.charge.persons.fixed);
                dest.writeInt(room.charge.persons.extra);
                dest.writeInt(room.charge.persons.extraCharge ? 1 : 0);
                dest.writeInt(room.charge.persons.breakfast);
            }
        }

        boolean hasVRInformation = room.vrInformationList != null && room.vrInformationList.size() > 0;
        dest.writeInt(hasVRInformation ? 1 : 0);
        if (hasVRInformation)
        {
            dest.writeInt(room.vrInformationList.size());

            for (StayDetailk.VRInformation vrInformation : room.vrInformationList)
            {
                dest.writeString(vrInformation.getName());
                dest.writeString(vrInformation.getType());
                dest.writeInt(vrInformation.getTypeIndex());
                dest.writeString(vrInformation.getUrl());
            }
        }

        boolean hasRefundInformation = room.refundInformation != null;
        dest.writeInt(hasRefundInformation ? 1 : 0);
        if (hasRefundInformation)
        {
            dest.writeString(room.refundInformation.getTitle());
            dest.writeString(room.refundInformation.getType());
            dest.writeStringList(room.refundInformation.getContentList());
            dest.writeString(room.refundInformation.getWarningMessage());
        }
    }

    private void readFromParcel(Parcel in)
    {
        room = new Room();

        room.index = in.readInt();
        room.name = in.readString();
        room.discountAverage = in.readInt();
        room.discountTotal = in.readInt();
        room.discountRate = in.readInt();
        room.priceAverage = in.readInt();
        room.bedCount = in.readInt();
        room.benefit = in.readString();
        room.checkInTime = in.readString();
        room.checkOutTime = in.readString();
        room.type = in.readString();
        room.amenities = in.createStringArrayList();
        room.descriptions = in.createStringArrayList();
        room.needToKnows = in.createStringArrayList();
        room.squareMeter = in.readFloat();
        room.hasUsableCoupon = in.readInt() == 1;
        room.provideRewardSticker = in.readInt() == 1;

        boolean hasBeTypeList = in.readInt() == 1;
        if (hasBeTypeList)
        {
            int bedTypeListSize = in.readInt();
            List<Room.BedType> bedTypeList = new ArrayList<>();

            for (int i = 0; i < bedTypeListSize; i++)
            {
                Room.BedType bedType = new Room.BedType();
                bedType.bedType = in.readString();
                bedType.count = in.readInt();

                bedTypeList.add(bedType);
            }
        }

        boolean hasAttribute = in.readInt() == 1;
        if (hasAttribute)
        {
            Room.Attribute attribute = new Room.Attribute();
            attribute.roomStructure = in.readString();
            attribute.isEntireHouse = in.readInt() == 1;
            attribute.isDuplex = in.readInt() == 1;
        }

        boolean hasImage = in.readInt() == 1;
        if (hasImage)
        {
            DetailImageInformation detailImageInformation = new DetailImageInformation();
            detailImageInformation.caption = in.readString();

            ImageMap imageMap = new ImageMap();
            imageMap.smallUrl = in.readString();
            imageMap.mediumUrl = in.readString();
            imageMap.bigUrl = in.readString();

            room.image = detailImageInformation;
        }

        boolean hasPersons = in.readInt() == 1;
        if (hasPersons)
        {
            Room.Persons persons = new Room.Persons();
            persons.fixed = in.readInt();
            persons.extra = in.readInt();
            persons.extraCharge = in.readInt() == 1;
            persons.breakfast = in.readInt();

            room.persons = persons;
        }

        boolean hasRoomCharge = in.readInt() == 1;
        if (hasRoomCharge)
        {
            Room.Charge charge = new Room.Charge();

            boolean hasRoomChargeConsecutive = in.readInt() == 1;
            if (hasRoomChargeConsecutive)
            {
                Room.Charge.Consecutive consecutive = new Room.Charge.Consecutive();
                consecutive.charge = in.readInt();
                consecutive.enable = in.readInt() == 1;

                charge.consecutive = consecutive;
            }

            boolean hasRoomChargeExtra = in.readInt() == 1;
            if (hasRoomChargeExtra)
            {
                Room.Charge.Extra extra = new Room.Charge.Extra();
                extra.descriptions = in.readString();
                extra.extraBed = in.readInt();
                extra.extraBedEnable = in.readInt() == 1;
                extra.extraBedding = in.readInt();
                extra.extraBeddingEnable = in.readInt();

                charge.extra = extra;

            }

            boolean hasRoomChargePersons = in.readInt() == 1;
            if (hasRoomChargePersons)
            {
                Room.Persons persons = new Room.Persons();
                persons.fixed = in.readInt();
                persons.extra = in.readInt();
                persons.extraCharge = in.readInt() == 1;
                persons.breakfast = in.readInt();

                charge.persons = persons;
            }

            room.charge = charge;
        }

        boolean hasVRInformation = in.readInt() == 1;
        if (hasVRInformation)
        {
            int vrListSize = in.readInt();
            List<StayDetailk.VRInformation> vrInformationList = new ArrayList<>();

            for (int i = 0; i < vrListSize; i++)
            {
                StayDetailk.VRInformation vrInformation = new StayDetailk.VRInformation();
                vrInformation.setName(in.readString());
                vrInformation.setType(in.readString());
                vrInformation.setTypeIndex(in.readInt());
                vrInformation.setUrl(in.readString());

                vrInformationList.add(vrInformation);
            }

            room.vrInformationList = vrInformationList;
        }

        boolean hasRefundInformation = in.readInt() == 1;
        if (hasRefundInformation)
        {
            StayDetailk.RefundInformation refundInformation = new StayDetailk.RefundInformation();
            refundInformation.setTitle(in.readString());
            refundInformation.setType(in.readString());
            refundInformation.setContentList(in.createStringArrayList());
            refundInformation.setWarningMessage(in.readString());
        }
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
