package com.daily.dailyhotel.screen.common.dialog.call.front;

import android.app.Dialog;

import com.daily.base.BaseDialogViewInterface;

public interface FrontCallDialogInterface extends BaseDialogViewInterface
{
    void showCallDialog(String message, Dialog.OnCancelListener onCancelListener);
}
