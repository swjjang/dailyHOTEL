package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.entity.GourmetCampaignTags;
import com.twoheart.dailyhotel.network.model.Sticker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 8. 8..
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

        return new ArrayList<>(stickerList);
    }

    private List<Gourmet> getGourmetList(Context context)
    {
        List<Gourmet> gourmetList = new ArrayList<>();
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
            Gourmet gourmet = gourmetSalesData.getEntityGourmet();

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
