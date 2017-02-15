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

    @JsonField
    public String title;

    @JsonField
    public String serviceType;

    @JsonField
    public String regionName;

    @JsonField
    public Prices prices;

    @JsonField
    public Map<String, Object> imgPathMain;

    @JsonField
    public HomeDetails details;

    @JsonIgnore
    public String imageUrl;

    @JsonIgnore
    public Constants.PlaceType placeType;

    @JsonField(name = "soldOut")
    public boolean isSoldOut;

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
    }
}
