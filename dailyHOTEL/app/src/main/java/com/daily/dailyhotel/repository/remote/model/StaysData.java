package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Configurations;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayCategory;
import com.daily.dailyhotel.entity.Stays;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 6. 15..
 */
@JsonObject
public class StaysData
{
    @JsonField(name = "categories")
    public List<StayCategoryData> stayCategoryDataList;

    @JsonField(name = "hotelSales")
    public List<StayData> stayDataList;

    @JsonField(name = "hotelSalesCount")
    public int hotelSalesCount;

    @JsonField(name = "imgUrl")
    public String imageUrl;

    @JsonField(name = "searchMaxCount")
    public int searchMaxCount;

    @JsonField(name = "stays")
    public int stays;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public Stays getStays()
    {
        Stays stays = new Stays();

        stays.totalCount = hotelSalesCount;
        stays.searchMaxCount = searchMaxCount;

        if (stayCategoryDataList != null || stayCategoryDataList.size() > 0)
        {
            List<StayCategory> stayCategoryList = new ArrayList<>();

            for (StayCategoryData stayCategoryData : stayCategoryDataList)
            {
                stayCategoryList.add(stayCategoryData.getStayCategory());
            }

            stays.setStayCategoryList(stayCategoryList);
        }

        if (stayDataList != null || stayDataList.size() > 0)
        {
            List<Stay> stayList = new ArrayList<>();

            for (StayData stayData : stayDataList)
            {
                stayList.add(stayData.getStay(imageUrl));
            }

            stays.setStayList(stayList);
        }

        if (configurations != null)
        {
            stays.activeReward = configurations.activeReward;
        }

        return stays;
    }

    @JsonObject
    class StayCategoryData
    {
        @JsonField(name = "alias")
        public String alias;

        @JsonField(name = "count")
        public int count;

        @JsonField(name = "name")
        public String name;

        public StayCategory getStayCategory()
        {
            StayCategory stayCategory = new StayCategory();

            stayCategory.code = alias;
            stayCategory.name = name;
            stayCategory.count = count;

            return stayCategory;
        }
    }

    @JsonObject
    class StayData
    {
        @JsonField(name = "hotelIdx")
        public int index;

        @JsonField(name = "displayText")
        public String displayText;

        @JsonField(name = "grade")
        public String grade;

        @JsonField(name = "name")
        public String name;

        @JsonField(name = "addrSummary")
        public String addrSummary;

        @JsonField(name = "truevr")
        public boolean isTrueVr;

        @JsonField(name = "roomIdx")
        public int roomIndex;

        @JsonField(name = "discount")
        public int discount;

        @JsonField(name = "price")
        public int price;

        @JsonField(name = "imgPathMain")
        public Map<String, Object> imgPathMain;

        @JsonField(name = "latitude")
        public double latitude;

        @JsonField(name = "longitude")
        public double longitude;

        @JsonField(name = "regionName")
        public String regionName;

        @JsonField(name = "districtName")
        public String districtName;

        @JsonField(name = "isDailyChoice")
        public boolean isDailyChoice;

        @JsonField(name = "sday")
        public String sday;

        @JsonField(name = "rating")
        public int rating;

        @JsonField(name = "benefit")
        public String benefit;

        @JsonField(name = "distance")
        public int distance;

        @JsonField(name = "overseas")
        public boolean overseas;

        @JsonField(name = "isSoldOut")
        public boolean isSoldOut;

        @JsonField(name = "category")
        public String categoryCode;

        @JsonField(name = "availableRooms")
        public int availableRooms;

        @JsonField(name = "reviewCount")
        public int reviewCount;

        @JsonField(name = "discountRate")
        public int discountRate;

        @JsonField(name = "newItem")
        public boolean newItem;

        @JsonField(name = "myWish")
        public boolean myWish;

        @JsonField(name = "couponDiscountText")
        public String couponDiscountText;

        @JsonField(name = "provideRewardSticker")
        public boolean provideRewardSticker;

        public Stay getStay(String imageUrl)
        {
            Stay stay = new Stay();

            stay.index = index;
            stay.imageUrl = imageUrl + getImagePath(imgPathMain);
            stay.name = name;
            stay.price = price;
            stay.discountPrice = discount;

            // 인트라넷에서 값을 잘못 넣는 경우가 있다.
            if (DailyTextUtils.isTextEmpty(addrSummary) == false)
            {
                if (addrSummary.indexOf('|') >= 0)
                {
                    addrSummary = addrSummary.replace(" | ", "ㅣ");
                } else if (addrSummary.indexOf('l') >= 0)
                {
                    addrSummary = addrSummary.replace(" l ", "ㅣ");
                }
            }

            stay.addressSummary = addrSummary;
            stay.latitude = latitude;
            stay.longitude = longitude;
            stay.dailyChoice = isDailyChoice;
            stay.soldOut = isSoldOut;
            stay.satisfaction = rating;
            stay.districtName = districtName;
            stay.trueVR = isTrueVr;
            stay.dBenefitText = benefit;
            stay.distance = distance; // 정렬시에 보여주는 내용
            stay.categoryCode = categoryCode;
            stay.grade = getStayGrade(grade);
            stay.displayText = displayText;
            stay.roomIndex = roomIndex;
            stay.regionName = regionName;
            stay.overseas = overseas;
            stay.availableRooms = availableRooms;
            stay.reviewCount = reviewCount;
            stay.discountRate = discountRate;
            stay.newStay = newItem;
            stay.myWish = myWish;
            stay.couponDiscountText = couponDiscountText;
            stay.provideRewardSticker = provideRewardSticker;

            return stay;
        }

        private Stay.Grade getStayGrade(String grade)
        {
            Stay.Grade stayGrade;
            try
            {
                stayGrade = Stay.Grade.valueOf(grade);
            } catch (Exception e)
            {
                stayGrade = Stay.Grade.etc;
            }

            return stayGrade;
        }

        private String getImagePath(Map<String, Object> imgPathMain)
        {
            if (imgPathMain == null || imgPathMain.size() == 0)
            {
                return null;
            }

            Iterator<Map.Entry<String, Object>> iterator = imgPathMain.entrySet().iterator();

            if (iterator == null)
            {
                return null;
            }

            String imagePath = null;

            while (iterator.hasNext())
            {
                Map.Entry<String, Object> entry = iterator.next();

                Object value = entry.getValue();

                if (value != null && value instanceof List)
                {
                    List list = ((List) value);

                    if (list.size() > 0)
                    {
                        imagePath = entry.getKey() + ((List) value).get(0);
                        break;
                    }
                }
            }

            return imagePath;
        }
    }
}
