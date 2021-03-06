package com.daily.dailyhotel.entity;

import java.util.List;

public class Room
{
    //    roomIdx (integer, optional): 객실 Index ,
    public int index;

    // roomName
    public String name;

    public int imageCount; // imageInformation 에 있는 room 의 이미지 개수

    public DetailImageInformation imageInformation;

    public AmountInformation amountInformation;

    public PersonsInformation personsInformation;

    public String benefit;

    public int bedCount;

    public BedInformation bedInformation;

    public boolean hasUsableCoupon;

    public boolean provideRewardSticker;

    public List<String> amenityList;

    public StayDetail.CheckTimeInformation checkTimeInformation;

    public List<String> descriptionList;

    public float squareMeter;

    public List<String> needToKnowList;

    public ChargeInformation roomChargeInformation;

    public AttributeInformation attributeInformation;

    public List<StayDetail.VRInformation> vrInformationList;

    public StayDetail.RefundInformation refundInformation;

    public static class AmountInformation
    {
        public int discountAverage;

        public int discountRate;

        public int discountTotal;

        public int priceAverage;
    }

    public static class PersonsInformation
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

    public static class BedInformation
    {
        public List<BedTypeInformation> bedTypeList;

        public List<String> filterList;

        public static class BedTypeInformation
        {
            public String bedType;

            public int count;
        }
    }

    public static class ChargeInformation
    {
        public ConsecutiveInformation consecutiveInformation;

        public ExtraInformation extraInformation;

        public List<ExtraPersonInformation> extraPersonInformationList;

        public List<String> descriptionList;

        public boolean isAllHidden()
        {
            boolean isEmptyPersonInformationList = false;
            if (extraPersonInformationList == null || extraPersonInformationList.size() == 0)
            {
                isEmptyPersonInformationList = true;
            }

            boolean isEmptyDescriptionList = false;
            if (descriptionList == null || descriptionList.size() == 0)
            {
                isEmptyDescriptionList = true;
            }

            return isEmptyPersonInformationList //
                && (extraInformation == null || extraInformation.isAllHidden()) //
                && (consecutiveInformation == null || !consecutiveInformation.enable) && isEmptyDescriptionList;
        }

        public static class ConsecutiveInformation
        {
            public int charge;
            public boolean enable;
        }

        public static class ExtraPersonInformation
        {
            public int minAge;
            public int maxAge;
            public String title;
            public int amount;
            public int maxPersons;
        }

        public static class ExtraInformation
        {
            public int extraBed;
            public boolean extraBedEnable;
            public int extraBedding;
            public boolean extraBeddingEnable;

            public boolean isAllHidden()
            {
                return !extraBedEnable && !extraBeddingEnable;
            }
        }
    }

    public static class AttributeInformation
    {
        public boolean isDuplex;
        public boolean isEntireHouse;
        public String roomStructure;
        public List<StructureInformation> structureInformationList;

        public static class StructureInformation
        {
            public String type;
            public int count;
        }
    }
}
