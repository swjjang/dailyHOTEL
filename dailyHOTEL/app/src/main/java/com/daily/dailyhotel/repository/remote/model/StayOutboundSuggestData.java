package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundSuggest;

@JsonObject
public class StayOutboundSuggestData
{
    @JsonField(name = "id")
    public long id;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "display")
    public String display;

    @JsonField(name = "displayText")
    public String displayText;

    @JsonField(name = "city")
    public String city;

    @JsonField(name = "categoryKey")
    public String categoryKey;

    @JsonField(name = "countryCode")
    public String countryCode;

    @JsonField(name = "country")
    public String country;

    @JsonField(name = "lat")
    public double lat;

    @JsonField(name = "lng")
    public double lng;

    public StayOutboundSuggestData()
    {

    }

    public StayOutboundSuggest getSuggests()
    {
        StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest();
        stayOutboundSuggest.id = id;
        stayOutboundSuggest.name = name;
        stayOutboundSuggest.city = city;
        stayOutboundSuggest.country = country;
        stayOutboundSuggest.countryCode = countryCode;
        stayOutboundSuggest.categoryKey = categoryKey;
        stayOutboundSuggest.display = display;
        stayOutboundSuggest.displayText = displayText;
        stayOutboundSuggest.latitude = lat;
        stayOutboundSuggest.longitude = lng;

        return stayOutboundSuggest;
    }
}
