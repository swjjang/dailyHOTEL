package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.daily.base.util.DailyTextUtils;

import java.util.Map;

@JsonObject
public abstract class RecommendationPlace
{
    @JsonField(name = "name")
    public String name;

    @JsonField(name = "regionName")
    public String regionName;

    @JsonField(name = "addrSummary")
    public String addrSummary;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "discount")
    public int discount;

    @JsonField(name = "price")
    public int price;

    @JsonField(name = "imgPathMain")
    public Map<String, Object> imgPathMain;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "districtName")
    public String districtName;

    @JsonField(name = "rating")
    public int rating;

    @JsonField(name = "benefit")
    public String benefit;

    @JsonField(name = "isSoldOut")
    public boolean isSoldOut;

    @JsonField(name = "truevr")
    public boolean truevr;

    @JsonField(name = "stickerIdx")
    public Integer stickerIdx;

    @JsonField(name = "distance")
    public int distance;

    @JsonField(name = "reviewCount")
    public int reviewCount;

    @JsonField(name = "discountRate")
    public int discountRate;

    @JsonField(name = "newItem")
    public boolean newItem;

    @JsonField(name = "myWish")
    public boolean myWish;

    @JsonField(name = "couponDiscountText")
    public String couponDiscountText;

    @JsonIgnore
    public String imageUrl;

    @JsonIgnore
    public String stickerUrl;

    @JsonIgnore
    public int entryPosition;

    @OnJsonParseComplete
    void onParseComplete()
    {
        // 인트라넷에서 값을 잘못 넣는 경우가 있다.
        if (DailyTextUtils.isTextEmpty(addrSummary) == false)
        {
            if (addrSummary.indexOf('|') >= 0)
            {
                addrSummary = addrSummary.replace(" | ", "ㅣ");
            } else if (addrSummary.indexOf('l') >= 0)
            {
                addrSummary = addrSummary.replace(" l ", "ㅣ");
            }
        }
    }
}
