package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.twoheart.dailyhotel.model.Stay;

/**
 * Created by android_sam on 2017. 5. 24..
 */
@JsonObject
public class StayWishDetails
{
    @JsonField(name = "category")
    public String category;

    @JsonField(name = "grade")
    public String grade;

    @JsonField(name = "truevr")
    public boolean isTrueVR;

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
