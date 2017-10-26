package com.daily.dailyhotel.screen.common.dialog.call.restaurant;

import android.app.Dialog;

import com.daily.base.BaseDialogViewInterface;

public interface RestaurantCallDialogInterface extends BaseDialogViewInterface
{
    void showCallDialog(String message, Dialog.OnCancelListener onCancelListener);
}
