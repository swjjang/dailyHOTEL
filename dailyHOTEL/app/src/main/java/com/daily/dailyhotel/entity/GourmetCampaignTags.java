package com.daily.dailyhotel.entity;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.repository.remote.model.CampaignTagData;
import com.daily.dailyhotel.repository.remote.model.GourmetSalesData;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.network.model.Sticker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iseung-won on 2017. 8. 8..
 */

public class GourmetCampaignTags
{
    private CampaignTag mCampaignTag;
    public String imageUrl;
    private ArrayList<Gourmet> mGourmetList;
    private ArrayList<Sticker> mStickerList;

    public void setCampaignTag(CampaignTag campaignTag)
    {
        mCampaignTag = campaignTag;
    }

    public CampaignTag getCampaignTag()
    {
        return mCampaignTag;
    }

    public void setStickerList(ArrayList<Sticker> list)
    {
        mStickerList = list;
    }

    public ArrayList<Sticker> getStickerList()
    {
        return mStickerList;
    }

    public void setGourmetList(ArrayList<Gourmet> list)
    {
        mGourmetList = list;
    }

    public ArrayList<Gourmet> getGourmetList()
    {
        return mGourmetList;
    }
}
