package com.daily.dailyhotel.screen.booking.detail.stay.refund;

import com.daily.base.BaseDialogViewInterface;
import com.twoheart.dailyhotel.model.StayBookingDetail;

interface StayAutoRefundInterface extends BaseDialogViewInterface
{
    void setPlaceBookingDetail(StayBookingDetail stayBookingDetail);

    void setCancelReasonText(String reason);

    String getCancelReasonText();

    void setBankText(String bankName);

    void setAccountLayoutVisible(boolean visible);

    void setRefundButtonEnabled(boolean enabled);

    String getAccountNumber();

    String getAccountName();
}
