package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Gourmet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 6. 20..
 */
@JsonObject
public class GourmetSalesData
{
    @JsonField(name = "addrSummary")
    public String addrSummary;

    @JsonField(name = "availableTicketNumbers")
    public int availableTicketNumbers;

    @JsonField(name = "minimumOrderQuantity")
    public int minimumOrderQuantity;

    @JsonField(name = "benefit")
    public String benefit;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "categoryCode")
    public int categoryCode;

    @JsonField(name = "categorySeq")
    public int categorySeq;

    @JsonField(name = "categorySub")
    public String subCategory;

    @JsonField(name = "closeTime")
    public String closeTime;

    @JsonField(name = "discount")
    public int discount;

    @JsonField(name = "distance")
    public int distance;

    @JsonField(name = "districtName")
    public String districtName;

    @JsonField(name = "endEatingTime")
    public String endEatingTime; // ISO-8601 -  "2017-06-20T00:05:37.768Z"

    @JsonField(name = "imgPathMain")
    public Map<String, Object> imgPathMain;

    @JsonField(name = "isDailychoice")
    public boolean isDailychoice;

    @JsonField(name = "isSoldOut")
    public boolean isSoldOut;

    @JsonField(name = "isExpired")
    public boolean isExpired;

    @JsonField(name = "lastOrderTime")
    public String lastOrderTime;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "menuBenefit")
    public String menuBenefit;

    @JsonField(name = "menuDetail")
    public List<String> menuDetail;

    @JsonField(name = "menuSummary")
    public String menuSummary;

    @JsonField(name = "needToKnow")
    public String needToKnow;

    @JsonField(name = "openTime")
    public String openTime;

    @JsonField(name = "persons")
    public int persons;

    @JsonField(name = "price")
    public int price;

    @JsonField(name = "pricePerPerson")
    public int pricePerPerson;

    @JsonField(name = "rating")
    public int rating;

    @JsonField(name = "regionName")
    public String regionName;

    @JsonField(name = "restaurantIdx")
    public int index;

    @JsonField(name = "startEatingTime") // ISO-8601 - "2017-06-20T00:05:37.770Z"
    public String startEatingTime;

    @JsonField(name = "stickerIdx")
    public int stickerIdx;

    @JsonField(name = "ticketIdx")
    public int ticketIdx;

    @JsonField(name = "truevr")
    public boolean isTrueVr;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "restaurantName")
    public String restaurantName;

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

    public Gourmet getGourmet()
    {
        Gourmet gourmet = new Gourmet();
        gourmet.index = index;

        if (DailyTextUtils.isTextEmpty(restaurantName) == false)
        {
            gourmet.name = restaurantName;
        } else if (DailyTextUtils.isTextEmpty(name) == false)
        {
            gourmet.name = name;
        }

        gourmet.price = price;
        gourmet.discountPrice = discount;

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

        gourmet.addressSummary = addrSummary;

        gourmet.grade = Gourmet.Grade.gourmet;
        gourmet.districtName = districtName;
        gourmet.latitude = latitude;
        gourmet.longitude = longitude;
        gourmet.isDailyChoice = isDailychoice;
        gourmet.isSoldOut = isSoldOut;
        gourmet.persons = persons;
        gourmet.category = category;
        gourmet.categoryCode = categoryCode;
        gourmet.categorySequence = categorySeq;
        gourmet.subCategory = subCategory;
        gourmet.satisfaction = rating;
        gourmet.distance = distance;
        gourmet.truevr = isTrueVr;
        gourmet.stickerIndex = stickerIdx;
        //        gourmet.stickerUrl // skip
        gourmet.imageUrl = getImageUrl(imgPathMain);
        gourmet.dBenefitText = benefit;
        gourmet.regionName = regionName;
        gourmet.availableTicketNumbers = availableTicketNumbers;
        gourmet.closeTime = closeTime;
        gourmet.endEatingTime = endEatingTime;
        gourmet.lastOrderTime = lastOrderTime;
        gourmet.menuBenefit = menuBenefit;
        gourmet.setMenuDetail(menuDetail);
        gourmet.menuSummary = menuSummary;
        gourmet.needToKnow = needToKnow;
        gourmet.openTime = openTime;
        gourmet.pricePerPerson = pricePerPerson;
        gourmet.startEatingTime = startEatingTime;
        gourmet.ticketIdx = ticketIdx;
        gourmet.reviewCount = reviewCount;
        gourmet.discountRate = discountRate;
        gourmet.newItem = newItem;
        gourmet.myWish = myWish;
        gourmet.couponDiscountText = couponDiscountText;

        return gourmet;
    }

    public com.daily.dailyhotel.entity.Gourmet getEntityGourmet()
    {
        com.daily.dailyhotel.entity.Gourmet gourmet = new com.daily.dailyhotel.entity.Gourmet();

        gourmet.index = index;

        if (DailyTextUtils.isTextEmpty(restaurantName) == false)
        {
            gourmet.name = restaurantName;
        } else if (DailyTextUtils.isTextEmpty(name) == false)
        {
            gourmet.name = name;
        }

        gourmet.price = price;
        gourmet.discountPrice = discount;

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

        gourmet.addressSummary = addrSummary;
        gourmet.grade = com.daily.dailyhotel.entity.Gourmet.Grade.gourmet;
        gourmet.districtName = districtName;
        gourmet.latitude = latitude;
        gourmet.longitude = longitude;
        gourmet.dailyChoice = isDailychoice;
        gourmet.soldOut = availableTicketNumbers == 0 || availableTicketNumbers < minimumOrderQuantity || isExpired;
        gourmet.persons = persons;
        gourmet.category = category;
        gourmet.subCategory = subCategory;
        gourmet.rating = rating;
        gourmet.distance = distance;
        gourmet.trueVR = isTrueVr;
        gourmet.stickerIndex = stickerIdx;
        gourmet.imageUrl = getImageUrl(imgPathMain);
        gourmet.dBenefitText = benefit;
        gourmet.regionName = regionName;
        gourmet.reviewCount = reviewCount;
        gourmet.discountRate = discountRate;
        gourmet.newItem = newItem;
        gourmet.myWish = myWish;
        gourmet.couponDiscountText = couponDiscountText;

        return gourmet;
    }

    private String getImageUrl(Map<String, Object> imgPathMain)
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

        String imageUrl = null;

        while (iterator.hasNext())
        {
            Map.Entry<String, Object> entry = iterator.next();

            Object value = entry.getValue();

            if (value != null && value instanceof List)
            {
                List list = ((List) value);

                if (list.size() > 0)
                {
                    imageUrl = entry.getKey() + ((List) value).get(0);
                    break;
                }
            }
        }

        return imageUrl;
    }
}
