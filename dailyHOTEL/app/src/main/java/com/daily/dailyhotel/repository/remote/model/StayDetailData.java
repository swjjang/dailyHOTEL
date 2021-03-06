package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.FacilitiesPictogram;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.Room;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayDetail;
import com.daily.dailyhotel.entity.TrueAwards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@JsonObject
public class StayDetailData
{
    @JsonField(name = "category")
    public String category;

    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "grade")
    public String grade;

    @JsonField(name = "discount")
    public int discount;

    @JsonField(name = "singleStay")
    public boolean singleStay;

    @JsonField(name = "primaryReview")
    public PrimaryReviewData primaryReview;

    @JsonField(name = "provideRewardSticker")
    public boolean provideRewardSticker;

    @JsonField(name = "coupon")
    public CouponData coupon;

    @JsonField(name = "rating")
    public RatingData rating;

    @JsonField(name = "awards")
    public AwardsData awards;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    @JsonField(name = "checkTime")
    public CheckTimeData checkTime;

    @JsonField(name = "address")
    public String address;

    @JsonField(name = "roomCount")
    public int roomCount;

    @JsonField(name = "facilities")
    public List<String> facilities;

    @JsonField(name = "benefit")
    public BenefitData benefit;

    @JsonField(name = "details")
    public List<DetailData> details;

    @JsonField(name = "refundPolicy")
    public RefundPolicyData refundPolicy;

    @JsonField(name = "wishCount")
    public int wishCount;

    @JsonField(name = "myWish")
    public boolean myWish;

    @JsonField(name = "dailyComment")
    public DetailData dailyComment;

    @JsonField(name = "checkList")
    public DetailData checkList;

    @JsonField(name = "waitingForBooking")
    public boolean waitingForBooking;

    @JsonField(name = "breakfast")
    public BreakfastData breakfast;

    @JsonField(name = "location")
    public LocationData location;

    @JsonField(name = "images")
    public List<ImageData> images;

    @JsonField(name = "province")
    public ProvinceData province;

    @JsonField(name = "rooms")
    public List<RoomData> rooms;

    @JsonField(name = "vrs")
    public List<VRData> vrs;

    @JsonField(name = "statistic")
    public ReviewStatisticData statistic;

    @JsonField(name = "rewardCard")
    public RewardCardData rewardCard;

    public StayDetailData()
    {

    }

    public StayDetail getStayDetail()
    {
        StayDetail stayDetail = new StayDetail();

        stayDetail.setIndex(index);
        stayDetail.setWishCount(wishCount);
        stayDetail.setWish(myWish);
        stayDetail.setSingleStay(singleStay);

        if (province != null)
        {
            stayDetail.setProvince(province.getProvince());
        }

        if (configurations != null)
        {
            stayDetail.activeReward = configurations.activeReward;
        }

        if (images != null && images.size() > 0)
        {
            List<DetailImageInformation> detailImageInformationList = new ArrayList<>();

            for (ImageData imageData : images)
            {
                if (imageData.primary)
                {
                    detailImageInformationList.add(0, imageData.getDetailImageInformation());
                } else
                {
                    detailImageInformationList.add(imageData.getDetailImageInformation());
                }
            }

            stayDetail.setImageList(detailImageInformationList);
        }

        if (vrs != null && vrs.size() > 0)
        {
            List<StayDetail.VRInformation> vrInformationList = new ArrayList<>();

            for (VRData vrData : vrs)
            {
                vrInformationList.add(vrData.getVRInformation());
            }

            stayDetail.setVrInformation(vrInformationList);
        }

        StayDetail.BaseInformation baseInformation = new StayDetail.BaseInformation();
        baseInformation.setCategory(category);

        try
        {
            baseInformation.setGrade(Stay.Grade.valueOf(grade));
        } catch (Exception e)
        {
            baseInformation.setGrade(Stay.Grade.etc);
        }

        baseInformation.setProvideRewardSticker(provideRewardSticker && stayDetail.activeReward);
        baseInformation.setName(name);
        baseInformation.setDiscount(discount);

        if (awards != null)
        {
            baseInformation.setAwards(awards.getTrueAwards());
        }

        stayDetail.setBaseInformation(baseInformation);

        if (rating == null && primaryReview == null && (statistic == null || statistic.reviewScoreTotalCount == 0))
        {

        } else
        {
            StayDetail.TrueReviewInformation trueReviewInformation = new StayDetail.TrueReviewInformation();

            if (rating != null)
            {
                trueReviewInformation.setRatingCount(rating.persons);
                trueReviewInformation.setRatingPercent(rating.values);
                trueReviewInformation.setShowRating(rating.show);
            }

            if (primaryReview != null)
            {
                trueReviewInformation.setReview(primaryReview.getPrimaryReview());
            }

            if (statistic != null)
            {
                trueReviewInformation.setReviewTotalCount(statistic.reviewScoreTotalCount);

                if (statistic.reviewScoreAvgs != null && statistic.reviewScoreAvgs.size() > 0)
                {
                    List<StayDetail.TrueReviewInformation.ReviewScore> reviewScoreList = new ArrayList<>();

                    for (ReviewStatisticData.ReviewScoreAvgData reviewScoreAvgData : statistic.reviewScoreAvgs)
                    {
                        reviewScoreList.add(reviewScoreAvgData.getReviewScore());
                    }

                    trueReviewInformation.setReviewScores(reviewScoreList);
                }
            }

            stayDetail.setTrueReviewInformation(trueReviewInformation);
        }

        StayDetail.BenefitInformation benefitInformation = new StayDetail.BenefitInformation();

        if (benefit != null)
        {
            benefitInformation.setTitle(benefit.title);
            benefitInformation.setContentList(benefit.contents);
        }

        if (coupon != null)
        {
            benefitInformation.setCoupon(coupon.getCoupon());
        }

        stayDetail.setBenefitInformation(benefitInformation);

        StayDetail.RoomInformation roomInformation = new StayDetail.RoomInformation();

        if (rooms != null && rooms.size() > 0)
        {
            List<Room> roomList = new ArrayList<>();

            for (RoomData roomData : rooms)
            {
                roomList.add(roomData.getRoom(stayDetail.activeReward));

                if (!stayDetail.getHasNRDRoom() && roomData.refundPolicy != null && "nrd".equalsIgnoreCase(roomData.refundPolicy.policy))
                {
                    stayDetail.setHasNRDRoom(true);
                }
            }

            roomInformation.setRoomList(roomList);

            stayDetail.setRoomInformation(roomInformation);
        }

        if (dailyComment != null && dailyComment.contents != null && dailyComment.contents.size() > 0)
        {
            stayDetail.setDailyCommentList(dailyComment.contents);
        }

        stayDetail.setTotalRoomCount(roomCount);

        if (facilities != null && facilities.size() > 0)
        {
            List<FacilitiesPictogram> facilitiesList = new ArrayList<>();

            for (String facilities : facilities)
            {
                try
                {
                    facilitiesList.add(FacilitiesPictogram.valueOf(facilities.toUpperCase()));
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
            }

            Collections.sort(facilitiesList, new Comparator<FacilitiesPictogram>()
            {
                @Override
                public int compare(FacilitiesPictogram o1, FacilitiesPictogram o2)
                {
                    return o1.ordinal() - o2.ordinal();
                }
            });

            stayDetail.setFacilitiesList(facilitiesList);
        }

        StayDetail.AddressInformation addressInformation = new StayDetail.AddressInformation();
        addressInformation.setAddress(address);

        if (location != null)
        {
            addressInformation.setLatitude(location.latitude);
            addressInformation.setLongitude(location.longitude);
        }

        stayDetail.setAddressInformation(addressInformation);

        if (checkTime != null)
        {
            stayDetail.setCheckTimeInformation(checkTime.getCheckTimeInformation());
        }

        StayDetail.DetailInformation detailInformation = new StayDetail.DetailInformation();

        if (details != null && details.size() > 0)
        {
            List<StayDetail.DetailInformation.Item> itemList = new ArrayList<>();

            for (DetailData detailData : details)
            {
                itemList.add(detailData.getItem());
            }

            detailInformation.setItemList(itemList);
        }

        stayDetail.setDetailInformation(detailInformation);

        if (breakfast != null)
        {
            stayDetail.setBreakfastInformation(breakfast.getBreakfastInformation());
        }

        if (refundPolicy != null)
        {
            stayDetail.setRefundInformation(refundPolicy.getRefundInformation());
        }

        StayDetail.CheckInformation checkInformation = new StayDetail.CheckInformation();

        if (checkList != null && checkList.contents != null && checkList.contents.size() > 0)
        {
            checkInformation.setTitle(checkList.title);
            checkInformation.setContentList(checkList.contents);
        }

        checkInformation.setWaitingForBooking(waitingForBooking);

        stayDetail.setCheckInformation(checkInformation);

        if (rewardCard != null)
        {
            stayDetail.setRewardStickerCount(rewardCard.rewardStickerCount);
        }

        return stayDetail;
    }

    @JsonObject
    static class PrimaryReviewData
    {
        @JsonField(name = "avgScore")
        public float avgScore;

        @JsonField(name = "comment")
        public String comment;

        @JsonField(name = "createdAt")
        public String createdAt;

        @JsonField(name = "userId")
        public String userId;

        StayDetail.TrueReviewInformation.PrimaryReview getPrimaryReview()
        {
            StayDetail.TrueReviewInformation.PrimaryReview primaryReview = new StayDetail.TrueReviewInformation.PrimaryReview();
            primaryReview.setScore(avgScore);
            primaryReview.setComment(comment);
            primaryReview.setUserId(userId);
            primaryReview.setCreatedAt(createdAt);

            return primaryReview;
        }
    }

    @JsonObject
    static class CouponData
    {
        @JsonField(name = "couponDiscount")
        public int couponDiscount;

        @JsonField(name = "isDownloaded")
        public boolean isDownloaded;

        StayDetail.BenefitInformation.Coupon getCoupon()
        {
            StayDetail.BenefitInformation.Coupon coupon = new StayDetail.BenefitInformation.Coupon();
            coupon.setCouponDiscount(couponDiscount);
            coupon.setDownloaded(isDownloaded);

            return coupon;
        }
    }

    @JsonObject
    static class RatingData
    {
        @JsonField(name = "persons")
        public int persons;

        @JsonField(name = "values")
        public int values;

        @JsonField(name = "show")
        public boolean show;
    }

    @JsonObject
    static class AwardsData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "serviceType")
        public String serviceType;

        @JsonField(name = "title")
        public String title;

        @JsonField(name = "description")
        public String description;

        @JsonField(name = "imgUrl")
        public String imageUrl;

        TrueAwards getTrueAwards()
        {
            TrueAwards trueAwards = new TrueAwards();
            trueAwards.index = Integer.toString(index);
            trueAwards.description = description;
            trueAwards.serviceType = serviceType;
            trueAwards.title = title;
            trueAwards.imageUrl = imageUrl;

            return trueAwards;
        }
    }

    @JsonObject
    static class CheckTimeData
    {
        @JsonField(name = "checkIn")
        public String checkIn;

        @JsonField(name = "checkOut")
        public String checkOut;

        @JsonField(name = "descriptions")
        public List<String> descriptions;

        StayDetail.CheckTimeInformation getCheckTimeInformation()
        {
            StayDetail.CheckTimeInformation checkTimeInformation = new StayDetail.CheckTimeInformation();

            try
            {
                checkTimeInformation.setCheckIn(checkIn.substring(0, checkIn.lastIndexOf(':')));
            } catch (Exception e)
            {
                checkTimeInformation.setCheckIn("00:00");
            }

            try
            {
                checkTimeInformation.setCheckOut(checkOut.substring(0, checkIn.lastIndexOf(':')));
            } catch (Exception e)
            {
                checkTimeInformation.setCheckOut("00:00");
            }

            checkTimeInformation.setDescription(descriptions);

            return checkTimeInformation;
        }
    }

    @JsonObject
    static class RefundPolicyData
    {
        @JsonField(name = "title")
        public String title;

        @JsonField(name = "contents")
        public List<String> contents;

        @JsonField(name = "warning")
        public String warning;

        @JsonField(name = "policy")
        public String policy;

        StayDetail.RefundInformation getRefundInformation()
        {
            StayDetail.RefundInformation refundInformation = new StayDetail.RefundInformation();
            refundInformation.setTitle(title);
            refundInformation.setWarningMessage(warning);
            refundInformation.setContentList(contents);
            refundInformation.setContentList(contents);
            refundInformation.setPolicy(policy);

            return refundInformation;
        }
    }

    @JsonObject
    static class BenefitData
    {
        @JsonField(name = "title")
        public String title;

        @JsonField(name = "contents")
        public List<String> contents;
    }

    @JsonObject
    static class DetailData
    {
        @JsonField(name = "title")
        public String title;

        @JsonField(name = "contents")
        public List<String> contents;

        StayDetail.DetailInformation.Item getItem()
        {
            StayDetail.DetailInformation.Item item = new StayDetail.DetailInformation.Item();

            item.setTitle(title);
            item.setContentList(contents);

            return item;
        }
    }

    @JsonObject
    static class BreakfastData
    {
        @JsonField(name = "descriptions")
        public List<String> descriptions;

        @JsonField(name = "items")
        public List<ItemData> items;

        @JsonObject
        static class ItemData
        {
            @JsonField(name = "amount")
            public int amount;

            @JsonField(name = "maxAge")
            public int maxAge;

            @JsonField(name = "maxPersons")
            public int maxPersons;

            @JsonField(name = "minAge")
            public int minAge;

            @JsonField(name = "title")
            public String title;

            StayDetail.BreakfastInformation.Item getItem()
            {
                StayDetail.BreakfastInformation.Item item = new StayDetail.BreakfastInformation.Item();

                item.setAmount(amount);
                item.setMaxAge(maxAge);
                item.setMinAge(minAge);
                item.setMaxPersons(maxPersons);
                item.setTitle(title);

                return item;
            }
        }

        StayDetail.BreakfastInformation getBreakfastInformation()
        {
            StayDetail.BreakfastInformation breakfastInformation = new StayDetail.BreakfastInformation();

            breakfastInformation.setDescriptionList(descriptions);

            if (items != null && items.size() > 0)
            {
                List<StayDetail.BreakfastInformation.Item> itemList = new ArrayList<>();

                for (ItemData itemData : items)
                {
                    itemList.add(itemData.getItem());
                }

                breakfastInformation.setItems(itemList);
            }

            return breakfastInformation;
        }
    }

    @JsonObject
    static class LocationData
    {
        @JsonField(name = "latitude")
        public double latitude;

        @JsonField(name = "longitude")
        public double longitude;
    }

    @JsonObject
    static class ImageData
    {
        @JsonField(name = "count")
        public int count;

        @JsonField(name = "url")
        public String url;

        @JsonField(name = "description")
        public String description; //

        @JsonField(name = "primary")
        public boolean primary;

        DetailImageInformation getDetailImageInformation()
        {
            DetailImageInformation detailImageInformation = new DetailImageInformation();
            detailImageInformation.caption = description;

            ImageMap imageMap = new ImageMap();
            imageMap.smallUrl = imageMap.mediumUrl = imageMap.bigUrl = url;

            detailImageInformation.setImageMap(imageMap);

            return detailImageInformation;
        }
    }

    @JsonObject
    static class ProvinceData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        StayDetail.Province getProvince()
        {
            StayDetail.Province province = new StayDetail.Province();
            province.setIndex(index);
            province.setName(name);

            return province;
        }
    }

    @JsonObject
    static class RoomData
    {
        @JsonField(name = "roomIdx")
        public int roomIdx;

        @JsonField(name = "roomName")
        public String roomName;

        @JsonField(name = "image")
        public ImageData image;

        @JsonField(name = "amount")
        public AmountData amount;

        @JsonField(name = "persons")
        public PersonsData persons;

        @JsonField(name = "benefit")
        public String benefit;

        @JsonField(name = "bedCount")
        public int bedCount;

        @JsonField(name = "bedInfo")
        public BedInfoData bedInfo;

        @JsonField(name = "hasUsableCoupon")
        public boolean hasUsableCoupon;

        @JsonField(name = "provideRewardSticker")
        public boolean provideRewardSticker;

        @JsonField(name = "amenities")
        public List<String> amenities;

        @JsonField(name = "checkTime")
        public CheckTimeData checkTime;

        @JsonField(name = "descriptions")
        public List<String> descriptions;

        @JsonField(name = "squareMeter")
        public float squareMeter;

        @JsonField(name = "needToKnows")
        public List<String> needToKnows;

        @JsonField(name = "roomCharge")
        public RoomChargeData roomCharge;

        @JsonField(name = "attribute")
        public AttributeData attribute;

        @JsonField(name = "vrs")
        public List<VRData> vrs;

        @JsonField(name = "refundPolicy")
        public RefundPolicyData refundPolicy;

        public RoomData()
        {

        }

        List<String> getBedTypeFilter()
        {
            return bedInfo != null ? bedInfo.filters : null;
        }

        Room getRoom(boolean activeReward)
        {
            Room room = new Room();

            room.index = roomIdx;
            room.name = roomName;
            room.bedCount = bedCount;
            room.benefit = benefit;
            room.provideRewardSticker = provideRewardSticker && activeReward;
            room.amenityList = amenities;
            room.descriptionList = descriptions;
            room.squareMeter = squareMeter;
            room.needToKnowList = needToKnows;
            room.hasUsableCoupon = hasUsableCoupon;

            if (amount != null)
            {
                room.amountInformation = amount.getAmount();
            }

            if (bedInfo != null)
            {
                room.bedInformation = bedInfo.getBedInfo();
            }

            if (attribute != null)
            {
                room.attributeInformation = attribute.getAttribute();
            }

            if (image != null)
            {
                room.imageInformation = image.getDetailImageInformation();
                room.imageCount = image.count;
            }

            if (persons != null)
            {
                room.personsInformation = persons.getPerson();
            }

            if (checkTime != null)
            {
                room.checkTimeInformation = checkTime.getCheckTimeInformation();
            }

            if (vrs != null && vrs.size() > 0)
            {
                List<StayDetail.VRInformation> vrInformationList = new ArrayList<>();

                for (VRData vrData : vrs)
                {
                    vrInformationList.add(vrData.getVRInformation());
                }

                room.vrInformationList = vrInformationList;
            }

            if (refundPolicy != null)
            {
                room.refundInformation = refundPolicy.getRefundInformation();
            }

            if (roomCharge != null)
            {
                room.roomChargeInformation = roomCharge.getRoomCharge();
            }

            return room;
        }

        @JsonObject
        static class AmountData
        {
            @JsonField(name = "discountAverage")
            public int discountAverage;

            @JsonField(name = "discountRate")
            public int discountRate;

            @JsonField(name = "discountTotal")
            public int discountTotal;

            @JsonField(name = "priceAverage")
            public int priceAverage;

            Room.AmountInformation getAmount()
            {
                Room.AmountInformation info = new Room.AmountInformation();
                info.discountAverage = discountAverage;
                info.discountRate = discountRate;
                info.discountTotal = discountTotal;
                info.priceAverage = priceAverage;

                return info;
            }
        }

        @JsonObject
        static class PersonsData
        {
            @JsonField(name = "fixed")
            public int fixed;

            @JsonField(name = "extra")
            public int extra;

            @JsonField(name = "extraCharge")
            public boolean extraCharge;

            @JsonField(name = "breakfast")
            public int breakfast;

            Room.PersonsInformation getPerson()
            {
                Room.PersonsInformation person = new Room.PersonsInformation();
                person.fixed = fixed;
                person.extra = extra;
                person.extraCharge = extraCharge;
                person.breakfast = breakfast;

                return person;
            }
        }

        @JsonObject
        static class RoomChargeData
        {
            @JsonField(name = "consecutive")
            public ConsecutiveData consecutive;

            @JsonField(name = "extra")
            public ExtraData extra;

            @JsonField(name = "extraPerson")
            public List<ExtraPersonData> extraPersonList;

            @JsonField(name = "descriptions")
            public List<String> descriptions;

            @JsonObject
            static class ConsecutiveData
            {
                @JsonField(name = "charge")
                public int charge;

                @JsonField(name = "enable")
                public boolean enable;

                Room.ChargeInformation.ConsecutiveInformation getConsecutive()
                {
                    Room.ChargeInformation.ConsecutiveInformation consecutive = new Room.ChargeInformation.ConsecutiveInformation();
                    consecutive.charge = charge;
                    consecutive.enable = enable;

                    return consecutive;
                }
            }

            @JsonObject
            static class ExtraPersonData
            {
                @JsonField(name = "minAge")
                public int minAge;

                @JsonField(name = "maxAge")
                public int maxAge;

                @JsonField(name = "title")
                public String title;

                @JsonField(name = "amount")
                public int amount;

                @JsonField(name = "maxPersons")
                public int maxPersons;

                Room.ChargeInformation.ExtraPersonInformation getExtraPerson()
                {
                    Room.ChargeInformation.ExtraPersonInformation extraPerson = new Room.ChargeInformation.ExtraPersonInformation();
                    extraPerson.minAge = minAge;
                    extraPerson.maxAge = maxAge;
                    extraPerson.title = title;
                    extraPerson.amount = amount;
                    extraPerson.maxPersons = maxPersons;

                    return extraPerson;
                }
            }

            @JsonObject
            static class ExtraData
            {
                @JsonField(name = "extraBed")
                public int extraBed;

                @JsonField(name = "extraBedEnable")
                public boolean extraBedEnable;

                @JsonField(name = "extraBedding")
                public int extraBedding;

                @JsonField(name = "extraBeddingEnable")
                public boolean extraBeddingEnable;

                Room.ChargeInformation.ExtraInformation getExtra()
                {
                    Room.ChargeInformation.ExtraInformation extra = new Room.ChargeInformation.ExtraInformation();
                    extra.extraBed = extraBed;
                    extra.extraBedEnable = extraBedEnable;
                    extra.extraBedding = extraBedding;
                    extra.extraBeddingEnable = extraBeddingEnable;

                    return extra;
                }
            }

            Room.ChargeInformation getRoomCharge()
            {
                Room.ChargeInformation roomCharge = new Room.ChargeInformation();

                if (consecutive != null)
                {
                    roomCharge.consecutiveInformation = this.consecutive.getConsecutive();
                }

                if (extra != null)
                {
                    roomCharge.extraInformation = extra.getExtra();
                }

                if (extraPersonList != null)
                {
                    List<Room.ChargeInformation.ExtraPersonInformation> list = new ArrayList<>();

                    for (ExtraPersonData data : extraPersonList)
                    {
                        list.add(data.getExtraPerson());
                    }

                    roomCharge.extraPersonInformationList = list;
                }

                roomCharge.descriptionList = descriptions;

                return roomCharge;
            }
        }

        @JsonObject
        static class AttributeData
        {
            @JsonField(name = "isDuplex")
            public boolean isDuplex;

            @JsonField(name = "isEntireHouse")
            public boolean isEntireHouse;

            @JsonField(name = "roomStructure")
            public String roomStructure;

            @JsonField(name = "structures")
            public List<StructureInformationData> structures;

            Room.AttributeInformation getAttribute()
            {
                Room.AttributeInformation attribute = new Room.AttributeInformation();

                attribute.isDuplex = isDuplex;
                attribute.isEntireHouse = isEntireHouse;
                attribute.roomStructure = roomStructure;

                List<Room.AttributeInformation.StructureInformation> list = new ArrayList<>();
                for (StructureInformationData data : structures)
                {
                    list.add(data.getStructure());
                }

                attribute.structureInformationList = list;

                return attribute;
            }

            @JsonObject
            static class StructureInformationData
            {
                @JsonField(name = "type")
                public String type;

                @JsonField(name = "count")
                public int count;

                Room.AttributeInformation.StructureInformation getStructure()
                {
                    Room.AttributeInformation.StructureInformation structure = new Room.AttributeInformation.StructureInformation();

                    structure.type = type;
                    structure.count = count;

                    return structure;
                }
            }
        }

        @JsonObject
        static class BedInfoData
        {
            @JsonField(name = "bedTypes")
            public List<BedTypeData> bedTypes;

            @JsonField(name = "filters")
            public List<String> filters;

            Room.BedInformation getBedInfo()
            {
                Room.BedInformation info = new Room.BedInformation();

                List<Room.BedInformation.BedTypeInformation> list = new ArrayList<>();
                for (BedTypeData data : bedTypes)
                {
                    list.add(data.getBedType());
                }

                info.bedTypeList = list;
                info.filterList = filters;

                return info;
            }

            @JsonObject
            static class BedTypeData
            {
                @JsonField(name = "bedType")
                public String bedType;

                @JsonField(name = "count")
                public int count;

                Room.BedInformation.BedTypeInformation getBedType()
                {
                    Room.BedInformation.BedTypeInformation bedType = new Room.BedInformation.BedTypeInformation();

                    bedType.bedType = this.bedType;
                    bedType.count = this.count;

                    return bedType;
                }
            }
        }
    }

    @JsonObject
    static class VRData
    {
        @JsonField(name = "name")
        public String name;

        @JsonField(name = "type")
        public String type;

        @JsonField(name = "typeIdx")
        public int typeIdx;

        @JsonField(name = "url")
        public String url;

        StayDetail.VRInformation getVRInformation()
        {
            StayDetail.VRInformation vrInformation = new StayDetail.VRInformation();

            vrInformation.setName(name);
            vrInformation.setType(type);
            vrInformation.setTypeIndex(typeIdx);
            vrInformation.setUrl(url);

            return vrInformation;
        }
    }

    @JsonObject
    static class ReviewStatisticData
    {
        @JsonField(name = "reviewScoreAvgs")
        public List<ReviewScoreAvgData> reviewScoreAvgs;

        @JsonField(name = "reviewScoreTotalCount")
        public int reviewScoreTotalCount;

        @JsonObject
        static class ReviewScoreAvgData
        {
            @JsonField(name = "type")
            public String type;

            @JsonField(name = "scoreAvg")
            public float scoreAvg;

            StayDetail.TrueReviewInformation.ReviewScore getReviewScore()
            {
                StayDetail.TrueReviewInformation.ReviewScore reviewScore = new StayDetail.TrueReviewInformation.ReviewScore();
                reviewScore.setType(type);
                reviewScore.setAverage(scoreAvg);

                return reviewScore;
            }
        }
    }

    @JsonObject
    static class RewardCardData
    {
        @JsonField(name = "expiredAt")
        public String expiredAt;

        @JsonField(name = "rewardStickerCount")
        public int rewardStickerCount;

        public RewardCardData()
        {

        }
    }
}