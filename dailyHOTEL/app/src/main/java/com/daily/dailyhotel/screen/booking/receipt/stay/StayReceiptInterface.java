package com.daily.dailyhotel.screen.booking.receipt.stay;

import android.content.DialogInterface;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayReceipt;

public interface StayReceiptInterface extends BaseDialogViewInterface
{
    void setReceipt(StayReceipt stayReceipt);

    void updateFullScreenStatus(boolean bUseFullscreen);

    void showSendEmailDialog(DialogInterface.OnDismissListener listener);

    void setBookingState(int bookingState);
}
