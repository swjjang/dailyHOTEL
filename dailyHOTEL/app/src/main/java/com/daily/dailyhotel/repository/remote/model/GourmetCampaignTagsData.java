package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.GourmetCampaignTags;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.network.model.Sticker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iseung-won on 2017. 8. 8..
 */
@JsonObject
public class GourmetCampaignTagsData
{
    @JsonField(name = "responseHashtagCampaign")
    public CampaignTagData responseHashtagCampaign;

    @JsonField(name = "imgUrl")
    public String imageUrl;

    @JsonField(name = "saleRecords")
    public ArrayList<GourmetSalesData> saleRecords;

    @JsonField(name = "stickers")
    public List<Sticker> stickerList;

    public GourmetCampaignTags getGourmetCampaignTags(Context context)
    {
        GourmetCampaignTags gourmetCampaignTags = new GourmetCampaignTags();

        gourmetCampaignTags.imageUrl = imageUrl;
        gourmetCampaignTags.setStickerList(getStickerList());
        gourmetCampaignTags.setCampaignTag(responseHashtagCampaign.getCampaignTag());
        gourmetCampaignTags.setGourmetList(getGourmetList(context));

        return gourmetCampaignTags;
    }

    private ArrayList<Sticker> getStickerList()
    {
        if (stickerList == null || stickerList.size() == 0)
        {
            return null;
        }

        ArrayList<Sticker> stickerArrayList = new ArrayList<>();
        stickerArrayList.addAll(stickerList);

        return stickerArrayList;
    }

    private ArrayList<Gourmet> getGourmetList(Context context)
    {
        ArrayList<Gourmet> gourmetList = new ArrayList<>();
        if (context == null)
        {
            return gourmetList;
        }

        boolean isLowResource = false;

        if (ScreenUtils.getScreenWidth(context) <= Sticker.DEFAULT_SCREEN_WIDTH)
        {
            isLowResource = true;
        }

        for (GourmetSalesData gourmetSalesData : saleRecords)
        {
            Gourmet gourmet = gourmetSalesData.getGourmet();
            if (gourmet == null)
            {
                continue;
            }

            gourmet.imageUrl = imageUrl + gourmet.imageUrl;

            if (stickerList != null && stickerList.size() > 0)
            {
                for (Sticker sticker : stickerList)
                {
                    int stickerIndex = sticker.index;
                    if (gourmet.stickerIndex == stickerIndex)
                    {
                        gourmet.stickerUrl = isLowResource == false ? sticker.defaultImageUrl : sticker.lowResolutionImageUrl;
                        break;
                    }
                }
            }

            gourmetList.add(gourmet);
        }

        return gourmetList;
    }
}