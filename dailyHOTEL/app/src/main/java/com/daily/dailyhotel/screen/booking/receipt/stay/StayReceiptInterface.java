package com.daily.dailyhotel.screen.booking.receipt.stay;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayReceipt;

public interface StayReceiptInterface extends BaseDialogViewInterface
{
    void setReceipt(StayReceipt stayReceipt);

    void setFullScreenMode(boolean isFullScreenMode);

    void setBookingState(int bookingState);
}
