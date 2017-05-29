package com.daily.dailyhotel.screen.common.call;

import android.app.Dialog;

import com.daily.base.BaseViewInterface;

public interface CallDialogInterface extends BaseViewInterface
{
    void showCallDialog(String message, Dialog.OnCancelListener onCancelListener);

    void showClosedTimeDialog(String message, Dialog.OnDismissListener onDismissListener);
}
