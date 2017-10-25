package com.daily.dailyhotel.screen.booking.cancel.detail.stay;

import android.app.Dialog;
import android.location.Location;
import android.text.SpannableString;

import com.daily.dailyhotel.base.BaseBlurViewInterface;
import com.daily.dailyhotel.entity.StayBookingDetail;

import io.reactivex.Observable;

public interface StayBookingCancelDetailInterface extends BaseBlurViewInterface
{
    void setBookingDetail(StayBookingDetail stayBookingDetail);

    Observable<Boolean> expandMap(double latitude, double longitude);

    Observable<Boolean> collapseMap();

    void setBookingDate(SpannableString checkInDate, SpannableString checkOutDate, int nights);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void setMyLocation(Location location);

    boolean isExpandedMap();

    Observable<Long> getLocationAnimation();

    void setBookingDetailToolbar();

    void setBookingDetailMapToolbar();

    void setDeleteBookingVisible(boolean isVisible);
}
