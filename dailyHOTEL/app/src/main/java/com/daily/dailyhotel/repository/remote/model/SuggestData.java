package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Suggest;

@JsonObject
public class SuggestData
{
    @JsonField(name = "id")
    public long id;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "display")
    public String display;

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

    public SuggestData()
    {

    }

    public Suggest getSuggests()
    {
        Suggest suggest = new Suggest();
        suggest.id = id;
        suggest.name = name;
        suggest.city = city;
        suggest.country = country;
        suggest.countryCode = countryCode;
        suggest.categoryKey = categoryKey;
        suggest.display = display;
        suggest.latitude = lat;
        suggest.longitude = lng;

        return suggest;
    }
}
