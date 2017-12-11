package com.daily.dailyhotel.screen.booking.receipt.gourmet;

import android.content.DialogInterface;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetReceipt;

public interface GourmetReceiptInterface extends BaseDialogViewInterface
{
    void setReceipt(GourmetReceipt gourmetReceipt);

    boolean isFullScreenStatus();

    void updateFullScreenStatus(boolean bUseFullscreen);

    void showSendEmailDialog(DialogInterface.OnDismissListener listener);
}
