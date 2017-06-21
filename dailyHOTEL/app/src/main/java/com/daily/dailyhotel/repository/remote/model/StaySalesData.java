package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.Stay;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 6. 15..
 */
@JsonObject
public class StaySalesData
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
    public int isOverseas;

    @JsonField(name = "isSoldOut")
    public boolean isSoldOut;

    @JsonField(name = "category")
    public String categoryCode;


    public Stay getStay()
    {
        Stay stay = new Stay();

        stay.index = index;
        stay.imageUrl = getImageUrl(imgPathMain);
        stay.name = name;
        stay.price = price;
        stay.discountPrice = discount;
        stay.addressSummary = addrSummary;
        stay.latitude = latitude;
        stay.longitude = longitude;
        stay.isDailyChoice = isDailyChoice;
        stay.isSoldOut = isSoldOut;
        stay.satisfaction = rating;
        stay.districtName = districtName;
        //        stay.entryPosition;
        stay.truevr = isTrueVr;
        //        stay.stickerUrl;
        //        stay.stickerIndex;

        stay.dBenefitText = benefit;
        stay.distance = distance; // 정렬시에 보여주는 내용
        stay.categoryCode = categoryCode;
        //        stay.isLocalPlus;

        stay.setGrade(getStayGrade(grade));
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