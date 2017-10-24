package com.daily.dailyhotel.screen.home.campaigntag.stay;

import android.app.Activity;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.twoheart.dailyhotel.model.PlaceViewItem;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public interface StayCampaignTagListInterface extends BaseDialogViewInterface
{
    void setData(ArrayList<PlaceViewItem> placeViewItemList, StayBookDateTime stayBookDateTime);

    void setCalendarText(String text);

    boolean getBlurVisibility();

    void setBlurVisibility(Activity activity, boolean visible);

    void setListScrollTop();

    PlaceViewItem getItem(int position);

    void notifyWishChanged(int position, boolean wish);
}
