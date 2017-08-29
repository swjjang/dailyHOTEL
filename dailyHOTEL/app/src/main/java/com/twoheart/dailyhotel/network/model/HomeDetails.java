package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.twoheart.dailyhotel.model.Stay;

/**
 * Created by android_sam on 2017. 2. 7..
 */
@JsonObject
public class HomeDetails
{
    @JsonField(name = "grade")
    public String grade;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "persons")
    public int persons;

    @JsonField(name = "truevr")
    public boolean isTrueVr;

    @JsonIgnore
    public Stay.Grade stayGrade;

    @OnJsonParseComplete
    void onParseComplete()
    {
        try
        {
            stayGrade = Stay.Grade.valueOf(grade);
        } catch (Exception e)
        {
            stayGrade = Stay.Grade.etc;
        }
    }
}
