package com.daily.dailyhotel.entity;

import com.twoheart.dailyhotel.model.Stay;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 8..
 */

public class StayCampaignTags
{
    public int msgCode;
    private CampaignTag mCampaignTag;
    public String imageUrl;
    private ArrayList<Stay> mStayList;

    public void setCampaignTag(CampaignTag campaignTag)
    {
        mCampaignTag = campaignTag;
    }

    public CampaignTag getCampaignTag()
    {
        return mCampaignTag;
    }

    public void setStayList(ArrayList<Stay> list)
    {
        mStayList = list;
    }

    public ArrayList<Stay> getStayList()
    {
        return mStayList;
    }
}
