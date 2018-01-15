package com.daily.dailyhotel.screen.common.dialog.refund;

import com.daily.dailyhotel.base.BaseMultiWindowViewInterface;
import com.daily.dailyhotel.entity.Bank;

import java.util.List;

public interface BankListDialogInterface extends BaseMultiWindowViewInterface
{
    void setBankList(Bank selectedBank, List<Bank> bankList);

    void onConfigurationChange(int orientation, boolean isInMultiWindowMode);
}
