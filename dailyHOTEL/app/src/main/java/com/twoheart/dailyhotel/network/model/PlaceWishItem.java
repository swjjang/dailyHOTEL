package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.Constants;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 5. 24..
 */
@JsonObject
public abstract class PlaceWishItem<T>
{
    @JsonField(name = "serviceType")
    public String serviceType;

    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "title")
    public String title;

    @JsonField(name = "rating")
    public int rating;

    @JsonField(name = "regionName")
    public String regionName;

    @JsonField(name = "addrSummary")
    public String addrSummary;

    @JsonField(name = "imgPathMain")
    public Map<String, Object> imgPathMain;

    @JsonField(name = "prices")
    public Prices prices;

    @JsonField(name = "details")
    public T details;

    @JsonIgnore
    public String imageUrl;

    @JsonIgnore
    public Constants.PlaceType placeType;

    @OnJsonParseComplete
    void onParseComplete()
    {
        placeType = "GOURMET".equalsIgnoreCase(serviceType) == true ? Constants.PlaceType.FNB : Constants.PlaceType.HOTEL;

        if (imgPathMain != null && imgPathMain.size() > 0)
        {
            Iterator<Map.Entry<String, Object>> iterator = imgPathMain.entrySet().iterator();

            if (iterator != null)
            {
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
    }

    public abstract T getDetails();
}
