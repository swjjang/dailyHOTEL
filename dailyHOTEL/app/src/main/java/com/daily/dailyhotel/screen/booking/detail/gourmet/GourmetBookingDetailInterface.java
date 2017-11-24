package com.daily.dailyhotel.screen.booking.detail.gourmet;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetBookingDetail;

import io.reactivex.Observable;

public interface GourmetBookingDetailInterface extends BaseDialogViewInterface
{
    void setBookingDetail(GourmetBookingDetail gourmetBookingDetail);

    void setReviewButtonLayout(String reviewStatus);

    Observable<Boolean> expandMap(double latitude, double longitude);

    Observable<Boolean> collapseMap();
}
