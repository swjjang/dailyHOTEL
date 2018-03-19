package com.daily.dailyhotel.entity;

import com.twoheart.dailyhotel.network.model.Sticker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 8. 8..
 */

public class GourmetCampaignTags
{
    public int msgCode;
    private CampaignTag mCampaignTag;
    public String imageUrl;
    private List<Gourmet> mGourmetList;
    private List<Sticker> mStickerList;

    public void setCampaignTag(CampaignTag campaignTag)
    {
        mCampaignTag = campaignTag;
    }

    public CampaignTag getCampaignTag()
    {
        return mCampaignTag;
    }

    public void setStickerList(List<Sticker> list)
    {
        mStickerList = list;
    }

    public List<Sticker> getStickerList()
    {
        return mStickerList;
    }

    public void setGourmetList(List<Gourmet> list)
    {
        mGourmetList = list;
    }

    public List<Gourmet> getGourmetList()
    {
        return mGourmetList;
    }
}
