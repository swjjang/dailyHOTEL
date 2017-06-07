package com.daily.dailyhotel.screen.booking.detail.stayoutbound;

import android.app.Dialog;
import android.location.Location;
import android.text.SpannableString;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;

import io.reactivex.Observable;

public interface StayOutboundBookingDetailInterface extends BaseDialogViewInterface
{
    void setBookingDetail(StayOutboundBookingDetail stayOutboundBookingDetail);

    Observable<Boolean> expandMap(double latitude, double longitude);

    Observable<Boolean> collapseMap();

    void setBookingDate(SpannableString checkInDate, SpannableString checkOutDate, int nights);

    void showNavigatorDialog(Dialog.OnDismissListener listener);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void setMyLocation(Location location);
}
