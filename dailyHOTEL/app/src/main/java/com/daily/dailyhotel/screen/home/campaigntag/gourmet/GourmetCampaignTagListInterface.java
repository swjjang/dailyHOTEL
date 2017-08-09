package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

import android.app.Activity;

import com.daily.base.BaseDialogViewInterface;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;

import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public interface GourmetCampaignTagListInterface extends BaseDialogViewInterface
{
    void setData(ArrayList<PlaceViewItem> placeViewItemList, GourmetBookingDay gourmetBookingDay);

    void setCalendarText(String text);

    boolean getBlurVisibility();

    void setBlurVisibility(Activity activity, boolean visible);

    void setUsedMultiTransition(boolean isUsedMultiTransition);

    void setListScrollTop();
}
