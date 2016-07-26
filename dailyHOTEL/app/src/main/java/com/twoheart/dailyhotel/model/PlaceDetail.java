package com.twoheart.dailyhotel.model;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class PlaceDetail
{
    public int index;
    public String name;
    public String address;
    public String benefit;
    public String satisfaction;
    public double latitude;
    public double longitude;
    protected ArrayList<ImageInformation> mImageInformationList;
    protected ArrayList<DetailInformation> mInformationList;
    protected ArrayList<TicketInformation> mTicketInformationList;

    public int entryIndex;
    public String showTagPriceYn;

    public PlaceDetail(int index, int entryIndex, String showTagPriceYn)
    {
        this.index = index;
        this.entryIndex = entryIndex;
        this.showTagPriceYn = showTagPriceYn;
    }

    public abstract void setData(JSONObject jsonObject) throws Exception;

    public ArrayList<ImageInformation> getImageInformationList()
    {
        return mImageInformationList;
    }

    public ArrayList<TicketInformation> getTicketInformation()
    {
        return mTicketInformationList;
    }

    public ArrayList<DetailInformation> getInformation()
    {
        return mInformationList;
    }
}
