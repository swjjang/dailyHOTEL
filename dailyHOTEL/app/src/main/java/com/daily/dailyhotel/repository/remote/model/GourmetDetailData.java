package com.daily.dailyhotel.repository.remote.model;

import android.net.Uri;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.PlaceDetailProvince;
import com.daily.dailyhotel.entity.Sticker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

@JsonObject
public class GourmetDetailData
{
    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "address")
    public String address;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "categorySub")
    public String categorySub;

    @JsonField(name = "imgPath")
    public LinkedHashMap<String, List<ImageInformationData>> imgPath;

    @JsonField(name = "price")
    public int price;

    @JsonField(name = "discount")
    public int discount;

    @JsonField(name = "ratingPersons")
    public int ratingPersons;

    @JsonField(name = "ratingValue")
    public int ratingValue;

    @JsonField(name = "ratingShow")
    public boolean ratingShow;

    @JsonField(name = "parking")
    public boolean parking;

    @JsonField(name = "valet")
    public boolean valet;

    @JsonField(name = "babySeat")
    public boolean babySeat;

    @JsonField(name = "privateRoom")
    public boolean privateRoom;

    @JsonField(name = "groupBooking")
    public boolean groupBooking;

    @JsonField(name = "corkage")
    public boolean corkage;

    @JsonField(name = "tickets")
    public List<MenuData> tickets;

    @JsonField(name = "details")
    public List<LinkedHashMap<String, List<String>>> details;

    @JsonField(name = "imgUrl")
    public String imgUrl;

    @JsonField(name = "benefit")
    public String benefit;

    @JsonField(name = "benefitContents")
    public List<String> benefitContents;

    @JsonField(name = "wishCount")
    public int wishCount;

    @JsonField(name = "myWish")
    public boolean myWish;

    @JsonField(name = "sticker")
    public StickerData sticker;

    @JsonField(name = "couponDiscount")
    public int couponDiscount;

    @JsonField(name = "awards")
    public TrueAwardsData awards;

    @JsonField(name = "province")
    public ProvinceData province;

    public GourmetDetailData()
    {

    }

    public GourmetDetail getGourmetDetail()
    {
        GourmetDetail gourmetDetail = new GourmetDetail();

        gourmetDetail.index = index;
        gourmetDetail.name = name;
        gourmetDetail.latitude = latitude;
        gourmetDetail.longitude = longitude;
        gourmetDetail.address = address;
        gourmetDetail.category = category;
        gourmetDetail.categorySub = categorySub;
        gourmetDetail.price = price;
        gourmetDetail.discount = discount;
        gourmetDetail.ratingPersons = ratingPersons;
        gourmetDetail.ratingValue = ratingValue;
        gourmetDetail.ratingShow = ratingShow;
        gourmetDetail.benefit = benefit;
        gourmetDetail.setBenefitContentList(benefitContents);
        gourmetDetail.wishCount = wishCount;
        gourmetDetail.myWish = myWish;
        gourmetDetail.couponPrice = couponDiscount;

        if (sticker != null)
        {
            gourmetDetail.setSticker(sticker.getSticker());
        }

        // 픽토그램
        List<GourmetDetail.Pictogram> pictogramList = new ArrayList<>();

        // 주차가능
        if (parking == true)
        {
            pictogramList.add(GourmetDetail.Pictogram.parking);
        }
        // 발렛가능
        if (valet == true)
        {
            pictogramList.add(GourmetDetail.Pictogram.valet);
        }
        // 프라이빗룸
        if (privateRoom == true)
        {
            pictogramList.add(GourmetDetail.Pictogram.privateRoom);
        }
        // 단체예약
        if (groupBooking == true)
        {
            pictogramList.add(GourmetDetail.Pictogram.groupBooking);
        }
        // 베이비시트
        if (babySeat == true)
        {
            pictogramList.add(GourmetDetail.Pictogram.babySeat);
        }
        // 코르키지
        if (corkage == true)
        {
            pictogramList.add(GourmetDetail.Pictogram.corkage);
        }

        gourmetDetail.setPictogramList(pictogramList);

        // 이미지
        List<DetailImageInformation> detailImageInformationList = new ArrayList<>();

        if (imgPath != null && imgPath.size() > 0)
        {
            Iterator<String> keyList = imgPath.keySet().iterator();

            while (keyList.hasNext())
            {
                String key = keyList.next();

                for (ImageInformationData imageInformationData : imgPath.get(key))
                {
                    DetailImageInformation detailImageInformation = new DetailImageInformation();

                    ImageMap imageMap = new ImageMap();
                    imageMap.smallUrl = null;
                    imageMap.mediumUrl = imgUrl + key + imageInformationData.name;
                    imageMap.bigUrl = imgUrl + key + imageInformationData.name;

                    detailImageInformation.caption = imageInformationData.description;
                    detailImageInformation.setImageMap(imageMap);

                    detailImageInformationList.add(detailImageInformation);
                }
            }
        }

        gourmetDetail.setImageInformationList(detailImageInformationList);

        // 메뉴
        List<GourmetMenu> gourmetMenuList = new ArrayList<>();

        for (MenuData menuData : tickets)
        {
            gourmetMenuList.add(menuData.getMenu());
        }

        gourmetDetail.setGourmetMenuList(gourmetMenuList);

        // 상세
        gourmetDetail.setDescriptionList(details);

        if (awards != null)
        {
            gourmetDetail.awards = awards.getTrueAwards();
        }

        if (province != null)
        {
            gourmetDetail.province = province.getProvince();
        }

        return gourmetDetail;
    }

    @JsonObject
    static class ImageInformationData
    {
        @JsonField(name = "description")
        public String description;

        @JsonField(name = "name")
        public String name;

        public ImageInformationData()
        {

        }
    }

    @JsonObject
    static class MenuData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "saleIdx")
        public int saleIdx;

        @JsonField(name = "ticketName")
        public String ticketName;

        @JsonField(name = "price")
        public int price;

        @JsonField(name = "discount")
        public int discountPrice;

        @JsonField(name = "persons")
        public int persons;

        @JsonField(name = "minimumOrderQuantity")
        public int minimumOrderQuantity;

        @JsonField(name = "maximumOrderQuantity")
        public int maximumOrderQuantity;

        @JsonField(name = "availableTicketNumbers")
        public int availableTicketNumbers;

        @JsonField(name = "startEatingTime")
        public String startEatingTime;

        @JsonField(name = "endEatingTime")
        public String endEatingTime;

        @JsonField(name = "timeInterval")
        public int timeInterval;

        @JsonField(name = "openTime")
        public String openTime;

        @JsonField(name = "closeTime")
        public String closeTime;

        @JsonField(name = "lastOrderTime")
        public String lastOrderTime;

        @JsonField(name = "menuSummary")
        public String menuSummary;

        @JsonField(name = "menuDetail")
        public List<String> menuDetail;

        @JsonField(name = "needToKnow")
        public String needToKnow;

        @JsonField(name = "menuBenefit")
        public String menuBenefit;

        @JsonField(name = "readyTime")
        public String readyTime;

        @JsonField(name = "images")
        public List<MenuImageData> images;

        @JsonField(name = "expiryTime")
        public String expiryTime;

        @JsonField(name = "reserveCondition")
        public String reserveCondition;

        @JsonField(name = "bookableEatingTimes")
        public List<String> bookableEatingTimes;

        public MenuData()
        {

        }

        public GourmetMenu getMenu()
        {
            GourmetMenu gourmetMenu = new GourmetMenu();

            gourmetMenu.index = index;
            gourmetMenu.saleIndex = saleIdx;
            gourmetMenu.name = ticketName;
            gourmetMenu.price = price;
            gourmetMenu.discountPrice = discountPrice;
            gourmetMenu.menuBenefit = menuBenefit;
            gourmetMenu.needToKnow = needToKnow;
            gourmetMenu.reserveCondition = reserveCondition;

            if (DailyTextUtils.isTextEmpty(openTime) == false)
            {
                gourmetMenu.openTime = openTime.substring(0, 5); // hh:mm:ss -> hh:mm
            }

            if (DailyTextUtils.isTextEmpty(closeTime) == false)
            {
                gourmetMenu.closeTime = closeTime.substring(0, 5); // hh:mm:ss -> hh:mm
            }

            if (DailyTextUtils.isTextEmpty(lastOrderTime) == false)
            {
                gourmetMenu.lastOrderTime = lastOrderTime.substring(0, 5); // hh:mm:ss -> hh:mm
            }

            gourmetMenu.menuSummary = menuSummary;
            gourmetMenu.persons = persons;
            gourmetMenu.minimumOrderQuantity = minimumOrderQuantity;
            gourmetMenu.maximumOrderQuantity = maximumOrderQuantity;
            gourmetMenu.availableTicketNumbers = availableTicketNumbers;

            if (DailyTextUtils.isTextEmpty(startEatingTime) == false)
            {
                gourmetMenu.startEatingTime = startEatingTime.substring(0, 5); // hh:mm:ss -> hh:mm
            }

            if (DailyTextUtils.isTextEmpty(endEatingTime) == false)
            {
                gourmetMenu.endEatingTime = endEatingTime.substring(0, 5); // hh:mm:ss -> hh:mm
            }

            if (DailyTextUtils.isTextEmpty(readyTime) == false)
            {
                gourmetMenu.readyTime = readyTime.substring(0, 5); // hh:mm:ss -> hh:mm
            }

            if (DailyTextUtils.isTextEmpty(expiryTime) == false)
            {
                gourmetMenu.expiryTime = expiryTime.substring(0, 5); // hh:mm:ss -> hh:mm
            }

            gourmetMenu.timeInterval = timeInterval;

            List<DetailImageInformation> detailImageInformationList = new ArrayList<>();

            if (images != null && images.size() > 0)
            {
                gourmetMenu.baseImageUrl = images.get(0).imageUrl.substring(0, images.get(0).imageUrl.lastIndexOf(Uri.parse(images.get(0).imageUrl).getLastPathSegment()));
            }

            for (MenuImageData menuImageData : images)
            {
                DetailImageInformation detailImageInformation = new DetailImageInformation();

                String fileName = Uri.parse(menuImageData.imageUrl).getLastPathSegment();

                ImageMap imageMap = new ImageMap();
                imageMap.smallUrl = null;
                imageMap.mediumUrl = fileName;
                imageMap.bigUrl = fileName;

                detailImageInformation.caption = menuImageData.imageDescription;
                detailImageInformation.setImageMap(imageMap);

                if (menuImageData.isPrimary == true)
                {
                    detailImageInformationList.add(0, detailImageInformation);
                } else
                {
                    detailImageInformationList.add(detailImageInformation);
                }
            }

            gourmetMenu.setImageList(detailImageInformationList);
            gourmetMenu.setMenuDetailList(menuDetail);
            gourmetMenu.setOperationTimeList(bookableEatingTimes);

            return gourmetMenu;
        }

        @JsonObject
        static class MenuImageData
        {
            @JsonField(name = "idx")
            public int index;

            @JsonField(name = "imageDescription")
            public String imageDescription;

            @JsonField(name = "imageUrl")
            public String imageUrl;

            @JsonField(name = "isPrimary")
            public boolean isPrimary;

            @JsonField(name = "restaurantTicketIdx")
            public int restaurantTicketIndex;

            @JsonField(name = "seq")
            public int seq;
        }
    }

    @JsonObject
    static class StickerData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "defaultImageUrl")
        public String defaultImageUrl;

        @JsonField(name = "lowResolutionImageUrl")
        public String lowResolutionImageUrl;

        public StickerData()
        {

        }

        public Sticker getSticker()
        {
            Sticker sticker = new Sticker();
            sticker.index = index;
            sticker.defaultImageUrl = defaultImageUrl;
            sticker.lowResolutionImageUrl = lowResolutionImageUrl;

            return sticker;
        }
    }

    @JsonObject
    static class ProvinceData
    {
        @JsonField(name = "id")
        public int index;

        @JsonField(name = "name")
        public String name;

        public PlaceDetailProvince getProvince()
        {
            PlaceDetailProvince province = new PlaceDetailProvince();
            province.index = index;
            province.name = name;

            return province;
        }
    }
}
