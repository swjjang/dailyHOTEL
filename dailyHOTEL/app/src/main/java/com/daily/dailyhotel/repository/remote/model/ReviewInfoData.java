package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.ReviewInfo;

/**
 * Created by android_sam on 2017. 11. 29..
 */
@JsonObject
public class ReviewInfoData
{
    @JsonField(name = "reviewStatusType")
    public String reviewStatusType; // (string): 리뷰상태 = ['ADDABLE', 'COMPLETE', 'NONE']

    public ReviewInfo getReviewInfo()
    {
        ReviewInfo reviewInfo = new ReviewInfo();

        reviewInfo.reviewStatusType = this.reviewStatusType;

        return reviewInfo;
    }
}
