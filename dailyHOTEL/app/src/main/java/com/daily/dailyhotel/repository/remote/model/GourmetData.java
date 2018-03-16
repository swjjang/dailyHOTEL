package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.entity.Sticker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@JsonObject
public class GourmetData
{
    @JsonField(name = "restaurantIdx")
    public int index;

    @JsonField(name = "regionName")
    public String regionName;

    @JsonField(name = "districtName")
    public String districtName;

    @JsonField(name = "imgPathMain")
    public Map<String, Object> imgPathMain;

    @JsonField(name = "availableTicketNumbers")
    public int availableTicketNumbers;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "categorySub")
    public String categorySub;

    @JsonField(name = "truevr")
    public boolean truevr;

    @JsonField(name = "stickerIdx")
    public int stickerIdx;

    @JsonField(name = "rating")
    public int rating;

    @JsonField(name = "discount")
    public int discount;

    @JsonField(name = "price")
    public int price;

    @JsonField(name = "isDailyChoice")
    public boolean isDailyChoice;

    @JsonField(name = "isExpired")
    public boolean isExpired;

    @JsonField(name = "minimumOrderQuantity")
    public int minimumOrderQuantity;

    @JsonField(name = "persons")
    public int persons;

    @JsonField(name = "isSoldOut")
    public boolean isSoldOut;

    @JsonField(name = "distance")
    public int distance;

    @JsonField(name = "addrSummary")
    public String addrSummary;

    @JsonField(name = "benefit")
    public String benefit;

    @JsonField(name = "reviewCount")
    public int reviewCount;

    @JsonField(name = "discountRate")
    public int discountRate;

    @JsonField(name = "newItem")
    public boolean newItem;

    @JsonField(name = "couponDiscountText")
    public String couponDiscountText;

    @JsonField(name = "myWish")
    public boolean myWish;

    public Gourmet getGourmet(String imageUrl)
    {
        Gourmet gourmet = new Gourmet();
        gourmet.index = index;
        gourmet.imageUrl = imageUrl + getImagePath(imgPathMain);
        gourmet.name = name;
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
        gourmet.latitude = latitude;
        gourmet.longitude = longitude;
        gourmet.dailyChoice = isDailyChoice;
        gourmet.soldOut = availableTicketNumbers == 0 || availableTicketNumbers < minimumOrderQuantity || isExpired;
        gourmet.rating = rating;
        gourmet.districtName = districtName;
//        gourmet.entryPosition;
        gourmet.trueVR = truevr;
//        gourmet.stickerUrl;
        gourmet.stickerIndex = stickerIdx;
        gourmet.reviewCount = reviewCount;
        gourmet.discountRate = discountRate;
        gourmet.newItem = newItem;
        gourmet.myWish = myWish;
        gourmet.couponDiscountText = couponDiscountText;
        gourmet.dBenefitText = benefit;
        gourmet.distance = distance;
        gourmet.category = category;
        gourmet.subCategory = categorySub;
        gourmet.persons = persons;
        gourmet.grade =com.daily.dailyhotel.entity.Gourmet.Grade.gourmet;
        gourmet.regionName = regionName;
//        gourmet.createdWishDateTime; // ISO-8601 위시 등록 시간

        return gourmet;
    }

    private String getImagePath(Map<String, Object> lowDpi)
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
