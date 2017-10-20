package com.daily.dailyhotel.entity;

import com.twoheart.dailyhotel.network.model.Prices;

import java.util.Map;

/**
 * Created by android_sam on 2017. 9. 4..
 */

public class RecentlyPlace
{
    public int index;
    public String title;
    public String serviceType;
    public String regionName;
    public Prices prices;
    public int rating;
    public String addrSummary;
    public Map<String, Object> imgPathMain;
    public RecentlyPlaceDetail details;
    public String imageUrl;
    public boolean isSoldOut;

    public boolean dailyReward;
    public int reviewCount;
    public boolean newItem;
    public boolean myWish;
}
