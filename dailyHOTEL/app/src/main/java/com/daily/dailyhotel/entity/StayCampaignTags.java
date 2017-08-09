package com.daily.dailyhotel.entity;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.daily.dailyhotel.repository.remote.model.CampaignTagData;
import com.daily.dailyhotel.repository.remote.model.StaySalesData;
import com.twoheart.dailyhotel.model.Stay;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 8..
 */

public class StayCampaignTags
{
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
