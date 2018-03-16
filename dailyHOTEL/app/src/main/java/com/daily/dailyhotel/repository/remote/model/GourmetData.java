package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.entity.Stay;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 6. 15..
 */
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

        return null;
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
