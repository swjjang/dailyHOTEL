package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;

import android.support.v4.util.Pair;
import android.text.SpannableString;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

import java.util.List;

public interface StayOutboundRefundInterface extends BaseDialogViewInterface
{
    void setBookingDate(SpannableString checkInDate, SpannableString checkOutDate, int nights);

    void setRefundDetail(StayOutboundRefundDetail stayOutboundRefundDetail);

    void showCancelDialog(List<Pair<String, String>> cancelList, String key, String message);

    void setCancelReasonText(String reason);
}
