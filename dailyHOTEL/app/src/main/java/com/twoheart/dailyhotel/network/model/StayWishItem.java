package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.daily.dailyhotel.entity.Stay;

/**
 * Created by android_sam on 2017. 5. 24..
 */
@JsonObject
public class StayWishItem extends PlaceWishItem<StayWishDetails>
{
    @Override
    public StayWishDetails getDetails()
    {
        return details;
    }

    @OnJsonParseComplete
    @Override
    void onParseComplete()
    {
        super.onParseComplete();
    }

    public Stay getStayWish(String url)
    {
        Stay stay = new Stay();

        stay.name = title;
        stay.addressSummary = addrSummary;

        StayWishDetails stayWishDetails = getDetails();
        stay.grade = stayWishDetails != null ? com.daily.dailyhotel.entity.Stay.Grade.valueOf(stayWishDetails.stayGrade.name()) : com.daily.dailyhotel.entity.Stay.Grade.etc;
        stay.index = index;
        stay.districtName = regionName;
        stay.categoryCode = stayWishDetails != null ? stayWishDetails.category : "";
        stay.satisfaction = rating;
        stay.trueVR = stayWishDetails != null ? stayWishDetails.isTrueVR : false;
        stay.imageUrl = url + imageUrl;
        stay.reviewCount = reviewCount;
        stay.newStay = newItem;
        stay.myWish = myWish;
        stay.createdWishDateTime = createdAt;

        return stay;
    }
}
