package com.daily.dailyhotel.screen.booking.cancel.detail.gourmet;

import android.app.Dialog;
import android.location.Location;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetBookingDetail;

import io.reactivex.Observable;

public interface GourmetBookingCancelDetailInterface extends BaseDialogViewInterface
{
    void setBookingDetail(GourmetBookingDetail gourmetBookingDetail);

    Observable<Boolean> expandMap(double latitude, double longitude);

    Observable<Boolean> collapseMap();

    void setBookingDate(String ticketDate, String ticketTime);

    void showConciergeDialog(String restaurantPhone, Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void setMyLocation(Location location);

    boolean isExpandedMap();

    Observable<Long> getLocationAnimation();

    void setBookingDetailToolbar();

    void setBookingDetailMapToolbar();

    void setDeleteBookingVisible(boolean isVisible);
}
