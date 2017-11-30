package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.daily.dailyhotel.entity.StayWish;

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

    public StayWish getStayWish(String url)
    {
        StayWish stay = new StayWish();

        stay.name = title;
        stay.addressSummary = addrSummary;

        StayWishDetails stayWishDetails = getDetails();
        stay.grade = stayWishDetails != null ? stayWishDetails.stayGrade : com.twoheart.dailyhotel.model.Stay.Grade.etc;
        stay.index = index;
        stay.districtName = regionName;
        stay.categoryCode = stayWishDetails != null ? stayWishDetails.category : "";
        stay.satisfaction = rating;
        stay.truevr = stayWishDetails != null ? stayWishDetails.isTrueVR : false;
        stay.imageUrl = url + imageUrl;
        stay.reviewCount = reviewCount;
        stay.newItem = newItem;
        stay.myWish = myWish;
        stay.createAt = createAt;

        return stay;
    }
}
