package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.twoheart.dailyhotel.network.model.Prices;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 9. 4..
 */
@JsonObject
public class RecentlyPlaceData
{
    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "title")
    public String title;

    @JsonField(name = "serviceType")
    public String serviceType;

    @JsonField(name = "regionName")
    public String regionName;

    @JsonField(name = "prices")
    public Prices prices;

    @JsonField(name = "rating")
    public int rating;

    @JsonField(name = "addrSummary")
    public String addrSummary;

    @JsonField(name = "imgPathMain")
    public Map<String, Object> imgPathMain;

    @JsonField(name = "details")
    public RecentlyPlaceDetailData details;

    @JsonField(name = "soldOut")
    public boolean isSoldOut;

    public RecentlyPlace getRecentlyPlace()
    {
        RecentlyPlace place = new RecentlyPlace();
        place.index = index;
        place.title = title;
        place.serviceType = serviceType;
        place.regionName = regionName;
        place.prices = prices;
        place.rating = rating;
        place.addrSummary = addrSummary;
        place.imgPathMain = imgPathMain;
        place.details = details.getDetail();
        place.isSoldOut = isSoldOut;
        place.imageUrl = getImageUrl();

        return place;
    }

    public String getImageUrl()
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
