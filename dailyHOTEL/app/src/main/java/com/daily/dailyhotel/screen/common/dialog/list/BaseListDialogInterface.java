package com.daily.dailyhotel.screen.common.dialog.list;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.base.BaseMultiWindowViewInterface;
import com.daily.dailyhotel.parcel.ListDialogItemParcel;

import java.util.List;

public interface BaseListDialogInterface
{
    interface ViewInterface extends BaseMultiWindowViewInterface
    {
        void setData(ListDialogItemParcel selectedItem, List<ListDialogItemParcel> list);

        void onConfigurationChange(int orientation, boolean isInMultiWindowMode);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onNativeButtonClick();

        void onPositiveButtonClick(ListDialogItemParcel selectedItem);

        void checkConfigChange();
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity, String screenName);
    }
}
