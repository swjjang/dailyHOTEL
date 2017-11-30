package com.daily.dailyhotel.entity;

public class StayWish
{
    public int index;
    public String imageUrl;
    public String name;
    public String addressSummary;
    public double latitude;
    public double longitude;
    public int satisfaction;
    public String districtName;
    public int entryPosition;
    public boolean truevr;

    public double distance; // 정렬시에 보여주는 내용
    public String categoryCode;

    public com.twoheart.dailyhotel.model.Stay.Grade grade;

    public String regionName;
    public boolean isOverseas;

    // 신규 추가
    public int reviewCount;
    public int discountRate;
    public boolean newItem;
    public boolean myWish;

    public String createAt; // ISO-8601 위시 등록 시간
}
