package com.daily.dailyhotel.screen.common.dialog.navigator;

import android.app.Dialog;

import com.daily.base.BaseDialogViewInterface;

public interface NavigatorDialogInterface extends BaseDialogViewInterface
{
    void showNavigatorInboundDialog(boolean skTelecomOperation);

    void showNavigatorOutboundDialog();
}
