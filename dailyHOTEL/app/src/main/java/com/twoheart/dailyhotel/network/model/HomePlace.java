package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.twoheart.dailyhotel.util.Constants;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 2. 7..
 */
@JsonObject
public class HomePlace
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
    public HomeDetails details;

    @JsonIgnore
    public String imageUrl;

    @JsonIgnore
    public Constants.PlaceType placeType;

    @JsonField(name = "soldOut")
    public boolean isSoldOut;

    @JsonIgnore
    public double distance; // 고메 추천 영역을 위해 삽입

    @OnJsonParseComplete
    void onParseComplete()
    {
        placeType = "GOURMET".equalsIgnoreCase(serviceType) == true ? Constants.PlaceType.FNB : Constants.PlaceType.HOTEL;

        if (imgPathMain == null || imgPathMain.size() == 0)
        {
            return;
        }

        Iterator<Map.Entry<String, Object>> iterator = imgPathMain.entrySet().iterator();

        if (iterator == null)
        {
            return;
        }

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
    }
}
