package com.daily.dailyhotel.screen.booking.cancel;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.BookingCancel;

import java.util.List;

public interface BookingCancelListInterface extends BaseDialogViewInterface
{
    void setRefreshing(boolean refreshing);

    void logoutLayout();

    void setBookingCancelList(List<BookingCancel> bookingCancelList);
}
