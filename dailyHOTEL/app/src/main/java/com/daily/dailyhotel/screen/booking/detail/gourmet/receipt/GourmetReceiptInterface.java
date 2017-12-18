package com.daily.dailyhotel.screen.booking.detail.gourmet.receipt;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetReceipt;

public interface GourmetReceiptInterface extends BaseDialogViewInterface
{
    void setReceipt(GourmetReceipt gourmetReceipt);

    void setFullScreenMode(boolean isFullScreenMode);
}
