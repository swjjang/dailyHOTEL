package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@JsonObject
public class RecommendationGourmet extends RecommendationPlace
{
    @JsonField(name = "restaurantIdx")
    public int index;

    @JsonField(name = "isDailychoice")
    public boolean isDailyChoice;

    @JsonField(name = "persons")
    public int persons;

    @JsonField(name = "categorySub")
    public String categorySub;

    @OnJsonParseComplete
    void onParseComplete()
    {
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
