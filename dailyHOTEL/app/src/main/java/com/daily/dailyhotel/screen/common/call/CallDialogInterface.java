package com.daily.dailyhotel.screen.common.call;

import android.app.Dialog;

import com.daily.base.BaseDialogViewInterface;

public interface CallDialogInterface extends BaseDialogViewInterface
{
    void showCallDialog(String message, Dialog.OnCancelListener onCancelListener);

    void showClosedTimeDialog(String message, Dialog.OnDismissListener onDismissListener);
}
