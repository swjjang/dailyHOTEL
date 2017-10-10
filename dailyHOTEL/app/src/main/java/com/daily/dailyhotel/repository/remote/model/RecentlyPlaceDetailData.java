package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RecentlyPlaceDetail;
import com.twoheart.dailyhotel.network.model.Sticker;

/**
 * Created by android_sam on 2017. 9. 4..
 */
@JsonObject
public class RecentlyPlaceDetailData
{
    @JsonField(name = "grade")
    public String grade;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "categorySub")
    public String subCategory;

    @JsonField(name = "sticker")
    public Sticker sticker;

    @JsonField(name = "persons")
    public int persons;

    @JsonField(name = "truevr")
    public boolean isTrueVr;

    public RecentlyPlaceDetail getDetail()
    {
        RecentlyPlaceDetail detail = new RecentlyPlaceDetail();
        detail.grade = grade;
        detail.category = category;
        detail.subCategory = subCategory;
        detail.persons = persons;
        detail.sticker = sticker;
        detail.isTrueVr = isTrueVr;

        return detail;
    }
}