package com.twoheart.dailyhotel.model;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class PlaceDetail
{
    public int index;
    public String name;
    public String address;
    public boolean isOverseas; // 0 : 국내 , 1 : 해외
    public String benefit;
    public String satisfaction;
    public double latitude;
    public double longitude;
    protected ArrayList<ImageInformation> mImageInformationList;
    protected ArrayList<DetailInformation> mInformationList;
    // GA용
    public int entryPosition;
    public String isShowOriginalPrice; // "Y", "N", empty

    public abstract void setData(JSONObject jsonObject) throws Exception;

    public ArrayList<ImageInformation> getImageInformationList()
    {
        return mImageInformationList;
    }

    public ArrayList<DetailInformation> getInformation()
    {
        return mInformationList;
    }
}
