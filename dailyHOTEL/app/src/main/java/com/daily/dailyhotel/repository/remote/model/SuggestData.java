package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Suggest;

@JsonObject
public class SuggestData
{
    @JsonField(name = "id")
    public String id;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "display")
    public String display;

    @JsonField(name = "categoryKey")
    public int categoryKey;

    public SuggestData()
    {

    }

    public Suggest getSuggests()
    {
        Suggest suggest = new Suggest();
        suggest.id = id;
        suggest.name = name;
        suggest.city = display;

        return suggest;
    }
}
