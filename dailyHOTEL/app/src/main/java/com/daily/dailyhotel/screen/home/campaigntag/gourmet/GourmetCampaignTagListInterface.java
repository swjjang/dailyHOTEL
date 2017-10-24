package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

import android.app.Activity;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.twoheart.dailyhotel.model.PlaceViewItem;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public interface GourmetCampaignTagListInterface extends BaseDialogViewInterface
{
    void setData(ArrayList<PlaceViewItem> placeViewItemList, GourmetBookDateTime gourmetBookDateTime);

    void setCalendarText(String text);

    boolean getBlurVisibility();

    void setBlurVisibility(Activity activity, boolean visible);

    void setListScrollTop();

    PlaceViewItem getItem(int position);

    void notifyWishChanged(int position, boolean wish);
}
