package com.daily.dailyhotel.screen.common.dialog.refund;

import com.daily.dailyhotel.base.BaseMultiWindowViewInterface;

/**
 * Created by android_sam on 2018. 1. 10..
 */

public interface AutoRefundDialogInterface extends BaseMultiWindowViewInterface
{
    void showInputKeyboard();

    void hideInputKeyboard();

    void onConfigurationChange(int orientation, boolean isInMultiWindowMode);

    void setCancelType(int cancelType);

    void setEtcMessage(String message);
}
