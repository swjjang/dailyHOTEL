package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@JsonObject
public class RecommendationStay extends RecommendationPlace
{
    @JsonField(name = "hotelIdx")
    public int index;

    @JsonField(name = "displayText")
    public String displayText;

    @JsonField(name = "grade")
    public String grade;

    @JsonField(name = "roomIdx")
    public int roomIndex;

    @JsonField(name = "sday")
    public String sday;

    @JsonField(name = "isDailyChoice")
    public boolean isDailyChoice;

    @JsonField(name = "overseas")
    public boolean overseas;

    @JsonField(name = "availableRooms")
    public int availableRooms;

    @JsonField(name = "dailyReward")
    public boolean dailyReward;

    @OnJsonParseComplete
    void onParseComplete()
    {
        super.onParseComplete();

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
