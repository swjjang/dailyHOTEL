package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.network.model.Sticker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 6. 20..
 * 최근 본 업장 데이터로 반영 됨 - 리스트 작업시 재확인 필요
 */
@JsonObject
public class GourmetListData
{
    @JsonField(name = "gourmetSales")
    public List<GourmetSalesData> gourmetSalesDataList;

    @JsonField(name = "imgUrl")
    public String imageUrl;

    @JsonField(name = "gourmetSalesCount")
    public int gourmetSalesCount;

    @JsonField(name = "searchCount")
    public int searchCount;

    @JsonField(name = "searchMaxCount")
    public int searchMaxCount;

    @JsonField(name = "filter")
    public GourmetFilterData gourmetFilterData;

    @JsonField(name = "stickers")
    public List<Sticker> stickerList;

    public List<Gourmet> getGourmetList(Context context)
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

        for (GourmetSalesData gourmetSalesData : gourmetSalesDataList)
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
