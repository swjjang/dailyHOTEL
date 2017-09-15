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

    @JsonField(name = "availableTicketNumbers")
    public int availableTicketNumbers;

    @JsonField(name = "isDailychoice")
    public boolean isDailyChoice;

    @JsonField(name = "persons")
    public int persons;

    @JsonField(name = "pricePerPerson")
    public int pricePerPerson;

    @JsonField(name = "categorySub")
    public String categorySub;

    @JsonField(name = "categorySeq")
    public int categorySeq;

    @JsonField(name = "categoryCode")
    public int categoryCode;

    @JsonField(name = "isExpired")
    public boolean isExpired;

    @JsonField(name = "minimumOrderQuantity")
    public int minimumOrderQuantity;

    @JsonField(name = "ticketIdx")
    public int ticketIdx;

    @JsonField(name = "openTime")
    public String openTime; // ISO-8601 -- HH:mm:ss

    @JsonField(name = "closeTime")
    public String closeTime; // ISO-8601 -- HH:mm:ss

    @JsonField(name = "lastOrderTime")
    public String lastOrderTime; // ISO-8601

    @JsonField(name = "startEatingTime")
    public String startEatingTime; // ISO-8601

    @JsonField(name = "endEatingTime")
    public String endEatingTime; // ISO-8601

    @JsonField(name = "menuSummary")
    public String menuSummary;

    @JsonField(name = "menuDetail")
    public List<String> menuDetail;

    @JsonField(name = "needToKnow")
    public String needToKnow;

    @JsonField(name = "menuBenefit")
    public String menuBenefit;

    @JsonField(name = "primaryTicketImageDescription")
    public String primaryTicketImageDescription;

    @JsonField(name = "primaryTicketImageUrl")
    public String primaryTicketImageUrl;

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
