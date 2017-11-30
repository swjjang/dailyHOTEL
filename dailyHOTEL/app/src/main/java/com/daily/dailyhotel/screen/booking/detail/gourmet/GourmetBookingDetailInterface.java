package com.daily.dailyhotel.screen.booking.detail.gourmet;

import android.app.Dialog;
import android.location.Location;

import com.daily.dailyhotel.base.BaseBlurViewInterface;
import com.daily.dailyhotel.entity.GourmetMultiBookingDetail;
import com.daily.dailyhotel.entity.ReviewInfo;

import io.reactivex.Observable;

public interface GourmetBookingDetailInterface extends BaseBlurViewInterface
{
    void setBookingDetailToolbar();

    void setBookingDetailMapToolbar();

    void setBookingDetail(GourmetMultiBookingDetail gourmetBookingDetail);

    void setRemindDate(String currentDateTime, String bookingDateTime);

    void setBookingDateAndPersons(String ticketDate, int persons);

    void setHiddenBookingVisible(int bookingState);

    void setReviewButtonLayout(ReviewInfo reviewInfo);

    boolean isExpandedMap();

    Observable<Boolean> expandMap(double latitude, double longitude);

    Observable<Boolean> collapseMap();

    Observable<Long> getLocationAnimation();

    void setMyLocation(Location location);

    void showConciergeDialog(String restaurantPhone, Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);
}
