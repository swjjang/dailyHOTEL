package com.daily.dailyhotel.screen.common.dialog.email.receipt;

import android.content.DialogInterface;

import com.daily.base.BaseDialogViewInterface;

public interface EmailDialogInterface extends BaseDialogViewInterface
{
    void showEmailDialog(String email, DialogInterface.OnCancelListener cancelListener);
}
