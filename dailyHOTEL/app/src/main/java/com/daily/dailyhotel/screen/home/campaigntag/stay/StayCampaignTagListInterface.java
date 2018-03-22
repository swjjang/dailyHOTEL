package com.daily.dailyhotel.screen.home.campaigntag.stay;

import com.daily.dailyhotel.base.BaseBlurViewInterface;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.twoheart.dailyhotel.model.PlaceViewItem;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 4..
 */

@Deprecated
public interface StayCampaignTagListInterface extends BaseBlurViewInterface
{
    void setData(ArrayList<PlaceViewItem> placeViewItemList, StayBookDateTime stayBookDateTime, boolean activeReward);

    void setCalendarText(String text);

    void setListScrollTop();

    PlaceViewItem getItem(int position);

    void notifyWishChanged(int position, boolean wish);
}
