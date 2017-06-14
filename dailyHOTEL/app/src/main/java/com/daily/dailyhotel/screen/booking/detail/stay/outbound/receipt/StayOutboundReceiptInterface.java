package com.daily.dailyhotel.screen.booking.detail.stay.outbound.receipt;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundReceipt;

public interface StayOutboundReceiptInterface extends BaseDialogViewInterface
{
    void setStayOutboundReceipt(StayOutboundReceipt stayOutboundReceipt);

    void setFullScreenEnabled(boolean enabled);

    void showSendEmailDialog(String email);
}
