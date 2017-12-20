package com.daily.dailyhotel.screen.booking.detail.wait;

import android.app.Dialog;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.WaitingDeposit;

public interface PaymentWaitInterface extends BaseDialogViewInterface
{
    void showConciergeDialog(Booking.PlaceType placeType, Dialog.OnDismissListener listener);

    void setPlaceName(String placeName);

    void setWaitingDeposit(WaitingDeposit waitingDeposit);
}
