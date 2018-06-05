package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.Room;
import com.daily.dailyhotel.entity.StayDetail;

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
        dest.writeInt(room.imageCount);

        DetailImageInformationParcel detailImageInformationParcel = room.imageInformation == null ? null : new DetailImageInformationParcel(room.imageInformation);
        dest.writeParcelable(detailImageInformationParcel, flags);

        AmountInformationParcel amountInformationParcel = room.amountInformation == null ? null : new AmountInformationParcel(room.amountInformation);
        dest.writeParcelable(amountInformationParcel, flags);

        PersonsInformationParcel personsInformationParcel = room.personsInformation == null ? null : new PersonsInformationParcel(room.personsInformation);
        dest.writeParcelable(personsInformationParcel, flags);

        dest.writeString(room.benefit);

        BedInformationParcel bedInformationParcel = room.bedInformation == null ? null : new BedInformationParcel(room.bedInformation);
        dest.writeParcelable(bedInformationParcel, flags);

        dest.writeInt(room.hasUsableCoupon ? 1 : 0);
        dest.writeInt(room.provideRewardSticker ? 1 : 0);

        dest.writeStringList(room.amenityList);

        CheckTimeInformationParcel checkTimeInformationParcel = room.checkTimeInformation == null ? null : new CheckTimeInformationParcel(room.checkTimeInformation);
        dest.writeParcelable(checkTimeInformationParcel, flags);

        dest.writeStringList(room.descriptionList);
        dest.writeFloat(room.squareMeter);
        dest.writeStringList(room.needToKnowList);

        ChargeInformationParcel chargeInformationParcel = room.roomChargeInformation == null ? null : new ChargeInformationParcel(room.roomChargeInformation);
        dest.writeParcelable(chargeInformationParcel, flags);

        AttributeInformationParcel attributeInformationParcel = room.attributeInformation == null ? null : new AttributeInformationParcel(room.attributeInformation);
        dest.writeParcelable(attributeInformationParcel, flags);

        List<VrInformationParcel> list = new ArrayList<>();

        if (room.vrInformationList != null)
        {
            for (StayDetail.VRInformation info : room.vrInformationList)
            {
                list.add(new VrInformationParcel(info));
            }
        }

        dest.writeList(list);

        RefundInformationParcel refundInformationParcel = room.refundInformation == null ? null : new RefundInformationParcel(room.refundInformation);
        dest.writeParcelable(refundInformationParcel, flags);
    }

    private void readFromParcel(Parcel in)
    {
        room = new Room();
        room.index = in.readInt();
        room.name = in.readString();
        room.imageCount = in.readInt();

        DetailImageInformationParcel detailImageInformationParcel = in.readParcelable(DetailImageInformationParcel.class.getClassLoader());
        room.imageInformation = detailImageInformationParcel == null ? null : detailImageInformationParcel.getDetailImageInformation();

        AmountInformationParcel amountInformationParcel = in.readParcelable(AmountInformationParcel.class.getClassLoader());
        room.amountInformation = amountInformationParcel == null ? null : amountInformationParcel.getAmountInformation();

        PersonsInformationParcel personsInformationParcel = in.readParcelable(PersonsInformationParcel.class.getClassLoader());
        room.personsInformation = personsInformationParcel == null ? null : personsInformationParcel.getPersonsInformation();

        room.benefit = in.readString();

        BedInformationParcel bedInformationParcel = in.readParcelable(BedInformationParcel.class.getClassLoader());
        room.bedInformation = bedInformationParcel == null ? null : bedInformationParcel.getBedInformation();

        room.hasUsableCoupon = in.readInt() == 1;
        room.provideRewardSticker = in.readInt() == 1;

        room.amenityList = new ArrayList<String>();
        in.readStringList(room.amenityList);

        CheckTimeInformationParcel checkTimeInformationParcel = in.readParcelable(CheckTimeInformationParcel.class.getClassLoader());
        room.checkTimeInformation = checkTimeInformationParcel == null ? null : checkTimeInformationParcel.getCheckTimeInformation();

        room.descriptionList = new ArrayList<String>();
        in.readStringList(room.descriptionList);

        room.squareMeter = in.readFloat();

        room.needToKnowList = new ArrayList<String>();
        in.readStringList(room.needToKnowList);

        ChargeInformationParcel chargeInformationParcel = in.readParcelable(ChargeInformationParcel.class.getClassLoader());
        room.roomChargeInformation = chargeInformationParcel == null ? null : chargeInformationParcel.getChargeInformation();

        AttributeInformationParcel attributeInformationParcel = in.readParcelable(AttributeInformationParcel.class.getClassLoader());
        room.attributeInformation = attributeInformationParcel == null ? null : attributeInformationParcel.getAttributeInformation();

        List<StayDetail.VRInformation> vrInformationList = new ArrayList<>();
        List<VrInformationParcel> vrParcelList = in.readArrayList(BedTypeInformationParcel.class.getClassLoader());
        if (vrParcelList != null)
        {
            for (VrInformationParcel parcel : vrParcelList)
            {
                vrInformationList.add(parcel.getVrInformation());
            }
        }
        room.vrInformationList = vrInformationList;

        RefundInformationParcel refundInformationParcel = in.readParcelable(RefundInformationParcel.class.getClassLoader());
        room.refundInformation = refundInformationParcel == null ? null : refundInformationParcel.getRefundInformation();
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

    public static class AmountInformationParcel implements Parcelable
    {
        private Room.AmountInformation amountInformation;

        public AmountInformationParcel(Room.AmountInformation amountInformation)
        {
            if (amountInformation == null)
            {
                throw new NullPointerException("amountInformation == null");
            }

            this.amountInformation = amountInformation;
        }

        protected AmountInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.AmountInformation getAmountInformation()
        {
            return amountInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(amountInformation.discountAverage);
            dest.writeInt(amountInformation.discountRate);
            dest.writeInt(amountInformation.discountTotal);
            dest.writeInt(amountInformation.priceAverage);
        }

        private void readFromParcel(Parcel in)
        {
            amountInformation = new Room.AmountInformation();
            amountInformation.discountAverage = in.readInt();
            amountInformation.discountRate = in.readInt();
            amountInformation.discountTotal = in.readInt();
            amountInformation.priceAverage = in.readInt();
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<AmountInformationParcel> CREATOR = new Creator<AmountInformationParcel>()
        {
            @Override
            public AmountInformationParcel createFromParcel(Parcel in)
            {
                return new AmountInformationParcel(in);
            }

            @Override
            public AmountInformationParcel[] newArray(int size)
            {
                return new AmountInformationParcel[size];
            }
        };
    }

    public static class PersonsInformationParcel implements Parcelable
    {
        private Room.PersonsInformation personsInformation;

        public PersonsInformationParcel(Room.PersonsInformation personsInformation)
        {
            if (personsInformation == null)
            {
                throw new NullPointerException("personsInformation == null");
            }

            this.personsInformation = personsInformation;
        }

        protected PersonsInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.PersonsInformation getPersonsInformation()
        {
            return personsInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(personsInformation.fixed);
            dest.writeInt(personsInformation.extra);
            dest.writeInt(personsInformation.extraCharge ? 1 : 0);
            dest.writeInt(personsInformation.breakfast);
        }

        private void readFromParcel(Parcel in)
        {
            personsInformation = new Room.PersonsInformation();
            personsInformation.fixed = in.readInt();
            personsInformation.extra = in.readInt();
            personsInformation.extraCharge = in.readInt() == 1;
            personsInformation.breakfast = in.readInt();
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<PersonsInformationParcel> CREATOR = new Creator<PersonsInformationParcel>()
        {
            @Override
            public PersonsInformationParcel createFromParcel(Parcel in)
            {
                return new PersonsInformationParcel(in);
            }

            @Override
            public PersonsInformationParcel[] newArray(int size)
            {
                return new PersonsInformationParcel[size];
            }
        };
    }

    public static class BedInformationParcel implements Parcelable
    {
        private Room.BedInformation bedInformation;

        public BedInformationParcel(Room.BedInformation bedInformation)
        {
            if (bedInformation == null)
            {
                throw new NullPointerException("bedInformation == null");
            }

            this.bedInformation = bedInformation;
        }

        protected BedInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.BedInformation getBedInformation()
        {
            return bedInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            List<BedTypeInformationParcel> list = new ArrayList<>();

            if (bedInformation.bedTypeList != null)
            {
                for (Room.BedInformation.BedTypeInformation info : bedInformation.bedTypeList)
                {
                    list.add(new BedTypeInformationParcel(info));
                }
            }

            dest.writeList(list);

            dest.writeStringList(bedInformation.filterList);
        }

        private void readFromParcel(Parcel in)
        {
            bedInformation = new Room.BedInformation();

            List<Room.BedInformation.BedTypeInformation> list = new ArrayList<>();
            List<BedTypeInformationParcel> typeParcelList = in.readArrayList(BedTypeInformationParcel.class.getClassLoader());
            if (typeParcelList != null)
            {
                for (BedTypeInformationParcel parcel : typeParcelList)
                {
                    list.add(parcel.getBedTypeInformation());
                }
            }

            bedInformation.bedTypeList = list;

            bedInformation.filterList = new ArrayList<String>();
            in.readStringList(bedInformation.filterList);
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<BedInformationParcel> CREATOR = new Creator<BedInformationParcel>()
        {
            @Override
            public BedInformationParcel createFromParcel(Parcel in)
            {
                return new BedInformationParcel(in);
            }

            @Override
            public BedInformationParcel[] newArray(int size)
            {
                return new BedInformationParcel[size];
            }
        };
    }

    public static class CheckTimeInformationParcel implements Parcelable
    {
        private StayDetail.CheckTimeInformation checkTimeInformation;

        public CheckTimeInformationParcel(StayDetail.CheckTimeInformation checkTimeInformation)
        {
            if (checkTimeInformation == null)
            {
                throw new NullPointerException("checkTimeInformation == null");
            }

            this.checkTimeInformation = checkTimeInformation;
        }

        protected CheckTimeInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public StayDetail.CheckTimeInformation getCheckTimeInformation()
        {
            return checkTimeInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(checkTimeInformation.getCheckIn());
            dest.writeString(checkTimeInformation.getCheckOut());
            dest.writeStringList(checkTimeInformation.getDescription());
        }

        private void readFromParcel(Parcel in)
        {
            checkTimeInformation = new StayDetail.CheckTimeInformation();
            checkTimeInformation.setCheckIn(in.readString());
            checkTimeInformation.setCheckOut(in.readString());

            List<String> descriptionList = new ArrayList<String>();
            in.readStringList(descriptionList);

            checkTimeInformation.setDescription(descriptionList);
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<CheckTimeInformationParcel> CREATOR = new Creator<CheckTimeInformationParcel>()
        {
            @Override
            public CheckTimeInformationParcel createFromParcel(Parcel in)
            {
                return new CheckTimeInformationParcel(in);
            }

            @Override
            public CheckTimeInformationParcel[] newArray(int size)
            {
                return new CheckTimeInformationParcel[size];
            }
        };
    }

    public static class BedTypeInformationParcel implements Parcelable
    {
        private Room.BedInformation.BedTypeInformation bedTypeInformation;

        public BedTypeInformationParcel(Room.BedInformation.BedTypeInformation bedTypeInformation)
        {
            if (bedTypeInformation == null)
            {
                throw new NullPointerException("bedTypeInformation == null");
            }

            this.bedTypeInformation = bedTypeInformation;
        }

        protected BedTypeInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.BedInformation.BedTypeInformation getBedTypeInformation()
        {
            return bedTypeInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(bedTypeInformation.bedType);
            dest.writeInt(bedTypeInformation.count);
        }

        private void readFromParcel(Parcel in)
        {
            bedTypeInformation = new Room.BedInformation.BedTypeInformation();
            bedTypeInformation.bedType = in.readString();
            bedTypeInformation.count = in.readInt();
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<BedTypeInformationParcel> CREATOR = new Creator<BedTypeInformationParcel>()
        {
            @Override
            public BedTypeInformationParcel createFromParcel(Parcel in)
            {
                return new BedTypeInformationParcel(in);
            }

            @Override
            public BedTypeInformationParcel[] newArray(int size)
            {
                return new BedTypeInformationParcel[size];
            }
        };
    }

    public static class ChargeInformationParcel implements Parcelable
    {
        private Room.ChargeInformation chargeInformation;

        public ChargeInformationParcel(Room.ChargeInformation chargeInformation)
        {
            if (chargeInformation == null)
            {
                throw new NullPointerException("chargeInformation == null");
            }

            this.chargeInformation = chargeInformation;
        }

        protected ChargeInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.ChargeInformation getChargeInformation()
        {
            return chargeInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            ConsecutiveInformationParcel consecutiveInformationParcel = chargeInformation.consecutiveInformation == null ? null : new ConsecutiveInformationParcel(chargeInformation.consecutiveInformation);
            ExtraInformationParcel extraInformationParcel = chargeInformation.extraInformation == null ? null : new ExtraInformationParcel(chargeInformation.extraInformation);

            List<ExtraPersonInformationParcel> extraPersonInformationParcelList = new ArrayList<>();
            if (chargeInformation.extraPersonInformationList != null)
            {
                for (Room.ChargeInformation.ExtraPersonInformation info : chargeInformation.extraPersonInformationList)
                {
                    extraPersonInformationParcelList.add(new ExtraPersonInformationParcel(info));
                }
            }

            dest.writeParcelable(consecutiveInformationParcel, flags);
            dest.writeParcelable(extraInformationParcel, flags);
            dest.writeList(extraPersonInformationParcelList);
            dest.writeStringList(chargeInformation.descriptionList);
        }

        private void readFromParcel(Parcel in)
        {
            chargeInformation = new Room.ChargeInformation();

            ConsecutiveInformationParcel consecutiveInformationParcel = in.readParcelable(ConsecutiveInformationParcel.class.getClassLoader());
            ExtraInformationParcel extraInformationParcel = in.readParcelable(ExtraInformationParcel.class.getClassLoader());

            List<Room.ChargeInformation.ExtraPersonInformation> list = new ArrayList<>();
            List<ExtraPersonInformationParcel> parcelList = in.readArrayList(ExtraPersonInformationParcel.class.getClassLoader());
            if (parcelList != null)
            {
                for (ExtraPersonInformationParcel parcel : parcelList)
                {
                    list.add(parcel.getExtraPersonInformation());
                }
            }

            chargeInformation.consecutiveInformation = consecutiveInformationParcel != null ? consecutiveInformationParcel.getConsecutiveInformation() : null;
            chargeInformation.extraInformation = extraInformationParcel != null ? extraInformationParcel.getExtraInformation() : null;
            chargeInformation.extraPersonInformationList = list;

            chargeInformation.descriptionList = new ArrayList<String>();
            in.readStringList(chargeInformation.descriptionList);
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<ChargeInformationParcel> CREATOR = new Creator<ChargeInformationParcel>()
        {
            @Override
            public ChargeInformationParcel createFromParcel(Parcel in)
            {
                return new ChargeInformationParcel(in);
            }

            @Override
            public ChargeInformationParcel[] newArray(int size)
            {
                return new ChargeInformationParcel[size];
            }
        };
    }

    public static class ConsecutiveInformationParcel implements Parcelable
    {
        private Room.ChargeInformation.ConsecutiveInformation consecutiveInformation;

        public ConsecutiveInformationParcel(Room.ChargeInformation.ConsecutiveInformation consecutiveInformation)
        {
            if (consecutiveInformation == null)
            {
                throw new NullPointerException("consecutiveInformation == null");
            }

            this.consecutiveInformation = consecutiveInformation;
        }

        protected ConsecutiveInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.ChargeInformation.ConsecutiveInformation getConsecutiveInformation()
        {
            return consecutiveInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(consecutiveInformation.charge);
            dest.writeInt(consecutiveInformation.enable ? 1 : 0);
        }

        private void readFromParcel(Parcel in)
        {
            consecutiveInformation = new Room.ChargeInformation.ConsecutiveInformation();
            consecutiveInformation.charge = in.readInt();
            consecutiveInformation.enable = in.readInt() == 1;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<ConsecutiveInformationParcel> CREATOR = new Creator<ConsecutiveInformationParcel>()
        {
            @Override
            public ConsecutiveInformationParcel createFromParcel(Parcel in)
            {
                return new ConsecutiveInformationParcel(in);
            }

            @Override
            public ConsecutiveInformationParcel[] newArray(int size)
            {
                return new ConsecutiveInformationParcel[size];
            }
        };
    }

    public static class ExtraPersonInformationParcel implements Parcelable
    {
        private Room.ChargeInformation.ExtraPersonInformation extraPersonInformation;

        public ExtraPersonInformationParcel(Room.ChargeInformation.ExtraPersonInformation extraPersonInformation)
        {
            if (extraPersonInformation == null)
            {
                throw new NullPointerException("extraPersonInformation == null");
            }

            this.extraPersonInformation = extraPersonInformation;
        }

        protected ExtraPersonInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.ChargeInformation.ExtraPersonInformation getExtraPersonInformation()
        {
            return extraPersonInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(extraPersonInformation.minAge);
            dest.writeInt(extraPersonInformation.maxAge);
            dest.writeString(extraPersonInformation.title);
            dest.writeInt(extraPersonInformation.amount);
            dest.writeInt(extraPersonInformation.maxPersons);
        }

        private void readFromParcel(Parcel in)
        {
            extraPersonInformation = new Room.ChargeInformation.ExtraPersonInformation();
            extraPersonInformation.minAge = in.readInt();
            extraPersonInformation.maxAge = in.readInt();
            extraPersonInformation.title = in.readString();
            extraPersonInformation.amount = in.readInt();
            extraPersonInformation.maxPersons = in.readInt();
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<ExtraPersonInformationParcel> CREATOR = new Creator<ExtraPersonInformationParcel>()
        {
            @Override
            public ExtraPersonInformationParcel createFromParcel(Parcel in)
            {
                return new ExtraPersonInformationParcel(in);
            }

            @Override
            public ExtraPersonInformationParcel[] newArray(int size)
            {
                return new ExtraPersonInformationParcel[size];
            }
        };
    }

    public static class ExtraInformationParcel implements Parcelable
    {
        private Room.ChargeInformation.ExtraInformation extraInformation;

        public ExtraInformationParcel(Room.ChargeInformation.ExtraInformation extraInformation)
        {
            if (extraInformation == null)
            {
                throw new NullPointerException("extraInformation == null");
            }

            this.extraInformation = extraInformation;
        }

        protected ExtraInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.ChargeInformation.ExtraInformation getExtraInformation()
        {
            return extraInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(extraInformation.extraBed);
            dest.writeInt(extraInformation.extraBedEnable ? 1 : 0);
            dest.writeInt(extraInformation.extraBedding);
            dest.writeInt(extraInformation.extraBeddingEnable ? 1 : 0);
        }

        private void readFromParcel(Parcel in)
        {
            extraInformation = new Room.ChargeInformation.ExtraInformation();

            extraInformation.extraBed = in.readInt();
            extraInformation.extraBedEnable = in.readInt() == 1;
            extraInformation.extraBedding = in.readInt();
            extraInformation.extraBeddingEnable = in.readInt() == 1;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<ExtraInformationParcel> CREATOR = new Creator<ExtraInformationParcel>()
        {
            @Override
            public ExtraInformationParcel createFromParcel(Parcel in)
            {
                return new ExtraInformationParcel(in);
            }

            @Override
            public ExtraInformationParcel[] newArray(int size)
            {
                return new ExtraInformationParcel[size];
            }
        };
    }

    public static class AttributeInformationParcel implements Parcelable
    {
        private Room.AttributeInformation attributeInformation;

        public AttributeInformationParcel(Room.AttributeInformation attributeInformation)
        {
            if (attributeInformation == null)
            {
                throw new NullPointerException("attributeInformation == null");
            }

            this.attributeInformation = attributeInformation;
        }

        protected AttributeInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.AttributeInformation getAttributeInformation()
        {
            return attributeInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(attributeInformation.isDuplex ? 1 : 0);
            dest.writeInt(attributeInformation.isEntireHouse ? 1 : 0);
            dest.writeString(attributeInformation.roomStructure);

            List<StructureInformationParcel> list = new ArrayList<>();

            if (attributeInformation.structureInformationList != null)
            {
                for (Room.AttributeInformation.StructureInformation info : attributeInformation.structureInformationList)
                {
                    list.add(new StructureInformationParcel(info));
                }
            }

            dest.writeList(list);
        }

        private void readFromParcel(Parcel in)
        {
            attributeInformation = new Room.AttributeInformation();
            attributeInformation.isDuplex = in.readInt() == 1;
            attributeInformation.isEntireHouse = in.readInt() == 1;
            attributeInformation.roomStructure = in.readString();

            List<Room.AttributeInformation.StructureInformation> list = new ArrayList<>();
            List<StructureInformationParcel> parcelList = in.readArrayList(StructureInformationParcel.class.getClassLoader());
            if (parcelList != null)
            {
                for (StructureInformationParcel parcel : parcelList)
                {
                    list.add(parcel.getStructureInformation());
                }
            }

            attributeInformation.structureInformationList = list;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<AttributeInformationParcel> CREATOR = new Creator<AttributeInformationParcel>()
        {
            @Override
            public AttributeInformationParcel createFromParcel(Parcel in)
            {
                return new AttributeInformationParcel(in);
            }

            @Override
            public AttributeInformationParcel[] newArray(int size)
            {
                return new AttributeInformationParcel[size];
            }
        };
    }

    public static class StructureInformationParcel implements Parcelable
    {
        private Room.AttributeInformation.StructureInformation structureInformation;

        public StructureInformationParcel(Room.AttributeInformation.StructureInformation structureInformation)
        {
            if (structureInformation == null)
            {
                throw new NullPointerException("structureInformation == null");
            }

            this.structureInformation = structureInformation;
        }

        protected StructureInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public Room.AttributeInformation.StructureInformation getStructureInformation()
        {
            return structureInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(structureInformation.type);
            dest.writeInt(structureInformation.count);
        }

        private void readFromParcel(Parcel in)
        {
            structureInformation = new Room.AttributeInformation.StructureInformation();
            structureInformation.type = in.readString();
            structureInformation.count = in.readInt();
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<StructureInformationParcel> CREATOR = new Creator<StructureInformationParcel>()
        {
            @Override
            public StructureInformationParcel createFromParcel(Parcel in)
            {
                return new StructureInformationParcel(in);
            }

            @Override
            public StructureInformationParcel[] newArray(int size)
            {
                return new StructureInformationParcel[size];
            }
        };
    }

    public static class VrInformationParcel implements Parcelable
    {
        private StayDetail.VRInformation vrInformation;

        public VrInformationParcel(StayDetail.VRInformation vrInformation)
        {
            if (vrInformation == null)
            {
                throw new NullPointerException("vrInformation == null");
            }

            this.vrInformation = vrInformation;
        }

        protected VrInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public StayDetail.VRInformation getVrInformation()
        {
            return vrInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(vrInformation.getName());
            dest.writeString(vrInformation.getType());
            dest.writeInt(vrInformation.getTypeIndex());
            dest.writeString(vrInformation.getUrl());
        }

        private void readFromParcel(Parcel in)
        {
            vrInformation = new StayDetail.VRInformation();
            vrInformation.setName(in.readString());
            vrInformation.setType(in.readString());
            vrInformation.setTypeIndex(in.readInt());
            vrInformation.setUrl(in.readString());
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<VrInformationParcel> CREATOR = new Creator<VrInformationParcel>()
        {
            @Override
            public VrInformationParcel createFromParcel(Parcel in)
            {
                return new VrInformationParcel(in);
            }

            @Override
            public VrInformationParcel[] newArray(int size)
            {
                return new VrInformationParcel[size];
            }
        };
    }

    public static class RefundInformationParcel implements Parcelable
    {
        private StayDetail.RefundInformation refundInformation;

        public RefundInformationParcel(StayDetail.RefundInformation refundInformation)
        {
            if (refundInformation == null)
            {
                throw new NullPointerException("refundInformation == null");
            }

            this.refundInformation = refundInformation;
        }

        protected RefundInformationParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public StayDetail.RefundInformation getRefundInformation()
        {
            return refundInformation;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(refundInformation.getTitle());
            dest.writeString(refundInformation.getType());
            dest.writeStringList(refundInformation.getContentList());
            dest.writeString(refundInformation.getWarningMessage());
            dest.writeString(refundInformation.getPolicy());
        }

        private void readFromParcel(Parcel in)
        {
            refundInformation = new StayDetail.RefundInformation();
            refundInformation.setTitle(in.readString());
            refundInformation.setType(in.readString());

            List<String> contentList = new ArrayList<String>();
            in.readStringList(contentList);
            refundInformation.setContentList(contentList);
            refundInformation.setWarningMessage(in.readString());
            refundInformation.setPolicy(in.readString());
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<RefundInformationParcel> CREATOR = new Creator<RefundInformationParcel>()
        {
            @Override
            public RefundInformationParcel createFromParcel(Parcel in)
            {
                return new RefundInformationParcel(in);
            }

            @Override
            public RefundInformationParcel[] newArray(int size)
            {
                return new RefundInformationParcel[size];
            }
        };
    }
}
