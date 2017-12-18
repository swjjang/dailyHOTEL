package com.daily.dailyhotel.screen.booking.detail.stay.receipt;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayReceipt;

public interface StayReceiptInterface extends BaseDialogViewInterface
{
    void setReceipt(StayReceipt stayReceipt);

    void setFullScreenMode(boolean isFullScreenMode);

    void setBookingState(int bookingState);
}
