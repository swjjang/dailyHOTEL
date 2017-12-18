package com.daily.dailyhotel.screen.home.stay.inbound.list;

import com.daily.base.BaseDialogViewInterface;

public interface StayTabInterface extends BaseDialogViewInterface
{
    void setToolbarDateText(String text);

    void setToolbarRegionText(String text);

    void setCategoryTabLayout();

    void setOptionFilterSelected(boolean selected);
}
