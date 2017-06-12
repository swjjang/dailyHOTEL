package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;

import android.text.SpannableString;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

public interface StayOutboundRefundInterface extends BaseDialogViewInterface
{
    void setBookingDate(SpannableString checkInDate, SpannableString checkOutDate, int nights);

    void setRefundDetail(StayOutboundRefundDetail stayOutboundRefundDetail);
}
