package com.daily.dailyhotel.repository.local.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by android_sam on 2017. 6. 23..
 */

public class AnalyticsRealmObject extends RealmObject
{
    public String screenName;

    @PrimaryKey
    public int placeIndex;
    public String placeName;
    public String provinceName;
    public String areaName; // province 의 area 명
    public String addressAreaName; // addressSummary 의 split 이름 stay.addressSummary.split("\\||l|ㅣ|I")  index : 0;
    public int price; // 정가
    public int discountPrice; // 표시가
    public String showOrginalPriceYn; // stay.price <= 0 || stay.price <= stay.discountPrice ? "N" : "Y"
    public int listPosition;
    public int totalListCount;
    public boolean isDailyChoice;
    public String gradeName;
}
