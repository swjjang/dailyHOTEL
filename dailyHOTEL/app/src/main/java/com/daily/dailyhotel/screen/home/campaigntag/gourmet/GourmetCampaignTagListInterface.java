package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

import com.daily.dailyhotel.base.BaseBlurViewInterface;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.twoheart.dailyhotel.model.PlaceViewItem;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 4..
 */

@Deprecated
public interface GourmetCampaignTagListInterface extends BaseBlurViewInterface
{
    void setData(ArrayList<PlaceViewItem> placeViewItemList, GourmetBookDateTime gourmetBookDateTime);

    void setCalendarText(String text);

    void setListScrollTop();

    PlaceViewItem getItem(int position);

    void notifyWishChanged(int position, boolean wish);
}
