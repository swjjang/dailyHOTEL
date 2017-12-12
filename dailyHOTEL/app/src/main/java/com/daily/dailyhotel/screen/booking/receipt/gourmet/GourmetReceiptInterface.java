package com.daily.dailyhotel.screen.booking.receipt.gourmet;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetReceipt;

public interface GourmetReceiptInterface extends BaseDialogViewInterface
{
    void setReceipt(GourmetReceipt gourmetReceipt);

    void setFullScreenMode(boolean isFullScreenMode);
}
