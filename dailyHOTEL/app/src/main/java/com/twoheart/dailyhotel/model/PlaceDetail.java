package com.twoheart.dailyhotel.model;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class PlaceDetail
{
    public int index = -1; // -1 값 일 경우 Deeplink 로 진입 된 경우이며, index는 1부터 시작함 GA 용
    public int listCount = -1; //  -1 값 일 경우 Deeplink 로 진입 된 경우이며, 상세 진입시 진입 전 list의 노출 갯수 GA 용
    public String name;
    public String address;
    public boolean isOverseas; // 0 : 국내 , 1 : 해외
    public String benefit;
    public int ratingPersons;
    public int ratingValue;
    public double latitude;
    public double longitude;

    public boolean myWish; // 위시리스트 클릭 상태
    public int wishCount; // 위시리스트 카운트

    protected ArrayList<ImageInformation> mImageInformationList;
    protected ArrayList<DetailInformation> mInformationList;
    protected ArrayList<String> mBenefitInformation;

    // GA용
    public int entryPosition;
    public String isShowOriginalPrice; // "Y", "N", empty
    public boolean isDailyChoice;

    public abstract void setData(JSONObject jsonObject) throws Exception;

    public ArrayList<ImageInformation> getImageInformationList()
    {
        return mImageInformationList;
    }

    public ArrayList<DetailInformation> getInformation()
    {
        return mInformationList;
    }

    public ArrayList<String> getBenefitInformation()
    {
        return mBenefitInformation;
    }
}
